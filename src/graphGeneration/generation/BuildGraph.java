package graphGeneration.generation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLTokens;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import elements.ArticleData;
import elements.LayoutData;
import graphGeneration.analyse.FileAnalysisResult;
import graphGeneration.analyse.PdfTextAnalysis;
import graphGeneration.generation.Article;
import graphGeneration.generation.GenGraphs;

/**
 * Build graphs based on analysis results and export them to graphml files
 */
public class BuildGraph {
	private static Graph keywordsGraph = new TinkerGraph();
	private final static String keywordsGraph_Prefix = "_KW";

	private static Graph saisonGraph = new TinkerGraph();
	private final static String saisonGraph_Prefix = "_SAISON";

	private static Graph auteurGraph = new TinkerGraph();
	private final static String auteurGraph_Prefix = "_AUTEUR";

	private static final int scoreMinKW = 1;

    private BuildGraph() {}

	public static void build(String filepathsmane){
		int nbe = 0;
		System.out.println("BuildGraph number of files: " + PdfTextAnalysis.getResults().size());

		//Building graph based on keywords
		for (FileAnalysisResult r1 : PdfTextAnalysis.getResults()){
			Vertex node1 = getKeywordsGraph().addVertex(r1);
			Vertex node2 = getSaisonGraph().addVertex(r1);
			Vertex node3 = getAuteurGraph().addVertex(r1);
			String na = r1.getFilePath();
			String[] ns = na.split("/");
			Article art  = GenGraphs.getAllEntries().searchArticle(na);
			String thumbnail="";
			String titre="";
			String auteur="";
			String url="";
			String urlSaison="";
			String filename ="";
			if (art != null){
                thumbnail = art.getThumbnail();
				titre = art.getTitre().replace('_',' ');
				url = GenGraphs.getArticlesBaseurl() + art.getURLend() + ".html";
				urlSaison = art.getUrlSaison();
				filename = art.getFilename();
				for (String t: art.getKeywords()){
					String[] ns1 = t.split("#");
					if ("".equals(auteur)){
						auteur = ns1[ns1.length - 1].replace('_',' ');
					} else {
						auteur = auteur + ", " + ns1[ns1.length - 1].replace('_',' ');
					}
				}
			}


            ArticleData art_data1 = new ArticleData(ns[ns.length - 1], titre, auteur, url, urlSaison, thumbnail, filename);
            LayoutData lay_data1 = new LayoutData(0,0,10);
            node1.setProperty("article_data", art_data1);
            node1.setProperty("layout_data", lay_data1);

            ArticleData art_data2 = new ArticleData(ns[ns.length - 1], titre, auteur, url, urlSaison, thumbnail, filename);
            LayoutData lay_data2 = new LayoutData(0,0,10);
            node2.setProperty("article_data", art_data2);
            node2.setProperty("layout_data", lay_data2);

            ArticleData art_data3 = new ArticleData(ns[ns.length - 1], titre, auteur, url, urlSaison, thumbnail, filename);
            LayoutData lay_data3 = new LayoutData(0,0,10);
            node3.setProperty("article_data", art_data3);
            node3.setProperty("layout_data", lay_data3);

			//System.out.println("Adding node: " + ns[ns.length - 1] + " " + thumbnail);
		}
		nbe = 0;
		//Concept
		if (GenGraphs.gui){
			for (int i = 0; i< PdfTextAnalysis.getResults().size(); i++){
				FileAnalysisResult r1 = PdfTextAnalysis.getResult(i);
				for (int j = i+1; j< PdfTextAnalysis.getResults().size(); j++){
					FileAnalysisResult r2 = PdfTextAnalysis.getResult(j);
					int score = r1.countProximity(r2) ;
					if (score >= scoreMinKW){
						Vertex n1 = getKeywordsGraph().getVertex(r1);
						Vertex n2 = getKeywordsGraph().getVertex(r2);
						Edge e1 = getKeywordsGraph().addEdge(null,n1, n2, Integer.toString(score));
                        //Edge e2 = getKeywordsGraph().addEdge(null,n2, n1, Integer.toString(score));
                        ArticleData a1 = (ArticleData) n1.getProperty("article_data");
                        ArticleData a2 = (ArticleData) n2.getProperty("article_data");
						//System.out.println("Adding edge between: " + a1.getName() + " --> " + a2.getName() + " val : " + score  );
						nbe++;
					}
				}
			}
			System.out.println("Concept number of edges added: "+ nbe);
		}
		//Auteur
		nbe = 0;
		for (int i = 0; i< PdfTextAnalysis.getResults().size(); i++){
			FileAnalysisResult r1 = PdfTextAnalysis.getResult(i);
			if (r1 == null) continue;
			for (int j = i+1; j< PdfTextAnalysis.getResults().size(); j++){
				FileAnalysisResult r2 = PdfTextAnalysis.getResult(j);
				if (r2 == null) continue;
				int score = r1.getArticleRes().countProximity(r2.getArticleRes()) ;
				if (score !=0){
					Vertex n1 = getAuteurGraph().getVertex(r1);
					Vertex n2 = getAuteurGraph().getVertex(r2);
					Edge e1 = getAuteurGraph().addEdge(null,n1, n2, Integer.toString(score));
					nbe++;
				}
			}
		}
		System.out.println("Auteur number of edges added: "+ nbe);
		//Saison
		nbe = 0;
		for (int i = 0; i< PdfTextAnalysis.getResults().size(); i++){
			FileAnalysisResult r1 = PdfTextAnalysis.getResult(i);
			for (int j = i+1; j< PdfTextAnalysis.getResults().size(); j++){
				FileAnalysisResult r2 = PdfTextAnalysis.getResult(j);
				if (r1.getArticleRes().getSaison().equals(r2.getArticleRes().getSaison())){
					Vertex n1 = getSaisonGraph().getVertex(r1);
					Vertex n2 = getSaisonGraph().getVertex(r2);
					Edge e1 = getSaisonGraph().addEdge(null,n1, n2, "");
					nbe++;
				}
			}
		}
		System.out.println("Saison number of edges added: "+ nbe);
		String nname =  filepathsmane.replaceAll(".graphml", keywordsGraph_Prefix+".graphml");
		try {
			writeXMLGraph(getKeywordsGraph(),nname);
			System.out.println("Written graph file: "+nname);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		nname =  filepathsmane.replaceAll(".graphml", saisonGraph_Prefix+".graphml");
		try {
			writeXMLGraph(getSaisonGraph(),nname);
			System.out.println("Written graph file: "+nname);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		nname =  filepathsmane.replaceAll(".graphml", auteurGraph_Prefix+".graphml");
		try {
			writeXMLGraph(getAuteurGraph(),nname);
			System.out.println("Written graph file: "+nname);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static void writeXMLGraph(Graph g, String filepathsmane) throws IOException{
		OutputStream so = null;
		File f = new File(filepathsmane);
		// create directories if not exist
		so = new FileOutputStream(f);
		GraphMLWriter writer = new GraphMLWriter(g);
		// used only to speedup output...
		Map<String, String> vertexKeyTypes = new HashMap<String, String>();
		vertexKeyTypes.put("name", GraphMLTokens.STRING);
		writer.setVertexKeyTypes(vertexKeyTypes);
		//write the graph...
		GraphMLWriter.outputGraph(g, so);
		so.close();
	}

	protected static Graph getKeywordsGraph() {
		return keywordsGraph;
	}

    protected static Graph getSaisonGraph() {
		return saisonGraph;
	}

	protected static Graph getAuteurGraph() {
		return auteurGraph;
	}
}
