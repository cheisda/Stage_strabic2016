package graphGeneration.UI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

// this class takes the text in a shared variable
public class FrameForTextOut extends JFrame implements ActionListener{

	public FrameForTextOut(String toDisplay, String namew) { // the frame constructor method
		super(namew);
        // Display the window.
		this.setLocationRelativeTo(null);
        this.setSize(550, 400);
        this.setVisible(true);
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Lets try Scolling panel 
        JTextArea textArea = new JTextArea(23, 40);
		this.getContentPane().setLayout(new FlowLayout());
		JScrollPane scrollableTextArea = new JScrollPane(textArea);
		int horizontalPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		int vericalPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED;
		scrollableTextArea.setHorizontalScrollBarPolicy(horizontalPolicy);
		scrollableTextArea.setVerticalScrollBarPolicy(vericalPolicy);
		this.getContentPane().add(scrollableTextArea);
		textArea.setText(toDisplay);
		this.setVisible(true); 
	}
	
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
	}
}
