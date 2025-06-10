import java.util.ArrayList;
import java.util.List;

// Class used to implement the approximate method to solve the K-Centers problem
// Method used: Greedy Farthest-First Traversal
public class AverageMethod {
    public AverageMethod() {
    }

    // Finds the best radius for a EdgeWeightedGraph
    public MethodResult execute(EdgeWeightedGraph graph) {
        long startTime = System.currentTimeMillis();
        long comparisons = 0;

        int V = graph.V();
        int k = graph.Centers();

        // Get all the smallest distances between all vertex
        double[][] distances = initializeDistanceMatrix(graph, comparisons);
        comparisons = floydWarshall(distances, V, comparisons);
        List<Integer> centers = new ArrayList<>();
        boolean[] isCenters = new boolean[V];

        // Start with the most central vertex
        int firstCenter = findMostCentralVertex(distances, V, comparisons);
        centers.add(firstCenter);
        isCenters[firstCenter] = true;

        // Iteratively add the vertex farthest from existing centers
        for (int i = 1; i < k; i++) {
            int farthestVertex = -1;
            double maxMinDistance = -1;

            for (int v = 0; v < V; v++) {
                if (!isCenters[v]) {
                    double minDistanceToCenter = Double.POSITIVE_INFINITY;

                    for (int center : centers) {
                        minDistanceToCenter = Math.min(minDistanceToCenter, distances[v][center]);
                        comparisons++;
                    }

                    if (minDistanceToCenter > maxMinDistance) {
                        maxMinDistance = minDistanceToCenter;
                        farthestVertex = v;
                        comparisons++;
                    }
                }
            }

            if (farthestVertex != -1) {
                centers.add(farthestVertex);
                isCenters[farthestVertex] = true;
            }
        }

        int[] centersArray = new int[k];
        for (int i = 0; i < k; i++) {
            centersArray[i] = centers.get(i);
        }

        double radius = calculateRadius(centersArray, V, distances, comparisons);

        // After finding initial centers, it applies local search:
        boolean improved = true;
        int iterations = 0;
        int maxIterations = 100;

        while (improved && iterations < maxIterations) {
            improved = false;
            iterations++;

            for (int i = 0; i < k; i++) {
                int currentCenter = centersArray[i];

                for (int v = 0; v < V; v++) {
                    if (!isCenter(v, centersArray)) {
                        centersArray[i] = v;
                        double newRadius = calculateRadius(centersArray, V, distances, comparisons);

                        if (newRadius < radius) {
                            radius = newRadius;
                            improved = true;
                            break;
                        } else {
                            centersArray[i] = currentCenter;
                        }
                    }
                }

                if (improved)
                    break;
            }
        }

        // Returns execution time, comparison count, and final radius
        long endTime = System.currentTimeMillis();
        long executionTimeMs = endTime - startTime;

        return new MethodResult(executionTimeMs, comparisons, radius, 2);
    }

    private int findMostCentralVertex(double[][] distances, int V, long comparisons) {
        int mostCentral = 0;
        double minTotalDistance = Double.POSITIVE_INFINITY;

        for (int v = 0; v < V; v++) {
            double totalDistance = 0;
            for (int u = 0; u < V; u++) {
                totalDistance += distances[v][u];
            }

            if (totalDistance < minTotalDistance) {
                minTotalDistance = totalDistance;
                mostCentral = v;
            }
        }

        return mostCentral;
    }

    private boolean isCenter(int vertex, int[] centers) {
        for (int center : centers) {
            if (center == vertex) {
                return true;
            }
        }
        return false;
    }

    private double[][] initializeDistanceMatrix(EdgeWeightedGraph graph, long comparisons) {
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

    private long floydWarshall(double[][] distances, int V, long comparisons) {
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                    }
                    comparisons++;
                }
            }
        }
        return comparisons;
    }

    private double calculateRadius(int[] centers, int V, double[][] distances, long comparisons) {
        double radius = 0.0;

        for (int v = 0; v < V; v++) {
            double minDistanceToCenter = Double.POSITIVE_INFINITY;

            for (int center : centers) {
                minDistanceToCenter = Math.min(minDistanceToCenter, distances[v][center]);
                comparisons++;
            }

            radius = Math.max(radius, minDistanceToCenter);
        }

        return radius;
    }
}