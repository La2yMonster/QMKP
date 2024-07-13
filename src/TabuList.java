import java.util.*;

public class TabuList {

    private Map<Move, Integer> tabuList; // 禁忌表，存储禁忌移动及其禁忌期限
    private int tabuTenure; // 禁忌期限

    // 构造函数，初始化禁忌期限
    public TabuList(List<Item> allItems, double alpha) {
        this.tabuTenure = (int) Math.ceil(allItems.size() * alpha); // 根据物品数量和alpha计算禁忌期限
        this.tabuList = new LinkedHashMap<>(); // 使用LinkedHashMap保持插入顺序
    }

    // 获取禁忌表
    public Map<Move, Integer> getTabuList() {
        return tabuList;
    }

    // 更新禁忌表
    public void updateTabuList(Move move) {
        // 减少所有移动的禁忌期限
        Iterator<Map.Entry<Move, Integer>> iterator = tabuList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Move, Integer> entry = iterator.next();
            int newTenure = entry.getValue() - 1;
            if (newTenure <= 0) {
                iterator.remove(); // 移除禁忌期限小于等于0的移动
            } else {
                tabuList.put(entry.getKey(), newTenure);
            }
        }

        // 如果移动类型是抽取、重新分配或交换，则将新移动添加到禁忌表中
        if (move.getMoveType() == Move.MoveType.EXTRACTION ||
                move.getMoveType() == Move.MoveType.REALLOCATION ||
                move.getMoveType() == Move.MoveType.EXCHANGE) {
            tabuList.put(move, tabuTenure);
        }
    }

    // 检查移动是否在禁忌表中
    public boolean isTabu(Move move) {
        return tabuList.containsKey(move);
    }
}
