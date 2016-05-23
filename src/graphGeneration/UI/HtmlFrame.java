package graphGeneration.UI;

import graphGeneration.generation.Article;
import graphGeneration.generation.GenGraphs;

import java.awt.Choice;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class HtmlFrame extends JFrame implements ActionListener ,ItemListener, KeyListener {
	final int fsizey = 680;
	final int fsizex = 940;
	TextField textConsole = null;
	static String generatorPath = "/Users/bodin/genhtml-strabic-read-only/strabicGenHTML.sh";
	static String defaultHTMLOutputFile = "/Users/bodin/Desktop/strabicG.html";
	static String defaultHTMLInputFile = "/Users/bodin/Desktop/Strabic/graph_SAISON.graphml";
	static String whereToEXecute = "/Users/bodin/genhtml-strabic-read-only";
	static String errorFile =  "/Users/bodin/genhtml-strabic-read-only/errorFile.txt";
	JButton generateHTML = null;


	public HtmlFrame(){
		super("HTML Generation Control Panel");
		JFrame.setDefaultLookAndFeelDecorated(true);
		this.setResizable(true);
		JPanel mainPanel = new JPanel();
		this.setLocationRelativeTo(GenGraphs.articlegui);
		this.setDefaultLookAndFeelDecorated(true);
		mainPanel.setLayout(null);

		int xoffset = 210;
		int yinc = 40;
		int y = 10;
		
		File f = new File(generatorPath);
		if(!f.exists() ||f.isDirectory()) { 
			generatorPath = MainFrame.selectFile(GenGraphs.articlegui, false, "Give the HTML generator");
		}
		if (generatorPath == null){
			System.exit(0);
		}
		if(!f.exists() ||f.isDirectory()) { 
			generatorPath = null;
			return;
		}
		
		generateHTML = new JButton("generate HTML");
		generateHTML.setLocation(40, y);
		generateHTML.setSize(200, 30);
		generateHTML.addActionListener(this);
		mainPanel.add(generateHTML); 
		
		textConsole = new TextField("");
		textConsole.setLocation(fsizex/2,y);
		textConsole.setSize(fsizex/2-10, fsizey/2);
		textConsole.addActionListener(this);
		textConsole.setEditable(true); 
		
		mainPanel.add(textConsole); 
		this.setContentPane(mainPanel);
		this.setSize(fsizex, fsizey+30);
		this.setVisible(true);
	}


	static void setChoiceList(Choice c){
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

	static void setKeywordList(Choice c, Article a){
		if (c == null) return;
		if (a == null) return;
		c.removeAll();
		for(String temp: a.getKeywords()){
			c.add(temp);
		}
	}
	static void setFoundList(Choice c, Article a){
		if (c == null) return;
		if (a == null) return;
		c.removeAll();
		for(String temp: a.getFoundURI()){
			c.add(temp);
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


	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source == generateHTML) {
			loadInputFile();
			try {
				String cmd = generatorPath + " "+ defaultHTMLInputFile + " " + defaultHTMLOutputFile;
				//String cmd = "echo \"xxx\";
				System.out.println(cmd);
				Process proc =  GenGraphs.getRt().exec(cmd);
				int exitVal;
				try {
					exitVal = proc.waitFor();
					System.out.println("Process exitValue: " + exitVal);
					if (exitVal !=0){
						textConsole.setText(cmd + "\n" + "process exit value = " + exitVal +"\n");
					} else {
						textConsole.setText("Generation done");
					}
				} catch (InterruptedException e) {
					textConsole.setText("could not generate the HTML: Process Exception ");
				}
			} catch (IOException e) {
				textConsole.setText("could not generate the HTML");
			}
		}
	}
	
	void loadInputFile(){
		String gpath = MainFrame.selectFile(this, false, "Give the graph file");
		if (gpath.endsWith(".graphml")){
			defaultHTMLInputFile = gpath;
		} else {
			JOptionPane.showMessageDialog(null,"Taking the default Graph file","Erreur", JOptionPane.PLAIN_MESSAGE); 
		}
		String opath = MainFrame.selectFile(this, false, "Give the output HTML file");
		if (opath.endsWith(".html")){
			defaultHTMLOutputFile = opath;
		} else {
			JOptionPane.showMessageDialog(null,"Taking the default HTML output file","Erreur", JOptionPane.PLAIN_MESSAGE); 
			return;
		}
	}

}
