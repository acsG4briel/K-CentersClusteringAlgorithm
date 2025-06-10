public class MethodResult {
    long executionTimeMs;
    long comparisons;
    double radius;
    int solutionType; // 1 - BetterThanAproximate, 2 - AproximateSolution

    public MethodResult(long executionTimeMs, long comparisons, double radius, int solutionType) {
        this.executionTimeMs = executionTimeMs;
        this.comparisons = comparisons;
        this.radius = radius;
        this.solutionType = solutionType;
    }
}
