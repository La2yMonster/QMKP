import java.util.*;

public class InfeasibleLocalSearch {
    private static Solution initialSolution; // 初始解
    private static Solution S; // 当前解
    private static Solution SLocalBest; // 局部最优解
    private static TabuList tabuList; // 禁忌表

    // 执行不可行局部搜索
    public static void performInfeasibleLocalSearch(Solution feasibleSolution, int M) {
        int m = 0;
        ValueContributionMatrix valueContributionMatrix=TabuSearchAlgorithm.getValueContributionMatrix();
        tabuList=new TabuList();
        S = feasibleSolution;//当前解
        InfeasibleLocalSearch.initialSolution=S.cloneSolution();
        SLocalBest = S.cloneSolution();//初始局部最优解

        while (m < M) {

            if (S.isFeasible() && S.getTotalValue() <= SLocalBest.getTotalValue()) { // 如果当前解可行且不为局部最优
                // 应用第一类操作符
                Neighborhood neighborhood = constructFirstTypeNeighborhoods(S);
                Move bestNeighborMove = neighborhood.chooseBestNeighborMove(tabuList);
                S.applyMove(bestNeighborMove);//执行最优移动
                tabuList.updateTabuList(bestNeighborMove); // 更新禁忌表

                // 更新局部最优解
                if (S.getTotalValue() > SLocalBest.getTotalValue()) {
                    SLocalBest = S.cloneSolution();
                }
            } else {
                // 应用第二类操作符
                Random rand = new Random();
                Set<Knapsack> knapsacks = S.getKnapsacks();
                Knapsack selectedKnapsack = (new ArrayList<>(knapsacks)).get(rand.nextInt(knapsacks.size()));
                Move bestMove;

                if (selectedKnapsack.getTotalWeight() < selectedKnapsack.getCapacity()) {
                    // 找到移动物品到选中的背包的最优操作
                    bestMove = moveItemToSelectedKnapsack(selectedKnapsack,S);
                } else {
                    // 找到从选中的背包移走物品的最优操作
                    bestMove = moveItemFromSelectedKnapsack(selectedKnapsack,S);
                }
                if (bestMove != null) {
                    S.applyMove(bestMove);//执行最优移动并更新当前解
                    tabuList.updateTabuList(bestMove); // 更新禁忌表
                    //更新局部最优解
                    if (S.isFeasible() && S.getTotalValue() > SLocalBest.getTotalValue()) {
                        SLocalBest = S.cloneSolution();
                    }
                }
//                else throw new IllegalStateException("Move is null");
            }

            m++;
        }
    }

    // 构造第一类邻域
    public static Neighborhood constructFirstTypeNeighborhoods(Solution solution) {
        Neighborhood neighborhood = new Neighborhood(solution);

        // 插入、重分配和交换邻域构造
        neighborhood.addMoves(Neighborhood.generateInsertionNeighborhoodMoves(solution));
        neighborhood.addMoves(Neighborhood.generateReallocationNeighborhoodMoves(solution));
        neighborhood.addMoves(Neighborhood.generateExchangeNeighborhoodMoves(solution));

        return neighborhood;
    }

    // 将物品移到选中的背包
    public static Move moveItemToSelectedKnapsack(Knapsack selectedKnapsack,Solution solution) {
        double bestGain = Double.NEGATIVE_INFINITY;
        Move bestMove=null;

        for (Knapsack knapsack : S.getKnapsacks()) {
            if (knapsack != selectedKnapsack && knapsack.getTotalWeight() >= selectedKnapsack.getTotalWeight()) {
                for (Item item : knapsack.getItems()) {
                    Move move=new Move(Move.MoveType.REALLOCATION,item,null,knapsack,selectedKnapsack);
                    move.calNormReallocationGain(solution);
                    double gain =move.getMoveGain();
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestMove=move;
                    }
                }
            }
        }

        return bestMove;
    }

    // 将物品从选中的背包移走
    public static Move moveItemFromSelectedKnapsack(Knapsack selectedKnapsack,Solution solution) {
        double bestGain = Double.NEGATIVE_INFINITY;
        Move bestMove=null;

        for (Item item : selectedKnapsack.getItems()) {
            for (Knapsack knapsack : S.getKnapsacks()) {
                if (knapsack != selectedKnapsack && knapsack.getTotalWeight() < selectedKnapsack.getTotalWeight()) {
                    Move move=new Move(Move.MoveType.REALLOCATION,item,null,selectedKnapsack,knapsack);
                    move.calNormReallocationGain(solution);
                    double gain = move.getMoveGain();
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestMove=move;
                    }
                }
            }
        }

        return bestMove;
    }

    // 获取当前解
    public static Solution getCurrentSolution() {
        return S;
    }

    // 获取局部最优解
    public static Solution getLocalBestFeasibleSolution() {
        return SLocalBest;
    }
}
