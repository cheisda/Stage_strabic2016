package graphGeneration.UI;

import graphGeneration.generation.FieldEntry;
import graphGeneration.generation.GenGraphs;
import graphGeneration.generation.IndexedEntry;

import java.awt.Choice;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MainFrame extends JFrame implements ActionListener ,ItemListener, KeyListener{
	private ArrayList<Choice> listChoiceInput = new ArrayList<Choice>();
	private ArrayList<Integer> listChoiceInputIndex = new ArrayList<Integer>();
	private ArrayList<TextField> listTextInput = new ArrayList<TextField>();
	private ArrayList<Integer> listTextInputIndex = new ArrayList<Integer>();
	private ArrayList<JButton> listTextInputButton = new ArrayList<JButton>();
	JButton loadInputFilesButton = null;
	JButton searchButton = null;
	TextField searchTextField = null;
	JButton fileAnalysisButton = null;
	JButton buildGraphButton = null;
	JButton checkXMLButton = null;
	JButton saveXMLButton = null;
	TextField textConsole = null;
	Choice entriesList = null;
	Choice dataConceptChoice = null;
	JButton removeXMLButton = null;
	JButton createNodeButton = null;
	IndexedEntry currentEntry=null;
	final int fsizey = 680;
	final int fsizex = 940;

	public MainFrame(){
		
		super("strabic Main Console");
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		JPanel totalGUI = new JPanel();
		this.setLocationRelativeTo(null);
		this.setDefaultLookAndFeelDecorated(true);
		totalGUI.setLayout(null); 
		
		///////////////////////////////////////////
		//left panel
		int xoffset = 100;
		int yinc = 40;
		int y = 10;
		JPanel actionPanel = new JPanel();
		actionPanel.setLocation(0,0);
		actionPanel.setSize(fsizex/2, fsizey);
		actionPanel.setLayout(null); 
		totalGUI.add(actionPanel); 

		y = 10;
		loadInputFilesButton = new JButton("Load (.skos, .data)");
		loadInputFilesButton.setLocation(xoffset, y);
		loadInputFilesButton.setSize(200, 30);
		loadInputFilesButton.addActionListener(this);
		actionPanel.add(loadInputFilesButton); 

		y = y+yinc;
		searchTextField = new TextField("filter");
		searchTextField.setLocation(xoffset, y);
		searchTextField.setSize(200, 30);
		actionPanel.add(searchTextField); 

		y = y+yinc;
		searchButton = new JButton("Search");
		searchButton.setLocation(xoffset, y);
		searchButton.setSize(200, 30);
		searchButton.addActionListener(this);
		actionPanel.add(searchButton); 

		y = y+yinc;
		fileAnalysisButton = new JButton("File Analysis");
		fileAnalysisButton.setLocation(xoffset,y);
		fileAnalysisButton.setSize(200, 30);
		fileAnalysisButton.addActionListener(this);
		actionPanel.add(fileAnalysisButton); 

		y = y+yinc;
		buildGraphButton = new JButton("Build Graph");
		buildGraphButton.setLocation(xoffset, y);
		buildGraphButton.setSize(200, 30);
		buildGraphButton.addActionListener(this);
		actionPanel.add(buildGraphButton); 

		y = y+yinc;
		checkXMLButton = new JButton("Check URI Reference");
		checkXMLButton.setLocation(xoffset, y);
		checkXMLButton.setSize(200, 30);
		checkXMLButton.addActionListener(this);
		actionPanel.add(checkXMLButton); 

		y = y+yinc;
		saveXMLButton = new JButton("Save XML");
		saveXMLButton.setLocation(xoffset, y);
		saveXMLButton.setSize(200, 30);
		saveXMLButton.addActionListener(this);
		actionPanel.add(saveXMLButton);

		y = y+yinc;
		textConsole = new TextField("");
		textConsole.setLocation(xoffset-90,y);
		textConsole.setSize(390, 660-y);
		textConsole.addActionListener(this);
		textConsole.setEditable(true); 
		actionPanel.add(textConsole); 

		//////////////////////////////////////////
		//right panel
		  
		JPanel nodePanel = new JPanel();
		nodePanel.setLocation(400,0);
		nodePanel.setSize(fsizex/2, fsizey);
		nodePanel.setLayout(null); 
		totalGUI.add(nodePanel); 
			
		xoffset = 10;
		y = 10;
		yinc = 30;
		entriesList = new Choice();
		entriesList.setLocation(xoffset, y);
		entriesList.setSize(380, 30);
		entriesList.addItemListener(this);
		nodePanel.add(entriesList);

		y = y+yinc;
		dataConceptChoice = new Choice();
		dataConceptChoice.setLocation(xoffset, y);
		dataConceptChoice.setSize(380, 30);
		dataConceptChoice.addItemListener(this);
		nodePanel.add(dataConceptChoice);
		dataConceptChoice.add("concept");
		dataConceptChoice.add("data");
		dataConceptChoice.add("all");

		y = y+yinc;
		int len = FieldEntry.numberOfFields();
		for (int i=0;i<len;i++){
			//String dataorconcept = IndexedEntry.getNodeFieldDescription()[i][0];
			String entrytype = FieldEntry.fieldDescription(i)[1];
			if (entrytype.equals("text")){
				//System.out.println("adding textfield "+FieldEntry.fieldDescription(i)[2]);
				TextField textfield = new TextField(FieldEntry.fieldDescription(i)[2]);
				textfield.addKeyListener(this);
				listTextInput.add(textfield);
				listTextInputIndex.add(i);
				textfield.setLocation(xoffset, y+i*yinc);
				textfield.setSize(250, 30);
				textfield.addActionListener(this);
				nodePanel.add(textfield); 
				// add a button to modify
				JButton updatebutton = new JButton("Update: " + FieldEntry.fieldDescription(i)[2]);
				listTextInputButton.add(updatebutton);
				updatebutton.addActionListener(this);
				updatebutton.setSize(150, 30);
				updatebutton.setLocation(xoffset+280, y+i*yinc);
				nodePanel.add(updatebutton);
//				Label label = new Label(FieldEntry.fieldDescription(i)[2]);
//				label.setLocation(xoffset+280, y+i*yinc);
//				label.setSize(100, 30);
//				label.setForeground(Color.red);
//				nodePanel.add(label);
			} else if (entrytype.equals("choice")){
				//System.out.println("adding choice "+FieldEntry.fieldDescription(i)[2]);
				Choice choicem = new Choice();
				listChoiceInput.add(choicem);
				listChoiceInputIndex.add(i);
				choicem.setLocation(xoffset, y+i*yinc);
				choicem.setSize(250, 30);
				choicem.addItemListener(this);
				nodePanel.add(choicem);
				Label label = new Label(FieldEntry.fieldDescription(i)[2]);
				label.setLocation(xoffset+280, y+i*yinc);
				//label.setLocation(0, 0);
				label.setSize(100, 30);
				//label.setHorizontalAlignment(0);
				label.setForeground(Color.red);
				nodePanel.add(label);
				// Set the choice button
				for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
					if (temp.isEntryTaggedWithConcept(FieldEntry.fieldDescription(i)[3])){
						if (temp.getURI() != null){
							choicem.add(temp.getURI());
						}
					}
				}
			}
		}	 

		removeXMLButton = new JButton("Remove");
		removeXMLButton.setLocation(xoffset, fsizey-30);
		removeXMLButton.setSize(150, 30);
		removeXMLButton.addActionListener(this);
		nodePanel.add(removeXMLButton);

		createNodeButton = new JButton("Create Node");
		createNodeButton.setLocation(xoffset+170, fsizey-30);
		createNodeButton.setSize(150, 30);
		createNodeButton.addActionListener(this);
		nodePanel.add(createNodeButton);
		  
		//////////////////////////////////////
		
		this.setContentPane(totalGUI);
		this.setSize(fsizex, fsizey+30);
		this.setVisible(true);
	}

	//	JButton loadInputFilesButton = null;
	//	JButton searchButton = null;
	//	TextField searchTextField = null;
	//	JButton fileAnalysisButton = null;
	//	JButton buildGraphButton = null;
	//	JButton checkXMLButton = null;
	//	JButton saveXMLButton = null;
	//	TextField textConsole = null;
	//	Choice entriesList = null;
	//	Choice dataConceptChoice = null;
	//	JButton saveNodeButton = null;
	//	JButton createNodeButton = null;

	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if (source == saveXMLButton){
			textConsole.setText(outPutXMLFile());
		} else if (source == loadInputFilesButton){
			loadInputFile(entriesList);
		} else if (source == fileAnalysisButton){
			String path = selectFile(this, true, "Give the Directory to analyze");
			String result = GenGraphs.doPdfAnalysis(path);
			result = path + "\n" + result;
			textConsole.setText(result);
		} else if (source == buildGraphButton){
			String path = selectFile(this, false, "Give the Graph file");
			GenGraphs.generateGraph(path);
			textConsole.setText("Graph written in:\n"+path);
		} else if (source == checkXMLButton){
			String result = "File Checking :\n";
			result = GenGraphs.checkXMLFile();
			textConsole.setText(result);
		} else if (source == removeXMLButton){
			if (getCurrentEntry() != null){
				textConsole.setText("removing: \n"+getCurrentEntry().getURI());
				getCurrentEntry().removeNode();
				if (getCurrentEntry().getEntryType() == IndexedEntry.typeOfEntries.concept){
					GenGraphs.setSkosFileNeedSaving(true);
				} else {
					GenGraphs.setStorageFileNeedSaving(true);
				}
			}
		} else if (source == createNodeButton){
			createNodeAction();
		}  else if (source == searchButton){
			entriesList.removeAll();
			setChoiceList(entriesList, searchTextField.getText());
		} 
		for (int j=0; j<listTextInput.size(); j++ ){
			JButton but = listTextInputButton.get(j);
			if (but == source){
				if (getCurrentEntry() != null){
					String val = listTextInput.get(j).getText();
					int ie = listTextInputIndex.get(j);
					if (FieldEntry.fieldIsURI(ie)){
						if (!val.isEmpty()){
							getCurrentEntry().setFieldValueAsString(ie, IndexedEntry.getPrefixURI()+val);
						} else {
							getCurrentEntry().setFieldValueAsString(ie,"");
						}
					} else {
						getCurrentEntry().setFieldValueAsString(ie,val);
					}
					if (getCurrentEntry().getEntryType() == IndexedEntry.typeOfEntries.concept){
						GenGraphs.setSkosFileNeedSaving(true);
					} else {
						GenGraphs.setStorageFileNeedSaving(true);
					}
					textConsole.setText(getCurrentEntry().getNodeText());
				}
			}
		}
	}
	public void itemStateChanged(ItemEvent ie) {
		int index = 0;
		if (dataConceptChoice == ie.getSource()){
			switch(dataConceptChoice.getSelectedIndex()){
			case 0:
				setChoiceListDC(entriesList, IndexedEntry.typeOfEntries.concept);
				break;
			case 1:
				setChoiceListDC(entriesList, IndexedEntry.typeOfEntries.data);
				break;
			case 2:
				setChoiceListDC(entriesList, IndexedEntry.typeOfEntries.unknown);
				break;
				
			}
		} else if (entriesList == ie.getSource()){
			String uriselected =  entriesList.getSelectedItem();
			IndexedEntry no = GenGraphs.getAllEntries().searchURI(uriselected);
			setCurrentEntry(no);
			if (getCurrentEntry() == null){
				textConsole.setText(uriselected + " not found");
				return;
			}
			textConsole.setText(no.getNodeText());
			if (getCurrentEntry().getEntryType() == IndexedEntry.typeOfEntries.concept)
				dataConceptChoice.select(0);
			else
				dataConceptChoice.select(1);
			//set all field by default (getCurrentEntry())
			int len = FieldEntry.numberOfFields();
			for (int i=0;i<len;i++){
				String entrytype = FieldEntry.fieldDescription(i)[1];
				if (entrytype.equals("text")){
					String val = getCurrentEntry().getFields()[i].getValue();
					//System.out.println("---> " + val);
					if (val != null) {
						for (int j=0; j<listTextInput.size(); j++ ){
							if (listTextInputIndex.get(j) == i){
								String[] toshow = val.split("#");
								listTextInput.get(j).setText(toshow[toshow.length-1]);
							}
						}
					}
				}
			}
		} else {
			for (Choice temp: listChoiceInput){
				if (temp == ie.getSource()){
					String uriselected =  ((Choice) ie.getSource()).getSelectedItem();
					textConsole.setText(uriselected);

					//				 System.out.println("Choice Happened --> " + listChoiceInputIndex.get(index) 
					//						 + " " + ((Choice) ie.getSource()).getSelectedItem() );
					//				 getCurrentEntry().setFieldValueAsString(listChoiceInputIndex.get(index),
					//						 ((Choice) ie.getSource()).getSelectedItem());
					//				 textConsole.setText(getCurrentEntry().getNodeText());
				}	
				index++;
			}
		}
	}
	void createNodeAction(){
		String newuri = IndexedEntry.getPrefixURI() +listTextInput.get(1).getText();
		if (GenGraphs.getAllEntries().searchURI(newuri) == null){
			IndexedEntry.typeOfEntries t = IndexedEntry.typeOfEntries.unknown;
			int dc = dataConceptChoice.getSelectedIndex();
			switch(dc){
			case 0:
				t =  IndexedEntry.typeOfEntries.concept;
				GenGraphs.setSkosFileNeedSaving(true);
				break;
			case 1:
				t =  IndexedEntry.typeOfEntries.data;
				GenGraphs.setStorageFileNeedSaving(true);
				break;
			}
			IndexedEntry nie = new IndexedEntry(t);
			nie.setURI(newuri);
			setCurrentEntry(nie);
			////////////////////
			int len = listTextInput.size();
			for (int i=0;i<len;i++){
				int index = listTextInputIndex.get(i);
				String value = listTextInput.get(i).getText();
				if (!value.isEmpty()){
					if (FieldEntry.fieldIsURI(i)){
						nie.setFieldValueAsString(index, IndexedEntry.getPrefixURI() + value);
					} else {
						nie.setFieldValueAsString(index,value);
					}
				}
			}
			////////////////////
			textConsole.setText(nie.getNodeText());
			entriesList.removeAll();
			setChoiceList(entriesList,null);
			// need to set on the current one
			
		} else {
			JOptionPane.showMessageDialog(null,"URI already exists: " + newuri,"Erreur", JOptionPane.PLAIN_MESSAGE); 
			//textConsole.setText("URI already exists: " + newuri);
		}
		
	}
	static String selectFile(JFrame fra, Boolean dir, String title){
		if (dir) {
			System.setProperty("apple.awt.fileDialogForDirectories", "true");
		} else {
			System.setProperty("apple.awt.fileDialogForDirectories", "false");
		}
		FileDialog fd = new FileDialog(fra, title, FileDialog.LOAD);
		fd.setDirectory("/Users/bodin/Desktop/");
		fd.setVisible(true);
		String filename = fd.getDirectory() + fd.getFile();
		return filename;
	}

	static void setChoiceList(Choice c, String filter){
		for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
			if (temp.getURI() != null){
				if (filter == null){
					c.add(temp.getURI());
				} else {
					if (temp.getURI().toLowerCase().contains(filter.toLowerCase())){
						c.add(temp.getURI());
					}
				}
			}
		}
	}
	
	static void setChoiceListDC(Choice c, IndexedEntry.typeOfEntries te){
		if (c == null) return;
		c.removeAll();
		for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
			if ((temp.getEntryType() == te)|| (te == IndexedEntry.typeOfEntries.unknown)){
				c.add(temp.getURI());
			} 
		}
	}

	void loadInputFile(Choice c){
		String skospath = selectFile(this, false, "Give the Skos File");
		if (skospath.endsWith(".skos")){
			GenGraphs.setSkosFile(skospath);
			GenGraphs.setSkosTree(GenGraphs.buildXMLTree(GenGraphs.getSkosFile()));
		} else {
			JOptionPane.showMessageDialog(null,"l'extension du fichier doit être .skos","Erreur", JOptionPane.PLAIN_MESSAGE); 
			return;
		}
		String datapath = selectFile(this, false, "Give the Data File");
		if (datapath.endsWith(".data")){
			GenGraphs.setStorageFile(datapath);
			GenGraphs.setStorageTree(GenGraphs.buildXMLTree(GenGraphs.getStorageFile()));
		} else {
			JOptionPane.showMessageDialog(null,"l'extension du fichier doit être .data","Erreur", JOptionPane.PLAIN_MESSAGE); 
			return;
		}
		// looking into the files.
		GenGraphs.builConceptandDataList();
		setChoiceList(c,null);
		GenGraphs.getAllEntries().buildAutocompletionList();
	}

	String outPutXMLFile(){
		// update the XML file before saving them
		for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
			temp.updateNode();
		}
		String res = "";
		if (GenGraphs.getSkosFileNeedSaving()){
			String skospath = selectFile(this, false, "Give the Skos File");
			if (!skospath.endsWith(".skos")){
				JOptionPane.showMessageDialog(null,"l'extension du fichier doit être .skos","Erreur", JOptionPane.PLAIN_MESSAGE); 
				return res;
			}
			try {
				String cmd = "/bin/cp " + skospath + "  " +skospath+".backup"+ GenGraphs.getBackupVersionNumber();
				System.out.println(cmd);
				Process proc =  GenGraphs.getRt().exec(cmd);
				int exitVal;
				try {
					exitVal = proc.waitFor();
					System.out.println("Process exitValue: " + exitVal);
				} catch (InterruptedException e) {
					System.out.println("Process Exception ");
				}
	            
			} catch (IOException e) {
				System.out.println("could not backup old skos file");
			}
			if (!skospath.endsWith("null")) GenGraphs.writeXMLTreeButtonActionSkos(skospath);
			res =  res + "Skos saved in :  "+skospath;
		}
		if (GenGraphs.getStorageFileNeedSaving()){
			String datapath = selectFile(this, false, "Give the Data File");
			if (!datapath.endsWith(".data")){
				JOptionPane.showMessageDialog(null,"l'extension du fichier doit être .data","Erreur", JOptionPane.PLAIN_MESSAGE); 
				return res;
			}
			try {
				String cmd = "/bin/cp " + datapath + "  " +datapath+".backup"+ GenGraphs.getBackupVersionNumber();
				System.out.println(cmd);
				Process proc = GenGraphs.getRt().exec(cmd);
				int exitVal;
				try {
					exitVal = proc.waitFor();
					System.out.println("Process exitValue: " + exitVal); 
				} catch (InterruptedException e) {
					 System.out.println("Process Exception ");
				}
	            
			} catch (IOException e) {
				System.out.println("could not backup old data file");
			}
			if (!datapath.endsWith("null")) GenGraphs.writeXMLTreeButtonActionStorage(datapath);
			res = res +"\n"+ "Data saved in :  "+datapath;
		}
		GenGraphs.setSkosFileNeedSaving(false);
		GenGraphs.setStorageFileNeedSaving(false);
		return res;
	}

	public IndexedEntry getCurrentEntry() {
		return currentEntry;
	}

	public void setCurrentEntry(IndexedEntry currentEntry) {
		this.currentEntry = currentEntry;
	}

	public void keyPressed(KeyEvent arg0) {
		//System.out.println("keyPressed");
	}

	public void keyReleased(KeyEvent arg0) {
		//System.out.println("keyReleased");
	}

	public void keyTyped(KeyEvent arg0) {
		//System.out.println("keyTyped");
		for (int j=0; j<listTextInput.size(); j++ ){
			TextField but = listTextInput.get(j);
			if (but == arg0.getSource()){
				int ie = listTextInputIndex.get(j);
				if (FieldEntry.fieldIsURI(ie)){
					//do auto completion
					String typed = but.getText();
					int position = but.getCaretPosition();
					typed= typed.substring(0, position);
					//System.out.println(typed + "  position is :" + position);
					Boolean found = false;
					for(String st : GenGraphs.getAllEntries().getVocabulary()) {
						if(st.startsWith(typed)) {
							but.setText(st.trim());
							but.setCaretPosition(position);
							found = true;
							break;
						} 
					}
					if (!found){
						but.setText(typed.trim());
					}
				}
			} 
		}
	}

	public TextField getTextConsole() {
		return textConsole;
	}

	public void setTextConsole(TextField textConsole) {
		this.textConsole = textConsole;
	}

}
