import java.util.List;

public class MoveOperator {

    // 抽取操作：将物品从背包中移除
    public static void extract(Solution solution, Move move) {
        Item item=move.getItem1();
        Knapsack knapsack=move.getKnapsack1();
        List<Item> unassignedItems=solution.getUnassignedItems();

        knapsack.getItems().remove(item);
        unassignedItems.add(item);
        solution.getValueContributionMatrix().updateMatrix(move);
        solution.updateTotalValue(move);
        solution.updateInfeasibilityDegree(move);
    }

    // 插入操作：将物品插入到背包中
    public static void insert(Solution solution,Move move) {
        Item item=move.getItem1();
        Knapsack knapsack=move.getKnapsack1();
        List<Item> unassignedItems=solution.getUnassignedItems();

        unassignedItems.remove(item);
        knapsack.getItems().add(item);
        solution.getValueContributionMatrix().updateMatrix(move);
        solution.updateTotalValue(move);
        solution.updateInfeasibilityDegree(move);
    }

    // 判断是否可以插入物品到背包中（不超过背包容量）
    public static boolean canInsert(Item item, Knapsack knapsack) {
        return knapsack.getTotalWeight() + item.getWeight() <= knapsack.getCapacity();
    }

    // 重新分配操作：将物品从一个背包移动到另一个背包
    public static void reallocate(Solution solution,Move move) {
        Item item=move.getItem1();
        Knapsack fromKnapsack=move.getKnapsack1();
        Knapsack toKnapsack=move.getKnapsack2();

        fromKnapsack.getItems().remove(item);
        toKnapsack.getItems().add(item);
        solution.getValueContributionMatrix().updateMatrix(move);
        solution.updateTotalValue(move);
        solution.updateInfeasibilityDegree(move);
    }

    // 判断是否可以将物品从一个背包重新分配到另一个背包（不超过目标背包的容量）
    public static boolean canReallocate(Item item, Knapsack fromKnapsack, Knapsack toKnapsack) {
        return toKnapsack.getTotalWeight() + item.getWeight() <= toKnapsack.getCapacity();
    }

    // 交换操作：交换两个物品在两个背包之间的位置
    public static void exchange(Solution solution,Move move) {
        Item item1=move.getItem1();
        Item item2=move.getItem2();
        Knapsack knapsack1=move.getKnapsack1();
        Knapsack knapsack2=move.getKnapsack2();

        knapsack1.getItems().remove(item1);
        knapsack1.getItems().add(item2);
        if (knapsack2 != null) {
            knapsack2.getItems().remove(item2);
            knapsack2.getItems().add(item1);
        }
        solution.getValueContributionMatrix().updateMatrix(move);
        solution.updateTotalValue(move);
        solution.updateInfeasibilityDegree(move);
    }


    // 判断是否可以交换两个物品在两个背包之间的位置（不超过各自背包的容量）
    public static boolean canExchange(Item item1, Item item2, Knapsack knapsack1, Knapsack knapsack2) {
        double newWeight1 = knapsack1.getTotalWeight() - item1.getWeight() + item2.getWeight();

        if (knapsack2 != null) {
            double newWeight2 = knapsack2.getTotalWeight() - item2.getWeight() + item1.getWeight();
            return newWeight1 <= knapsack1.getCapacity() && newWeight2 <= knapsack2.getCapacity();
        } else {
            return newWeight1 <= knapsack1.getCapacity();
        }
    }
}
