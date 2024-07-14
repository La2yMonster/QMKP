import java.util.*;

public class Repair {
    // 修复解的方法
    public static void repairSolution(Solution solution) {
        while (!solution.isFeasible()) { // 如果解不可行
            Neighborhood neighborhood=constructRepairNeighborhood(solution);//生成修复邻域
            Move bestMove = findBestImprovementMove(neighborhood); // 查找最佳改进操作
            if (bestMove == null) { // 如果没有有效的操作
                throw new RuntimeException("No valid moves to repair the solution."); // 抛出异常
            }
            solution.applyMove(bestMove); // 应用最佳操作
        }
    }

    // 查找最佳改进操作
    public static Move findBestImprovementMove(Neighborhood neighborhood) {
        Move bestMove = null;
        double bestMoveGain = Double.NEGATIVE_INFINITY;

        for (Move move : neighborhood.getMoves()) { // 遍历所有操作
            double moveGain = move.getMoveGain(); // 获取操作增益
            if (moveGain > bestMoveGain) { // 如果增益更大
                bestMoveGain = moveGain;//更新最优增益
                bestMove = move; // 更新最佳操作
            }
        }

        return bestMove; // 返回最佳操作
    }
    public static Neighborhood constructRepairNeighborhood(Solution solution){
        Neighborhood neighborhood=new Neighborhood(solution);

        // 插入不可能使得不可行解变为可行，因此只需考虑其它三种算子
        neighborhood.addMoves(Neighborhood.generateExtractionNeighborhoodMoves(solution));//生成抽取邻域
        neighborhood.addMoves(Neighborhood.generateReallocationNeighborhoodMoves(solution));//生成重分配邻域
        neighborhood.addMoves(Neighborhood.generateExchangeNeighborhoodMoves(solution));//生成交换邻域

        return neighborhood;
    }
}
