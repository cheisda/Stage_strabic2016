import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Cheisda on 19/07/2016.
 */
public class tools {

  //Patterns
  private static Pattern p_image = Pattern.compile("(http://.*?)",Pattern.MULTILINE);

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
          // System.out.println("Groupe " + i +  ":" + m.group(1));
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





}
