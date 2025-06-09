import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Class created to apply the methods on all graph instances
public class ProblemTester {
    public ProblemTester() {

    }

    // Execute the tests
    public void runTests(List<EdgeWeightedGraph> graphs, ExactMethod em, AverageMethod am) {
        try {
            // Print the results in two different archives
            PrintWriter resultsMethod1 = new PrintWriter(new FileWriter("results_method1.txt"));
            PrintWriter resultsMethod2 = new PrintWriter(new FileWriter("results_method2.txt"));

            resultsMethod1.println("Instance,Time(ms),Comparisons,Radius");
            resultsMethod2.println("Instance,Time(ms),Comparisons,Radius");

            for (var graph : graphs) {
                int i = 1;
                MethodResult resultM1 = em.execute(graph);
                resultsMethod1.println(String.format("%d,%d,%d,%d",
                        i, resultM1.executionTimeMs,
                        resultM1.comparisons, resultM1.radius));

                MethodResult resultM2 = am.execute(graph);
                resultsMethod2.println(String.format("%d,%d,%d,%d",
                        i, resultM2.executionTimeMs,
                        resultM2.comparisons, resultM2.radius));

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
}
