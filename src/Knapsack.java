import java.util.*;

public class Knapsack {
    private final int id; // 背包ID
    private final double capacity; // 背包容量
    private double totalWeight;//背包总重量
    private double totalValue;//背包物品总价值（含二次价值）
    private Set<Item> items; // 背包中的物品集合

    // 构造函数，初始化背包的id和容量
    public Knapsack(int id, double capacity) {
        this.id = id;
        this.capacity = capacity;
        this.totalWeight=0;
        this.totalValue=0;
        this.items = new HashSet<>();
    }

    // 重写equals方法，根据背包id、容量和物品集合判断是否相等
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Knapsack otherKnapsack = (Knapsack) obj;
        return id == otherKnapsack.id &&
                Double.compare(otherKnapsack.capacity, capacity) == 0 &&
                items.equals(otherKnapsack.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, capacity, items);
    }

    // 获取背包的id
    public int getId() {
        return id;
    }

    // 获取背包的容量
    public double getCapacity() {
        return capacity;
    }

    // 获取背包中的物品总重量
    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(double totalWeight) {
        this.totalWeight = totalWeight;
    }

    // 获取背包中的物品总价值，包括二次价值
    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    // 获取背包中的物品列表
    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public void updateKnapsack(double totalWeight,double totalValue,Set<Item> items){
        this.totalWeight=totalWeight;
        this.totalValue=totalValue;
        this.items=items;
    }

    public Knapsack cloneKnapsack(){
        Knapsack clonedKnapsack=new Knapsack(id,capacity);
        clonedKnapsack.updateKnapsack(totalWeight,totalValue,new HashSet<>(items));
        return clonedKnapsack;
    }

}
