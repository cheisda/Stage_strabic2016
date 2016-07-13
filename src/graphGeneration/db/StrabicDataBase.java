package graphGeneration.db;
import graphGeneration.UI.ArticleFrame;
import graphGeneration.generation.Article;
import graphGeneration.generation.GenGraphs;
import graphGeneration.generation.IndexedEntry;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



//cheisda
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

//EndOfCheisda

//CREATE TABLE spip_articles (
//		id_article INTEGER NOT NULL ,
//		surtitre text NOT NULL DEFAULT '' COLLATE NOCASE,
//		titre text NOT NULL DEFAULT '' COLLATE NOCASE,
//		soustitre text NOT NULL DEFAULT '' COLLATE NOCASE,
//		id_rubrique bigint(21) NOT NULL DEFAULT '0',
//		descriptif text NOT NULL DEFAULT '' COLLATE NOCASE,
//		chapo mediumtext NOT NULL DEFAULT '' COLLATE NOCASE,
//		texte longtext NOT NULL DEFAULT '' COLLATE NOCASE,
//		ps mediumtext NOT NULL DEFAULT '' COLLATE NOCASE,
//		date datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
//		statut varchar(10) NOT NULL DEFAULT '0' COLLATE NOCASE,
//		id_secteur bigint(21) NOT NULL DEFAULT '0',
//		maj timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
//		export varchar(10) DEFAULT 'oui' COLLATE NOCASE,
//		date_redac datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
//		visites int(11) NOT NULL DEFAULT '0',
//		referers int(11) NOT NULL DEFAULT '0',
//		popularite double NOT NULL DEFAULT '0',
//		accepter_forum char(3) NOT NULL DEFAULT '' COLLATE NOCASE,
//		date_modif datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
//		lang varchar(10) NOT NULL DEFAULT '' COLLATE NOCASE,
//		langue_choisie varchar(3) DEFAULT 'non' COLLATE NOCASE,
//		id_trad bigint(21) NOT NULL DEFAULT '0',
//		extra longtext COLLATE NOCASE,
//		id_version int UNSIGNED NOT NULL DEFAULT '0',
//		nom_site tinytext NOT NULL DEFAULT '' COLLATE NOCASE,
//		url_site varchar(255) NOT NULL DEFAULT '' COLLATE NOCASE,
//		lesauteurs text NOT NULL DEFAULT '' COLLATE NOCASE,
//		basdepage text NOT NULL DEFAULT '' COLLATE NOCASE,
//		licenses text NOT NULL DEFAULT '' COLLATE NOCASE,
//		couleur_titre text NOT NULL DEFAULT '' COLLATE NOCASE,
//		couleur_une text NOT NULL DEFAULT '' COLLATE NOCASE,
//		couleur_liens text NOT NULL DEFAULT '' COLLATE NOCASE,
//		couleur_fond text NOT NULL DEFAULT '' COLLATE NOCASE,
//		couleur_texte text NOT NULL DEFAULT '' COLLATE NOCASE,
//		type_article text NOT NULL DEFAULT '' COLLATE NOCASE,
//		type_une text NOT NULL DEFAULT '' COLLATE NOCASE,
//		image_une text NOT NULL DEFAULT '' COLLATE NOCASE,
//		une_repeat text NOT NULL DEFAULT '' COLLATE NOCASE,
//		virtuel varchar(255) NOT NULL DEFAULT '' COLLATE NOCASE,
//		PRIMARY KEY (id_article));



// voir http://www.tutorialspoint.com/sqlite/sqlite_java.htm
public class StrabicDataBase {
	final static int maxEntry = 10000;
	static String[] saisons = new String[maxEntry];
	static String[] auteurs = new String[maxEntry];
	static String[] urls = new String[maxEntry];
	static String[] urlsEnd = new String[maxEntry];
	static String[] urlsSaison = new String[maxEntry];
	static String[] documents = new String[maxEntry];
	static Pattern pattern = Pattern.compile("([0-9])+\\]");
	static Pattern patternInteger = Pattern.compile("([0-9])+");
	static int breveNumber = -1;
	static int livreNumber = -1;

	public static void importDB() {
		Connection c = null;
		Statement stmt = null;

		File f = new File(GenGraphs.strabicDBPath);
		//if(!f.exists() ||f.isDirectory()) {
		//	strabic.strabicDBPath = MainFrame.selectFile(strabic.articlegui, false, "Give the DB File");
		//}

		for (int k=0;k<maxEntry;k++){
			saisons[k]="pas_de_saison";
			auteurs[k]="pas_d_auteur";
			urls[k]="pas_d_url";
			urlsEnd[k]="pas_d_url";
			urlsSaison[k]="pas_d_url";
			documents[k]="txt/pas_doc";
		}
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:"+ GenGraphs.strabicDBPath);

		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		System.out.println("Opened database successfully");
		try {
			///
			stmt = c.createStatement();
			//ResultSet rs = stmt.executeQuery( "SELECT * FROM spip_auteurs;" );
			// SAISONS = spip_rubriques;
			ResultSet rs = stmt.executeQuery( "SELECT * FROM spip_rubriques;" );
			int i=0;
      //added by cheisda
     // System.out.println("resultSet : "+rs);
			while ( rs.next() ) {
				if (i >= maxEntry) break;
				//System.out.println("----------------------------------------------------------------------------------");
				//System.out.println("----------------------------------" + i + "--------------------------------------------");
				//System.out.println("----------------------------------------------------------------------------------");
				int id_rubrique  = rs.getInt("id_rubrique");
        /**
         * 07.062016la ligne de code suivante était en commentaire
         * System.out.println("id_rubrique :" + id_rubrique);
         */
				//System.out.println("id_rubrique :" + id_rubrique);

				String  titre = rs.getString("titre");
							//	System.out.println("titre :" + titre);
				if ("brèves".equals(titre)) breveNumber = id_rubrique;
				if ("livres".equals(titre)) livreNumber = id_rubrique;
				if ((id_rubrique >=0) && (id_rubrique < maxEntry)){
					saisons[id_rubrique] = titre;
				}
				i++;
			}
			rs.close();
			stmt.close();

			stmt = c.createStatement();
			//ResultSet rs = stmt.executeQuery( "SELECT * FROM spip_auteurs;" );
			// SAISONS = spip_rubriques;
			rs = stmt.executeQuery( "SELECT * FROM spip_auteurs;" );
			i=0;
			while ( rs.next() ) {
				if (i >= maxEntry) break;

				int id_auteur  = rs.getInt("id_auteur");
				//System.out.println("id_auteur :" + id_auteur);

				String  nom = rs.getString("nom");
				//System.out.println("nom :" + nom);
				if ((id_auteur >=0) && (id_auteur < maxEntry)){
					auteurs[id_auteur] = nom;
				}
				i++;
			}
			rs.close();
			stmt.close();
			//spip_documents
			stmt = c.createStatement();
			rs = stmt.executeQuery( "SELECT * FROM spip_documents;" );
			i=0;
			while ( rs.next() ) {
				//				fichier :jpg/freinet-grimault3.jpg
				//				fichier :jpg/freinet-grimault2.jpg
				//				fichier :jpg/freinet-grimault4.jpg
				//				fichier :jpg/freinet-limographe.jpg

				int id_document  = rs.getInt("id_document");
				String fichier = rs.getString("fichier");
				//System.out.println("fichier :" + fichier +" ("+id_document+")");
				if ((id_document >=0) && (id_document < maxEntry)){
					//String[] ss =fichier.split("/");
					//documents[id_document] = ss[ss.length-1];

          /**
           * construction d'un tableau qui récupère tous les éléments dont nous avons besoins pour créer les pages HTML
           */
					documents[id_document] = GenGraphs.urlImage+fichier;
				}
				//				int id_objet  = rs.getInt("id_objet");
				//				System.out.println("id_objet :" + id_objet);
				//				if ((id_objet >=0) && (id_objet < maxEntry)){
				//					urls[id_objet] = "http://strabic.fr/"+url;
				//				}
			}
			//spip_urls
			stmt = c.createStatement();
			rs = stmt.executeQuery( "SELECT * FROM spip_urls;" );
			i=0;
			while ( rs.next() ) {
				String url = rs.getString("url");
				String utype = rs.getString("type");
				//				if ("rubrique".equals(utype)) {
				//					System.out.println("url"+" (" +utype + "):" + urlStrabic+url );
				//				}
				int id_objet  = rs.getInt("id_objet");
				//System.out.println("id_objet :" + id_objet);
				if ((id_objet >=0) && (id_objet < maxEntry)){
					if ("article".equals(utype)){
						urlsEnd[id_objet] = url;
						urls[id_objet] = GenGraphs.urlStrabic+url;
					}
					if ("rubrique".equals(utype)){
						urlsSaison[id_objet] = GenGraphs.urlStrabic+url;
					}
				}
			}
			/// get the article
			stmt = c.createStatement();
			rs = stmt.executeQuery( "SELECT * FROM spip_articles;" );
			i=0;
			while ( rs.next() ) {
				String saison = "pas_de_saison";
				if (i == maxEntry) return; // for debug
				//				System.out.println("----------------------------------------------------------------------------------");
				//				System.out.println("----------------------------------" + i++ + "--------------------------------------------");
				//				System.out.println("----------------------------------------------------------------------------------");

				//				image_une
				String  image_une = rs.getString("image_une");
       // System.out.println("image_une :" + image_une);
       // System.out.println("un truc : " + rs.getString(6));


				//int id_article  = rs.getInt(1);
				int id_article  = rs.getInt("id_article");
				//System.out.println("id_article :" + id_article);

				int id_rubrique  = rs.getInt("id_rubrique");
				//System.out.println("id_rubrique :" + id_rubrique);

				int id_secteur  = rs.getInt("id_secteur");
				//System.out.println("id_secteur :" + id_secteur);

				String  statut = rs.getString("statut");
				//System.out.println("statut :" + statut);



				if (!("publie".equals(statut))) continue;
				if (1 != id_secteur) continue; // if not an article skip.

				//				if (breveNumber == id_rubrique) continue; // let's skip the breves.
				//				if (livreNumber == id_rubrique) continue; // let's skip the books.

				if ((id_rubrique >=0) && (id_rubrique < maxEntry)){
					saison = saisons[id_rubrique];
				}

				String  surtitre = rs.getString("surtitre");
				String  titre = rs.getString("titre");
				String rtitre = titre;
				titre = html2text(titre);
				//System.out.println("titre :" + titre);

				String chapo = rs.getString("chapo");

				String soustitre = rs.getString("soustitre");
				//				System.out.println("soustitre: " +soustitre);

				/*String type_article = rs.getString("type_article"); //addedby Cheisda
        System.out.println("type_article: " + type_article);*/

				String texte = null;
				String rawtexte = rs.getString("texte");
				if (rawtexte.isEmpty()) continue;
				texte = html2text(rawtexte);
				//				 System.out.println("texte: " + texte);

				//				 String nom_site = rs.getString("nom_site");
				//				 System.out.println("nom_site: " + nom_site);

				String date = rs.getString("date");
				//				 System.out.println("date: " + date);

				String lesauteurs =  rs.getString("lesauteurs");
				//System.out.println("lesauteurs: " + lesauteurs);
				List<Integer> listMatches =findAuteurId(lesauteurs);
				// if (listMatches == null) continue;
				// if (listMatches.isEmpty()) continue;
				Article no = GenGraphs.getAllEntries().searchArticle(titre);
				if (no == null){
					no = new Article();
					GenGraphs.getAllEntries().getArticlelist().add(no);
					no.setSaison(saison);
					no.setTitre(titre);
					no.setRawSousTitre(soustitre);
					no.setRawSurTitre(surtitre);
					no.setDate(date);
					no.setRawTitre(rtitre);
					no.setRawChapo(chapo);
					no.setChapo("<chapo>" + chapo.replace("\n"," ")+ "</chapo>");
					no.setText(texte);
					no.setRawtext(solveReferences(rawtexte));
					if ((id_article >=0) && (id_article < maxEntry)){
						no.setURL(urls[id_article]);
						no.setURLend(urlsEnd[id_article]);

						// build thumbnail name
						String urlImage = no.getURLend().trim().replace('/', '_');
						no.setFilename(urlImage);
						no.setThumbnail(GenGraphs.getThumbBaseurl() + urlImage + GenGraphs.thumbExtension);
					}
					if ((id_rubrique >=0) && (id_rubrique < maxEntry)){
						no.setUrlSaison(urlsSaison[id_rubrique]);

						// add url season in the list
						if( !GenGraphs.getUrlSeasonsList().contains(urlsSaison[id_rubrique]) )
						{
							GenGraphs.getUrlSeasonsList().add(urlsSaison[id_rubrique]);
						}

						//System.out.println("Article id: "+id_article+" Rubrique id: "+ id_rubrique);
						//System.out.println("ARTICLE: "+urls[id_article]+" RUBRIQUE: "+ urlsSaison[id_rubrique]);
					}
					if (listMatches != null){
						for(int s : listMatches){
							//System.out.println(s);
							if ((s >=0) && (s < maxEntry)){
								//System.out.println(auteurs[s].replace(" ","_"));
								no.getRawAuthors().add(auteurs[s]);
								no.getKeywords().add(IndexedEntry.getPrefixURI()+auteurs[s].replace(" ","_"));
							}
						}
					}
					no.setNeedUpdate(true);
				} else {
					//System.out.println("Already exist: "+titre);
				}
			}
      /**ajout méthode au  june 14 2016
      //Vérifie que les images soient bien présentes, que ce soit en local ou sur internet**/
      //checkImage(c, stmt);


/*
      Statement st = c.createStatement();
      String query = "SELECT * FROM ";
      ResultSet rsCheisda = st.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();

      int columnsNumber = rsmd.getColumnCount();
*/

			rs.close();
			stmt.close();
			if (GenGraphs.gui){
				ArticleFrame.setChoiceList(GenGraphs.articlegui.articleList);
				if (i>0)
					GenGraphs.articlegui.articleList.select(0);
			}
		///
		c.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
} //fin importBD

	public static String html2text(String html) {
	return Jsoup.parse(html).text();
}

	static List<Integer> findAuteurId(String str){
	Matcher matcher = pattern.matcher(str);
	List<Integer> listMatches = new ArrayList<Integer>();

	while(matcher.find()){
		String m  = matcher.group();
		if (m != null){
			int v = 0;
			m = m.substring(0, m.length()-1); //of the form 8898]
			//System.out.println(m);
			try {
				v = Integer.parseInt(m);
				listMatches.add(v);
			}  catch ( NumberFormatException e ) {

			}
		}
	}
	return listMatches;
}

    static int findAuteurImageId(String str){
	if (str == null) return -1;
	Matcher matcher = patternInteger.matcher(str);
	while(matcher.find()){
		String m  = matcher.group();
		if (m != null){
			int v = 0;
			m = m.substring(0, m.length()); //of the form 8898
			//System.out.println(m);
			try {
				v = Integer.parseInt(m);
				//System.out.println("m: " + m+" v:" +v);
				return v;
			}  catch ( NumberFormatException e ) {

			}
		}
	}
	return -1;
}

    static String solveReferences(String in){
	String out = "";
	String ref = "";
	char ina[] = in.toCharArray();
	Boolean inreference = false;
	for( char c: ina){
		if (c== '<'){
			inreference = true;
			ref = "";
		} else if (inreference && (c=='>')){
			inreference = false;
			if (ref.startsWith("im") || ref.startsWith("media")){
				int id = findAuteurImageId(ref);
				//System.out.println("Looking reference: " + ref +" ("+id+")");
				if ((id >=0) && (id < maxEntry)){
					//System.out.println(documents[id]);
					out = out + "<image=" +documents[id] + ">";
				}
			} else {
				out = out + "<" + ref + ">";
			}
		} else if (!inreference){
			out = out + c;
		} else if (inreference){
			ref = ref+c;
		}
	}
	return out;
}
/**
  public static void checkImage(Connection c,Statement stmt){
    try {
      stmt = c.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM spip_documents;" );
      String resultat = rs.toString();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

/**
  public static Boolean lookImageThumbs(String image){

    CloseableHttpClient client = new DefaultHttpClient();
    String query = url +"/thumb_" + image;
    System.out.println(query);

    HttpGet httpGet = new HttpGet(query);
    CloseableHttpResponse response1 = null;
    try {
      response1 = client.execute(httpGet);
      System.out.println(response1);
      int status = response1.getStatusLine().getStatusCode();
      if (status == 200)
        return true;
      else
        return false;

    } catch (ClientProtocolException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return false; //request did not work.
  }*/
}//fin de classe
