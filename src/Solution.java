import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Solution {
    private final List<Knapsack> knapsacks; // 解中的所有背包
    private double totalValue; // 解的总价值
    private double infeasibilityDegree; // 不可行度（DI）

    // 构造函数，初始化解
    public Solution(List<Knapsack> knapsacks) {
        this.knapsacks = knapsacks;
        calculateTotalValue(); // 计算总价值
        calculateInfeasibilityDegree(); // 计算不可行度
    }

    // 覆盖equals方法，用于解的比较
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Solution solution = (Solution) obj;

        // 比较背包及其物品是否相同（根据背包ID）
        if (knapsacks.size() != solution.knapsacks.size()) return false;

        for (Knapsack thisKnapsack : knapsacks) {
            boolean matchFound = false;
            for (Knapsack otherKnapsack : solution.knapsacks) {
                if (thisKnapsack.equals(otherKnapsack)) {
                    matchFound = true;
                    break;
                }
            }
            if (!matchFound) return false;
        }

        return true;
    }

    // 覆盖hashCode方法
    @Override
    public int hashCode() {
        return Objects.hash(knapsacks);
    }

    // 获取解中的所有背包
    public List<Knapsack> getKnapsacks() {
        return knapsacks;
    }

    // 获取解中的所有物品
    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        for (Knapsack knapsack : knapsacks) {
            allItems.addAll(knapsack.getItems());
        }
        return allItems;
    }

    // 获取解的总价值
    public double getTotalValue() {
        return totalValue;
    }

    // 获取解的不可行度（超载部分）
    public double getInfeasibilityDegree() {
        return infeasibilityDegree;
    }

    // 计算解的总价值
    public void calculateTotalValue() {
        totalValue = knapsacks.stream().mapToDouble(Knapsack::getTotalValue).sum();
    }

    // 获取未分配的物品列表
    public List<Item> getUnassignedItems(List<Item> allItems) {
        List<Item> unassignedItems = new ArrayList<>(allItems);
        for (Knapsack knapsack : knapsacks) {
            unassignedItems.removeAll(knapsack.getItems());
        }
        return unassignedItems;
    }

    // 计算解的不可行度（总超载部分）
    public void calculateInfeasibilityDegree() {
        infeasibilityDegree = knapsacks.stream()
                .mapToDouble(k -> Math.max(0, k.getTotalWeight() - k.getCapacity()))
                .sum();
    }

    // 验证解是否可行
    public boolean isFeasible() {
        return infeasibilityDegree == 0;
    }

    // 覆盖toString方法，返回解的字符串表示
    @Override
    public String toString() {
        return "Solution{" +
                "knapsacks=" + knapsacks +
                ", totalValue=" + totalValue +
                ", infeasibilityDegree=" + infeasibilityDegree +
                ", feasible=" + isFeasible() +
                '}';
    }

    // 显示解的详细信息
    public void displaySolution() {
        System.out.println("Solution Details:");
        for (Knapsack knapsack : knapsacks) {
            System.out.println("Knapsack ID: " + knapsack.getId());
            System.out.println("Knapsack Capacity: " + knapsack.getCapacity());
            System.out.println("Knapsack Total Weight: " + knapsack.getTotalWeight());
            System.out.println("Knapsack Total Value: " + knapsack.getTotalValue());
            System.out.println("Objects in Knapsack: " + knapsack.getItems());
            System.out.println();
        }
        System.out.println("Total Value: " + totalValue);
        System.out.println("Infeasibility Degree: " + infeasibilityDegree);
        System.out.println("Feasible: " + isFeasible());
    }
}
