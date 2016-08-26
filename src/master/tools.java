package master;

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
 * Cette classe sert de boite à outils pour les fonctions utilisées dans le projet
 */
public class tools {

  //Patterns
  private static Pattern p_image = Pattern.compile("(http://.*?)",Pattern.MULTILINE);

    public static boolean verifImage(String lien){

        DefaultHttpClient client = new DefaultHttpClient();
        String query = lien;

        HttpGet httpGet = new HttpGet(query);
        HttpResponse response1 = null;
        try {
            response1 = client.execute(httpGet);
            int status = response1.getStatusLine().getStatusCode();
            if (status == 200) {
                return true;
            }
            else {
                //si le lien n'est pas le bon, on le remplace
                //replaceByDefault(lien);
                return false;
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false; //request did not work.
    }

  public void checkImage(String texteBrut){
    String text = texteBrut;
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
          String newFileName =  "src\\lienImagesNew.txt";
              if (!verifImage(m.group(0))) {
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
  }

  public static String  replaceByDefault(String lienDefectueux){
    String lienDefaut = "src\\pikachu.png\"";
      String lienRecu= lienDefectueux;
      lienRecu=lienDefaut;
      System.out.println("c'est fait !!!"+lienRecu);
      return lienRecu;
  }


}
