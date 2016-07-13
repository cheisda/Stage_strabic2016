package graphGeneration.generation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import graphGeneration.analyse.FileAnalysisResult;
import graphGeneration.analyse.PdfTextAnalysis;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Article {
	private Node nodeXML = null;
	private String saison = null;
	private String titre = null;
	private String urlSaison = null;
	private String rawTitre = null;
	private String rawSousTitre = null;
	private String rawSurTitre = null;
	private String chapo = null;
	private String rawChapo = null;
	private String date = null;
	private String thumbnail = null;
	private String filename = null;
	private List<String> rawAuthors = null;
	private List<String> keywordsURI= null; //concepts or data URI, official keywords
	private String text = "no text"; // plain, simple text
	private String rawtext = "no text"; // raw text store in the database
	private List<String>  foundURI = null;  //concepts or data found in the text
	private String URL = null;
	private String URLend = null;
	private final int displayLength = 100;
	private Boolean needUpdate = false;

	public Article(Node n){
		setNodeXML(n);
		keywordsURI = new ArrayList<String>();
		foundURI = new ArrayList<String>();
		rawAuthors = new ArrayList<String>();
		 buildXML();
	}

	public Article(){
		keywordsURI = new ArrayList<String>();
		foundURI = new ArrayList<String>();
		rawAuthors = new ArrayList<String>();
		setNeedUpdate(true);
	}

	public String toString(){
		int i = 0;
		int len = 0;
		String outs = "";
    System.out.println("le out dde la fonction toString : line 64 "+ outs);
		outs = outs + "saison: " + getSaison() + "\n";
		outs = outs + "titre: " + getTitre() + "\n";
		outs = outs + "URL: " + getURL() + "\n";
		outs = outs + "Url image: " + getThumbnail() + "\n";
		len = getKeywords().size();
		for (i=0;i<len;i++){
			outs = outs + "keyword: " + getKeywords().get(i) + "\n";
		}
		len = getFoundURI().size();
		for (i=0;i<len;i++){
			outs = outs + "found: " + getFoundURI().get(i) + "\n";
		}
    System.out.println("le out dde la fonction toString : line 77 "+ outs);
		if (getText().length() > displayLength)
			outs = outs + "text: \n" + getText().substring(0, displayLength) + "...\n";
		else
			outs = outs + "text: \n" + getText() + "...\n";
    //01.07.2016
    //System.out.println("le out dde la fonction toString : "+ outs);


		return outs;
	}

	private void buildXML(){
		int j;
		if (getNodeXML() == null) return;
		NodeList list = getNodeXML().getChildNodes();
		for(j=0; j<list.getLength(); j++){
			Node c = list.item(j);
			String cname = c.getNodeName();
			switch(c.getNodeType()) {
			case Node.ELEMENT_NODE:
				if ("saison".equals(cname)){
					setSaison(c.getTextContent().trim());
				} else if ("titre".equals(cname)){
					setTitre(c.getTextContent().trim());
				} else if ("URL".equals(cname)){
					setURL(c.getTextContent().trim());
				} else if ("text".equals(cname)){
					setText(c.getTextContent());
				} else if ("image".equals(cname)){
					setThumbnail(c.getTextContent().trim());
				} else if ("keyword".equals(cname)){
					String val1 = getAttValue(c,"rdf:about");
					if (val1 != null) getKeywords().add(val1);
				} else if ("found".equals(cname)){
					String val1 = getAttValue(c,"rdf:about");
					if (val1 != null) getFoundURI().add(val1);
				}
				break;
			}
		}
	}

	private void removeXMLNode(){
		Node n = getNodeXML();
		if (n == null) return;
		GenGraphs.getArticleRoot().removeChild(n);
	}

	private Node addXMLNode(){
		Element ncn = null;
		if (GenGraphs.getArticleRoot() == null) {
			JOptionPane.showMessageDialog(null,"Pas de racine d'arbre XML","Internal Erreur", JOptionPane.PLAIN_MESSAGE);
			return null;
		}
		ncn = GenGraphs.getArticleTree().createElement("article");
		GenGraphs.getArticleRoot().appendChild(ncn);
		System.out.println("adding XML node for an article");
		return ncn;
	}

	public void updateNode(){
		int i,len;
		Document cdoc = null;
		Element ncn = null;

		if (!getNeedUpdate()) return;
		removeXMLNode();
		setNodeXML(addXMLNode());

		cdoc = GenGraphs.getArticleTree();
		if (cdoc == null) {
			JOptionPane.showMessageDialog(null,"Pas d'arbre XML","Internal Erreur", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		System.out.println("updating: "+getTitre());
		ncn = cdoc.createElement("saison");
		ncn.setTextContent(getSaison());
		getNodeXML().appendChild(ncn);

		ncn = cdoc.createElement("titre");
		ncn.setTextContent(getTitre());
		getNodeXML().appendChild(ncn);

		ncn = cdoc.createElement("urlImage");
		ncn.setTextContent(getThumbnail());
    System.out.println("document ncn gettextContent method : "+ncn.getTextContent());
		getNodeXML().appendChild(ncn);

		ncn = cdoc.createElement("URL");
		ncn.setTextContent(getURL());
		getNodeXML().appendChild(ncn);

		ncn = cdoc.createElement("text");
		ncn.setTextContent(getText());
		getNodeXML().appendChild(ncn);

		len = getKeywords().size();
		for (i=0;i<len;i++){
			ncn = cdoc.createElement("keyword");
			ncn.setAttribute("rdf:about", getKeywords().get(i));
			getNodeXML().appendChild(ncn);
		}
		len = getFoundURI().size();
		for (i=0;i<len;i++){
			ncn = cdoc.createElement("found");
			ncn.setAttribute("rdf:about", getFoundURI().get(i));
			getNodeXML().appendChild(ncn);
		}
		setNeedUpdate(false);
	}

	public String searchKeyword(String kw){
		if (kw == null) return null;
		for(String temp2: getKeywords()){
			//System.out.println("Comparing: "+temp2.getTitre() + " with "+titre);
			if (temp2.equals(kw)){
				return temp2;
			}
		}
		return null;
	}

	public String searchConcept(String kw){
		if (kw == null) return null;
		for(String temp2: getFoundURI()){
			//System.out.println("Comparing: "+temp2.getTitre() + " with "+titre);
			if (temp2.equals(kw)){
				return temp2;
			}
		}
		return null;
	}

	public FileAnalysisResult analyseConcept(){

		int len = PdfTextAnalysis.getResults().size();
		for (int j = 0; j < len; j++){
			if (getTitre().equals(PdfTextAnalysis.getResults().get(j).getFilePath())) return PdfTextAnalysis.getResults().get(j);
		}

		PdfTextAnalysis analysis  = new PdfTextAnalysis(getTitre(), true);
		analysis.setText(getText());
		analysis.searchKeywordInText();
		FileAnalysisResult an = PdfTextAnalysis.getLastResult();
		if (an == null) return null;
		an.setArticleRes(this);
		len = an.numberOfKeywords();
		for (int i=0; i<len; i++){
			String uri = an.getUri().get(i);
			if (searchConcept(uri)== null){
				foundURI.add(uri);
				setNeedUpdate(true);
			}
		}
		return an;
	}

	public String getSaison() {
		return saison;
	}

	public void setSaison(String saison) {
		this.saison = saison.replace(' ', '_');
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public List<String> getKeywords() {
		return keywordsURI;
	}

	public void setKeywords(List<String> keywords) {
		this.keywordsURI = keywords;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		final int ls = 60;
		if (text == null) return;
		text = text.replace('<', ' ');
		text = text.replace('>', ' ');
		text = text.replace('/', ' ');
		char[] textwithlinebreak = text.toCharArray(); //new char[text.length()+1];
		int count = 0;
		for (int i=0;i<text.length(); i++){
			count++;
			if (textwithlinebreak[i] == ' '){
				if (count > ls){
					textwithlinebreak[i] = '\n';
					count = 0;
				}
			}
		}
		this.text = new String(textwithlinebreak);
	}

	public List<String> getFoundURI() {
		return foundURI;
	}

	public void setFoundURI(List<String> foundURI) {
		this.foundURI = foundURI;
	}

	public Node getNodeXML() {
		return nodeXML;
	}

	public void setNodeXML(Node nodeXML) {
		this.nodeXML = nodeXML;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre.replace(' ', '_');
	}

	String getAttValue(Node n, String l){
		Attr attrType = (Attr) n.getAttributes().getNamedItem(l);
		if ( attrType != null){
			return attrType.getValue();
		}
		return null;
	}

	public Boolean getNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(Boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	private Boolean compareKeywords(String kw1, String kw2){
		if (kw1 == null) return false;
		if (kw2 == null) return false;
		if (kw1.equals(kw2)) return true;
		return false;
	}

	int countProximity(Article otherone){
		int score = 0;
		for (String kw1: getKeywords()){
			for (String kw2: otherone.getKeywords()){
				if (compareKeywords(kw1,kw2))  score++;
			}
		}
		return score;
	}

	public String getRawtext() {
		return rawtext;
	}

	public void setRawtext(String rawtext) {
		this.rawtext = rawtext;
	}

	// write the article in a file for generation of the HTML via the perl script
	public void writeInFile(String filename){
		if (filename == null) return;
		File file = new File(filename);
		try {
			try {
				FileOutputStream is = new FileOutputStream(file);
				PrintStream outf = null;
				outf = new PrintStream(is, true, "UTF-8");
				try {
					outf.println(getRawTitre());
					outf.println(getRawSousTitre());
					outf.println(getRawSurTitre());
					outf.println(getDate());
					for (String s: getRawAuthors()){
						outf.print(s + "   ");
					}
					outf.println();
					outf.println(getChapo());
					outf.print(getRawtext());
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	public List<String> getRawAuthors() {
		return rawAuthors;
	}

	public void setRawAuthors(List<String> rawAuthors) {
		this.rawAuthors = rawAuthors;
	}

	public String getRawTitre() {
		return rawTitre;
	}

	public void setRawTitre(String rawTitre) {
		this.rawTitre = rawTitre;
	}

	public String getUrlSaison() {
		return urlSaison;
	}

	public void setUrlSaison(String urlSaison) {
		this.urlSaison = urlSaison;
	}

	public String getRawSousTitre() {
		return rawSousTitre;
	}

	public void setRawSousTitre(String rawSousTitre) {
		this.rawSousTitre = rawSousTitre;
	}

	public String getRawSurTitre() {
		return rawSurTitre;
	}

	public void setRawSurTitre(String rawSurTitre) {
		this.rawSurTitre = rawSurTitre;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getChapo() {
		return chapo;
	}

	public void setChapo(String chapo) {
		this.chapo = chapo;
	}

	public String getRawChapo() {
		return rawChapo;
	}

	public void setRawChapo(String rawChapo) {
		this.rawChapo = chapo;
	}

	public String getURLend() {
		return URLend;
	}

	public void setURLend(String uRLend) {
		URLend = uRLend;
	}
}
