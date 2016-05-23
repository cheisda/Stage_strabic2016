package graphGeneration.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalEntriesList {
	private List<IndexedEntry> globalist = null;
	private List<Article> articlelist = null;
	private List<String> vocabulary = new ArrayList(); // for autocompletion

	public GlobalEntriesList(){
		globalist = new ArrayList<IndexedEntry>();
		articlelist = new ArrayList<Article>();
	}

	public void add(IndexedEntry e){
		if (e == null) return;
		//e.display();
		globalist.add(e);
	}
	
	public void add(Article e){
		if (e == null) return;
		//e.display();
		articlelist.add(e);
	}
	
	public void buildAutocompletionList(){
		getVocabulary().clear();
		for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
			String uri= temp.getURI();
			 if (uri != null){
				 String[] kw = uri.split("#");
				 getVocabulary().add(kw[kw.length-1]);
			 }
		 }
		Collections.sort(getVocabulary());
	}

	public int size(){
		return globalist.size();
	}

	public int numberOfArticles(){
		return articlelist.size();
	}

	public Article getArticle(int i){
		if (i < 0) return null;
		if (i >= numberOfArticles()) return null;
		return articlelist.get(i);
	}

	public IndexedEntry getEntry(int i){
		if (i < 0) return null;
		if (i >= size()) return null;
		return globalist.get(i);
	}

	public IndexedEntry getEntry(int i, IndexedEntry.typeOfEntries t){
		if (i < 0) return null;
		if (i >= size()) return null;
		int j = 0;
		 for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
			 if (temp.getEntryType() == t){
				 if (temp.getURI() != null){
					 if (j == i) return temp;
					 j++;
				 }
			 }
		 }
		return null;
	}

	public void display(){
		Collections.sort(globalist, IndexedEntry.entryNameComparator);
		for(IndexedEntry temp: globalist){
			temp.display();
		}
	}

	public void unsetFoundFlag(){
		for(IndexedEntry temp:getGlobalist()){
			temp.setFound(false);
		}
	}

	public Article searchArticle(String titre){
		if (titre == null) return null;
		for(Article temp2: getArticlelist()){
			//System.out.println("Comparing: "+temp2.getTitre() + " with "+titre);
			if (temp2.getTitre().equals(titre)){
				return temp2;
			}
		}
		return null;
	}
	
	public IndexedEntry searchConcept(String uri){
		if (uri == null) return null;
		for(IndexedEntry temp2: getGlobalist()){
			if (temp2.getEntryType() == IndexedEntry.typeOfEntries.concept){
				if (temp2.getURI().equals(uri)){
					return temp2;
				}
			}
		}
		return null;
	}

	public IndexedEntry searchURI(String uri){
		if (uri == null) return null;
		for(IndexedEntry temp2: getGlobalist()){
			//System.out.println("Comparing: "+temp2.getURI() + " with "+uri);
			if (temp2.getURI().equals(uri)){
				//System.out.println("FOUND");
				return temp2;
			}
		}
		return null;
	}

	public IndexedEntry searchURIPart(String uri){
		if (uri == null) return null;
		for(IndexedEntry temp2: getGlobalist()){
			if (temp2.getURI().toLowerCase().contains(uri.toLowerCase())){
				return temp2;
			}
		}
		return null;
	}

	public IndexedEntry searchLabel(String lab){
		if (lab == null) return null;
		String lowerlab = lab.toLowerCase();
		for(IndexedEntry temp2: getGlobalist()){
			if (temp2.getPreflabel() != null){
				if (temp2.getPreflabel().toLowerCase().contains(lowerlab)){
					return temp2;
				}
			}
		}
		return null;
	}

	public String check(){
		int count = 0;
		String result = "";
		// check for reference
		for(IndexedEntry turi: globalist){
			for (int k = 0; k < FieldEntry.numberOfFields(); k++){
				if (k == 1) continue; //this is the URI of the entry
				if (k == 14) continue; //TODO this is the concept of the entry
				if (FieldEntry.fieldIsURI(k)){
					String uri = turi.getFields()[k].getValue();
					if (uri != null){
						if (!uri.isEmpty()){
							Boolean found = true;
							if (searchURI(uri) == null){
								result = result + uri + " MISSING IN \n    "+ turi.getURI() + "\n";
								found = false;
							}
							if (!found) System.out.println("Failed finding " + uri);
						}
					}
				}
			}
		}
//			switch(temp1.getEntryType()){
//			case data:
//				Boolean found = false;
//				if (searchConcept(temp1.getConceptOfIndex()) != null) found = true;
//				if (found==false){
//					//System.out.println("CONCEPT NOT FOUND FOR ");
//					result = result + temp1.getConceptOfIndex() + " CONCEPT NOT FOUND FOR "+ temp1.getURI() + "\n";
//					//temp1.display();
//				}
				//				//check children
				//				NodeList list = temp1.getNode().getChildNodes();	
				//				int i = list.getLength()-1;
				//				while (i >= 0) {
				//					Node n = list.item(i);
				//					if (n.getAttributes() != null){
				//						Attr attrType = (Attr) n.getAttributes().getNamedItem("uri");
				//						if ( attrType != null){
				//							found=false;
				//							if (searchURI(attrType.getValue()) != null) found = true;
				//							if (found==false){
				//								result = result + "CONCEPT " + attrType.getValue() + " NOT FOUND FOR \n";
				//								result = result +  temp1.getURI() + "\n";
				//								//System.out.println("CONCEPT " + attrType.getValue() + " NOT FOUND FOR ");
				//								//temp1.display();
				//							}
				//						} 
				//					}
				//					i--;
				//				}
//				break;
//			}
//		}
		//check for double entry (XXXX are palcehorders not to check)
		for(IndexedEntry temp1: globalist){
			Boolean found = false;
			count++;
			if (!temp1.getURI().contains("XXXX")){
				for(IndexedEntry temp2: globalist){
					if (temp2 != temp1){
						if (temp2.getURI().equals(temp1.getURI())){
							found = true;
							break;
						}
					}
				}
				if (found==true){
					result = result + "CONCEPT FOUND TWICE \n";
					result = result +  temp1.getURI() + "\n";
					//System.out.println("CONCEPT FOUND TWICE ");
					//temp1.display();

				}
			}
		}
		System.out.println("Found " + count + " uri");
		result = result + "Found " + count + " uri\n";
		return result;
	}

	public List<IndexedEntry> getGlobalist() {
		return globalist;
	}

	public void setGlobalist(List<IndexedEntry> globalist) {
		this.globalist = globalist;
	}

	public List<String> getVocabulary() {
		return vocabulary;
	}

	public void setVocabulary(List<String> vocabulary) {
		this.vocabulary = vocabulary;
	}

	public List<Article> getArticlelist() {
		return articlelist;
	}

	public void setArticlelist(List<Article> articlelist) {
		this.articlelist = articlelist;
	}
}
