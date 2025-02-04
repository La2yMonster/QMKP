import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Neighborhood {
    private Solution solution; // 当前解
    private Set<Move> moves; // 产生邻居解的移动操作

    public Neighborhood(Solution solution) {
        this.solution = solution;
        this.moves=new HashSet<>();
    }

    public Solution getSolution() {
        return solution;
    }

    public Set<Move> getMoves() {
        return moves;
    }

    public void setMoves(Set<Move> moves) {
        this.moves = moves;
    }

    public void addMoves(Set<Move> moves) {
        this.moves.addAll(moves);
    }

    // 生成抽取邻域
    public static Set<Move> generateExtractionNeighborhoodMoves(Solution solution) {
        Set<Move> moves=new HashSet<>();
        Set<Knapsack> knapsacks=solution.getKnapsacks();
        for (Knapsack knapsack : knapsacks) {
            for (Item item : knapsack.getItems()) {
                Move move = new Move(Move.MoveType.EXTRACTION, item, null, knapsack, null);
                move.calculateMoveGain(solution);
                moves.add(move);
            }
        }
        return moves;
    }

    // 生成插入邻域
    public static Set<Move> generateInsertionNeighborhoodMoves(Solution solution,boolean considerConstraint) {
        Set<Move> moves=new HashSet<>();
        Set<Knapsack> knapsacks=solution.getKnapsacks();

        for (Knapsack knapsack : knapsacks) {
            for (Item item : solution.getUnassignedItems()) {
                if (considerConstraint ?MoveOperator.canInsert(item, knapsack):true) {
                    Move move = new Move(Move.MoveType.INSERTION, item, null, knapsack, null);
                    move.calculateMoveGain(solution);
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    // 生成重分配邻域
    public static Set<Move> generateReallocationNeighborhoodMoves(Solution solution,boolean considerConstraint) {
        Set<Move> moves=new HashSet<>();
        Set<Knapsack> knapsacks=solution.getKnapsacks();

        for (Knapsack fromKnapsack : knapsacks) {
            for (Item item : fromKnapsack.getItems()) {
                for (Knapsack toKnapsack : knapsacks) {
                    if (fromKnapsack != toKnapsack && (considerConstraint? MoveOperator.canReallocate(item, fromKnapsack, toKnapsack):true)) {
                        Move move = new Move(Move.MoveType.REALLOCATION, item, null, fromKnapsack, toKnapsack);
                        move.calculateMoveGain(solution);
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    // 生成交换邻域
    public static Set<Move> generateExchangeNeighborhoodMoves(Solution solution,boolean considerConstraint) {
        Set<Move> moves=new HashSet<>();
        List<Knapsack> knapsacks=new ArrayList<>(solution.getKnapsacks());

        for (int i = 0; i < knapsacks.size(); i++) {
            Knapsack knapsack1 = knapsacks.get(i);
            for (int j = i + 1; j < knapsacks.size(); j++) {
                Knapsack knapsack2 = knapsacks.get(j);
                // case 1: 交换不同背包中的两个已分配物品
                for (Item item1 : knapsack1.getItems()) {
                    for (Item item2 : knapsack2.getItems()) {
                        if (considerConstraint? MoveOperator.canExchange(item1, item2, knapsack1, knapsack2):true) {
                            Move move = new Move(Move.MoveType.EXCHANGE, item1, item2, knapsack1, knapsack2);
                            move.calculateMoveGain(solution);
                            moves.add(move);
                        }
                    }
                }
            }
            // case 2: 交换背包中的一个已分配物品与一个未分配物品
            for (Item item1 : knapsack1.getItems()) {
                for (Item unassignedItem : solution.getUnassignedItems()) {
                    if (considerConstraint?MoveOperator.canExchange(item1, unassignedItem, knapsack1, null):true) {
                        Move move = new Move(Move.MoveType.EXCHANGE, item1, unassignedItem, knapsack1, null);
                        move.calculateMoveGain(solution);
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

    // 选择最优邻居
    public Move chooseBestNeighborMove(TabuList tabuList) {
        Move bestNeighborMove = null;
        double bestGain = Double.NEGATIVE_INFINITY;

        for (Move neighborMove : this.getMoves()) {
            if ((!tabuList.isTabu(neighborMove) && neighborMove.getMoveGain() > bestGain)|| meetsAspirationCriterion(neighborMove)) {
                bestGain = neighborMove.getMoveGain();
                bestNeighborMove=neighborMove;
            }
        }

        return bestNeighborMove;
    }

    // 检查是否满足aspiration criterion
    public boolean meetsAspirationCriterion(Move move) {
        Solution SBest = TabuSearchAlgorithm.getSBest();
        Solution FLSLocalBest = FeasibleLocalSearch.getLocalBestSolution();
        Solution ILSLocalBest = InfeasibleLocalSearch.getLocalBestFeasibleSolution();

        // 考虑到一个阶段要迭代很多次，每次SBest都是在阶段结束后才更新。因此迄今为止的最优解为SBest与两个阶段的局部最优解中更加优秀的解
        Solution globalBest = SBest;

        if (FLSLocalBest != null && (globalBest == null || FLSLocalBest.getTotalValue() > globalBest.getTotalValue())) {
            globalBest = FLSLocalBest;
        }

        if (ILSLocalBest != null && (globalBest == null || ILSLocalBest.getTotalValue() > globalBest.getTotalValue())) {
            globalBest = ILSLocalBest;
        }

        // 返回当前解加上移动增益后的总值是否大于迄今为止的最优解的总值
        return this.getSolution().getTotalValue() + move.getMoveGain() > (globalBest != null ? globalBest.getTotalValue() : Double.NEGATIVE_INFINITY);
    }

}
