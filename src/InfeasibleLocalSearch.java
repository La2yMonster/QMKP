import java.util.*;

public class InfeasibleLocalSearch {
    private Solution initialSolution; // 初始解
    private List<Item> allItems; // 所有物品列表
    private int M; // InfeasibleLocalSearch的最大迭代次数
    private double alpha; // alpha参数
    private double beta; // beta参数
    private ValueContributionMatrix valueContributionMatrix; // 价值贡献矩阵
    private Solution S; // 当前解
    private Solution SLocalBest; // 局部最优解
    private TabuList tabuList; // 禁忌表

    // 构造函数，初始化各属性
    public InfeasibleLocalSearch(Solution initialSolution, ValueContributionMatrix valueContributionMatrix, List<Item> allItems, int M, double alpha, double beta) {
        this.initialSolution = initialSolution;
        this.allItems = allItems;
        this.M = M;
        this.alpha = alpha;
        this.beta = beta;
        this.valueContributionMatrix = valueContributionMatrix;
        this.tabuList = new TabuList(allItems, alpha); // 初始化禁忌表
    }

    // 执行不可行局部搜索
    public void performInfeasibleLocalSearch() {
        int m = 0;
        S = cloneSolution(initialSolution);
        SLocalBest = cloneSolution(S);

        while (m < M) {

            if (S.isFeasible() && S.getTotalValue() <= SLocalBest.getTotalValue()) { // 如果当前解可行且不为局部最优
                // 应用第一类操作符
                List<Neighbor> neighborhood = constructFirstTypeNeighborhoods();
                Neighbor bestNeighbor = chooseBestNeighbor(neighborhood);
                S = bestNeighbor.getSolution();
                Move bestMove = bestNeighbor.getMove();
                valueContributionMatrix.updateMatrix(bestMove); // 执行最优动作并更新价值贡献矩阵
                tabuList.updateTabuList(bestMove); // 更新禁忌表

                // 更新局部最优解
                if (S.getTotalValue() > SLocalBest.getTotalValue()) {
                    SLocalBest = cloneSolution(S);
                }
            } else {
                // 应用第二类操作符
                Random rand = new Random();
                List<Knapsack> knapsacks = S.getKnapsacks();
                Knapsack selectedKnapsack = knapsacks.get(rand.nextInt(knapsacks.size()));
                Move bestMove;

                if (selectedKnapsack.getTotalWeight() < selectedKnapsack.getCapacity()) {
                    // 移动物品到选中的背包并更新当前解
                    bestMove = moveItemToSelectedKnapsack(selectedKnapsack);
                } else {
                    // 从选中的背包移走物品并更新当前解
                    bestMove = moveItemFromSelectedKnapsack(selectedKnapsack);
                }
                if (bestMove != null) {
                    valueContributionMatrix.updateMatrix(bestMove); // 执行最优动作并更新价值贡献矩阵
                    tabuList.updateTabuList(bestMove); // 更新禁忌表
                    if (S.isFeasible() && S.getTotalValue() > SLocalBest.getTotalValue()) {
                        SLocalBest = cloneSolution(S);
                    }
                } else {
//                    throw new IllegalStateException("Move is null");
                    continue;
                }
            }

            m++;
        }
    }

    // 构造第一类邻域
    private List<Neighbor> constructFirstTypeNeighborhoods() {
        List<Neighbor> neighborhoods = new ArrayList<>();
        List<Knapsack> knapsacks = S.getKnapsacks();

        // 插入、重分配和交换邻域构造
        neighborhoods.addAll(generateInsertionNeighborhood(knapsacks));
        neighborhoods.addAll(generateReallocationNeighborhood(knapsacks));
        neighborhoods.addAll(generateExchangeNeighborhood(knapsacks));

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
        for (int i = 0; i < knapsacks.size(); i++) {
            Knapsack knapsack1 = knapsacks.get(i);
            for (int j = i + 1; j < knapsacks.size(); j++) {
                Knapsack knapsack2 = knapsacks.get(j);
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
        }
        return neighborhoods;
    }

    // 将物品移到选中的背包
    private Move moveItemToSelectedKnapsack(Knapsack selectedKnapsack) {
        Item bestItem = null;
        double bestGain = Double.NEGATIVE_INFINITY;
        Knapsack otherKnapsack = null;

        for (Knapsack knapsack : S.getKnapsacks()) {
            if (knapsack != selectedKnapsack && knapsack.getTotalWeight() >= selectedKnapsack.getTotalWeight()) {
                for (Item item : knapsack.getItems()) {
                    double gain = valueContributionMatrix.getNormReallocationGain(beta, item, knapsack, selectedKnapsack);
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestItem = item;
                        otherKnapsack = knapsack;
                    }
                }
            }
        }

        if (bestItem != null) {
            Solution newSolution = cloneSolution(S);
            Knapsack fromKnapsack = findKnapsackById(newSolution, otherKnapsack.getId());
            Knapsack toKnapsack = findKnapsackById(newSolution, selectedKnapsack.getId());
            MoveOperator.reallocation(bestItem, fromKnapsack, toKnapsack);
            S = newSolution;
            return new Move(Move.MoveType.REALLOCATION, bestItem, null, fromKnapsack, toKnapsack, bestGain);
        }
        return null;
    }

    // 将物品从选中的背包移走
    private Move moveItemFromSelectedKnapsack(Knapsack selectedKnapsack) {
        Item bestItem = null;
        double bestGain = Double.NEGATIVE_INFINITY;
        Knapsack otherKnapsack = null;

        for (Item item : selectedKnapsack.getItems()) {
            for (Knapsack knapsack : S.getKnapsacks()) {
                if (knapsack != selectedKnapsack && knapsack.getTotalWeight() < selectedKnapsack.getTotalWeight()) {
                    double gain = valueContributionMatrix.getNormReallocationGain(beta, item, selectedKnapsack, knapsack);
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestItem = item;
                        otherKnapsack = knapsack;
                    }
                }
            }
        }

        if (bestItem != null) {
            Solution newSolution = cloneSolution(S);
            Knapsack fromKnapsack = findKnapsackById(newSolution, selectedKnapsack.getId());
            Knapsack toKnapsack = findKnapsackById(newSolution, otherKnapsack.getId());
            MoveOperator.reallocation(bestItem, fromKnapsack, toKnapsack);
            S = newSolution;
            return new Move(Move.MoveType.REALLOCATION, bestItem, null, fromKnapsack, toKnapsack, bestGain);
        }
        return null;
    }

    // 选择最佳邻居
    private Neighbor chooseBestNeighbor(List<Neighbor> neighborhood) {
        Neighbor bestNeighbor = null;
        double bestGain = Double.NEGATIVE_INFINITY;

        for (Neighbor neighbor : neighborhood) {
            Move neighborMove = neighbor.getMove();
            if ((!tabuList.isTabu(neighborMove) && neighborMove.getMoveGain() > bestGain)||meetsAspirationCriterion(neighbor)) {
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

    // 帮助方法：克隆一个解
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

    // 帮助方法：在解中通过ID查找背包
    private static Knapsack findKnapsackById(Solution solution, int id) {
        for (Knapsack knapsack : solution.getKnapsacks()) {
            if (knapsack.getId() == id) {
                return knapsack;
            }
        }
        return null; // 如果ID总是有效，这种情况不会发生
    }

    // 获取当前解
    public Solution getCurrentSolution() {
        return S;
    }

    // 获取局部最优解
    public Solution getLocalBestFeasibleSolution() {
        return SLocalBest;
    }
}
