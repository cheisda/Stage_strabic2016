package graphGeneration.generation;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.tinkerpop.blueprints.Graph;
import graphGeneration.UI.ArticleFrame;
import graphGeneration.UI.HtmlFrame;
import graphGeneration.UI.MainFrame;
import graphGeneration.analyse.PdfTextAnalysis;
import graphGeneration.db.StrabicDataBase;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GenGraphs {
	static public final boolean gui = false;
	static private final boolean debug = true;

	static public final String urlStrabic = "http://strabic.fr/";
    static public final String urlImage = urlStrabic + "IMG/";
    static public final String thumbExtension = ".jpg";
	static public final String folderDataTMP = "data/tmp/";
	static private String skosFile = folderDataTMP + "temp.skos";
	static private String storageFile = folderDataTMP + "temp.data";
	static private String articleFile = folderDataTMP + "temp.article";
	static final private String storageFileOut = folderDataTMP + "storageOut.xml";
	static final private String graphFile = folderDataTMP + "graphOut.graphml"; //must have a graphml file extension
	static final private String articleTxtPath = "data/articles/";

	static private List<String> seasonsList = null;
	static public String thumbBaseurl = null;
	static public String strabicDBPath = null;
	static private String seasonsFilePath = null;
	static private String articles_baseurl = null;

	static boolean validate = false;
	static private Document skosTree = null;
	static private Node skosRoot = null;
	static private Document storageTree = null;
	static private Node storageRoot = null;
	static private Document articleTree = null;
	static private Node articleRoot = null;
	static private GlobalEntriesList allEntries = null;
	static private Boolean skosFileNeedSaving = false;
	static private Boolean storageFileNeedSaving = false;
	final static Runtime rt = Runtime.getRuntime();

	static MainFrame window = null;
	public static ArticleFrame articlegui = null;
	static HtmlFrame htmlgui = null;
	static int numBackupVersion = 0;

    /**
     * - Load the database<br>
     * - Write articles in files<br>
     * - Analyse articles<br>
     * - Generate graphML files
	 * @param db_path path of the strabic database (.sqlite)
	 * @param seasons_file_directory name of the directory which contains seasons' url file
	 * @param article_baseurl baseurl for all articles
	 * @param thumb_baseurl baseurl for all thumbnails
	 * @param writeArticleFiles true if articles must be written in txt files
     */
    public static void execute(String db_path, String seasons_file_directory, String article_baseurl, String thumb_baseurl, boolean writeArticleFiles) {
		thumbBaseurl = thumb_baseurl;
		strabicDBPath = db_path;
		seasonsFilePath = seasons_file_directory + "seasons.txt";
		articles_baseurl = article_baseurl;

		// CREATE DIRECTORIES IF NOT EXIST
		new File(folderDataTMP).mkdirs();

		// CREATE DIRECTORIES IF NOT EXIST
		new File(seasons_file_directory).mkdirs();

        // LOAD THE DATA BASE
        allEntries = new GlobalEntriesList();
		seasonsList = new ArrayList<String>();
        if (!GenGraphs.gui){
            StrabicDataBase.importDB();
            System.out.println("Imported "+ GenGraphs.getAllEntries().getArticlelist().size() +" articles");

            // WRITE ARTICLES IN FILES
			if(writeArticleFiles)
			{
				if (GenGraphs.getAllEntries().getArticlelist().size() > 0){
					for (Article a: GenGraphs.getAllEntries().getArticlelist()){
						String path = articleTxtPath + a.getFilename() + ".txt";
						System.out.println("Writing file: "+path);
						a.writeInFile(path);
					}
				}
			}

            //ANALYSE THE FILE NOW
            for(Article no: GenGraphs.getAllEntries().getArticlelist()){
                no.analyseConcept();
            }

			//WRITE SEASONS FILE
			writeUrlSeasonsFile();

            //NOW GENERATE THE GRAPH FILE
            GenGraphs.generateGraph(graphFile);
        } else {
            window = new MainFrame();
            articlegui = new ArticleFrame();
            htmlgui = new HtmlFrame();
        }
    }

	/**
	 * Write url seasons in a file
	 */
	private static void writeUrlSeasonsFile() {
		File file = new File(seasonsFilePath);
		FileOutputStream is = null;
		PrintStream outf = null;

		try {
			is = new FileOutputStream(file);
			outf = new PrintStream(is, true, "UTF-8");
			for(String urlSeason : getUrlSeasonsList())
			{
				outf.println(urlSeason);
			}
			System.out.println("Written url seasons file: data/tmp/seasons.txt");
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int getBackupVersionNumber(){
		numBackupVersion++;
		return numBackupVersion;
	}

	public static void generateGraph(String path){
		BuildGraph.build(path);
	}

    // Graph accessors
    /**
     * Get "SaisonGraph"
     * @return tinkerpop graph
     */
    public static Graph getSaisonGraph() {
        return BuildGraph.getSaisonGraph();
    }

    /**
     * Get "AuteurGraph"
     * @return tinkerpop graph
     */
    public static Graph getAuteurGraph() {
        return BuildGraph.getAuteurGraph();
    }

    /**
     * Get "KeywordsGraph"
     * @return tinkerpop graph
     */
    public static Graph getKeywordsGraph() {
        return BuildGraph.getKeywordsGraph();
    }

	public static String checkXMLFile(){
		return getAllEntries().check();
	}
	
	public static String doPdfAnalysis(String filename){
		String result = "No file given";
		if (filename == null) return result;
		
		File[] files = new File(filename).listFiles();
		if (files.length > 0){
			result = "";
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.println("Directory: " + file.getName());
				} else {
					String p = file.getPath();
					System.out.println("File: " + p);
					if (p.endsWith(".pdf") || p.endsWith(".txt") || p.endsWith(".html")) {
						PdfTextAnalysis analysis  = new PdfTextAnalysis(p, false);
						analysis.searchKeywordInText();
						result = result + p  + "\n";
						if (PdfTextAnalysis.nbResults() > 0)
							PdfTextAnalysis.getResult(PdfTextAnalysis.nbResults()-1).show();
					}
				}
			}
		} else {
			PdfTextAnalysis analysis  = new PdfTextAnalysis(filename, false);
			result = analysis.searchKeywordInText();
		}
		return result;
	}
	
	public static void builConceptandDataList(){
		NodeList nodes = null;
		XPath xPath = XPathFactory.newInstance().newXPath();
		if (skosTree == null) return;
		setSkosRoot(skosTree.getDocumentElement());
		try {
			nodes = (NodeList)xPath.evaluate("/RDF/Concept",
					skosTree.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		int i = nodes.getLength()-1;
		while (i >= 0) {
			Node n = nodes.item(i);
			Attr attrType = (Attr) n.getAttributes().getNamedItem("rdf:about");
			if ( attrType != null){
				getAllEntries().add(new IndexedEntry(attrType.getValue(),n));
			}
			i--;
		}
		nodes = null;
		xPath = XPathFactory.newInstance().newXPath();
		if (storageTree == null) return;
		setStorageRoot(storageTree.getDocumentElement());
		try {
			nodes = (NodeList)xPath.evaluate("/xml/data",
					storageTree.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		i = nodes.getLength()-1;
		while (i >= 0) {
			Node n = nodes.item(i);
			Attr attrType = (Attr) n.getAttributes().getNamedItem("uri");
			if ( attrType != null){
				getAllEntries().add(new IndexedEntry(attrType.getValue(),n));
			}
			i--;
		}
		Collections.sort(getAllEntries().getGlobalist(), IndexedEntry.entryNameComparator);
	}
	
	public static void builArticlessList(){
		NodeList nodes = null;
		XPath xPath = null;
		int i;
		nodes = null;
		System.out.println("In builArtistsList-1");
		xPath = XPathFactory.newInstance().newXPath();
		if (articleTree == null) return;
		//System.out.println("In builArtistsList-2");
		setArticleRoot(articleTree.getDocumentElement());
		try {
			nodes = (NodeList)xPath.evaluate("/xml/article",
					articleTree.getDocumentElement(), XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		i = nodes.getLength()-1;
		//System.out.println("In builArtistsList-3 nb artistes: "+i);
		while (i >= 0) {
			Node n = nodes.item(i);
			Article art = new Article(n);
			getAllEntries().add(art);
			System.out.println(art.toString());
			i--;
		}
	}

	public static Document buildXMLTree(String filepathsmane){
		Document atree = null;
		InputStream sinput = null;
		File f = new File(filepathsmane);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		dbf.setValidating(validate);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringElementContentWhitespace(true);
		try {
			sinput = new FileInputStream(f);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		try {
			atree = builder.parse(new InputSource(sinput));
			sinput.close();
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return atree;
	}

	public static void writeXMLTreeButtonActionStorage(String filepath){
		// update the data file
		//storageFileOut
		writeXMLTree(filepath,storageTree);
	}

	public static void writeXMLTreeButtonActionSkos(String filepath){
		// update the data file
		//storageFileOut
		writeXMLTree(filepath,skosTree);
	}
	
	public static void writeXMLTreeButtonAction(){
		// update the data file
		//storageFileOut
		writeXMLTree(storageFileOut,storageTree);
	}

	public static void writeXMLTree(String filepathsmane, Document atree){
		OutputStream soutput = null;
		File f = new File(filepathsmane);
		try {
			soutput = new FileOutputStream(f);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            // send DOM to file
            tr.transform(new DOMSource(atree), new StreamResult(soutput));

        } catch (TransformerException te) {
            System.out.println(te.getMessage());
        } 
		try {
			soutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static GlobalEntriesList getAllEntries() {
		return allEntries;
	}

	public static Document getStorageTree() {
		return storageTree;
	}

	public static void setStorageTree(Document storageTree) {
		GenGraphs.storageTree = storageTree;
	}

	public static Document getSkosTree() {
		return skosTree;
	}

	public static void setSkosTree(Document skosTree) {
		GenGraphs.skosTree = skosTree;
	}

	public static String getSkosFile() {
		return skosFile;
	}

	public static void setSkosFile(String skosFile) {
		if (skosFile != null){
			GenGraphs.skosFile = skosFile;
		}
	}

	public static String getStorageFile() {
		return storageFile;
	}

	public static void setStorageFile(String storageFile) {
		if (storageFile != null){
			GenGraphs.storageFile = storageFile;
		}
	}

	public static Node getSkosRoot() {
		return skosRoot;
	}

	public static void setSkosRoot(Node skosRoot) {
		GenGraphs.skosRoot = skosRoot;
	}

	public static Node getStorageRoot() {
		return storageRoot;
	}

	public static void setStorageRoot(Node storageRoot) {
		GenGraphs.storageRoot = storageRoot;
	}

	public static Boolean getSkosFileNeedSaving() {
		return skosFileNeedSaving;
	}

	public static void setSkosFileNeedSaving(Boolean skosFileNeedSaving) {
		GenGraphs.skosFileNeedSaving = skosFileNeedSaving;
	}

	public static Boolean getStorageFileNeedSaving() {
		return storageFileNeedSaving;
	}

	public static void setStorageFileNeedSaving(Boolean storageFileNeedSaving) {
		GenGraphs.storageFileNeedSaving = storageFileNeedSaving;
	}

	public static Runtime getRt() {
		return rt;
	}

	public static MainFrame getWindow() {
		return window;
	}

	public static Document getArticleTree() {
		return articleTree;
	}

	public static void setArticleTree(Document articleTree) {
		GenGraphs.articleTree = articleTree;
	}

	public static Node getArticleRoot() {
		return articleRoot;
	}

	public static void setArticleRoot(Node articleRoot) {
		GenGraphs.articleRoot = articleRoot;
	}

	public static String getArticleFile() {
		return articleFile;
	}

	public static void setArticleFile(String articleFile) {
		GenGraphs.articleFile = articleFile;
	}

	public static List<String> getUrlSeasonsList() { return seasonsList; }

	public static String getThumbBaseurl() { return  thumbBaseurl; }

	public static String getArticlesBaseurl() { return articles_baseurl; }
}
