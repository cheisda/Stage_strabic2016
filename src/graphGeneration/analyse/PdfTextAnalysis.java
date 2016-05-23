package graphGeneration.analyse;

import graphGeneration.generation.GenGraphs;
import graphGeneration.generation.IndexedEntry;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class PdfTextAnalysis {
	String text = "";
	static ArrayList<FileAnalysisResult> results = new ArrayList<FileAnalysisResult>() ;
	private FileAnalysisResult current = null;
	
	public PdfTextAnalysis(String filename, Boolean inputisfile){
		if (filename == null) return;
		current = new FileAnalysisResult(filename,null); // filename is the URI in this case
		results.add(current);
		if (!inputisfile) return;
		if (filename.endsWith(".txt")){
			setText(readTxtFile(filename));
		} else if (filename.endsWith(".pdf")){
			setText(readPDFFile(filename));
		} else if (filename.endsWith(".html")){
			setText(readHtmlFile(filename));
		}
	}
	
	// pb des mots courts --> contains inapproprié
	public String searchKeywordInText(){
		String result ="";
		String[] tmp = getText().split(" ");
		String previous = "";
		if (current == null) return result;
		GenGraphs.getAllEntries().unsetFoundFlag();
		for (int j=0; j< tmp.length; j++){
			for(IndexedEntry temp: GenGraphs.getAllEntries().getGlobalist()){
				if (!temp.getFound() && (temp.getEntryType() == IndexedEntry.typeOfEntries.concept)){
					if (temp.getPreflabel() != null){
						int lendiff = tmp[j].length()-(temp.getPreflabelWords()[temp.getPreflabelWords().length-1]).length();
						if (lendiff < 0) lendiff = - lendiff;
						if (lendiff <=2){
							if (tmp[j].toLowerCase().contains(temp.getPreflabelWords()[temp.getPreflabelWords().length-1])){
								if (temp.getPreflabelWords().length == 1){
									result = result + temp.getPreflabel() + "\n";
									current.addKeyWord(temp.getPreflabel(),temp.getURI());
									System.out.println("found --> " + temp.getPreflabel());
									temp.setFound(true);
								} else {
									if (temp.getPreflabelWords().length > 1){
										if (previous.contains(temp.getPreflabelWords()[temp.getPreflabelWords().length-2])){
											result = result + temp.getPreflabel() + "\n";
											current.addKeyWord(temp.getPreflabel(),temp.getURI());
											System.out.println("found --> " + temp.getPreflabel());
											//System.out.println("words --> " + temp.getPreflabelWords()[0]);
											temp.setFound(true);
										}
									}
								}
							}
						}
					}
				}
			}
			previous = tmp[j].toLowerCase();
		}
		return result;
	}

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	static String readHtmlFile(String pathname) {
		//To be modified
		String res = StringEscapeUtils.unescapeHtml(readTxtFile(pathname));
		return res;
	}
	
	static String readTxtFile(String pathname) {
		File file = new File(pathname);
		StringBuilder fileContents = new StringBuilder((int)file.length());
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			String lineSeparator = System.getProperty("line.separator");

			try {
				while(scanner.hasNextLine()) {        
					fileContents.append(scanner.nextLine() + lineSeparator);
				}
				return fileContents.toString();
			} finally {
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	static String readPDFFile(String filename) {
		PDDocument pd;
		BufferedWriter wr;
		File input = new File(filename);  
		File output = new File("/Users/bodin/Desktop/strabic/files/filepdf.txt"); 
		System.out.println("-------- Analyzing pdf --------");
		try {
			pd = PDDocument.load(input);
			System.out.println("Number of pages: " + pd.getNumberOfPages());
			System.out.println("Pdf is encrypted :" + pd.isEncrypted());
			//pd.save("pdffilecopy.pdf"); // Creates a copy called "pdffilecopy.pdf"
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setLineSeparator(" ");
			stripper.setStartPage(0); //Start extracting from page 0
			stripper.setEndPage(pd.getNumberOfPages()-1); //Extract till page X

			wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			stripper.writeText(pd, wr);

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			stripper.writeText(pd, new OutputStreamWriter(bout));
//			if (getText().isEmpty()) {
//				System.out.println("No words extracted!");
//			}
			//System.out.println(getText());
			if (pd != null) {
				pd.close();
			}
			// I use close() to flush the stream.
			//wr.close();
			return bout.toString();
		} catch (Exception e){
			e.printStackTrace();
		} 
		return "";
	}
//	public void  ReadDocFile() {
//		File file = null;
//		WordExtractor extractor = null ;
//		try {
//
//			file = new File("c:\\New.doc");
//			FileInputStream fis=new FileInputStream(file.getAbsolutePath());
//			HWPFDocument document=new HWPFDocument(fis);
//			extractor = new WordExtractor(document);
//			String [] fileData = extractor.getParagraphText();
//			for(int i=0;i<fileData.length;i++){
//				if(fileData[i] != null)
//					System.out.println(fileData[i]);
//			}
//		}
//		catch(Exception exep){}
//	}

	public static ArrayList<FileAnalysisResult> getResults() {
		return results;
	}
	public static int nbResults(){
		return results.size();
	}
	public static FileAnalysisResult getResult(int index){
		return results.get(index);
	}
	public static FileAnalysisResult getLastResult(){
		if (results.size() < 1) return null;
		return results.get(results.size()-1);
	}
}
