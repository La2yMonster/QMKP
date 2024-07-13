import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueContributionMatrix {
    private Map<Item, Map<Knapsack, Double>> matrix; // 价值贡献矩阵，由物品和对应背包的贡献值构成

    public ValueContributionMatrix(List<Item> items, List<Knapsack> knapsacks) {
        matrix = new HashMap<>(); // 初始化矩阵
        initializeMatrix(items, knapsacks); // 调用初始化方法
    }

    // 初始化矩阵
    private void initializeMatrix(List<Item> items, List<Knapsack> knapsacks) {
        for (Item item : items) {
            Map<Knapsack, Double> valueContribution = new HashMap<>();
            for (Knapsack knapsack : knapsacks) {
                valueContribution.put(knapsack, 0.0); // 初始贡献值设为0
            }
            matrix.put(item, valueContribution); // 将物品和其对应的贡献值映射存入矩阵
        }
    }

    // 更新矩阵
    public void updateMatrix(Move move) {
        Item item1 = move.getItem1();
        Item item2 = move.getItem2();
        Knapsack knapsack1 = move.getKnapsack1();
        Knapsack knapsack2 = move.getKnapsack2();

        switch (move.getMoveType()) {
            case EXTRACTION:
                // 如果是抽取操作，从knapsack1中移出item1
                for (Item j : knapsack1.getItems()) {
                    if (!j.equals(item1)) {
                        matrix.get(j).put(knapsack1, matrix.get(j).get(knapsack1) - item1.getQuadraticValue(j));
                    }
                }
                matrix.get(item1).put(knapsack1, 0.0); // 更新item1在knapsack1中的贡献值为0
                break;
            case INSERTION:
                // 如果是插入操作，将item1插入knapsack1
                for (Item j : knapsack1.getItems()) {
                    if (!j.equals(item1)) {
                        matrix.get(j).put(knapsack1, matrix.get(j).get(knapsack1) + item1.getQuadraticValue(j));
                    }
                }
                matrix.get(item1).put(knapsack1, computeValueContribution(item1, knapsack1)); // 计算并更新item1在knapsack1中的贡献值
                break;
            case REALLOCATION:
                // 如果是重新分配操作，从knapsack1中移出item1
                for (Item j : knapsack1.getItems()) {
                    if (!j.equals(item1)) {
                        matrix.get(j).put(knapsack1, matrix.get(j).get(knapsack1) - item1.getQuadraticValue(j));
                    }
                }
                matrix.get(item1).put(knapsack1, 0.0); // 更新item1在knapsack1中的贡献值为0

                // 将item1移入knapsack2
                for (Item j : knapsack2.getItems()) {
                    if (!j.equals(item1)) {
                        matrix.get(j).put(knapsack2, matrix.get(j).get(knapsack2) + item1.getQuadraticValue(j));
                    }
                }
                matrix.get(item1).put(knapsack2, computeValueContribution(item1, knapsack2)); // 计算并更新item1在knapsack2中的贡献值
                break;
            case EXCHANGE:
                if (knapsack2 != null) {
                    // 如果是交换操作，从knapsack1中移出item1，将item2移入knapsack1
                    for (Item j : knapsack1.getItems()) {
                        if (!j.equals(item1)) {
                            matrix.get(j).put(knapsack1, matrix.get(j).get(knapsack1) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
                        }
                    }
                    matrix.get(item1).put(knapsack1, 0.0); // 更新item1在knapsack1中的贡献值为0
                    matrix.get(item2).put(knapsack1, computeValueContribution(item2, knapsack1)); // 计算并更新item2在knapsack1中的贡献值

                    // 从knapsack2中移出item2，将item1移入knapsack2
                    for (Item j : knapsack2.getItems()) {
                        if (!j.equals(item2)) {
                            matrix.get(j).put(knapsack2, matrix.get(j).get(knapsack2) - item2.getQuadraticValue(j) + item1.getQuadraticValue(j));
                        }
                    }
                    matrix.get(item2).put(knapsack2, 0.0); // 更新item2在knapsack2中的贡献值为0
                    matrix.get(item1).put(knapsack2, computeValueContribution(item1, knapsack2)); // 计算并更新item1在knapsack2中的贡献值
                } else {
                    // 如果只有一个knapsack2，从knapsack1中移出item1，将item2移入knapsack1
                    for (Item j : knapsack1.getItems()) {
                        if (!j.equals(item1)) {
                            matrix.get(j).put(knapsack1, matrix.get(j).get(knapsack1) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
                        }
                    }
                    matrix.get(item1).put(knapsack1, 0.0); // 更新item1在knapsack1中的贡献值为0
                    matrix.get(item2).put(knapsack1, computeValueContribution(item2, knapsack1)); // 计算并更新item2在knapsack1中的贡献值
                }
                break;
        }
    }

    // 计算给定物品和背包的贡献值
    private double computeValueContribution(Item item, Knapsack knapsack) {
        double profitValue = item.getValue(); // 物品的利润值
        double jointValues = 0.0; // 物品与其他物品的联合价值

        for (Item other : knapsack.getItems()) {
            if (item != other) {
                jointValues += item.getQuadraticValue(other); // 计算物品与其他物品的联合价值
            }
        }

        return profitValue + jointValues; // 返回总贡献值
    }

    // 获取给定物品和背包的贡献值
    public double getValueContribution(Item item, Knapsack knapsack) {
        if (matrix.containsKey(item) && matrix.get(item).containsKey(knapsack)) {
            return matrix.get(item).get(knapsack); // 返回物品在背包中的贡献值
        }
        return 0.0; // 如果物品不在背包中，返回0
    }

    // 计算物品对背包的价值密度
    public double getValueDensity(Item item, Knapsack knapsack) {
        return getValueContribution(item, knapsack) / item.getWeight(); // 返回物品对背包的价值密度
    }

    // 获取抽取操作的收益
    public double getExtractionGain(Item item, Knapsack knapsack) {
        return -this.getValueContribution(item, knapsack); // 返回抽取操作的收益，为负贡献值
    }

    // 获取插入操作的收益
    public double getInsertionGain(Item item, Knapsack knapsack) {
        return this.getValueContribution(item, knapsack); // 返回插入操作的收益，为正贡献值
    }

    // 获取重新分配操作的收益
    public double getReallocationGain(Item item, Knapsack fromKnapsack, Knapsack toKnapsack) {
        return this.getValueContribution(item, toKnapsack) - this.getValueContribution(item, fromKnapsack); // 返回重新分配操作的收益
    }

    // 获取归一化重新分配操作的收益
    public double getNormReallocationGain(double beta, Item item, Knapsack fromKnapsack, Knapsack toKnapsack) {
        return (this.getValueContribution(item, toKnapsack) - this.getValueContribution(item, fromKnapsack))
                / Math.pow(item.getWeight(), beta); // 返回归一化重新分配操作的收益
    }

    // 获取交换操作的收益
    public double getExchangeGain(Item item1, Item item2, Knapsack knapsack1, Knapsack knapsack2) {
        if (knapsack2 != null) {
            // 如果有两个背包，返回交换操作的收益
            return this.getValueContribution(item1, knapsack2) - this.getValueContribution(item1, knapsack1)
                    + this.getValueContribution(item2, knapsack1) - this.getValueContribution(item2, knapsack2)
                    - 2 * item1.getQuadraticValue(item2);
        }
        // 如果只有一个背包，返回交换操作的收益
        return this.getValueContribution(item2, knapsack1) - this.getValueContribution(item1, knapsack1)
                - item1.getQuadraticValue(item2);
    }

}
