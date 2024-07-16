import java.util.*;

public class Solution {
    private final Set<Knapsack> knapsacks; // 解中的所有背包
    private double totalValue; // 解的总价值
    private double infeasibilityDegree; // 总不可行度（总超载部分）
    private Set<Item> unassignedItems;//解的没有分配物品

    // 构造函数，初始化解
    public Solution(Set<Knapsack> knapsacks) {
        //totalValue,infeasibilityDegree,unassignedItems均采用增量更新
        this.knapsacks = knapsacks;
        this.totalValue=0;
        this.infeasibilityDegree=0;
        this.unassignedItems=new HashSet<>(TabuSearchAlgorithm.getAllItems());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Solution solution = (Solution) obj;

        // 直接比较 knapsacks 集合是否相等
        return Objects.equals(knapsacks, solution.knapsacks);
    }

    // 覆盖hashCode方法
    @Override
    public int hashCode() {
        return Objects.hash(knapsacks);
    }

    // 获取解中的所有背包
    public Set<Knapsack> getKnapsacks() {
        return knapsacks;
    }

    // 获取解的总价值
    public double getTotalValue() {
        return totalValue;
    }

    // 获取解的不可行度（超载部分）
    public double getInfeasibilityDegree() {
        return infeasibilityDegree;
    }

    //    public ValueContributionMatrix getValueContributionMatrix() {
//        return valueContributionMatrix;
//    }

    // 获取未分配的物品列表
    public Set<Item> getUnassignedItems() {
//        for (Knapsack knapsack : knapsacks) {
//            unassignedItems.removeAll(knapsack.getItems());
//        }
        return unassignedItems;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public void setInfeasibilityDegree(double infeasibilityDegree) {
        this.infeasibilityDegree = infeasibilityDegree;
    }

    public void setUnassignedItems(Set<Item> unassignedItems) {
        this.unassignedItems = unassignedItems;
    }

    // 应用操作
    public void applyMove(Move move) {
        switch (move.getMoveType()) {
            case EXTRACTION:
                MoveOperator.extract(this,move); // 执行抽取操作
                break;
            case INSERTION:
                MoveOperator.insert(this,move);
                break;
            case REALLOCATION:
                MoveOperator.reallocate(this,move); // 执行重分配操作
                break;
            case EXCHANGE:
                MoveOperator.exchange(this,move); // 执行交换操作
                break;
        }
    }

    public void updateSolution(double totalValue,double infeasibilityDegree,Set<Item> unassignedItems){
        this.totalValue=totalValue;
        this.infeasibilityDegree=infeasibilityDegree;
        this.unassignedItems=unassignedItems;
    }

    // 克隆解
    public Solution cloneSolution() {
        Set<Knapsack> clonedKnapsacks = new HashSet<>();
        for (Knapsack knapsack : knapsacks) {
            clonedKnapsacks.add(knapsack.cloneKnapsack());
        }

        Solution clonedSolution = new Solution(clonedKnapsacks);
        clonedSolution.updateSolution(totalValue, infeasibilityDegree, new HashSet<>(unassignedItems));
        return clonedSolution;
    }

    // 在解中根据ID查找背包
    public Knapsack findKnapsackById(int id) {
        for (Knapsack knapsack : this.getKnapsacks()) {
            if (knapsack.getId() == id) {
                return knapsack;
            }
        }
        return null;
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
