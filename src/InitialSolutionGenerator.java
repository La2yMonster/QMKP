import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class InitialSolutionGenerator {
    // 生成初始解
    public static Solution generateInitialSolution(Set<Item> items, Set<Knapsack> knapsacks) {
        Random random = new Random();
//        ValueContributionMatrix valueContributionMatrix=new ValueContributionMatrix(items, knapsacks);
//        Solution initialSolution=new Solution(knapsacks,valueContributionMatrix);
        Solution initialSolution=new Solution(knapsacks);
        Set<Item> unassignedItems = initialSolution.getUnassignedItems();

        // 遍历每一个背包
        for (Knapsack knapsack : knapsacks) {
            // 随机选择一个未分配的物品放入当前背包
            if (!unassignedItems.isEmpty()) {
                Item randomItem = (new ArrayList<>(unassignedItems)).get(random.nextInt(unassignedItems.size()));
                Move move=new Move(Move.MoveType.INSERTION,randomItem,null,knapsack,null);
                move.calculateMoveGain(initialSolution);
                initialSolution.applyMove(move);
            }

            boolean canAddMoreObjects = true;
            while (canAddMoreObjects) {
                Move bestMove = null;  // 最佳插入操作
                double bestDensity = Double.NEGATIVE_INFINITY;  // 最佳物品的价值密度

                // 遍历未分配的物品列表，寻找最适合当前背包的物品
                for (Item item : unassignedItems) {
                    // 判断物品是否可以放入当前背包（不超重）
                    if (MoveOperator.canInsert(item, knapsack)) {
                        // 计算物品在当前背包中的价值密度
                        Move move=new Move(Move.MoveType.INSERTION,item,null,knapsack,null);
                        move.calculateMoveGain(initialSolution);
                        double density = move.getMoveGain()/item.getWeight();
                        // 如果当前物品的价值密度更高，则更新最佳物品和价值密度
                        if (density > bestDensity) {
                            bestDensity = density;
                            bestMove = move;
                        }
                    }
                }

                // 如果找到最佳物品，则执行插入操作
                if (bestMove != null) {
                    initialSolution.applyMove(bestMove);
                } else {
                    canAddMoreObjects = false;  // 如果没有找到合适的物品，则停止当前背包的填充
                }
            }
        }

        return initialSolution;  // 返回生成的初始解
    }
}