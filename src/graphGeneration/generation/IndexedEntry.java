package graphGeneration.generation;

import java.util.Comparator;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IndexedEntry implements Comparable<IndexedEntry> {
	private final static String prefixURI = "http://www.my.com/#";
	private String URI;
	private Node node;
	private Boolean uptodate;
	static public enum typeOfEntries {
		conceptscheme, concept, data, unknown
	};
	private String uriConceptScheme = "";
	private typeOfEntries entryType = typeOfEntries.unknown;
	FieldEntry fields[]= null;
	private String preflabel = "";
	private String[] preflabelWords;
	private Boolean found;
	private String conceptOfIndex = null;

	public IndexedEntry(String uri, Node n){
		super();
		setUptodate(true);
		setURI(uri.trim());
		setNode(n);
		fields = new FieldEntry[FieldEntry.numberOfFields()];
		for (int i = 0; i< FieldEntry.numberOfFields(); i++){
			fields[i] = new FieldEntry(i);
		}		
		analyzeXMLNode();
	}

	//create a node 
	public IndexedEntry(typeOfEntries t){
		setEntryType(t);
		fields = new FieldEntry[FieldEntry.numberOfFields()];
		for (int i = 0; i< FieldEntry.numberOfFields(); i++){
			fields[i] = new FieldEntry(i);
		}
		setNode(null);
		//setNode(addXMLNode(t)); // need also to be attached to the document
		//if (getNode() != null){
		GenGraphs.getAllEntries().getGlobalist().add(this);
		//}
		setUptodate(false);
	}
	
/////////////////////////////////////////////////
// example of node
//	 <skos:Concept rdf:about="http://www.my.com/#0000">
//	    <skos:definition>Related to testing </skos:definition>
//	    <skos:prefLabel>mypreflabel</skos:prefLabel>
//	    <skos:altLabel>another label 0</skos:altLabel>
//	    <skos:altLabel>another label 1</skos:altLabel>
//	    <skos:altLabel>another label 2</skos:altLabel>
//	    <skos:related rdf:resource="http://www.my.com/#hpc"/>
//	    <skos:narrower rdf:resource="http://www.my.com/#exascale"/>
//	    <skos:broader rdf:resource="http://www.my.com/#computing"/>
//	    <skos:scopeNote>All data, projects, person, organizations related to specifying
//	                    and implementing exscale hardware and software</skos:scopeNote>
//	    <skos:inScheme rdf:resource="http:/www.my.com/#fieldconcept"/>
//	  </skos:Concept>
/////////////
//	<data skos:Concept="http://www.my.com/#country"
//    uri="http://www.my.com/#11111">
//<skos:prefLabel>Belgium</skos:prefLabel>
//<skos:altLabel>another label 0</skos:altLabel>
//<skos:altLabel>another label 1</skos:altLabel>
//<skos:altLabel>another label 2</skos:altLabel>
//<definition> not a definition </definition>
//<link skos:Concept="http://www.my.com/#url"
//	value="http://notanurl"/>
//<skos:related rdf:resource="http://www.my.com/#notatopic"/>
//<data uri="http://www.my.com/#EU"/>
//<data uri="http://www.my.com/#USA"/>
//<skos:scopeNote> notascope </skos:scopeNote>
//</data>

	// from XML to Value
	public void analyzeXMLNode(){
		int j;
		Node n = getNode();
		entryType = typeOfEntries.unknown;
		if (n == null) return;
		String tagname =  n.getNodeName();
		//WHAT TYPE OF NODE DO WE HAVE
		if ("skos:Concept".equals(tagname)) setEntryType(typeOfEntries.concept);
		else   if ("skos:ConceptScheme".equals(tagname)) setEntryType(typeOfEntries.conceptscheme);
		else if ("data".equals(tagname)) {
			setEntryType(typeOfEntries.data);
			String val = getAttValue(n,"skos:Concept");
			if ( val != null){
				getFields()[0].setValue(val);
				setConceptOfIndex(val);
			}
		}
		getFields()[1].setValue(getURI());
		// LOOK AT THE CHILDREN
		int altname = 0;
		int properties = 0;
		int nbdatac = 0;
		NodeList list = getNode().getChildNodes();
		for(j=0; j<list.getLength(); j++){
			Node c = list.item(j);
			String cname = c.getNodeName();
			switch(c.getNodeType()) {
			case Node.ELEMENT_NODE:
				if ("skos:prefLabel".equals(cname)){
					setPreflabel(c.getTextContent().trim());
					setPreflabelWords(getPreflabel().toLowerCase().split(" "));
					getFields()[2].setValue(getPreflabel());
				} else if ("skos:altLabel".equals(cname)){
					if (altname < 3){
						getFields()[3+altname].setValue(c.getTextContent());
						altname++;
					} else {
						System.out.println("Alternative name ignored: "+c.getTextContent());
					}
				} else if ("skos:related".equals(cname)){
					String val1 = getAttValue(c,"rdf:resource");
					if ( (val1!= null) && (properties < 3)){
						getFields()[8+properties].setValue(val1);
						properties++;
					} else {
						System.out.println("Ignored properties : "+val1);
					}
				} else if ("skos:definition".equals(cname)){
					getFields()[13].setValue(c.getTextContent());
				} else if ("skos:related".equals(cname)){
					properties++;
				} else if ("skos:scopeNote".equals(cname)){
					getFields()[15].setValue(c.getTextContent());
				} 
				// specific to some nodes
				switch (this.getEntryType()){
				case concept:
					if ("skos:inScheme".equals(cname)){
						String val = getAttValue(c,"rdf:resource");
						if ( val != null){
							//System.out.println("Concept being set to : " + attrType.getValue());
							setUriConceptScheme(val);
							getFields()[14].setValue(getUriConceptScheme());
						}
					} else if ("skos:narrower".equals(cname)){
						String val = getAttValue(c,"rdf:resource");
						if ( val != null){
							getFields()[11].setValue(val);
						}
					} else if ("skos:broader".equals(cname)){
						String val = getAttValue(c,"rdf:resource");
						if ( val != null){
							getFields()[12].setValue(val);
						}
					}  
					break;
				case data:
					 if ("link".equals(cname)){
						String val1 = getAttValue(c,"skos:Concept");
						String val2 = getAttValue(c,"value");
						//System.out.println(val1 + "  -- " + val2);
						if ((val1 != null) && (val2 !=null)){
							getFields()[16].setValue(val1);
							getFields()[17].setValue(val2);
						}
					} else if ("data".equals(cname)){
						String val1 = getAttValue(c,"uri");
						if ( (val1!= null) && (nbdatac < 2)){
							getFields()[6+nbdatac].setValue(val1);
							nbdatac++;
						} else {
							System.out.println("Data properties ignored : "+val1);
						}
					}
					break;
				case conceptscheme:
					break;
				}
				break;
			}
		}
	}

	// Update the field values
	public void setFieldValueAsString(int index, String value) {
		Element ncn = null;
		getFields()[index].setValue(value);
		if ((index == 0)&&(getEntryType() == typeOfEntries.data)){
			setConceptOfIndex(value);
		}
		if ((index == 2)&&(getEntryType() == typeOfEntries.concept)){
			setPreflabel(value);
			setPreflabelWords(getPreflabel().toLowerCase().split(" "));
		}
		setUptodate(false);
		// need to update 
	}
	
	// updating node destroy the old XML and create a new XML one
	public void updateNode(){
		Document cdoc = null;
		Element ncn = null;
		if (getUptodate()) return;
		removeXMLNode();
		setNode(addXMLNode(getEntryType()));
		if (getEntryType() == typeOfEntries.data){
			cdoc = GenGraphs.getStorageTree();
			((Element) getNode()).setAttribute("uri", getURI());
			GenGraphs.setStorageFileNeedSaving(true);
		} else {
			cdoc = GenGraphs.getSkosTree();
			if (getNode() != null){
				((Element) getNode()).setAttribute("rdf:about", getURI());
				GenGraphs.setSkosFileNeedSaving(true);
			} else {
				System.out.println("XML node could not be created");
				setUptodate(true);
				return;
			}
		}
		for(int index=0; index<getFields().length; index++){
			String value = getFields()[index].getValue();
			if (value.isEmpty()) continue;
			switch (index){
			case 0:
				if (getEntryType() == typeOfEntries.data){
					((Element) getNode()).setAttribute("skos:Concept", value);
				}
				break;
			case 1: // URI nothing to do here
				break;
			case 2: //skos:prefLabel
				ncn = cdoc.createElement("skos:prefLabel");
				ncn.setTextContent(value);
				getNode().appendChild(ncn);
				break;
			case 3: //skos:altLabel
			case 4:
			case 5:
				ncn = cdoc.createElement("skos:altLabel");
				ncn.setTextContent(value);
				getNode().appendChild(ncn);
				break;
			case 6: 
			case 7:
				//#data
				if (getEntryType() == typeOfEntries.data){
					System.out.println("Updating node -2- " + index + "  " + value);
					ncn = cdoc.createElement("data");
					ncn.setAttribute("uri", value);
					getNode().appendChild(ncn);
				} else {
					System.out.println("Entry is not a data");
				}
				break;
			case 8: //skos:related
			case 9: //skos:related
			case 10: //skos:related (let's use this instead of property) works for data and concept
				ncn = cdoc.createElement("skos:related");
				ncn.setAttribute("rdf:resource", value);
				getNode().appendChild(ncn);
				break;
			case 11: // skos:narrower - concept only
				if (getEntryType() == typeOfEntries.concept){
					ncn = cdoc.createElement("skos:narrower");
					ncn.setAttribute("rdf:resource", value);
					getNode().appendChild(ncn);
				} else {
					System.out.println("Entry is not a concept");
				}
				break;
			case 12://skos:broader - concept only
				if (getEntryType() == typeOfEntries.concept){
					ncn = cdoc.createElement("skos:broader");
					ncn.setAttribute("rdf:resource", value);
					getNode().appendChild(ncn);
				} else {
					System.out.println("Entry is not a concept");
				}
				break;
			case 13://skos:definition
				ncn = cdoc.createElement("skos:definition");
				ncn.setTextContent(value);
				getNode().appendChild(ncn);
				break;
			case 14: //skos:inScheme - concept only
				if (getEntryType() == typeOfEntries.concept){
					ncn = cdoc.createElement("skos:inScheme");
					ncn.setAttribute("rdf:resource", value);
					getNode().appendChild(ncn);
				} else {
					System.out.println("Entry is not a concept");
				}
				break;
			case 15: //skos:scopeNote
				ncn = cdoc.createElement("skos:scopeNote");
				ncn.setTextContent(value);
				getNode().appendChild(ncn);
				break;
			case 16: // link & value (17) - data only
				if (getEntryType() == typeOfEntries.data){
					ncn = cdoc.createElement("link");
					ncn.setAttribute("skos:Concept", value);
					ncn.setAttribute("value", getFields()[index+1].getValue());
					getNode().appendChild(ncn);
				} else {
					System.out.println("Entry is not a data");
				}
				break;
			case 17: // nothing to do, done with 16
				break;
			default:
				System.out.println("Unknow index in updateNode(): " + index);
			}	
		}
		setUptodate(true);
	}
	
	String getAttValue(Node n, String l){
		Attr attrType = (Attr) n.getAttributes().getNamedItem(l);
		if ( attrType != null){
			return attrType.getValue();
		}
		return null;
	}
	
	public String getURI() {
		return URI;
	}

	public void setURI(String uRI) {
		URI = uRI;
	}

	void display(){
		switch (getEntryType()) {
		case conceptscheme:
			System.out.println("Concept scheme : " + getURI());
			break;
		case concept:
			System.out.println("Concept : " + getURI());
			for (int i = 0; i< FieldEntry.numberOfFields(); i++){
				if (fields[i].applyToConcept())
					System.out.println(fields[i].getFieldName() +": "+ fields[i].getValue());
			}
			break;
		case data:
			System.out.println("Data : " + getURI());
			for (int i = 0; i< FieldEntry.numberOfFields(); i++){
				if (fields[i].applyToData())
					System.out.println(fields[i].getFieldName() +": "+ fields[i].getValue());
			}
			break;
		default:
			System.out.println("???? : " + getURI());
			break;
		}
	}

	public int compareTo(IndexedEntry e) {
		return this.getURI().compareTo(e.getURI());
	}

	public static Comparator<IndexedEntry> entryNameComparator = new Comparator<IndexedEntry>() {

		public int compare(IndexedEntry e1, IndexedEntry e2) {

			String se1 = e1.getURI().toUpperCase();
			String se2 = e2.getURI().toUpperCase();

			//ascending order
			return se1.compareTo(se2);
			//descending order
			//return se1.compareTo(se2);
		}
	};

	class MyErrorListener implements ErrorListener {
		public void warning(TransformerException e)
				throws TransformerException {
			show("Warning",e);
			throw(e);
		}
		public void error(TransformerException e)
				throws TransformerException {
			show("Error",e);
			throw(e);
		}
		public void fatalError(TransformerException e)
				throws TransformerException {
			show("Fatal Error",e);
			throw(e);
		}
		private void show(String type,TransformerException e) {
			System.out.println(type + ": " + e.getMessage());
			if(e.getLocationAsString() != null)
				System.out.println(e.getLocationAsString());
		}
	}

	private String nodeToString(String outs) {
		switch (getEntryType()) {
		case conceptscheme:
			outs = outs + "Concept scheme : " + getURI()+ "\n";
			break;
		case concept:
			outs = outs +"Concept : " + getURI()+ "\n";
			for (int i = 0; i< FieldEntry.numberOfFields(); i++){
				if (fields[i].applyToConcept())
					outs = outs + fields[i].getFieldName() +": "+ fields[i].getValue() + "\n";
			}
			break;
		case data:
			outs = outs +"Data : " + getURI()+ "\n";
			for (int i = 0; i< FieldEntry.numberOfFields(); i++){
				if (fields[i].applyToData())
					outs = outs +fields[i].getFieldName() +": "+ fields[i].getValue()+ "\n";
			}
			break;
		default:
			outs = outs +"???? : " + getURI();
			break;
		}
		return outs;
	}
	//Concept start with #xxx 
	public Boolean isEntryTaggedWithConcept(String tc){
		if (getUriConceptScheme().length()>0){
			if(getUriConceptScheme().contains(tc)) return true;
		}
		Attr attrType = (Attr) getNode().getAttributes().getNamedItem("skos:Concept");
		if ( attrType != null){
			if(attrType.getValue().contains(tc)) return true;
		}
		return false;
	}

	public String getNodeText(){
		String outs = "";
		if (getUriConceptScheme().length() > 0) outs = outs + this.getUriConceptScheme() + "\n";
		return nodeToString(outs);
	}

	void addConcept(IndexedEntry nc){
		Element ncn = GenGraphs.getStorageTree().createElement("property");
		ncn.setAttribute("uri", nc.getURI());
		getNode().appendChild(ncn);
	}

	void addData(IndexedEntry nc){
		Element ncn = GenGraphs.getStorageTree().createElement("data");
		ncn.setAttribute("uri", nc.getURI());
		getNode().appendChild(ncn);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getConceptOfIndex() {
		return conceptOfIndex;
	}

	public void setConceptOfIndex(String conceptOfIndex) {
		this.conceptOfIndex = conceptOfIndex;
	}

	public typeOfEntries getEntryType() {
		return entryType;
	}

	public void setEntryType(typeOfEntries entryType) {
		this.entryType = entryType;
	}

	public String getPreflabel() {
		return preflabel;
	}

	public void setPreflabel(String preflabel) {
		this.preflabel = preflabel;
	}

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}

	public String[] getPreflabelWords() {
		return preflabelWords;
	}

	public void setPreflabelWords(String[] preflabelWords) {
		this.preflabelWords = preflabelWords;
	}

	public String getUriConceptScheme() {
		return uriConceptScheme;
	}

	public void setUriConceptScheme(String uriConceptScheme) {
		this.uriConceptScheme = uriConceptScheme;
	}

	public FieldEntry[] getFields() {
		return fields;
	}

	public void setFields(FieldEntry[] fields) {
		this.fields = fields;
	}

	Node addXMLNode(typeOfEntries t){
		Element ncn = null;
		System.out.println("adding XML for an indexEntry the node");
		if (getEntryType() == typeOfEntries.data){
			if (GenGraphs.getStorageRoot() == null) return null;
			ncn = GenGraphs.getStorageTree().createElement("data");
			GenGraphs.getStorageRoot().appendChild(ncn);
		} else {
			if (GenGraphs.getSkosTree() == null) return null;
			ncn = GenGraphs.getSkosTree().createElement("skos:Concept");
			if (GenGraphs.getSkosRoot() == null) {
				System.out.println("No Root to add SKOS Node");
				return null;
			}
			GenGraphs.getSkosRoot().appendChild(ncn);
		}
		return ncn;
	}

	void removeXMLNode(){
		Node n = getNode();
		if (n == null) return;
		if (getEntryType() == typeOfEntries.data){
			GenGraphs.getStorageRoot().removeChild(n);
		} else {
			GenGraphs.getSkosRoot().removeChild(n);
		}
	}
	
	public void removeNode(){
		GenGraphs.getAllEntries().getGlobalist().remove(this);
		//let first remove it from the XML
		removeXMLNode();
	}

	public Boolean getUptodate() {
		return uptodate;
	}

	public void setUptodate(Boolean uptodate) {
		this.uptodate = uptodate;
	}

	public static String getPrefixURI() {
		return prefixURI;
	}
}
