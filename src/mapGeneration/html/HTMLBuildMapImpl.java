package mapGeneration.html;


import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import elements.ArticleData;
import elements.LayoutData;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import utils.Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import articleGeneration.HTMLBuildArticleImpl;
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
    //Patterns
    private static Pattern p_image = Pattern.compile("http://(.*)",Pattern.MULTILINE);

  static boolean verifImage(String lien){

    DefaultHttpClient client = new DefaultHttpClient();
    //la quesry correspond à l'adresse que l'on va tester
    String query = lien;
    //vérif
    //System.out.println(query);


    HttpGet httpGet = new HttpGet(query);
    HttpResponse response1 = null;
    try {
      response1 = client.execute(httpGet);
      //System.out.println(response1);
      int status = response1.getStatusLine().getStatusCode();
      if (status == 200) {
        System.out.println("STATUS : " + status);
        return true;
      }
      else {
        return false;
        //System.out.println("STATUS : " + status);
      }

    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
        /*try {
            client.close();
        } catch (IOException e) {
            System.out.println("client closing issue");
        }*/
    return false; //request did not work.

  }

  public void checkImage(String texteBrut){
    String text = texteBrut;
    //System.out.println(text);
    int compteur =0;
    try{
      Pattern p= p_image;
      String entree = text;
      Matcher m = p.matcher(entree);

      while(m.find()){
        System.out.println("FOUND");
        compteur++;
        //our chaque groupe capturé
        for (int i=0; i<= m.groupCount(); i++){
          //affichage de la sous-chaine trouvée (seuls les lien directs sont affichées
          System.out.println("Groupe " + i +  ":" + m.group(0));
          //Vérification des liens.
          //verifImage(m.group(1));
          String newFileName =  "src\\lienImages.txt";
          if (verifImage(m.group(1))) {
            try {
              BufferedWriter writer = new BufferedWriter(new FileWriter(new File(newFileName), true));
              // normalement si le fichier n'existe pas, il est crée à la racine du projet
              writer.write(m.group(1));
              writer.newLine();
              writer.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }else {
            System.out.println("Le lien ne fonctionne pas");
          }
        }//fin de for
      }//fin WHILE
    }catch(PatternSyntaxException pse){
      System.err.println("Le pattern n'a pas un format correct.");
    }
    //System.out.println("Il y a "+compteur+" images.");
  }

    public HTMLBuildMapImpl(String output_directory, String resources_directory) {
        OUPUT_DIRECTORY = output_directory;
        RESOURCES_FOLDER = resources_directory;
      /**
       * Added By cheisda on June 13th 2016
       */
     // System.out.println("it's me, Wombat! the ouptPut directory " +OUPUT_DIRECTORY );

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
/**
 * Modif à faire ici soit ajouter un ../ avant le data/articles soit dans la balise href ajouter directement
 * l'adresse complète qui mène vers l'article.
 */
        // href article
        HTMLString.append("<a href=\"");
      //modif on 06.14.2016

        HTMLString.append("http://127.0.0.1/Stage2016/Strabic-master/"+data_article.getUrl_article());
        //System.out.println("href artiche.getURL : "+  data_article.getUrl_article());

        HTMLString.append("\" target=\"_parent\" class=\"href-article\">");

        // thumbnail
        HTMLString.append("<img src=\"");

        //System.out.println(data_article.toString());
      String lienThumbnail = "http://127.0.0.1/Stage2016/Strabic-master/"+data_article.getThumbnail();
     // checkImage("http://127.0.0.1/Stage2016/Strabic-master/data/img//L-esprit-Castor-Mythe-et-realites.jpg");
      //erreur sur le lien testé :  java.lang.IllegalArgumentException: Illegal character in path at index 44: 127.0.0.1/Stage2016/Strabic-master/data/img/\L-esprit-Castor-Mythe-et-realites.jpg
        HTMLString.append(lienThumbnail);


        HTMLString.append("\" alt=\"");
        HTMLString.append(data_article.getTitle());
      //System.out.println("titre artice : " +data_article.getTitle());
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
