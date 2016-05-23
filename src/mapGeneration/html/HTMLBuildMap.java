package mapGeneration.html;

import com.tinkerpop.blueprints.Graph;

/**
 * HTML Builder interface
 * @author Loann Neveu
 */
public interface HTMLBuildMap {

    /**
     * Create file which contains HTML representation of the graph
     * @param graph graph to use
     * @param filename name of the HTML output file
     */
    public void create(Graph graph, String filename);
}
