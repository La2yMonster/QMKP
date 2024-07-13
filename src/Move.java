import java.util.Objects;

public class Move {

    // 移动类型的枚举，包括提取、插入、重分配和交换
    public enum MoveType {
        EXTRACTION, INSERTION, REALLOCATION, EXCHANGE
    }

    private final MoveType moveType; // 移动类型
    private final Item item1; // 第一个物品
    private final Item item2; // 第二个物品（交换操作使用）
    private final Knapsack knapsack1; // 第一个背包
    private final Knapsack knapsack2; // 第二个背包（重分配和交换操作使用）
    private final double moveGain; // 移动收益

    // 构造函数，初始化移动的各个属性
    public Move(MoveType moveType, Item item1, Item item2, Knapsack knapsack1, Knapsack knapsack2, double moveGain) {
        this.moveType = moveType;
        this.item1 = item1;
        this.item2 = item2;
        this.knapsack1 = knapsack1;
        this.knapsack2 = knapsack2;
        this.moveGain = moveGain;
    }

    // 获取移动类型
    public MoveType getMoveType() {
        return moveType;
    }

    // 获取第一个物品
    public Item getItem1() {
        return item1;
    }

    // 获取第二个物品
    public Item getItem2() {
        return item2;
    }

    // 获取第一个背包
    public Knapsack getKnapsack1() {
        return knapsack1;
    }

    // 获取第二个背包
    public Knapsack getKnapsack2() {
        return knapsack2;
    }

    // 获取移动收益
    public double getMoveGain() {
        return moveGain;
    }

    // 重写equals方法，比较两个Move对象是否相等
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return Double.compare(move.moveGain, moveGain) == 0 &&
                moveType == move.moveType &&
                Objects.equals(item1, move.item1) &&
                Objects.equals(item2, move.item2) &&
                Objects.equals(knapsack1, move.knapsack1) &&
                Objects.equals(knapsack2, move.knapsack2);
    }

    // 重写hashCode方法，生成基于移动属性的哈希值
    @Override
    public int hashCode() {
        return Objects.hash(moveType, item1, item2, knapsack1, knapsack2, moveGain);
    }

    // 重写toString方法，返回移动的字符串表示
    @Override
    public String toString() {
        return "Move{" +
                "moveType=" + moveType +
                ", item1=" + item1 +
                ", item2=" + item2 +
                ", knapsack1=" + knapsack1 +
                ", knapsack2=" + knapsack2 +
                ", moveGain=" + moveGain +
                '}';
    }
}
