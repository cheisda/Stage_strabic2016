package graphGeneration.analyse;

import graphGeneration.generation.Article;
import graphGeneration.generation.GenGraphs;
import graphGeneration.UI.FrameForTextOut;

import java.util.ArrayList;

// need to add sorting here.
public class FileAnalysisResult {
	String FilePath = "";
	ArrayList<String> keywords = new ArrayList<String>();
	ArrayList<String> uri = new ArrayList<String>();
	Article articleRes = null;
	
	public FileAnalysisResult(String path, Article art){
		if (path != null) setFilePath(path);
		if (art != null) setArticleRes(art);
		return;
	}

	void addKeyWord(String kw, String uri){
		if (kw != null) {
			if (!kw.isEmpty()) {
				getKeywords().add(kw);
				getUri().add(uri); 
			}
		}
	}

	public int numberOfKeywords(){
		return getKeywords().size();
	}

	public String getKeyword(int index){
		return getKeywords().get(index);
	}

	public String getURI(int index){
		return getUri().get(index);
	}

	Boolean compareKeywords(String kw1, String kw2){
		if (kw1 == null) return false;
		if (kw2 == null) return false;
		if (kw1.equals(kw2)) return true;
		return false;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		FilePath = filePath;
	}

	// stupid one to be change later...
	public int countProximity(FileAnalysisResult otherone){
		int score = 0;
		for (String kw1: getKeywords()){
			for (String kw2: otherone.getKeywords()){
				if (compareKeywords(kw1,kw2))  score++;
			}
		}
		return score;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public void show(){
		String res = "";
		for (String temp: getKeywords()){
			System.out.println("--"+temp);
			res = res + "\n"+temp;
		}
		if (GenGraphs.gui){
			new FrameForTextOut(res,getFilePath());
		}
	}

	public ArrayList<String> getUri() {
		return uri;
	}

	public void setUri(ArrayList<String> uri) {
		this.uri = uri;
	}

	public Article getArticleRes() {
		return articleRes;
	}

	public void setArticleRes(Article articleRes) {
		this.articleRes = articleRes;
	}
}
