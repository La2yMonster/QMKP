import java.util.*;

public class Repair {
    private Solution solution; // 当前解
    private ValueContributionMatrix valueContributionMatrix; // 价值贡献矩阵
    private List<Item> unassignedItems; // 未分配物品列表

    // 构造函数，初始化各属性
    public Repair(Solution solution, ValueContributionMatrix valueContributionMatrix) {
        this.solution = solution;
        this.valueContributionMatrix = valueContributionMatrix;
        this.unassignedItems = solution.getUnassignedItems(solution.getAllItems());
    }

    // 修复解的方法
    public Solution repairSolution() {
        while (!solution.isFeasible()) { // 如果解不可行
            Move bestMove = findBestImprovementMove(); // 查找最佳改进操作
            if (bestMove == null) { // 如果没有有效的操作
                throw new RuntimeException("No valid moves to repair the solution."); // 抛出异常
            }
            applyMove(bestMove); // 应用最佳操作
        }
        return solution; // 返回修复后的解
    }

    // 查找最佳改进操作
    private Move findBestImprovementMove() {
        List<Move> possibleMoves = generateAllPossibleMoves(); // 生成所有可能的操作
        Move bestMove = null;
        double bestMoveGain = Double.NEGATIVE_INFINITY;

        for (Move move : possibleMoves) { // 遍历所有操作
            double moveGain = move.getMoveGain(); // 获取操作增益
            if (moveGain > bestMoveGain) { // 如果增益更大
                bestMoveGain = moveGain;
                bestMove = move; // 更新最佳操作
            }
        }

        return bestMove; // 返回最佳操作
    }

    // 生成所有可能的操作
    private List<Move> generateAllPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        List<Knapsack> knapsacks = solution.getKnapsacks();

        // 插入不可能使得不可行解变为可行，因此只需考虑其它三种算子
        // 生成抽取操作
        for (Knapsack knapsack : knapsacks) {
            for (Item item : knapsack.getItems()) {
                double moveGain = valueContributionMatrix.getExtractionGain(item, knapsack);
                moves.add(new Move(Move.MoveType.EXTRACTION, item, null, knapsack, null, moveGain));
            }
        }

        // 生成重分配操作
        for (Knapsack knapsack1 : knapsacks) {
            for (Knapsack knapsack2 : knapsacks) {
                if (!knapsack1.equals(knapsack2)) {
                    for (Item item : knapsack1.getItems()) {
                        double moveGain = valueContributionMatrix.getReallocationGain(item, knapsack1, knapsack2);
                        moves.add(new Move(Move.MoveType.REALLOCATION, item, null, knapsack1, knapsack2, moveGain));
                    }
                }
            }
        }

        // 生成交换操作（情况1和情况2）
        for (int i = 0; i < knapsacks.size(); i++) {
            Knapsack knapsack1 = knapsacks.get(i);
            for (int j = i + 1; j < knapsacks.size(); j++) {
                Knapsack knapsack2 = knapsacks.get(j);
                // 情况1: 交换不同背包中的两个已分配物品
                for (Item item1 : knapsack1.getItems()) {
                    for (Item item2 : knapsack2.getItems()) {
                        if (MoveOperator.canExchange(item1, item2, knapsack1, knapsack2)) {
                            double moveGain = valueContributionMatrix.getExchangeGain(item1, item2, knapsack1, knapsack2);
                            moves.add(new Move(Move.MoveType.EXCHANGE, item1, item2, knapsack1, knapsack2, moveGain));
                        }
                    }
                }
            }
            // 情况2: 交换背包中的一个已分配物品与一个未分配物品
            for (Item item1 : knapsack1.getItems()) {
                for (Item item2 : unassignedItems) {
                    double moveGain = valueContributionMatrix.getExchangeGain(item1, item2, knapsack1, null);
                    moves.add(new Move(Move.MoveType.EXCHANGE, item1, item2, knapsack1, null, moveGain));
                }
            }
        }
        return moves; // 返回所有生成的操作
    }

    // 应用操作
    private void applyMove(Move move) {
        switch (move.getMoveType()) {
            case EXTRACTION:
                MoveOperator.extraction(move.getItem1(), move.getKnapsack1()); // 执行抽取操作
                break;
            case REALLOCATION:
                MoveOperator.reallocation(move.getItem1(), move.getKnapsack1(), move.getKnapsack2()); // 执行重分配操作
                break;
            case EXCHANGE:
                if (move.getKnapsack2() != null) {
                    MoveOperator.exchange(move.getItem1(), move.getItem2(), move.getKnapsack1(), move.getKnapsack2()); // 执行交换操作（两个已分配物品）
                } else {
                    MoveOperator.exchange(move.getItem1(), move.getItem2(), move.getKnapsack1(), null); // 执行交换操作（一个已分配物品与一个未分配物品）
                }
                break;
        }
        valueContributionMatrix.updateMatrix(move); // 更新价值贡献矩阵
    }
}
