import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExactMethod {
    private double[][] distances;
    private double minRadius;
    private int[] bestCenters;
    private long comparisons;
    private AtomicBoolean timeoutOccurred;
    private long startTime;
    private int timeoutCheckCounter;

    public ExactMethod() {
        timeoutOccurred = new AtomicBoolean(false);
    }

    public void signalTimeout() {
        timeoutOccurred.set(true);
    }

    public void resetTimeout() {
        timeoutOccurred.set(false);
    }

    public long getComparisons() {
        return comparisons;
    }

    public double getBestRadius() {
        return minRadius;
    }

    // Finds the exact solution to the K-Center problem but not in a polinomial time
    public MethodResult execute(EdgeWeightedGraph graph, double superiorLimit) {
        timeoutCheckCounter = 0;
        resetTimeout();

        startTime = System.currentTimeMillis();
        comparisons = 0;

        // Inicializes the superior limit as the aproximate result from the average method
        minRadius = superiorLimit;

        int V = graph.V();
        int k = graph.Centers();

        distances = initializeDistanceMatrix(graph);
        comparisons = floydWarshall(distances, V);

        bestCenters = new int[k];
        int[] currentCenters = new int[k];


        // Find the most suitable vertex to be centers and order them
        Integer[] vertexOrder = precomputeVertexOrder(V);


        // Analyze all combinations with the improvements made before until the best solution is found or the time is over
        try {
            findBestCentersOrdered(0, 0, currentCenters, k, V, vertexOrder);
        } catch (InterruptedException e) {
            System.out.println("Search interrupted after timeout. Using best solution found so far.");
        }

        long endTime = System.currentTimeMillis();
        long executionTimeMs = endTime - startTime;

        if (minRadius == superiorLimit) {
            System.out.println("No better solution than the upper bound was found.");
        } else {
            System.out.println("Best solution found: " + minRadius);
        }

        return new MethodResult(executionTimeMs, comparisons, minRadius);
    }

    private Integer[] precomputeVertexOrder(int V) {
        Integer[] order = new Integer[V];
        double[] centrality = new double[V];

        for (int i = 0; i < V; i++) {
            order[i] = i;
            double sum = 0;
            for (int j = 0; j < V; j++) {
                sum += distances[i][j];
            }
            centrality[i] = sum;
        }

        Arrays.sort(order, (a, b) -> Double.compare(centrality[a], centrality[b]));
        return order;
    }

    private double[][] initializeDistanceMatrix(EdgeWeightedGraph graph) {
        int V = graph.V();
        double[][] distances = new double[V][V];

        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                distances[i][j] = Double.POSITIVE_INFINITY;
            }
            distances[i][i] = 0.0;
        }

        for (Edge e : graph.edges()) {
            int v = e.either();
            int w = e.other(v);
            distances[v][w] = e.weight();
            distances[w][v] = e.weight();
            comparisons += 2;
        }

        return distances;
    }

    private long floydWarshall(double[][] distances, int V) {
        long localComparisons = 0;
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                    }
                    localComparisons++;
                }
            }
        }
        return localComparisons;
    }

    private void findBestCentersOrdered(int start, int index, int[] centers, int k, int V, Integer[] vertexOrder)
            throws InterruptedException {
        if (++timeoutCheckCounter % 100 == 0 && timeoutOccurred.get()) {
            throw new InterruptedException("Time limit exceeded");
        }

        if (index == k) {
            double radius = calculateRadius(centers, V);

            if (radius < minRadius) {
                minRadius = radius;
                System.arraycopy(centers, 0, bestCenters, 0, k);
                System.out.println("New best solution: " + radius);
            }
            return;
        }

        for (int i = start; i < V; i++) {
            centers[index] = vertexOrder[i];

            int nextStart = i + 1;

            findBestCentersOrdered(nextStart, index + 1, centers, k, V, vertexOrder);
        }
    }

    private double calculateRadius(int[] centers, int V) {
        double radius = 0.0;

        for (int v = 0; v < V; v++) {
            double minDistanceToCenter = Double.POSITIVE_INFINITY;

            for (int center : centers) {
                minDistanceToCenter = Math.min(minDistanceToCenter, distances[v][center]);
                comparisons++;
            }

            if (minDistanceToCenter > minRadius) {
                return Double.POSITIVE_INFINITY;
            }

            radius = Math.max(radius, minDistanceToCenter);

            if (radius > minRadius) {
                return Double.POSITIVE_INFINITY;
            }
        }

        return radius;
    }
}