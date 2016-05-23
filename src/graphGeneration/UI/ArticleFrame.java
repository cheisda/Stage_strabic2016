package graphGeneration.UI;

import java.awt.Choice;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import graphGeneration.analyse.FileAnalysisResult;
import graphGeneration.db.StrabicDataBase;
import graphGeneration.generation.Article;
import graphGeneration.generation.GenGraphs;
import graphGeneration.generation.IndexedEntry;
import org.jsoup.nodes.Document;

public class ArticleFrame extends JFrame implements ActionListener ,ItemListener, KeyListener {
	final int fsizey = 680;
	final int fsizex = 940;
	TextField textConsole = null;
	public Choice articleList = null;

	JButton loadFile = null;

	TextField saison = null;
	JButton updateSaison = null;

	TextField titre = null;
	JButton updateTitre = null;

	TextField URL = null;
	JButton updateURL = null;
	JButton fetchURL = null;

	TextField image = null;
	JButton updateImage = null;

	JButton loadInputFilesButton = null;

	Choice keywordList = null;
	TextField keywordAdd = null;
	JButton keywordAddButton = null;

	Choice foundList = null;
	TextField foundAdd = null;
	JButton foundAddButton = null;

	JButton createButton = null;

	JButton saveButton = null;

	JButton updateText = null;
	
	JButton analyseText = null;
	
	JButton analyseAllText = null;
	
	JButton importFromDataBase = null;
	
	JButton removeArticle = null;

	public ArticleFrame(){
		super("Article");
		JFrame.setDefaultLookAndFeelDecorated(true);
		this.setResizable(true);
		JPanel mainPanel = new JPanel();
		//this.setLocationRelativeTo(null);
		this.setDefaultLookAndFeelDecorated(true);
		mainPanel.setLayout(null);

		int xoffset = 210;
		int yinc = 40;
		int y = 10;

		loadInputFilesButton = new JButton("Load file xxx.artiste");
		loadInputFilesButton.setLocation(40, y);
		loadInputFilesButton.setSize(200, 30);
		loadInputFilesButton.addActionListener(this);
		mainPanel.add(loadInputFilesButton); 

		textConsole = new TextField("");
		textConsole.setLocation(fsizex/2,y);
		textConsole.setSize(fsizex/2-10, fsizey-10);
		textConsole.addActionListener(this);
		textConsole.setEditable(true); 
		
		mainPanel.add(textConsole); 

		y = y+yinc;
		articleList = new Choice();
		articleList.setLocation(10, y);
		articleList.setSize(400, 30);
		articleList.addItemListener(this);
		mainPanel.add(articleList);

		y = y+yinc;
		saison = new TextField("saison");
		saison.setLocation(10, y);
		saison.setSize(200, 30);
		mainPanel.add(saison); 
		updateSaison = new JButton("update saison");
		updateSaison.setLocation(xoffset, y);
		updateSaison.setSize(200, 30);
		updateSaison.addActionListener(this);
		mainPanel.add(updateSaison);

		y = y+yinc;
		titre = new TextField("titre");
		titre.setLocation(10, y);
		titre.setSize(200, 30);
		mainPanel.add(titre); 
		updateTitre = new JButton("update titre");
		updateTitre.setLocation(xoffset, y);
		updateTitre.setSize(200, 30);
		updateTitre.addActionListener(this);
		mainPanel.add(updateTitre);

		y = y+yinc;
		URL = new TextField("url");
		URL.setLocation(10, y);
		URL.setSize(200, 30);
		mainPanel.add(URL); 
		updateURL = new JButton("update URL");
		updateURL.setLocation(xoffset, y);
		updateURL.setSize(90, 30);
		updateURL.addActionListener(this);
		mainPanel.add(updateURL);
		fetchURL = new JButton("fetch URL");
		fetchURL.setLocation(xoffset+100, y);
		fetchURL.setSize(90, 30);
		fetchURL.addActionListener(this);
		mainPanel.add(fetchURL);

		y = y+yinc;
		image = new TextField("");
		image.setLocation(10, y);
		image.setSize(200, 30);
		mainPanel.add(image); 
		updateImage = new JButton("update image");
		updateImage.setLocation(xoffset, y);
		updateImage.setSize(200, 30);
		updateImage.addActionListener(this);
		mainPanel.add(updateImage);

		y = y+yinc;
		keywordList = new Choice();
		keywordList.setLocation(10, y);
		keywordList.setSize(400, 30);
		keywordList.addItemListener(this);
		mainPanel.add(keywordList);
		//		TextField keywordAdd = null;
		//		JButton keywordAddButton = null;
		y = y+yinc;
		keywordAdd = new TextField("keyword");
		keywordAdd.setLocation(10, y);
		keywordAdd.setSize(200, 30);
		mainPanel.add(keywordAdd); 
		keywordAddButton = new JButton("add keyword");
		keywordAddButton.setLocation(xoffset, y);
		keywordAddButton.setSize(200, 30);
		keywordAddButton.addActionListener(this);
		mainPanel.add(keywordAddButton);


		//		Choice foundList = null;
		//		TextField foundAdd = null;
		//		JButton foundAddButton = null;
		y = y+yinc;
		foundList = new Choice();
		foundList.setLocation(10, y);
		foundList.setSize(400, 30);
		foundList.addItemListener(this);
		mainPanel.add(foundList);
		y = y+yinc;
		foundAdd = new TextField("found");
		foundAdd.setLocation(10, y);
		foundAdd.setSize(200, 30);
		mainPanel.add(foundAdd); 
		foundAddButton = new JButton("add concept");
		foundAddButton.setLocation(xoffset, y);
		foundAddButton.setSize(200, 30);
		foundAddButton.addActionListener(this);
		mainPanel.add(foundAddButton);

		y = y+yinc;
		updateText = new JButton("update Text");
		updateText.setLocation(50, y);
		updateText.setSize(250, 30);
		updateText.addActionListener(this);
		mainPanel.add(updateText);

		y = y+yinc;
		removeArticle = new JButton("Remove article");
		removeArticle.setLocation(50, y);
		removeArticle.setSize(250, 30);
		removeArticle.addActionListener(this);
		mainPanel.add(removeArticle);
		
		
		y = y+yinc;
		createButton = new JButton("Create");
		createButton.setLocation(50, y);
		createButton.setSize(250, 30);
		createButton.addActionListener(this);
		mainPanel.add(createButton);



		y = y+yinc;
		saveButton = new JButton("Save file");
		saveButton.setLocation(50, y);
		saveButton.setSize(250, 30);
		saveButton.addActionListener(this);
		mainPanel.add(saveButton);

		y = y+yinc;
		analyseText = new JButton("Analyse for concepts");
		analyseText.setLocation(50, y);
		analyseText.setSize(250, 30);
		analyseText.addActionListener(this);
		mainPanel.add(analyseText);
		
		y = y+yinc;
		analyseAllText = new JButton("Analyse all texts for concepts");
		analyseAllText.setLocation(50, y);
		analyseAllText.setSize(250, 30);
		analyseAllText.addActionListener(this);
		mainPanel.add(analyseAllText);
		
		y = y+yinc;
		importFromDataBase = new JButton("Import from Data Base");
		importFromDataBase.setLocation(50, y);
		importFromDataBase.setSize(250, 30);
		importFromDataBase.addActionListener(this);
		mainPanel.add(importFromDataBase);
		
		this.setContentPane(mainPanel);
		this.setSize(fsizex, fsizey+30);
		this.setVisible(true);
	}


	public static void setChoiceList(Choice c){
		List<String> ln = new ArrayList();
		if (c == null) return;
		c.removeAll();
		for(Article temp: GenGraphs.getAllEntries().getArticlelist()){
			ln.add(temp.getTitre());
		}
		Collections.sort(ln);
		for(String temp: ln){
			c.add(temp);
		}
	}

	public static void setKeywordList(Choice c, Article a){
		if (c == null) return;
		if (a == null) return;
		c.removeAll();
		for(String temp: a.getKeywords()){
			c.add(temp);
		}
	}

	public static void setFoundList(Choice c, Article a){
		if (c == null) return;
		if (a == null) return;
		c.removeAll();
		for(String temp: a.getFoundURI()){
			c.add(temp);
		}
	}
	
	public void loadInputFile(Choice c){
		String articlepath = MainFrame.selectFile(this, false, "Give the Article File");
		if (articlepath.endsWith(".article")){
			GenGraphs.setArticleFile(articlepath);
			GenGraphs.setArticleTree(GenGraphs.buildXMLTree(GenGraphs.getArticleFile()));
			if (GenGraphs.getArticleTree() == null){
				JOptionPane.showMessageDialog(null,"problème de fichier XML","Erreur Intern", JOptionPane.PLAIN_MESSAGE); 
				return;
			}
			GenGraphs.setArticleRoot(GenGraphs.getArticleTree().getDocumentElement());
		} else {
			JOptionPane.showMessageDialog(null,"l'extension du fichier doit être .article","Erreur", JOptionPane.PLAIN_MESSAGE); 
			return;
		}
		GenGraphs.builArticlessList();
		setChoiceList(c);
		//TODO LIST CAN BE EMPTY HERE.... NEED TO BE CHECKED
		if (GenGraphs.getAllEntries().numberOfArticles() > 0){
			articleList.select(0);
			String ti =  articleList.getSelectedItem();
			updateDisplayFields(ti);
		}
	}

	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	void updateDisplayFields(String ti){
		if (ti == null) return;
		Article no = GenGraphs.getAllEntries().searchArticle(ti);
		if (no == null) return;
		//System.out.println("In itemStateChanged articleList-2");
		if (no.getSaison() != null) saison.setText(no.getSaison());
		if (no.getTitre() != null) titre.setText(no.getTitre());
		if (no.getURL() != null) URL.setText(no.getURL());
		if (no.getThumbnail() != null) image.setText(no.getThumbnail());
		if (no.getText() != null) textConsole.setText(no.getText());
		setKeywordList(keywordList,no);
		setFoundList(foundList,no);
	}
	public void itemStateChanged(ItemEvent ie) {
		if (articleList == ie.getSource()){
			String ti =  articleList.getSelectedItem();
			updateDisplayFields(ti);
		}	
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == analyseAllText) {
			for(Article no: GenGraphs.getAllEntries().getArticlelist()){
				FileAnalysisResult fa = no.analyseConcept();
			}
			JOptionPane.showMessageDialog(null,"Analyse des articles terminée","Message", JOptionPane.PLAIN_MESSAGE); 
		} else if (source == analyseText) {
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			FileAnalysisResult fa = no.analyseConcept();
			String rea ="";
			int nbc = fa.numberOfKeywords();
			rea = rea + "concept found: " + nbc + "\n";
			JOptionPane.showMessageDialog(null,rea,"Message", JOptionPane.PLAIN_MESSAGE); 
			updateDisplayFields(ti);
		} else if (source == loadInputFilesButton){
			loadInputFile(articleList);
		} else if (source == keywordAddButton){
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			if (keywordAdd.getText().isEmpty()) return;
			if (no.searchKeyword(IndexedEntry.getPrefixURI()+keywordAdd.getText())!= null){
				JOptionPane.showMessageDialog(null,"le Keyword existe déjà","Erreur", JOptionPane.PLAIN_MESSAGE); 
				return;
			}
			no.getKeywords().add(IndexedEntry.getPrefixURI()+keywordAdd.getText());
			setKeywordList(keywordList,no);
			no.setNeedUpdate(true);
		} else if (source == foundAddButton){
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			if (foundAdd.getText().isEmpty()) return;
			if (no.searchConcept(IndexedEntry.getPrefixURI()+foundAdd.getText())!= null){
				JOptionPane.showMessageDialog(null,"le concept existe déjà","Erreur", JOptionPane.PLAIN_MESSAGE); 
				return;
			}
			no.getFoundURI().add(IndexedEntry.getPrefixURI()+foundAdd.getText());
			setFoundList(foundList,no);
			no.setNeedUpdate(true);
		} else if (source == updateText){
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			if (textConsole.getText().isEmpty()) return;
			no.setText(textConsole.getText());
			no.setNeedUpdate(true);
		} else if (source == updateTitre){
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			if (titre.getText().isEmpty()) return;
			no.setTitre(titre.getText());
			no.setNeedUpdate(true);
			setChoiceList(articleList);
			articleList.select(no.getTitre());
		} else if (source == updateImage){
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			no.setThumbnail(image.getText());
			no.setNeedUpdate(true);
		} else if (source == updateURL){
			String ti =  articleList.getSelectedItem();
			Article no = GenGraphs.getAllEntries().searchArticle(ti);
			if (no == null) return;
			no.setURL(URL.getText());
			no.setNeedUpdate(true);
		} else if (source == fetchURL){
			String ur = URL.getText();
			Query q = new Query();
			Document doc = q.get(ur);
			if (doc== null){
				textConsole.setText("URL not found");
			} else {
				String title = doc.title();
				if (title != null)
					textConsole.setText("titre page: "+title);
			}
		} 
		else if (source == createButton){
			Article no = GenGraphs.getAllEntries().searchArticle(titre.getText());
			if (no != null){
				JOptionPane.showMessageDialog(null,"L'article existe déjà","Erreur", JOptionPane.PLAIN_MESSAGE); 
				return;
			}
			no = new Article();
			GenGraphs.getAllEntries().getArticlelist().add(no);
			no.setTitre(titre.getText());
			no.setSaison(saison.getText());
			no.setURL(URL.getText());
			no.setThumbnail(image.getText());
			no.setText(textConsole.getText());
			setChoiceList(articleList);
			articleList.select(no.getTitre());
			no.setNeedUpdate(true);
		} else if (source == saveButton){
			String p = outPutXMLFile();
			textConsole.setText("File saved in:\n"+p);
		} else if (source == importFromDataBase){
			StrabicDataBase db = new StrabicDataBase();
			db.importDB();
			System.out.println("Imported "+ GenGraphs.getAllEntries().getArticlelist().size() +" articles");
			if (GenGraphs.getAllEntries().getArticlelist().size() > 0){
				for (Article a: GenGraphs.getAllEntries().getArticlelist()){
					String filename = "/tmp/"+a.getURLend().trim().replace('/', '_') + ".txt";
					System.out.println("Writing file: "+filename);
					a.writeInFile(filename);	
				}
			}
		} else if (source == removeArticle){
			JOptionPane.showMessageDialog(null,"Not implemented yet","Message", JOptionPane.PLAIN_MESSAGE); 
		}
	}

	static String outPutXMLFile(){
		String res="";
		String path = GenGraphs.getArticleFile();
		if (path == null) return res;
		for(Article temp: GenGraphs.getAllEntries().getArticlelist()){
			temp.updateNode();
		}
		try {
			String cmd = "/bin/cp " + path + "  " +path+".backup"+ GenGraphs.getBackupVersionNumber();
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
			System.out.println("could not backup old article file");
		}
		GenGraphs.writeXMLTree(GenGraphs.getArticleFile(), GenGraphs.getArticleTree());
		res = path;
		return res;
	}
}
