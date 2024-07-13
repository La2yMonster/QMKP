public class Neighbor {
    private Solution solution; // 邻居解
    private Move move; // 产生当前邻居解的移动操作

    public Neighbor(Solution solution, Move move) {
        this.solution = solution;
        this.move = move;
    }

    // 获取邻居解
    public Solution getSolution() {
        return solution;
    }

    // 获取产生当前邻居解的移动操作
    public Move getMove() {
        return move;
    }
}
