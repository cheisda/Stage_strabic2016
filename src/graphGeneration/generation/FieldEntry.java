package graphGeneration.generation;

// fields of IndexedEntry
public class FieldEntry {
	private String value = "";
	private int fieldIndexDescription = 0;
	private static final String nodeFieldDescription[][] = {
		{"data","text","data concept","","att","skos:Concept","uri","0"}, // entry format description
		{"data","text","uri","","att","uri","uri","1"},
		{"all","text","pref label","","tag","skos:prefLabel","content","2"},
		{"all","text","alt1 label","","tag","skos:altLabel","content","3"},
		{"all","text","alt2 label","","tag","skos:altLabel","content","4"},
		{"all","text","alt3 label","","tag","skos:altLabel","content","5"},
		{"data","text","data label1","","tag","data","uri","6"}, //#country is a filter on the entry concept (work for data only)
		{"data","text","data label2","","tag","data","uri","7"},
		{"all","text","prop1 label","","tag","skos:related","uri","8"},
		{"all","text","prop2 label","","tag","skos:related","uri","9"},
		{"all","text","prop3 label","","tag","skos:related","uri","10"},
		{"concept","text","narrower","","tag","skos:narrower","uri","11"},
		{"concept","text","broader","","tag","skos:broader","uri","12"},
		{"all","text","description","","tag","skos:definition","content","13"},
		{"concept","text","scheme","","tag","skos:inScheme","uri","14"},
		{"all","text","note","","tag","skos:scopeNote","content","15"},
		{"data","text","link type","","tag","link","uri","16"},
		{"data","text","link value","","att","value","content","17"}
	};

	public FieldEntry(int index){
		if (index < 0) index =0;
		if (index >= nodeFieldDescription.length) index = 0;
		setFieldIndexDescription(index);
	}

	public int getFieldIndexDescription() {
		return fieldIndexDescription;
	}

	public void setFieldIndexDescription(int fieldIndexDescription) {
		this.fieldIndexDescription = fieldIndexDescription;
	}

	public Boolean applyToConcept(){
		if (nodeFieldDescription[getFieldIndexDescription()][0].equals("concept")) return true;
		if (nodeFieldDescription[getFieldIndexDescription()][0].equals("all")) return true;
		return false;
	}

	public Boolean applyToData(){
		if (nodeFieldDescription[getFieldIndexDescription()][0].equals("data")) return true;
		if (nodeFieldDescription[getFieldIndexDescription()][0].equals("all")) return true;
		return false;
	}

	public Boolean requireTextForm(){
		if (nodeFieldDescription[getFieldIndexDescription()][1].equals("text")) return true;
		return false;
	}

	public Boolean requireChoiceForm(){
		if (nodeFieldDescription[getFieldIndexDescription()][1].equals("choice")) return true;
		return false;
	}

	public String getFieldName(){
		return nodeFieldDescription[getFieldIndexDescription()][2];
	}

	public String getDataFilterField(){
		return nodeFieldDescription[getFieldIndexDescription()][3];
	}

	public Boolean fieldIsChildren(){
		if (nodeFieldDescription[getFieldIndexDescription()][4].equals("tag")) return true;
		return false;
	}

	public Boolean fieldIsAttribute(){
		if (nodeFieldDescription[getFieldIndexDescription()][4].equals("att")) return true;
		return false;
	}

	public static Boolean fieldIsURI(int index){
		if (nodeFieldDescription[index][6].equals("uri")) return true;
		return false;
	}

	public String fieldXMLName(){
		return nodeFieldDescription[getFieldIndexDescription()][5];
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static String[] fieldDescription(int index) {
		return nodeFieldDescription[index];
	}

	public static int numberOfFields(){
		return nodeFieldDescription.length;
	}

	public static String[][] getNodefielddescription() {
		return nodeFieldDescription;
	}

	public static String getDefaultValue(int index){
		return nodeFieldDescription[index][2];
	}
}
