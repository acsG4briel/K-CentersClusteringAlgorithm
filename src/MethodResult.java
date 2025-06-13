public class MethodResult {
    long executionTimeMs;
    long comparisons;
    double radius;

    public MethodResult(long executionTimeMs, long comparisons, double radius) {
        this.executionTimeMs = executionTimeMs;
        this.comparisons = comparisons;
        this.radius = radius;
    }
}
