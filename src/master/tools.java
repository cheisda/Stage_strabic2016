package master;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
/**
 * Created by Cheisda on 19/07/2016.
 * Cette classe sert de boite à outils pour les fonctions utilisées dans le projet
 */
public class tools {

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
}
