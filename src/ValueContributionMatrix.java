import java.util.*;

public class ValueContributionMatrix {
    private Map<Integer, Map<Integer, Double>> matrix; // 价值贡献矩阵，由物品ID和对应背包ID的贡献值构成

    public ValueContributionMatrix(Set<Item> items, Set<Knapsack> knapsacks) {
        matrix = new HashMap<>(); // 初始化矩阵
        initializeMatrix(items, knapsacks); // 调用初始化方法
    }

    public Map<Integer, Map<Integer, Double>> getMatrix() {
        return matrix;
    }
    // 初始化矩阵
    private void initializeMatrix(Set<Item> items, Set<Knapsack> knapsacks) {
        for (Item item : items) {
            Map<Integer, Double> valueContribution = new HashMap<>();
            for (Knapsack knapsack : knapsacks) {
                valueContribution.put(knapsack.getId(), 0.0); // 初始贡献值设为0
            }
            matrix.put(item.getId(), valueContribution); // 将物品ID和其对应的贡献值映射存入矩阵
        }
    }

    // 计算给定物品和背包的贡献值
    public static double computeValueContribution(Item item, Knapsack knapsack) {
        double profitValue = item.getValue(); // 物品的利润值
        double jointValues = 0.0; // 物品与其他物品的联合价值

        for (Item other : knapsack.getItems()) {
            if (!item.equals(other)) {
                jointValues += item.getQuadraticValue(other); // 计算物品与其他物品的联合价值
            }
        }

        return profitValue + jointValues; // 返回总贡献值
    }

    // 获取给定物品和背包的贡献值
    public double getValueContribution(Item item, Knapsack knapsack) {
        int itemId = item.getId();
        int knapsackId = knapsack.getId();
        Map<Integer, Map<Integer, Double>> matrix = this.getMatrix();

        return matrix.get(itemId).get(knapsackId); // 返回物品在背包中的贡献值
    }

}
