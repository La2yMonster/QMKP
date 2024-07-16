import java.util.*;

public class FeasibleLocalSearch {
    private static Solution initialSolution; // 初始解
    private static Solution S; // 当前解
    private static Solution SLocalBest; // 局部最优解

    // 执行可行局部搜索
    public static void performFeasibleLocalSearch(Solution initialSolution, int Ncons) {
        int n = 0; // 计数器
//        ValueContributionMatrix valueContributionMatrix=TabuSearchAlgorithm.getValueContributionMatrix();
        TabuList tabuList=new TabuList();
        S=initialSolution;//当前解
        FeasibleLocalSearch.initialSolution=S.cloneSolution();// 初始解
        SLocalBest = S.cloneSolution(); // 初始局部最优解

        while (n < Ncons) {
            // 构造邻域
            Neighborhood neighborhood = constructNeighborhoods(S);
            // 选择最优邻居
            Move bestNeighborMove = neighborhood.chooseBestNeighborMove(tabuList);
            S.applyMove(bestNeighborMove);//执行最优移动
            tabuList.updateTabuList(bestNeighborMove); // 更新禁忌表

            // 如果当前解优于局部最优解，更新局部最优解并重置计数器
            if (S.getTotalValue() > SLocalBest.getTotalValue()) {
                SLocalBest = S.cloneSolution();
                n = 0;
            } else {
                n++;
            }
        }
    }

    // 构造所有邻域
    public static Neighborhood constructNeighborhoods(Solution solution) {
        Neighborhood neighborhood=new Neighborhood(solution);

        neighborhood.addMoves(Neighborhood.generateExtractionNeighborhoodMoves(solution));// 生成抽取邻域
        neighborhood.addMoves(Neighborhood.generateInsertionNeighborhoodMoves(solution,true));// 生成插入邻域
        neighborhood.addMoves(Neighborhood.generateReallocationNeighborhoodMoves(solution,true));// 生成重分配邻域
        neighborhood.addMoves(Neighborhood.generateExchangeNeighborhoodMoves(solution,true));// 生成交换邻域

        return neighborhood;
    }

    // 获取当前解
    public static Solution getCurrentSolution() {
        return S;
    }

    // 获取局部最优解
    public static Solution getLocalBestSolution() {
        return SLocalBest;
    }
}
