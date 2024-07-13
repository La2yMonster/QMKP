import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Item {
    private int id; // 唯一标识符
    private double weight; // 重量
    private double value; // 价值
    private Map<Integer, Double> quadraticValues; // 物品对的二次价值

    // 构造函数，初始化物品的id、重量和价值
    public Item(int id, double weight, double value) {
        this.id = id;
        this.weight = weight;
        this.value = value;
        this.quadraticValues = new HashMap<>();
    }

    // 重写equals方法，根据物品id判断是否相等
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Item item = (Item) obj;
        return id == item.getId();
    }

    // 重写hashCode方法，生成基于物品id、重量、价值和二次价值的哈希值
    @Override
    public int hashCode() {
        return Objects.hash(id, weight, value, quadraticValues);
    }

    // 获取物品的id
    public int getId() {
        return id;
    }

    // 获取物品的重量
    public double getWeight() {
        return weight;
    }

    // 获取物品的价值
    public double getValue() {
        return value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setValue(double value) {
        this.value = value;
    }

    // 设置与另一个物品的二次价值
    public void setQuadraticValue(Item otherItem, double value) {
        this.quadraticValues.put(otherItem.getId(), value);
    }

    // 获取与另一个物品的二次价值，如果没有设置，返回0.0
    public double getQuadraticValue(Item otherItem) {
        return this.quadraticValues.getOrDefault(otherItem.getId(), 0.0);
    }

}
