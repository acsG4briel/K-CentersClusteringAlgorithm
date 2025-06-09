import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Class responsible for reading the files and building the graphs
public class GraphReader {
    private String filePath;

    public GraphReader(String filePath) {
        this.filePath = filePath;
    }

    // Read the file and return a graph based on that file
    public EdgeWeightedGraph execute() {
        EdgeWeightedGraph graph = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] firstLine = reader.readLine().trim().split("\\s+");
            int V = Integer.parseInt(firstLine[0]); // número de vértices
            int centers = Integer.parseInt(firstLine[2]); // número de centros

            // Instance a graph based on the number of vertex and centers
            graph = new EdgeWeightedGraph(V, centers);

            // Add the edges based on the file
            String line;
            while ((line = reader.readLine()) != null) {
                String[] edgeData = line.trim().split("\\s+");
                if (edgeData.length >= 3) {
                    // Test index is 1 to V instead of 0 to V - 1
                    int v = Integer.parseInt(edgeData[0]) - 1;
                    int w = Integer.parseInt(edgeData[1]) - 1;
                    double weight = Double.parseDouble(edgeData[2]);

                    // Create and add Edge to the graph
                    Edge edge = new Edge(v, w, weight);
                    graph.addEdge(edge);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Error converting numbers: " + e.getMessage());
            e.printStackTrace();
        }

        return graph;
    }
}