import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

        Arrays.sort(testFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String name1 = f1.getName();
                String name2 = f2.getName();

                if (name1.startsWith("pmed") && name2.startsWith("pmed")) {
                    int num1 = Integer.parseInt(name1.substring(4, name1.lastIndexOf(".")));
                    int num2 = Integer.parseInt(name2.substring(4, name2.lastIndexOf(".")));
                    return Integer.compare(num1, num2);
                }
                
                return name1.compareTo(name2);
            }
        });

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
