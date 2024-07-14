import java.util.List;
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
    private double moveGain; // 移动收益

    // 构造函数，初始化移动的各个属性
    public Move(MoveType moveType, Item item1, Item item2, Knapsack knapsack1, Knapsack knapsack2) {
        this.moveType = moveType;
        this.item1 = item1;
        this.item2 = item2;
        this.knapsack1 = knapsack1;
        this.knapsack2 = knapsack2;
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

    public void setMoveGain(double moveGain) {
        this.moveGain = moveGain;
    }

    public void calculateMoveGain(Solution solution){
        double moveGain;
        ValueContributionMatrix valueContributionMatrix=solution.getValueContributionMatrix();
        ValueContributionMatrix cloneValueContributionMatrix;

        switch (moveType){
            case  EXTRACTION:
                moveGain=-valueContributionMatrix.getValueContribution(item1,knapsack1); // 返回抽取操作的收益，为负贡献值
                break;
            case INSERTION://插入情况比较特殊，只有执行完插入操作后才能得到移动增益
                cloneValueContributionMatrix=solution.getValueContributionMatrix().cloneValueContributionMatrix();
                cloneValueContributionMatrix.updateMatrix(this);//克隆的价值贡献矩阵模拟插入后的情况
                moveGain=cloneValueContributionMatrix.getValueContribution(item1,knapsack1); // 返回插入操作的收益，为正贡献值
                break;
            case REALLOCATION:
                cloneValueContributionMatrix=solution.getValueContributionMatrix().cloneValueContributionMatrix();
                cloneValueContributionMatrix.updateMatrix(this);//克隆的价值贡献矩阵模拟重分配后的情况
                //公式前半部分应为重分配后的矩阵，后半部分为重分配前的矩阵
                moveGain=cloneValueContributionMatrix.getValueContribution(item1,knapsack2) - valueContributionMatrix.getValueContribution(item1,knapsack1); // 返回重新分配操作的收益
                break;
            case EXCHANGE:
                cloneValueContributionMatrix=solution.getValueContributionMatrix().cloneValueContributionMatrix();
                cloneValueContributionMatrix.updateMatrix(this);//克隆的价值贡献矩阵模拟交换后的情况
                if (knapsack2 != null) {
                    // 如果有两个背包，返回交换操作的收益
                    moveGain=cloneValueContributionMatrix.getValueContribution(item1, knapsack2) - valueContributionMatrix.getValueContribution(item1, knapsack1)
                            + cloneValueContributionMatrix.getValueContribution(item2, knapsack1) - valueContributionMatrix.getValueContribution(item2, knapsack2)
                            - 2 * item1.getQuadraticValue(item2);
                }
                else {
                    // 如果只有一个背包，返回交换操作的收益
                    moveGain= cloneValueContributionMatrix.getValueContribution(item2, knapsack1) - valueContributionMatrix.getValueContribution(item1, knapsack1)
                            - item1.getQuadraticValue(item2);
                }
                break;
            default:
                moveGain=Double.NaN;
                break;

        }
        setMoveGain(moveGain);
    }

    // 获取归一化重新分配操作的收益
    public void calNormReallocationGain(Solution solution) {
        double beta=TabuSearchAlgorithm.BETA;
        double moveGain;
        ValueContributionMatrix valueContributionMatrix=solution.getValueContributionMatrix();
        ValueContributionMatrix cloneValueContributionMatrix=solution.getValueContributionMatrix().cloneValueContributionMatrix();
        cloneValueContributionMatrix.updateMatrix(this);//克隆的价值贡献矩阵模拟重分配后的情况
        moveGain= (cloneValueContributionMatrix.getValueContribution(item1, knapsack2) - valueContributionMatrix.getValueContribution(item1, knapsack1))
                / Math.pow(item1.getWeight(), beta); // 返回归一化重新分配操作的收益
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return moveType == move.moveType &&
                Objects.equals(item1 != null ? item1.getId() : null, move.item1 != null ? move.item1.getId() : null) &&
                Objects.equals(item2 != null ? item2.getId() : null, move.item2 != null ? move.item2.getId() : null) &&
                Objects.equals(knapsack1 != null ? knapsack1.getId() : null, move.knapsack1 != null ? move.knapsack1.getId() : null) &&
                Objects.equals(knapsack2 != null ? knapsack2.getId() : null, move.knapsack2 != null ? move.knapsack2.getId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                moveType,
                item1 != null ? item1.getId() : null,
                item2 != null ? item2.getId() : null,
                knapsack1 != null ? knapsack1.getId() : null,
                knapsack2 != null ? knapsack2.getId() : null
        );
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
