import java.util.*;

public class Repair {
    // 修复解的方法
    public static void repairSolution(Solution solution) {
        while (!solution.isFeasible()) { // 如果解不可行
            Neighborhood neighborhood=constructRepairNeighborhood(solution);//生成修复邻域
            Move bestMove = findBestImprovementMove(neighborhood,solution); // 查找最佳改进操作
            if (bestMove != null) { // 如果没有有效的操作
                solution.applyMove(bestMove); // 应用最佳操作
            }
//            else throw new RuntimeException("No valid moves to repair the solution."); // 抛出异常
        }
    }

    // 查找最佳改进操作
    public static Move findBestImprovementMove(Neighborhood neighborhood, Solution solution) {
        Move bestMove = null;
        double bestMoveGain = Double.NEGATIVE_INFINITY;
        double currentInfeasibilityDegree = solution.getInfeasibilityDegree();

        for (Move move : neighborhood.getMoves()) { // 遍历所有操作
            double moveGain = move.getMoveGain(); // 获取操作增益
            double newInfeasibilityDegree=calNewInfeasibilityDegree(solution,move);
//            Solution newSolution = solution.cloneSolution();
//            Knapsack clonedKnapsack1=newSolution.findKnapsackById(move.getKnapsack1().getId());
//            Knapsack clonedKnapsack2=null;
//
//            if (move.getKnapsack2()!=null){
//                clonedKnapsack2=newSolution.findKnapsackById(move.getKnapsack2().getId());
//            }
//
//            Move clonedMove=new Move(move.getMoveType(),move.getItem1(),move.getItem2(),clonedKnapsack1,clonedKnapsack2);
//            newSolution.applyMove(clonedMove); // 应用操作到新解
//            double newInfeasibilityDegree = newSolution.getInfeasibilityDegree(); // 获取新解的不可行度

            // 检查是否改进了不可行度
            if (newInfeasibilityDegree < currentInfeasibilityDegree && moveGain > bestMoveGain) {
                bestMoveGain = moveGain; // 更新最优增益
                bestMove = move; // 更新最佳操作
            }
        }

        return bestMove; // 返回最佳操作
    }

    //此时move没有被执行
    public static double calNewInfeasibilityDegree(Solution solution, Move move) {
        double solutionInfeasibilityDegree = solution.getInfeasibilityDegree();
        double oldOverload1, newOverload1, oldOverload2, newOverload2;
        Item item1 = move.getItem1();
        Knapsack knapsack1 = move.getKnapsack1();
        Item item2 = move.getItem2();
        Knapsack knapsack2 = move.getKnapsack2();

        switch (move.getMoveType()) {
            case EXTRACTION:
                oldOverload1 = Math.max(0, knapsack1.getTotalWeight() - knapsack1.getCapacity());
                newOverload1 = Math.max(0, knapsack1.getTotalWeight() - item1.getWeight() - knapsack1.getCapacity());
                solutionInfeasibilityDegree = solutionInfeasibilityDegree - oldOverload1 + newOverload1;
                break;

            case INSERTION:
                oldOverload1 = Math.max(0, knapsack1.getTotalWeight() - knapsack1.getCapacity());
                newOverload1 = Math.max(0, knapsack1.getTotalWeight() + item1.getWeight() - knapsack1.getCapacity());
                solutionInfeasibilityDegree = solutionInfeasibilityDegree - oldOverload1 + newOverload1;
                break;

            case REALLOCATION:
                oldOverload1 = Math.max(0, knapsack1.getTotalWeight() - knapsack1.getCapacity());
                newOverload1 = Math.max(0, knapsack1.getTotalWeight() - item1.getWeight() - knapsack1.getCapacity());
                oldOverload2 = Math.max(0, knapsack2.getTotalWeight() - knapsack2.getCapacity());
                newOverload2 = Math.max(0, knapsack2.getTotalWeight() + item1.getWeight() - knapsack2.getCapacity());
                solutionInfeasibilityDegree = solutionInfeasibilityDegree - oldOverload1 + newOverload1 - oldOverload2 + newOverload2;
                break;

            case EXCHANGE:
                oldOverload1 = Math.max(0, knapsack1.getTotalWeight() - knapsack1.getCapacity());
                newOverload1 = Math.max(0, knapsack1.getTotalWeight() - item1.getWeight() + item2.getWeight() - knapsack1.getCapacity());
                if (knapsack2 != null) {
                    oldOverload2 = Math.max(0, knapsack2.getTotalWeight() - knapsack2.getCapacity());
                    newOverload2 = Math.max(0, knapsack2.getTotalWeight() - item2.getWeight() + item1.getWeight() - knapsack2.getCapacity());
                    solutionInfeasibilityDegree = solutionInfeasibilityDegree - oldOverload1 + newOverload1 - oldOverload2 + newOverload2;
                } else {
                    solutionInfeasibilityDegree = solutionInfeasibilityDegree - oldOverload1 + newOverload1;
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown move type: " + move.getMoveType());
        }

        return solutionInfeasibilityDegree;
    }


    public static Neighborhood constructRepairNeighborhood(Solution solution){
        Neighborhood neighborhood=new Neighborhood(solution);

        // 插入不可能使得不可行解变为可行，因此只需考虑其它三种算子
        neighborhood.addMoves(Neighborhood.generateExtractionNeighborhoodMoves(solution));//生成抽取邻域
        neighborhood.addMoves(Neighborhood.generateReallocationNeighborhoodMoves(solution,false));//生成重分配邻域
        neighborhood.addMoves(Neighborhood.generateExchangeNeighborhoodMoves(solution,false));//生成交换邻域

        return neighborhood;
    }
}
