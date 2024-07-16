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

    public void updateMatrix(Map<Integer, Map<Integer, Double>> matrix){
        this.matrix=matrix;
    }
//    // 更新矩阵，此处move是被执行过了的
//    public void updateMatrix(Move move) {
//        Item item1 = move.getItem1();
//        Item item2 = move.getItem2();
//        Knapsack knapsack1 = move.getKnapsack1();
//        Knapsack knapsack2 = move.getKnapsack2();
//
//        int item1Id = item1.getId();
//        int item2Id;
//        int knapsack1Id = knapsack1.getId();
//        int knapsack2Id;
//
//        switch (move.getMoveType()) {
//            case EXTRACTION:
//                // 如果是抽取操作，从knapsack1中移出item1
//                for (Item j : knapsack1.getItems()) {
////                    if (!j.equals(item1)) {
////                        int jId = j.getId();
////                        matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j));
////                    }
//
//                    int jId = j.getId();
//                    matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j));
//
//                }
//                matrix.get(item1Id).put(knapsack1Id, 0.0); // 更新item1在knapsack1中的贡献值为0
//                break;
//            case INSERTION:
//                // 如果是插入操作，将item1插入knapsack1
//                for (Item j : knapsack1.getItems()) {
//                    if (!j.equals(item1)) {
//                        int jId = j.getId();
//                        matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) + item1.getQuadraticValue(j));
//                    }
//                }
//                matrix.get(item1Id).put(knapsack1Id, computeValueContribution(item1, knapsack1)); // 计算并更新item1在knapsack1中的贡献值
//                break;
//            case REALLOCATION:
//                // 如果是重新分配操作，从knapsack1中移出item1
//                for (Item j : knapsack1.getItems()) {
////                    if (!j.equals(item1)) {
////                        int jId = j.getId();
////                        matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j));
////                    }
//
//                        int jId = j.getId();
//                        matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j));
//
//                }
//                matrix.get(item1Id).put(knapsack1Id, 0.0); // 更新item1在knapsack1中的贡献值为0
//
//                knapsack2Id = knapsack2.getId();
//                // 将item1移入knapsack2
//                for (Item j : knapsack2.getItems()) {
//                    if (!j.equals(item1)) {
//                        int jId = j.getId();
//                        matrix.get(jId).put(knapsack2Id, matrix.get(jId).get(knapsack2Id) + item1.getQuadraticValue(j));
//                    }
//                }
//                matrix.get(item1Id).put(knapsack2Id, computeValueContribution(item1, knapsack2)); // 计算并更新item1在knapsack2中的贡献值
//                break;
//            case EXCHANGE:
//                item2Id = item2.getId();
//                if (knapsack2 != null) {
//                    knapsack2Id = knapsack2.getId();
//                    // 如果是交换操作，从knapsack1中移出item1，将item2移入knapsack1
//                    for (Item j : knapsack1.getItems()) {
////                        if (!j.equals(item1) && !j.equals(item2)) {
////                            int jId = j.getId();
////                            matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
////                        }
//                        if (!j.equals(item2)) {
//                            int jId = j.getId();
//                            matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
//                        }
//                    }
//                    matrix.get(item1Id).put(knapsack1Id, 0.0); // 更新item1在knapsack1中的贡献值为0
//                    matrix.get(item2Id).put(knapsack1Id, computeValueContribution(item2, knapsack1)); // 计算并更新item2在knapsack1中的贡献值
//
//                    // 从knapsack2中移出item2，将item1移入knapsack2
//                    for (Item j : knapsack2.getItems()) {
////                        if (!j.equals(item1) && !j.equals(item2)) {
////                            int jId = j.getId();
////                            matrix.get(jId).put(knapsack2Id, matrix.get(jId).get(knapsack2Id) - item2.getQuadraticValue(j) + item1.getQuadraticValue(j));
////                        }
//                        if (!j.equals(item1)) {
//                            int jId = j.getId();
//                            matrix.get(jId).put(knapsack2Id, matrix.get(jId).get(knapsack2Id) - item2.getQuadraticValue(j) + item1.getQuadraticValue(j));
//                        }
//                    }
//                    matrix.get(item2Id).put(knapsack2Id, 0.0); // 更新item2在knapsack2中的贡献值为0
//                    matrix.get(item1Id).put(knapsack2Id, computeValueContribution(item1, knapsack2)); // 计算并更新item1在knapsack2中的贡献值
//                } else {
//                    // 如果只有一个knapsack1，从knapsack1中移出item1，将item2移入knapsack1
//                    for (Item j : knapsack1.getItems()) {
////                        if (!j.equals(item1) && !j.equals(item2)) {
////                            int jId = j.getId();
////                            matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
////                        }
//                        if (!j.equals(item2)) {
//                            int jId = j.getId();
//                            matrix.get(jId).put(knapsack1Id, matrix.get(jId).get(knapsack1Id) - item1.getQuadraticValue(j) + item2.getQuadraticValue(j));
//                        }
//                    }
//                    matrix.get(item1Id).put(knapsack1Id, 0.0); // 更新item1在knapsack1中的贡献值为0
//                    matrix.get(item2Id).put(knapsack1Id, computeValueContribution(item2, knapsack1)); // 计算并更新item2在knapsack1中的贡献值
//                }
//                break;
//        }
//    }

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

//    // 克隆矩阵
//    public ValueContributionMatrix cloneValueContributionMatrix() {
//        ValueContributionMatrix clonedMatrix = new ValueContributionMatrix(new ArrayList<>(), new ArrayList<>());
//        Map<Integer, Map<Integer, Double>> clonedData = new HashMap<>();
//
//        for (Map.Entry<Integer, Map<Integer, Double>> entry : matrix.entrySet()) {
//            Integer itemId = entry.getKey();
//            Map<Integer, Double> knapsackContributions = entry.getValue();
//            Map<Integer, Double> clonedContributions = new HashMap<>();
//
//            for (Map.Entry<Integer, Double> knapsackEntry : knapsackContributions.entrySet()) {
//                clonedContributions.put(knapsackEntry.getKey(), knapsackEntry.getValue());
//            }
//
//            clonedData.put(itemId, clonedContributions);
//        }
//
//        clonedMatrix.matrix = clonedData;
//        return clonedMatrix;
//    }

}
