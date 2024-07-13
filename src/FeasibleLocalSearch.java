import java.util.*;

public class FeasibleLocalSearch {
    private Solution initialSolution; // 初始解
    private List<Item> allItems; // 所有物品列表
    private List<Item> unassignedItems;//未分配物品列表
    private int Ncons; // 最大迭代次数
    private double alpha; // 禁忌参数
    private ValueContributionMatrix valueContributionMatrix; // 价值贡献矩阵
    private Solution S; // 当前解
    private Solution SLocalBest; // 局部最优解
    private TabuList tabuList; // 禁忌表

    // 构造函数，初始化各属性
    public FeasibleLocalSearch(Solution initialSolution,ValueContributionMatrix valueContributionMatrix, List<Item> allItems, int Ncons, double alpha) {
        this.initialSolution = initialSolution;
        this.allItems = allItems;
        this.Ncons = Ncons;
        this.alpha = alpha;
        this.valueContributionMatrix = valueContributionMatrix;
        this.tabuList = new TabuList(allItems, alpha);
    }

    // 执行可行局部搜索
    public void performFeasibleLocalSearch() {
        int n = 0; // 计数器
        S = cloneSolution(initialSolution); // 初始解
        SLocalBest = cloneSolution(initialSolution); // 初始局部最优解

        while (n < Ncons) {
            // 构造邻域
            List<Neighbor> neighborhood = constructNeighborhoods();
            // 选择最优邻居
            Neighbor bestNeighbor = chooseBestNeighbor(neighborhood);
            S = bestNeighbor.getSolution(); // 更新当前解
            valueContributionMatrix.updateMatrix(bestNeighbor.getMove());//执行最优动作并更新ValueContributionMatrix
            tabuList.updateTabuList(bestNeighbor.getMove()); // 更新禁忌表

            // 如果当前解优于局部最优解，更新局部最优解并重置计数器
            if (S.getTotalValue() > SLocalBest.getTotalValue()) {
                SLocalBest = cloneSolution(S);
                n = 0;
            } else {
                n++;
            }
        }
    }

    // 构造所有邻域
    public List<Neighbor> constructNeighborhoods() {
        List<Neighbor> neighborhoods = new ArrayList<>();
        List<Knapsack> knapsacks = S.getKnapsacks();

        neighborhoods.addAll(generateExtractionNeighborhood(knapsacks)); // 生成抽取邻域
        neighborhoods.addAll(generateInsertionNeighborhood(knapsacks)); // 生成插入邻域
        neighborhoods.addAll(generateReallocationNeighborhood(knapsacks)); // 生成重分配邻域
        neighborhoods.addAll(generateExchangeNeighborhood(knapsacks)); // 生成交换邻域

        return neighborhoods;
    }

    // 生成抽取邻域
    private List<Neighbor> generateExtractionNeighborhood(List<Knapsack> knapsacks) {
        List<Neighbor> neighborhoods = new ArrayList<>();
        for (Knapsack knapsack : knapsacks) {
            for (Item item : knapsack.getItems()) {
                Solution newSolution = cloneSolution(S);
                Knapsack targetKnapsack = findKnapsackById(newSolution, knapsack.getId());
                MoveOperator.extraction(item, targetKnapsack);
                double moveGain = valueContributionMatrix.getExtractionGain(item, targetKnapsack);
                Move move = new Move(Move.MoveType.EXTRACTION, item, null, targetKnapsack, null, moveGain);
                neighborhoods.add(new Neighbor(newSolution, move));
            }
        }
        return neighborhoods;
    }

    // 生成插入邻域
    private List<Neighbor> generateInsertionNeighborhood(List<Knapsack> knapsacks) {
        List<Neighbor> neighborhoods = new ArrayList<>();
        for (Knapsack knapsack : knapsacks) {
            for (Item item : S.getUnassignedItems(allItems)) {
                if (MoveOperator.canInsert(item, knapsack)) {
                    Solution newSolution = cloneSolution(S);
                    Knapsack targetKnapsack = findKnapsackById(newSolution, knapsack.getId());
                    MoveOperator.insertion(item, targetKnapsack);
                    double moveGain = valueContributionMatrix.getInsertionGain(item, targetKnapsack);
                    Move move = new Move(Move.MoveType.INSERTION, item, null, targetKnapsack, null, moveGain);
                    neighborhoods.add(new Neighbor(newSolution, move));
                }
            }
        }
        return neighborhoods;
    }

    // 生成重分配邻域
    private List<Neighbor> generateReallocationNeighborhood(List<Knapsack> knapsacks) {
        List<Neighbor> neighborhoods = new ArrayList<>();
        for (Knapsack fromKnapsack : knapsacks) {
            for (Item item : fromKnapsack.getItems()) {
                for (Knapsack toKnapsack : knapsacks) {
                    if (fromKnapsack != toKnapsack && MoveOperator.canReallocate(item, fromKnapsack, toKnapsack)) {
                        Solution newSolution = cloneSolution(S);
                        Knapsack targetFromKnapsack = findKnapsackById(newSolution, fromKnapsack.getId());
                        Knapsack targetToKnapsack = findKnapsackById(newSolution, toKnapsack.getId());
                        MoveOperator.reallocation(item, targetFromKnapsack, targetToKnapsack);
                        double moveGain = valueContributionMatrix.getReallocationGain(item, targetFromKnapsack, targetToKnapsack);
                        Move move = new Move(Move.MoveType.REALLOCATION, item, null, targetFromKnapsack, targetToKnapsack, moveGain);
                        neighborhoods.add(new Neighbor(newSolution, move));
                    }
                }
            }
        }
        return neighborhoods;
    }

    // 生成交换邻域
    private List<Neighbor> generateExchangeNeighborhood(List<Knapsack> knapsacks) {
        List<Neighbor> neighborhoods = new ArrayList<>();
        unassignedItems=S.getUnassignedItems(allItems);
        for (int i = 0; i < knapsacks.size(); i++) {
            Knapsack knapsack1 = knapsacks.get(i);
            for (int j = i + 1; j < knapsacks.size(); j++) {
                Knapsack knapsack2 = knapsacks.get(j);
                // case 1: 交换不同背包中的两个已分配物品
                for (Item item1 : knapsack1.getItems()) {
                    for (Item item2 : knapsack2.getItems()) {
                        if (MoveOperator.canExchange(item1, item2, knapsack1, knapsack2)) {
                            Solution newSolution = cloneSolution(S);
                            Knapsack targetKnapsack1 = findKnapsackById(newSolution, knapsack1.getId());
                            Knapsack targetKnapsack2 = findKnapsackById(newSolution, knapsack2.getId());
                            MoveOperator.exchange(item1, item2, targetKnapsack1, targetKnapsack2);
                            double moveGain = valueContributionMatrix.getExchangeGain(item1, item2, targetKnapsack1, targetKnapsack2);
                            Move move = new Move(Move.MoveType.EXCHANGE, item1, item2, targetKnapsack1, targetKnapsack2, moveGain);
                            neighborhoods.add(new Neighbor(newSolution, move));
                        }
                    }
                }
            }
            // case 2: 交换背包中的一个已分配物品与一个未分配物品
            for (Item item1 : knapsack1.getItems()) {
                for (Item unassignedItem : unassignedItems) {
                    if (MoveOperator.canExchange(item1, unassignedItem, knapsack1, null)) {
                        Solution newSolution = cloneSolution(S);
                        Knapsack targetKnapsack1 = findKnapsackById(newSolution, knapsack1.getId());
                        MoveOperator.exchange(item1, unassignedItem, targetKnapsack1, null);
                        double moveGain = valueContributionMatrix.getExchangeGain(item1, unassignedItem, targetKnapsack1, null);
                        Move move = new Move(Move.MoveType.EXCHANGE, item1, unassignedItem, targetKnapsack1, null, moveGain);
                        neighborhoods.add(new Neighbor(newSolution, move));
                    }
                }
            }
        }
        return neighborhoods;
    }


    // 选择最优邻居
    private Neighbor chooseBestNeighbor(List<Neighbor> neighborhood) {
        Neighbor bestNeighbor = null;
        double bestGain = Double.NEGATIVE_INFINITY;

        for (Neighbor neighbor : neighborhood) {
            Move neighborMove = neighbor.getMove();
            if ((!tabuList.isTabu(neighborMove) && neighborMove.getMoveGain() > bestGain)|| meetsAspirationCriterion(neighbor)) {
                bestGain = neighborMove.getMoveGain();
                bestNeighbor = neighbor;
            }
        }

        return bestNeighbor;
    }

    // 检查是否满足aspiration criterion
    public boolean meetsAspirationCriterion(Neighbor neighbor) {
        return neighbor.getSolution().getTotalValue()> SLocalBest.getTotalValue();
    }

    // 克隆解的辅助方法
    private static Solution cloneSolution(Solution original) {
        List<Knapsack> originalKnapsacks = original.getKnapsacks();
        List<Knapsack> clonedKnapsacks = new ArrayList<>();
        for (Knapsack knapsack : originalKnapsacks) {
            Knapsack clonedKnapsack = new Knapsack(knapsack.getId(), knapsack.getCapacity());
            clonedKnapsack.getItems().addAll(knapsack.getItems());
            clonedKnapsacks.add(clonedKnapsack);
        }
        return new Solution(clonedKnapsacks);
    }

    // 根据ID查找背包的辅助方法
    private static Knapsack findKnapsackById(Solution solution, int id) {
        for (Knapsack knapsack : solution.getKnapsacks()) {
            if (knapsack.getId() == id) {
                return knapsack;
            }
        }
        return null;
    }

    // 获取当前解
    public Solution getCurrentSolution() {
        return S;
    }

    // 获取局部最优解
    public Solution getLocalBestSolution() {
        return SLocalBest;
    }
}


