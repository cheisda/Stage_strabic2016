package articleGeneration;

import graphGeneration.generation.Article;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Cheisda
import java.util.regex.PatternSyntaxException;


/**
 * Implementation of a HTML builder for articles
 * @author Arno Simon
 */
public class HTMLBuildArticleImpl implements HTMLBuildArticle{

    private static final String CSS_FOLDER = "css/";
    private static final String JS_FOLDER = "js/";

    private static String OUPUT_DIRECTORY;

    // Patterns
    private static Pattern p_div1 = Pattern.compile("<div.*>",Pattern.MULTILINE);
    private static Pattern p_div2 = Pattern.compile("</div>",Pattern.MULTILINE);
    private static Pattern p_br = Pattern.compile("<br\\s?/?>",Pattern.MULTILINE);
    private static Pattern p_ytb = Pattern.compile("<youtube.*>",Pattern.MULTILINE);
    private static Pattern p_noteimage = Pattern.compile("<note-image\\d*>",Pattern.MULTILINE);
    private static Pattern p_notegallerie = Pattern.compile("<note-galerie\\d*>",Pattern.MULTILINE);
    private static Pattern p_album = Pattern.compile("<album.*>",Pattern.MULTILINE);
    private static Pattern p_notebas = Pattern.compile("\\[\\[.*\\]\\]",Pattern.MULTILINE);
    private static Pattern p_chapo = Pattern.compile("<chapo>\n?(.*?)</chapo>",Pattern.MULTILINE);
    private static Pattern p_quote = Pattern.compile("<quote>\n?(.*?)</quote>",Pattern.MULTILINE);
    private static Pattern p_poesie = Pattern.compile("<poesie>\n?(.*?)</poesie>",Pattern.MULTILINE);
    private static Pattern p_image = Pattern.compile("<image=(http://.*?)>",Pattern.MULTILINE);
    private static Pattern p_iframe = Pattern.compile("<iframe.*?src=\"(.*?)\".*?>.*?</iframe>",Pattern.MULTILINE);
    private static Pattern p_notetext = Pattern.compile("<note-texte\\|texte=(.*?)>",Pattern.MULTILINE);
    private static Pattern p_h3 = Pattern.compile("\\{\\{\\{\n?(.*?\n*.*?)\\}\\}\\}",Pattern.MULTILINE);
    private static Pattern p_gras = Pattern.compile("\\{\\{\n?(.*?\n*.*?)\\}\\}",Pattern.MULTILINE);
    private static Pattern p_italique = Pattern.compile("\\{\n?(.*?\n*.*?)\\}",Pattern.MULTILINE);
    private static Pattern p_li = Pattern.compile("^- (.*)\n",Pattern.MULTILINE);
    private static Pattern p_ul = Pattern.compile("(<li>(.*</li>\n<li>.*)*</li>)",Pattern.MULTILINE);
    private static Pattern p_color1 = Pattern.compile("\\[([a-z\\s]+)\\]",Pattern.MULTILINE);
    private static Pattern p_color2 = Pattern.compile("\\[/[a-z\\s]+\\]",Pattern.MULTILINE);
    private static Pattern p_link = Pattern.compile("\\[(.*?)(->)+(.*?)\\]",Pattern.MULTILINE);
    private static Pattern p_parag = Pattern.compile("(^[^<\n]+.+|.+[^/\n>]+$)",Pattern.MULTILINE);
    private static Pattern p_parag_b = Pattern.compile("(^<b>.*?</b>$)",Pattern.MULTILINE);
    private static Pattern p_parag_q = Pattern.compile("(^<q>.*?</q>$)",Pattern.MULTILINE);
    private static Pattern p_parag_span = Pattern.compile("(^<span class=\".*?\">.*?</span>$)",Pattern.MULTILINE);

    private static Pattern p_rem_parag_ul = Pattern.compile("<p>(</ul>)</p>",Pattern.MULTILINE);
    private static Pattern p_rem_parag_img = Pattern.compile("<p>( ?<img .*?/>)</p>",Pattern.MULTILINE);
    private static Pattern p_rem_parag_h3 = Pattern.compile("<p>(<h3>.*?</h3>)</p>",Pattern.MULTILINE);
    private static Pattern p_rem_parag_marg = Pattern.compile("<p>(<div class=\"marge\">.*</div>)</p>",Pattern.MULTILINE);


    public HTMLBuildArticleImpl(String output_directory) {
        OUPUT_DIRECTORY = output_directory;

        // CREATE DIRECTORY IF NOT EXIST
        new File(OUPUT_DIRECTORY).mkdirs();
    }

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
        //System.out.println("STATUS : " + status);
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
        //System.out.println("FOUND");
        compteur++;
        //our chaque groupe capturé
        for (int i=0; i<= m.groupCount(); i++){
          //affichage de la sous-chaine trouvée (seuls les lien directs sont affichées
           System.out.println("Groupe " + i +  ":" + m.group(1));
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


  public void create(Article article)
    {

      String text = article.getChapo() + article.getRawtext();
      String filename = article.getFilename() + ".html";
        checkImage(text);
      text = this.parser(text);
      String html = this.getHtml(article, text);
        //System.out.println(html);


        // write buffer in a file
        BufferedWriter out = null;
        String path = this.OUPUT_DIRECTORY + filename;
        try {
            out =  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
            out.append(html);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) try {
                out.close();
                //System.out.println("Written article html file: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private String parser(String text)
    {
        // Suppression des div
        text = p_div1.matcher(text).replaceAll("");
        text = p_div2.matcher(text).replaceAll("");

        // Suppression des <br>
        text = p_br.matcher(text).replaceAll("");

        // Suppression des <youtube...>
        text = p_ytb.matcher(text).replaceAll("");

        //Suppression des <note-image>
        text = p_noteimage.matcher(text).replaceAll("");

        //Suppression des <note-galerie>
        text = p_notegallerie.matcher(text).replaceAll("");

        // Suppression des <album...>
        text = p_album.matcher(text).replaceAll("");

        // Suppression des notes de bas de page [[..]]
        text = p_notebas.matcher(text).replaceAll("");

        // Chapo
        text = p_chapo.matcher(text).replaceAll("\n\n<div class=\\\"chapo\"><p>$1<\\/p><\\/div>\n");

        // Quotes
        text = p_quote.matcher(text).replaceAll("\n\n<q>$1</q>\n");

        // Poesie
        text = p_poesie.matcher(text).replaceAll("\n\n<q>$1</q>\n");

        // Images
      text = p_image.matcher(text).replaceAll("\n\n<div class=\\\"border\"><img src=\"$1\" alt=\"//\" class=\"miniature\"/></div>\n\n");


        // Iframe
        text = p_iframe.matcher(text).replaceAll("\n\n<div class=\"iframe-wrapper\"><iframe class=\"iframe-content\" src=\"$1\"></iframe><div class=\"iframe-blocker\"></div></div>\n\n");

        // <note-texte|texte=...
        text = p_notetext.matcher(text).replaceAll("<div class=\\\"marge\"><div class=\"inner\"><p>$1</p></div></div>\n\n");

        // H3 : {{{ }}}
        text = p_h3.matcher(text).replaceAll("<h3>$1</h3>");

        // Gras : {{ }}
        text = p_gras.matcher(text).replaceAll("<b>$1</b>");

        // Italique : { }
        text = p_italique.matcher(text).replaceAll("<i>$1</i>");

        // <li> : -
        text = p_li.matcher(text).replaceAll("<li><p>$1</p></li>\n");

        // Rajout des <ul> autour des <li>
        text = p_ul.matcher(text).replaceAll("<ul>\n$1\n</ul>");

        // [couleur]
        text = p_color1.matcher(text).replaceAll("<span class=\\\"$1\">");
        text = p_color2.matcher(text).replaceAll("</span>");

        // Link
        text = p_link.matcher(text).replaceAll("<a href=\\\"$3\" class=\"link\">$1</a>");

        // Paragraphes
        text = p_parag.matcher(text).replaceAll("<p>$1</p>\n\n");

        // Paragraphes autour des <b> sur une seule ligne
        text = p_parag_b.matcher(text).replaceAll("<p>$1</p>\n\n");

        // Paragraphes autour des <q> sur une seule ligne
        text = p_parag_q.matcher(text).replaceAll("<p>$1</p>\n\n");

        // Paragraphes autour des <span> sur une seule ligne
        text = p_parag_span.matcher(text).replaceAll("<p>$1</p>\n\n");

        //Supprimer les <p> des <ul>, <img>, <h3> et <div marge>
        text = p_rem_parag_ul.matcher(text).replaceAll("$1");
        text = p_rem_parag_img.matcher(text).replaceAll("$1");
        text = p_rem_parag_h3.matcher(text).replaceAll("$1");
        text = p_rem_parag_marg.matcher(text).replaceAll("$1");

        return text;
    }

    private String getHtml(Article article, String text)
    {
        // parse date
        String date = parseDate(article.getDate());

        // authors
        String authors = "";
        for (String s: article.getRawAuthors()){
            authors += s + "  ";
        }

        String html = ("<html>" +
                        "<head>" +
                            "<meta charset=\"UTF-8\">" +
                            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\">" +
                            "<title>"+article.getRawTitre()+"</title>" +
                            "<link rel=\"stylesheet\" href=\""+this.CSS_FOLDER+"style.css\" type=\"text/css\">" +
                            "<script src=\"http://code.jquery.com/jquery-1.11.2.min.js\"></script>" +
                            "<script type=\"text/javascript\" src=\""+this.JS_FOLDER+"libjs.js\"></script>" +
                        "</head>" +
                        "<body>" +
                            "<div class=\"lines\"></div>" +
                            "<div id=\"popup_img\" class=\"popup\"><img id=\"imgpopup\" class=\"imgpopup\" src=\"\"/></div>" +
                            "<div class=\"container\">" +
                                "<div class=\"top\">" +
                                    "<div class=\"header\">" +
                                        "<div class=\"author\">" + authors + " - " + date + "</div>" +
                                        "<div class=\"title\">"+article.getRawTitre()+"</div>" +
                                        "<div class=\"sub-title\">"+article.getRawSousTitre()+"</div>" +
                                    "</div>" +
                                "</div>" +
                                "<div class=\"content\">" +
                                    text +
                                "</div>" +
                            "</div>" +
                        "</body>" +
                    "</html>");

        return html;
    }

    private String parseDate(String dateStr)
    {
        SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatOut = new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH);

        String date = "";
        Date d = null;
        try {
            d = formatIn.parse(dateStr);
            date = formatOut.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
}
