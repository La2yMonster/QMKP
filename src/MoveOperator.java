import java.util.List;
import java.util.Map;
import java.util.Set;

public class MoveOperator {

    // 抽取操作：将物品从背包中移除
    public static void extract(Solution solution, Move move) {
        Item item=move.getItem1();
        Knapsack knapsack=move.getKnapsack1();
        int itemId = item.getId();
        int knapsackId = knapsack.getId();

        knapsack.getItems().remove(item);//执行操作
        solution.getUnassignedItems().add(item);

        //增量更新背包
        double knapsackTotalWeight= knapsack.getTotalWeight()-item.getWeight();
        double quadraticValueSum=0;
        for(Item j: knapsack.getItems()){
            quadraticValueSum-=item.getQuadraticValue(j);
        }
        double knapsackTotalValue=knapsack.getTotalValue()-item.getValue()+quadraticValueSum;
        knapsack.updateKnapsack(knapsackTotalWeight,knapsackTotalValue,knapsack.getItems());

        //增量更新解
        double solutionTotalValue=solution.getTotalValue()+move.getMoveGain();
        double oldOverload=Math.max(0,knapsack.getTotalWeight()+item.getWeight()-knapsack.getCapacity());
        double newOverload=Math.max(0,knapsack.getTotalWeight()-knapsack.getCapacity());
        double solutionInfeasibilityDegree=solution.getInfeasibilityDegree()-oldOverload+newOverload;
        solution.updateSolution(solutionTotalValue,solutionInfeasibilityDegree,solution.getUnassignedItems());

        //增量更新价值贡献矩阵
        Map<Integer, Map<Integer, Double>> matrix=TabuSearchAlgorithm.getValueContributionMatrix().getMatrix();
        for (Item j : knapsack.getItems()) {
            int jId = j.getId();
            matrix.get(jId).put(knapsackId, matrix.get(jId).get(knapsackId) - item.getQuadraticValue(j));
        }
        matrix.get(itemId).put(knapsackId, 0.0); // 更新item在knapsack中的贡献值为0
    }

    // 插入操作：将物品插入到背包中
    public static void insert(Solution solution,Move move) {
        Item item=move.getItem1();
        Knapsack knapsack=move.getKnapsack1();
        int itemId = item.getId();
        int knapsackId = knapsack.getId();

        knapsack.getItems().add(item);//执行操作
        solution.getUnassignedItems().remove(item);

        //增量更新背包
        double knapsackTotalWeight=knapsack.getTotalWeight()+item.getWeight();
        double quadraticValueSum=0;
        for (Item j: knapsack.getItems()){
            quadraticValueSum+=item.getQuadraticValue(j);
        }
        double knapsackTotalValue=knapsack.getTotalValue()+item.getValue()+quadraticValueSum;
        knapsack.updateKnapsack(knapsackTotalWeight,knapsackTotalValue,knapsack.getItems());

        //增量更新解
        double solutionTotalValue=solution.getTotalValue()+move.getMoveGain();
        double oldOverload=Math.max(0,knapsack.getTotalWeight()-item.getWeight()-knapsack.getCapacity());
        double newOverload=Math.max(0,knapsack.getTotalWeight()-knapsack.getCapacity());
        double solutionInfeasibilityDegree=solution.getInfeasibilityDegree()-oldOverload+newOverload;
        solution.updateSolution(solutionTotalValue,solutionInfeasibilityDegree,solution.getUnassignedItems());

        //增量更新价值贡献矩阵
        Map<Integer, Map<Integer, Double>> matrix=TabuSearchAlgorithm.getValueContributionMatrix().getMatrix();
        for (Item j : knapsack.getItems()) {
            if (!j.equals(item)) {
                int jId = j.getId();
                matrix.get(jId).put(knapsackId, matrix.get(jId).get(knapsackId) + item.getQuadraticValue(j));
            }
        }
        matrix.get(itemId).put(knapsackId, ValueContributionMatrix.computeValueContribution(item, knapsack)); // 计算并更新item在knapsack中的贡献值
    }

    // 判断是否可以插入物品到背包中（不超过背包容量）
    public static boolean canInsert(Item item, Knapsack knapsack) {
        return knapsack.getTotalWeight() + item.getWeight() <= knapsack.getCapacity();
    }

    // 重新分配操作：将物品从一个背包移动到另一个背包
    public static void reallocate(Solution solution,Move move) {
        Item item=move.getItem1();
        Knapsack knapsack1=move.getKnapsack1();
        Knapsack knapsack2=move.getKnapsack2();
        int itemId = item.getId();
        int knapsack1Id = knapsack1.getId();
        int knapsack2Id= knapsack2.getId();

        knapsack1.getItems().remove(item);
        knapsack2.getItems().add(item);

        //增量更新背包
        double knapsack1TotalWeight= knapsack1.getTotalWeight()-item.getWeight();
        double knap1QuadraSum=0;
        for (Item j: knapsack1.getItems()){
            knap1QuadraSum-=item.getQuadraticValue(j);
        }
        double knapsack1TotalValue=knapsack1.getTotalValue()-item.getValue()+knap1QuadraSum;
        knapsack1.updateKnapsack(knapsack1TotalWeight,knapsack1TotalValue,knapsack1.getItems());

        double knapsack2TotalWeight=knapsack2.getTotalWeight()+item.getWeight();
        double knap2QuadraSum=0;
        for (Item j: knapsack2.getItems()){
            knap2QuadraSum+=item.getQuadraticValue(j);
        }
        double knapsack2TotalValue=knapsack2.getTotalValue()+item.getValue()+knap2QuadraSum;
        knapsack2.updateKnapsack(knapsack2TotalWeight,knapsack2TotalValue,knapsack2.getItems());

        //增量更新解
        double solutionTotalValue=solution.getTotalValue()+move.getMoveGain();
        double oldOverload1=Math.max(0,knapsack1.getTotalWeight()+item.getWeight()-knapsack1.getCapacity());
        double newOverload1=Math.max(0,knapsack1.getTotalWeight()-knapsack1.getCapacity());
        double oldOverload2=Math.max(0,knapsack2.getTotalWeight()-item.getWeight()-knapsack2.getCapacity());
        double newOverload2=Math.max(0,knapsack2.getTotalWeight()-knapsack2.getCapacity());
        double solutionInfeasibilityDegree=solution.getInfeasibilityDegree()-oldOverload1+newOverload1-oldOverload2+newOverload2;
        solution.updateSolution(solutionTotalValue,solutionInfeasibilityDegree,solution.getUnassignedItems());

        //增量更新价值贡献矩阵
        Map<Integer, Map<Integer, Double>> matrix=TabuSearchAlgorithm.getValueContributionMatrix().getMatrix();
        for (Item j : knapsack1.getItems()) {
                int jId = j.getId();
                matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item.getQuadraticValue(j));

        }
        matrix.get(itemId).put(knapsack1Id, 0.0); // 更新item在knapsack1中的贡献值为0
        // 将item移入knapsack2
        for (Item j : knapsack2.getItems()) {
            if (!j.equals(item)) {
                int jId = j.getId();
                matrix.get(jId).put(knapsack2Id, matrix.get(jId).get(knapsack2Id) + item.getQuadraticValue(j));
            }
        }
        matrix.get(itemId).put(knapsack2Id, ValueContributionMatrix.computeValueContribution(item, knapsack2)); // 计算并更新item在knapsack2中的贡献值
    }

    // 判断是否可以将物品从一个背包重新分配到另一个背包（不超过目标背包的容量）
    public static boolean canReallocate(Item item, Knapsack fromKnapsack, Knapsack toKnapsack) {
        return toKnapsack.getTotalWeight() + item.getWeight() <= toKnapsack.getCapacity();
    }

    // 交换操作：交换两个物品在两个背包之间的位置
    public static void exchange(Solution solution,Move move) {
        Item item1=move.getItem1();
        int item1Id = item1.getId();
        Item item2=move.getItem2();
        int item2Id=item2.getId();
        Knapsack knapsack1=move.getKnapsack1();
        int knapsack1Id = knapsack1.getId();
        Knapsack knapsack2=null;
        int knapsack2Id;
        if (move.getKnapsack2()!=null){
            knapsack2=move.getKnapsack2();
        }

        knapsack1.getItems().remove(item1);
        knapsack1.getItems().add(item2);
        if (knapsack2 != null) {
            knapsack2.getItems().remove(item2);
            knapsack2.getItems().add(item1);
        }else {
            solution.getUnassignedItems().remove(item2);
            solution.getUnassignedItems().add(item1);
        }

        //增量更新背包
        double knapsack1TotalWeight=knapsack1.getTotalWeight()-item1.getWeight()+ item2.getWeight();
        double knap1QuadraSum=0;
        for (Item j:knapsack1.getItems()){
            knap1QuadraSum=knap1QuadraSum-item1.getQuadraticValue(j)+item2.getQuadraticValue(j);
        }
        knap1QuadraSum+=item1.getQuadraticValue(item2);
        double knapsack1TotalValue= knapsack1.getTotalValue()-item1.getValue()+item2.getValue()+knap1QuadraSum;
        knapsack1.updateKnapsack(knapsack1TotalWeight,knapsack1TotalValue,knapsack1.getItems());

        if (knapsack2!=null){
            double knapsack2TotalWeight=knapsack2.getTotalWeight()+item1.getWeight()-item2.getWeight();
            double knap2QuadraSum=0;
            for (Item j: knapsack2.getItems()){
                knap2QuadraSum=knap2QuadraSum-item2.getQuadraticValue(j)+item1.getQuadraticValue(j);
            }
            knap2QuadraSum+=item2.getQuadraticValue(item1);
            double knapsack2TotalValue=knapsack2.getTotalValue()-item2.getValue()+item1.getValue()+knap2QuadraSum;
            knapsack2.updateKnapsack(knapsack2TotalWeight,knapsack2TotalValue,knapsack2.getItems());
        }

        //增量更新解
        double solutionTotalValue=solution.getTotalValue()+move.getMoveGain();
        double oldOverload1=Math.max(0,knapsack1.getTotalWeight()+item1.getWeight()-item2.getWeight()-knapsack1.getCapacity());
        double newOverload1=Math.max(0,knapsack1.getTotalWeight()-knapsack1.getCapacity());
        double solutionInfeasibilityDegree;
        if (knapsack2!=null){
            double oldOverload2=Math.max(0,knapsack2.getTotalWeight()+item2.getWeight()-item1.getWeight()-knapsack2.getCapacity());
            double newOverload2=Math.max(0,knapsack2.getTotalWeight()-knapsack2.getCapacity());
            solutionInfeasibilityDegree=solution.getInfeasibilityDegree()-oldOverload1+newOverload1-oldOverload2+newOverload2;
        }
        else{
            solutionInfeasibilityDegree=solution.getInfeasibilityDegree()-oldOverload1+newOverload1;
        }
        solution.updateSolution(solutionTotalValue,solutionInfeasibilityDegree,solution.getUnassignedItems());

        //增量更新价值贡献矩阵
        Map<Integer, Map<Integer, Double>> matrix=TabuSearchAlgorithm.getValueContributionMatrix().getMatrix();
        for (Item j : knapsack1.getItems()) {
            if (!j.equals(item2)) {
                int jId = j.getId();
                matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
            }
        }
        matrix.get(item1Id).put(knapsack1Id, 0.0); // 更新item1在knapsack1中的贡献值为0
        matrix.get(item2Id).put(knapsack1Id, ValueContributionMatrix.computeValueContribution(item2, knapsack1)); // 计算并更新item2在knapsack1中的贡献值
        if (knapsack2 != null) {
            knapsack2Id=knapsack2.getId();
            // 从knapsack2中移出item2，将item1移入knapsack2
            for (Item j : knapsack2.getItems()) {
                if (!j.equals(item1)) {
                    int jId = j.getId();
                    matrix.get(jId).put(knapsack2Id, matrix.get(jId).get(knapsack2Id) - item2.getQuadraticValue(j) + item1.getQuadraticValue(j));
                }
            }
            matrix.get(item2Id).put(knapsack2Id, 0.0); // 更新item2在knapsack2中的贡献值为0
            matrix.get(item1Id).put(knapsack2Id, ValueContributionMatrix.computeValueContribution(item1, knapsack2)); // 计算并更新item1在knapsack2中的贡献值
        }
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
