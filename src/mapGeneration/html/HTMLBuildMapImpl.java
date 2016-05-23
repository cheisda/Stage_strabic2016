package mapGeneration.html;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import elements.ArticleData;
import elements.LayoutData;
import org.apache.commons.lang.StringUtils;
import utils.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Implementation of a HTML builder for maps
 * @author Loann Neveu
 */
public class HTMLBuildMapImpl implements HTMLBuildMap{
    private static final String PROLOGUE = "prologue.html";
    private static final String EPILOGUE = "epilogue.html";

    private static final String TITLE_POSITION[] = {"top-right", "top-left", "bottom-right", "bottom-left"};
    private static final Integer LEN_MAX_TITLE = 25;
    private Random rand = new Random();

    StringBuilder HTMLString = null;

    private static String OUPUT_DIRECTORY;
    private static String RESOURCES_FOLDER;


    public HTMLBuildMapImpl(String output_directory, String resources_directory) {
        OUPUT_DIRECTORY = output_directory;
        RESOURCES_FOLDER = resources_directory;

        // CREATE DIRECTORY IF NOT EXIST
        new File(OUPUT_DIRECTORY).mkdirs();
    }

    @Override
    public void create(Graph graph, String filename) {
        HTMLString = new StringBuilder();
        // write prologue
        prologue();

        for(Vertex n : graph.getVertices()) {
            node(n);
        }

        // write epilogue
        epilogue();

        // write buffer in a file
        BufferedWriter out = null;
        try {
            System.out.println("Written graph HTML file: " + OUPUT_DIRECTORY + filename);

            out =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUPUT_DIRECTORY + filename),"UTF-8"));
            out.append(HTMLString);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write a single node
     */
    private void node(Vertex node) {
        ArticleData data_article = (ArticleData) node.getProperty("article_data");
        LayoutData data_layout = (LayoutData) node.getProperty("layout_data");

        // container + pos
        HTMLString.append("<div class=\"article_container\" style=\"");
        HTMLString.append("position:absolute;left:");
        HTMLString.append(Utils.round(data_layout.getX(), 2));
        HTMLString.append("px;top:");
        HTMLString.append(Utils.round(data_layout.getY(), 2));
        HTMLString.append("px;z-index:");
        HTMLString.append(Utils.round(data_layout.getZ(), 2));
        HTMLString.append("\">");

        // href article
        HTMLString.append("<a href=\"");
        HTMLString.append(data_article.getUrl_article());
        HTMLString.append("\" target=\"_parent\" class=\"href-article\">");

        // thumbnail
        HTMLString.append("<img src=\"");
        HTMLString.append(data_article.getThumbnail());
        HTMLString.append("\" alt=\"");
        HTMLString.append(data_article.getTitle());
        HTMLString.append("\" class=\"thumbnail\">");
        HTMLString.append("</a>");

        // title
        HTMLString.append("<div class=\"title ");
        // random rotation
        HTMLString.append(TITLE_POSITION[rand.nextInt(4)]);
        HTMLString.append("\">");

        HTMLString.append(StringUtils.abbreviate(data_article.getTitle(),LEN_MAX_TITLE));
        HTMLString.append("</div>");

        // end container
        HTMLString.append("</div>");
    }

    /**
     * Write prologue
     */
    private void prologue() {
        String content = "";
        try {
            content = Utils.readFile(RESOURCES_FOLDER + PROLOGUE,  Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HTMLString.append(content);
    }

    /**
     * Write epilogue
     */
    private void epilogue() {
        String content = "";
        try {
            content = Utils.readFile(RESOURCES_FOLDER + EPILOGUE, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HTMLString.append(content);
    }
}
