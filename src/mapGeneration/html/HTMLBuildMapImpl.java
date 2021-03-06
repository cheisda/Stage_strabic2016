package mapGeneration.html;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import elements.ArticleData;
import elements.LayoutData;
import master.StrabicLog;
import master.tools;
import org.apache.commons.lang.StringUtils;
import utils.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
    //pattern
    private static Pattern p_image = Pattern.compile("http://(.*)",Pattern.MULTILINE);
    //création d'un objet LOGGER
    private final static Logger logBuilMapImpl = Logger.getLogger(HTMLBuildMapImpl.class.getName());



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
        HTMLString.append("http://strabic.irisa.fr/"+data_article.getUrl_article());
        HTMLString.append("\" target=\"_parent\" class=\"href-article\">");

        // thumbnail
        /**
         * Vérification des liens des thumbnail lors de la génération
         * Si les liens sont défectueux, ils sont inscrit dans un fichier LOG, et une image par défaut y est placée
         * (pikachu)
         */
        HTMLString.append("<img src=\"");
        String lienThumbnail ="http://strabic.irisa.fr/"+data_article.getThumbnail().replace("\\", "/");
        HTMLString.append(checkImage(lienThumbnail, p_image));
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


    /**
     * Added By cheisda
     * Fonction qui vérifie si le thumbnail existe bien.
     * Si ce n'est pas le cas, elle inscrit le lien dans un LOG et remplace l'image par pikachu (defaut)
     */

    public static String checkImage(String texteBrut, Pattern pattern){

        String text = texteBrut;
        String resultat ="";
        try{
            Pattern p= pattern;
            String entree = text;
            Matcher m = p.matcher(entree);
            String lienPika = "http://strabic.irisa.fr/maps/pikachu.png";

            while(m.find()){
                //System.out.println("FOUND");
                for (int i=0; i<= m.groupCount(); i++){
                    if (!tools.verifImage(m.group(0))){
                        StrabicLog.init();
                        logBuilMapImpl.log(Level.WARNING,m.group(0));

                        //changer le lien par l'image Pikachu
                        resultat =  m.group(0).replaceAll("http://(.*)", lienPika);

                    }else {
                        resultat = m.group(0);
                    }
                    return resultat;
                }//fin de for
            }//fin WHILE
            return resultat;
        }catch(PatternSyntaxException pse){
            System.err.println("Le pattern n'a pas un format correct.");
        }
        System.out.println("le text en entrée après passage par vérif image : "+text);
       return resultat;
    }
}
