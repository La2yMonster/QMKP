import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabuSearchAlgorithm {

    public static final int Ncons = 1000; // The search depth of each FLS phase
    public static final int M = 600;//Maximum number of iterations of each ILS phase
    public static final long TIME_LIMIT = 1000 * 60; // 停止条件，时间限制，单位为毫秒，1秒为1000毫秒
    public static final double ALPHA = 0.1; // Tabu tenure management factor
    public static final double BETA = 0.7; // Weight factor

    private static Set<Item> allItems;
    private static Set<Knapsack> allKnapsacks;
    private static Solution initialSolution;
    private static ValueContributionMatrix valueContributionMatrix;//价值贡献矩阵
    private static Solution SBest; // 最佳解
    private static double fBest; // 最佳目标值
    private static Solution S;//当前解
    private static Solution SLocalBest;//局部最优解
    private static long elapsedTime;//算法执行的时间

    public static Solution findBestSolution(QMKPInstance qmkpInstance) {
        allItems = new HashSet<>(qmkpInstance.getItems());
        allKnapsacks = new HashSet<>(qmkpInstance.getKnapsacks());
        valueContributionMatrix=new ValueContributionMatrix(allItems, allKnapsacks);

        long startTime = System.currentTimeMillis(); // 记录开始时间
        elapsedTime = 0; // 记录经过的时间

        // Step 2: Construct a feasible initial solution
        S = InitialSolutionGenerator.generateInitialSolution(allItems,allKnapsacks);
        initialSolution=S.cloneSolution();
        SBest = S.cloneSolution();
        fBest = SBest.getTotalValue();

        while (StopConditionIsNotMet()) {

            // Step 7: Feasible local search phase
            FeasibleLocalSearch.performFeasibleLocalSearch(S, Ncons);
            SLocalBest = FeasibleLocalSearch.getLocalBestSolution();
            S = FeasibleLocalSearch.getCurrentSolution();

            // Step 8-11: Update best solution found so far
            if (SLocalBest.getTotalValue() > fBest) {
                SBest = SLocalBest;
                fBest = SBest.getTotalValue();
            }

//            System.out.println("FLS阶段结束，当前解："+S.getTotalValue());
            // Step 12-17: Infeasible local search phase
            InfeasibleLocalSearch.performInfeasibleLocalSearch(S,M);
            SLocalBest = InfeasibleLocalSearch.getLocalBestFeasibleSolution();
            S = InfeasibleLocalSearch.getCurrentSolution();

            if (SLocalBest.getTotalValue() > fBest) {
                SBest = SLocalBest;
                fBest = SBest.getTotalValue();
            }
//            System.out.println("ILS阶段结束，当前解："+S.getTotalValue());

            // Step 18-20: Solution repair procedure
            if (!S.isFeasible()) {
                Repair.repairSolution(S);
            }

            // Update elapsed time
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        return SBest;
    }

    public static Set<Item> getAllItems() {
        return allItems;
    }

    public static Set<Knapsack> getAllKnapsacks() {
        return allKnapsacks;
    }

    public static ValueContributionMatrix getValueContributionMatrix() {
        return valueContributionMatrix;
    }

    public static Solution getInitialSolution() {
        return initialSolution;
    }

    public static Solution getSBest() {
        return SBest;
    }

    public static double getfBest() {
        return fBest;
    }

    public static Solution getS() {
        return S;
    }

    public static Solution getSLocalBest() {
        return SLocalBest;
    }

    public static boolean StopConditionIsNotMet(){
        return elapsedTime <= TIME_LIMIT;
    }
}
