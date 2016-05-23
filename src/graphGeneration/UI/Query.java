package graphGeneration.UI;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Query {

	public Query(){
//		later
	}

	public Document get(String url){
		Document doc;
		try {
			//doc = Jsoup.connect("http://en.wikipedia.org/").get();
			doc = Jsoup.connect(url).get();
			String title = doc.title();
			System.out.println("Titre document URL: "+ title);
		} catch (IOException e) {
			System.out.println("Echec chargement");
			return null;
		}
		//Elements newsHeadlines = doc.select("#mp-itn b a");
		return doc;
	}
}