import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Class created to apply the methods on all graph instances
public class ProblemTester {
    private static final long TIME_LIMIT_MINUTES = 30;

    public ProblemTester() {
    }

    // Execute the tests
    public void runTests(List<EdgeWeightedGraph> graphs, ExactMethod em, AverageMethod am) {
        try {
            // Print the results in two different archives
            PrintWriter resultsMethod1 = new PrintWriter(new FileWriter("results_method1.txt"));
            PrintWriter resultsMethod2 = new PrintWriter(new FileWriter("results_method2.txt"));

            resultsMethod1.println("Instance,Time(ms),Comparisons,Radius,SolutionType");
            resultsMethod2.println("Instance,Time(ms),Comparisons,Radius,SolutionType");

            resultsMethod1.flush();
            resultsMethod2.flush();

            int i = 1;

            for (var graph : graphs) {
                System.out.println("Processing graph " + i + "...");

                // Execute the average method
                MethodResult resultM2 = am.execute(graph);
                resultsMethod2.println(String.format("%d,%d,%d,%.1f",
                        i, resultM2.executionTimeMs,
                        resultM2.comparisons, resultM2.radius));
                resultsMethod2.flush();

                em.resetTimeout();

                // Execute the exact method with a time limit
                MethodResult resultM1 = executeWithTimeLimit(em, graph, resultM2.radius);
                resultsMethod1.println(String.format("%d,%d,%d,%.1f",
                        i, resultM1.executionTimeMs,
                        resultM1.comparisons, resultM1.radius));

                resultsMethod1.flush();

                i++;
            }

            // Close Files
            resultsMethod1.close();
            resultsMethod2.close();

            System.out.println("\nTests done.");

        } catch (IOException e) {
            System.err.println("There was an error processing tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private MethodResult executeWithTimeLimit(ExactMethod em, EdgeWeightedGraph graph, double superiorLimit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<MethodResult> future = executor.submit(() -> em.execute(graph, superiorLimit));

        try {
            MethodResult result = future.get(TIME_LIMIT_MINUTES, TimeUnit.MINUTES);
            executor.shutdown();
            return result;
        } catch (TimeoutException e) {
            System.err.println("Exact method exceeded the time limit. Using best solution found so far.");
            em.signalTimeout();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            future.cancel(true);
            executor.shutdownNow();

            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            long executionTime = TIME_LIMIT_MINUTES * 60 * 1000;
            return new MethodResult(executionTime, em.getComparisons(), em.getBestRadius());

        } catch (Exception e) {
            System.err.println("Error executing exact method: " + e.getMessage());
            e.printStackTrace();
            executor.shutdownNow();

            return new MethodResult(0, 0, superiorLimit);
        }
    }
}