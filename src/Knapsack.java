import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.HashSet;

public class Knapsack {
    private final int id; // 背包ID
    private double capacity; // 背包容量
    private double totalWeight;//背包负载
    private List<Item> items; // 背包中的物品列表

    // 构造函数，初始化背包的id和容量
    public Knapsack(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    // 重写equals方法，根据背包id、容量和物品集合判断是否相等
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Knapsack otherKnapsack = (Knapsack) obj;
        return id == otherKnapsack.id;
    }

    // 重写hashCode方法，生成基于背包id
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // 获取背包的id
    public int getId() {
        return id;
    }

    // 获取背包的容量
    public double getCapacity() {
        return capacity;
    }

    // 获取背包中的物品列表
    public List<Item> getItems() {
        return items;
    }

    // 获取背包中的物品总重量
    public double getTotalWeight() {
        totalWeight=items.stream().mapToDouble(Item::getWeight).sum();
        return totalWeight;
    }

    // 获取背包中的物品总价值，包括二次价值
    public double getTotalValue() {
        double totalValue = items.stream().mapToDouble(Item::getValue).sum();
        double quadraticValue = 0.0;
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                quadraticValue += items.get(i).getQuadraticValue(items.get(j));
            }
        }
        return totalValue + quadraticValue;
    }
}
