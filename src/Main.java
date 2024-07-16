//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击间距中的 <icon src="AllIcons.Actions.Execute"/> 图标。
public class Main {
    public static void main(String[] args) {
        QMKPInstance qmkpInstance=new QMKPInstance("./data/jeu_100_25_1.txt", 3);
        Solution bestSolution=TabuSearchAlgorithm.findBestSolution(qmkpInstance);
        System.out.println("算法运行时长："+TabuSearchAlgorithm.getElapsedTime()/1000+"s\n全局最优解："+bestSolution.getTotalValue());
    }
}