import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InitialSolutionGenerator {
    private List<Item> items;  // 所有物品的列表
    private List<Knapsack> knapsacks;  // 所有背包的列表
    private List<Item> unassignedItems;  // 未分配的物品列表
    private ValueContributionMatrix valueContributionMatrix;  // 值贡献矩阵，用于计算物品在不同背包中的价值密度

    // 构造函数，初始化物品和背包列表，并将未分配的物品列表初始化为所有物品
    public InitialSolutionGenerator(List<Item> items, List<Knapsack> knapsacks,ValueContributionMatrix valueContributionMatrix) {
        this.items = items;
        this.knapsacks = knapsacks;
        this.unassignedItems = new ArrayList<>(items);
        this.valueContributionMatrix=valueContributionMatrix;
    }

    // 生成初始解
    public Solution generateInitialSolution() {
        Random random = new Random();

        // 遍历每一个背包
        for (Knapsack knapsack : knapsacks) {
            // 随机选择一个未分配的物品放入当前背包
            if (!unassignedItems.isEmpty()) {
                Item randomItem = unassignedItems.remove(random.nextInt(unassignedItems.size()));
                knapsack.getItems().add(randomItem);
            }

            boolean canAddMoreObjects = true;
            while (canAddMoreObjects) {
                Item bestItem = null;  // 最佳物品
                double bestDensity = Double.NEGATIVE_INFINITY;  // 最佳物品的价值密度

                // 遍历未分配的物品列表，寻找最适合当前背包的物品
                for (Item item : unassignedItems) {
                    // 判断物品是否可以放入当前背包（不超重）
                    if (knapsack.getTotalWeight() + item.getWeight() <= knapsack.getCapacity()) {
                        // 计算物品在当前背包中的价值密度
                        double density = valueContributionMatrix.getValueDensity(item, knapsack);
                        // 如果当前物品的价值密度更高，则更新最佳物品和价值密度
                        if (density > bestDensity) {
                            bestDensity = density;
                            bestItem = item;
                        }
                    }
                }

                // 如果找到最佳物品，则将其加入当前背包，并从未分配物品列表中移除
                if (bestItem != null) {
                    knapsack.getItems().add(bestItem);
                    unassignedItems.remove(bestItem);
                } else {
                    canAddMoreObjects = false;  // 如果没有找到合适的物品，则停止当前背包的填充
                }
            }
        }

        return new Solution(knapsacks);  // 返回生成的初始解
    }
}
