package mapGeneration.layout;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import elements.LayoutData;
import utils.Utils;
import java.util.List;

/**
 * Implementation of FruchtermanReingold algorithm
 * @author Loann Neveu
 */
public class FruchtermanReingold implements LayoutAlgorithm {

    private static final float SPEED_DIVISOR = 100;
    private static final float AREA_MULTIPLICATOR = 10000;

    // Default properties
    private double speed = 5;
    private double gravity = 2;
    private float area = 5000;

    @Override
    public void apply(Graph graph, int iteration) {

        for(int i = 0; i < iteration; i++) {

            List<Vertex> nodes = Utils.toList(graph.getVertices());
            List<Edge> edges = Utils.toList(graph.getEdges());

            float maxDisplace = (float) (Math.sqrt(AREA_MULTIPLICATOR * area) / 10f);
            float k = (float) Math.sqrt((AREA_MULTIPLICATOR * area) / (1f + nodes.size()));

            // reset dx & dy
            for (Vertex n : nodes) {
                LayoutData n_data = ((LayoutData) n.getProperty("layout_data"));
                n_data.setDx(0);
                n_data.setDx(0);
            }

            // apply repulsive force between nodes
            for (Vertex n1 : nodes) {
                for (Vertex n2 : nodes) {
                    if (n1 != n2) {
                        LayoutData n1_data = ((LayoutData) n1.getProperty("layout_data"));
                        LayoutData n2_data = ((LayoutData) n2.getProperty("layout_data"));

                        // if 2 nodes are in exact same positon, we need to move them apart
                        if (n1_data.getX() == n2_data.getX() && n1_data.getY() == n2_data.getY()) {
                            n1_data.setX((float) Math.random());
                            n1_data.setY((float) Math.random());
                            n2_data.setX((float) Math.random());
                            n2_data.setY((float) Math.random());
                        }

                        // distance between the 2 nodes
                        float xDist = n1_data.getX() - n2_data.getX();
                        float yDist = n1_data.getY() - n2_data.getY();
                        float dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);


                        if (dist > 0) {
                            float repulsiveF = k * k / dist;

                            //System.out.println("dist1 " + dist);

                            float newDx = n1_data.getDx() + (xDist / dist * repulsiveF);
                            n1_data.setDx(newDx);

                            float newDy = n1_data.getDy() + (yDist / dist * repulsiveF);
                            n1_data.setDy(newDy);
                        }

                    }
                }
            }

            // apply attractive force between nodes
            for (Edge e : edges) {
                Vertex nsource = e.getVertex(Direction.IN); // source
                Vertex ntarget = e.getVertex(Direction.OUT); // target

                LayoutData nsource_data = ((LayoutData) nsource.getProperty("layout_data"));
                LayoutData ntarget_data = ((LayoutData) ntarget.getProperty("layout_data"));

                float xDist = nsource_data.getX() - ntarget_data.getX();
                float yDist = nsource_data.getY() - ntarget_data.getY();
                float dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);

                if (dist > 0) {
                    float attractiveF = dist * dist / k;

                    float newDx_source = nsource_data.getDx() - (xDist / dist * attractiveF);
                    nsource_data.setDx(newDx_source);
                    float newDy_source = nsource_data.getDy() - (yDist / dist * attractiveF);
                    nsource_data.setDy(newDy_source);

                    float newDx_target = ntarget_data.getDx() + (xDist / dist * attractiveF);
                    ntarget_data.setDx(newDx_target);
                    float newDy_target = ntarget_data.getDy() + (yDist / dist * attractiveF);
                    ntarget_data.setDy(newDy_target);
                }
            }

            // gravity
            for (Vertex n : nodes) {
                LayoutData n_data = ((LayoutData) n.getProperty("layout_data"));

                float d = (float) Math.sqrt(n_data.getX() * n_data.getX() + n_data.getY() * n_data.getY());
                float gf = (float)(0.01f * k * gravity * d);

                if (d > 0) {
                    float newDx = n_data.getDx() - (gf * n_data.getX() / d);
                    n_data.setDx(newDx);
                    float newDy = n_data.getDy() - (gf * n_data.getY() / d);
                    n_data.setDy(newDy);
                }
            }

            // speed
            for (Vertex n : nodes) {
                LayoutData n_data = ((LayoutData) n.getProperty("layout_data"));

                float newDx = (float)(n_data.getDx() * (speed / SPEED_DIVISOR));
                n_data.setDx(newDx);
                float newDy = (float)(n_data.getDy() * (speed / SPEED_DIVISOR));
                n_data.setDy(newDy);
            }

            // apply shiffting
            for (Vertex n : nodes) {
                LayoutData n_data = ((LayoutData) n.getProperty("layout_data"));
                float xDist = n_data.getDx();
                float yDist = n_data.getDy();
                //System.out.println("dx " + n_data.getDx() + "dy " + n_data.getDy());
                float dist = (float) Math.sqrt(n_data.getDx() * n_data.getDx() + n_data.getDy() * n_data.getDy());
                //System.out.println("dist" + dist);
                if (dist > 0) {
                    float limitDist = Math.min(maxDisplace * ((float) speed / SPEED_DIVISOR), dist);
                    n_data.setX(n_data.getX() + xDist / dist * limitDist);
                    n_data.setY(n_data.getY() + yDist / dist * limitDist);
                }
            }
        }

        // center graph
        centerGraph(graph, 100);
    }

    private void centerGraph(Graph graph, float offset) {
        List<Vertex> nodes = Utils.toList(graph.getVertices());

        LayoutData n_data = ((LayoutData) nodes.get(0).getProperty("layout_data"));
        float minTop = n_data.getY();
        float minLeft  = n_data.getX();

        // get minTop and minLeft
        for (Vertex n : nodes) {
            n_data = ((LayoutData) n.getProperty("layout_data"));

            if( n_data.getY() < minTop ) minTop = n_data.getY();

            if( n_data.getX() < minLeft ) minLeft = n_data.getX();
        }

        // recomputing coordinates
        for (Vertex n : nodes) {
            n_data = ((LayoutData) n.getProperty("layout_data"));
            float newX = n_data.getX() - minLeft + offset;
            float newY =  n_data.getY() - minTop + offset;

            n_data.setX(newX);
            n_data.setY(newY);
        }
    }
}
