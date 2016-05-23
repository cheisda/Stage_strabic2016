package mapGeneration.layout;

import com.tinkerpop.blueprints.Graph;

/**
 * Layout algorithm interface
 * @author Loann Neveu
 */
public interface LayoutAlgorithm {

    /**
     * Apply the algorithm on a graph
     * @param graph (Tinkerpop graph)
     * @param num_iteration number of iteration to do
     */
    void apply(Graph graph, int num_iteration);
}
