import java.util.List;

public class TabuSearchAlgorithm {

    private static final int Ncons = 1000; // The search depth of each FLS phase
    private static final int M = 600;//Maximum number of iterations of each ILS phase
    private static final long STOP_CONDITION = 10 * 60; // 停止条件，时间限制，单位为毫秒
    private static final double ALPHA = 0.1; // Tabu tenure management factor
    private static final double BETA = 0.7; // Weight factor

    private QMKPInstance qmkpInstance;
    private List<Item> allItems;
    private List<Knapsack> allKnapsacks;
    private Solution initialSolution;
    private Solution SBest; // 最佳解
    private double fBest; // 最佳目标值
    private Solution S;//当前解
    private Solution SLocalBest;//局部最优解
    private TabuList tabuList; // 禁忌表
    private ValueContributionMatrix valueContributionMatrix;//价值贡献矩阵
    private InitialSolutionGenerator initialSolutionGenerator;
    private FeasibleLocalSearch feasibleLocalSearch;
    private InfeasibleLocalSearch infeasibleLocalSearch;
    private Repair repair;

    public TabuSearchAlgorithm(QMKPInstance qmkpInstance) {
        this.qmkpInstance = qmkpInstance;
        this.allItems = qmkpInstance.getItems();
        this.allKnapsacks = qmkpInstance.getKnapsacks();
        this.tabuList = new TabuList(allItems, ALPHA);
        this.valueContributionMatrix = new ValueContributionMatrix(allItems, allKnapsacks);
        this.initialSolutionGenerator = new InitialSolutionGenerator(allItems, allKnapsacks,valueContributionMatrix);
    }

    public Solution findBestSolution() {
        long startTime = System.currentTimeMillis(); // 记录开始时间
        long elapsedTime = 0; // 记录经过的时间

        // Step 2: Construct a feasible initial solution

        initialSolution = initialSolutionGenerator.generateInitialSolution();
        S=initialSolution;
        SBest = new Solution(initialSolution.getKnapsacks());
        fBest = SBest.getTotalValue();



        while (elapsedTime <= STOP_CONDITION) {

            // Step 7: Feasible local search phase
            feasibleLocalSearch = new FeasibleLocalSearch(S, valueContributionMatrix, allItems, Ncons, ALPHA);
            feasibleLocalSearch.performFeasibleLocalSearch();
            SLocalBest = feasibleLocalSearch.getLocalBestSolution();
            S = feasibleLocalSearch.getCurrentSolution();

            // Step 8-11: Update best solution found so far
            if (SLocalBest.getTotalValue() > fBest) {
                SBest = SLocalBest;
                fBest = SBest.getTotalValue();
            }

            // Step 12-17: Infeasible local search phase
            infeasibleLocalSearch = new InfeasibleLocalSearch(S, valueContributionMatrix, allItems, M, ALPHA, BETA);
            infeasibleLocalSearch.performInfeasibleLocalSearch();
            SLocalBest = infeasibleLocalSearch.getLocalBestFeasibleSolution();
            S = infeasibleLocalSearch.getCurrentSolution();

            if (SLocalBest.getTotalValue() > fBest) {
                SBest = SLocalBest;
                fBest = SBest.getTotalValue();
            }

            // Step 18-20: Solution repair procedure
            if (!S.isFeasible()) {
                repair = new Repair(S, valueContributionMatrix);
                repair.repairSolution();
            }

            // Update elapsed time
            elapsedTime = System.currentTimeMillis() - startTime;
        }

        return SBest;
    }
}
