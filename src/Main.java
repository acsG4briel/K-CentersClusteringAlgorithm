import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Graph List that will be used on the ProblemTester method
        List<EdgeWeightedGraph> graphs = new ArrayList<>();

        // Process to obtain ou graphs from the directory /TestCases
        File testCasesDir = new File("TestCases");

        if (!testCasesDir.exists() || !testCasesDir.isDirectory()) {
            System.err.println("TestCases does not exist.");
            return;
        }

        File[] testFiles = testCasesDir.listFiles();

        if (testFiles == null || testFiles.length == 0) {
            System.out.println("There are no files on /TestCases");
            return;
        }

        for (File testFile : testFiles) {
            if (testFile.isFile()) {
                System.out.println("Processing file: " + testFile.getName());

                GraphReader reader = new GraphReader(testFile.getAbsolutePath());

                EdgeWeightedGraph graph = reader.execute();

                if (graph != null) {
                    graphs.add(graph);

                }
            }

        }

        // Execute both methods on all the graphs
        ExactMethod em = new ExactMethod();
        AverageMethod am = new AverageMethod();
        ProblemTester pm = new ProblemTester();

        pm.runTests(graphs, em, am);
    }
}
