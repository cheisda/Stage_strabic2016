package master;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import elements.LayoutData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * UI TEST
 */
public class UITest extends JFrame {

    private Graph graph;

    public UITest(Graph graph) {
        super();
        this.graph = graph;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().add(new GraphPanel());
        this.setSize(900,900);
        this.setLocation(200,200);
        this.setVisible(true);
    }

    private class GraphPanel extends JPanel{
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.setBackground(Color.WHITE);
            Graphics2D g2 = (Graphics2D)g;

            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(5));

            for(Vertex i : graph.getVertices()) {
                LayoutData d = (LayoutData) i.getProperty("layout_data");
                g2.draw(new Line2D.Float(d.getX()/4,  d.getY()/4, d.getX()/4,  d.getY()/4));
            }
        }
    }
}
