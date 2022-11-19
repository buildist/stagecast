/* FatalErrorAWTDialog - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class FatalErrorAWTDialog extends Dialog
    implements ResourceIDs.DialogIDs
{
    public FatalErrorAWTDialog(Frame parent) {
	super(parent);
	String title = "Fatal Error";
	String label
	    = "An unexpected error has occurred.  There may not be enough memory to run this application.";
	String button = "quit";
	try {
	    title = Resource.getText("dialog fatal title");
	    label = Resource.getText("dialog fatal 4 text");
	    button = Resource.getText("dialog fatal 4 button");
	} catch (Throwable throwable) {
	    /* empty */
	}
	this.setTitle(title);
	this.setResizable(false);
	java.awt.event.WindowListener listener = new WindowAdapter() {
	    public void windowClosing(WindowEvent event) {
		FatalErrorAWTDialog.this.performQuit();
	    }
	};
	this.addWindowListener(listener);
	populate(label, button);
	Dimension screenSize = this.getToolkit().getScreenSize();
	Dimension ourSize = this.getSize();
	Point ourPosition
	    = new Point((screenSize.width - ourSize.width) / 2,
			(screenSize.height - ourSize.height) / 3);
	this.setLocation(ourPosition);
	parent.show();
	this.show();
    }
    
    private void populate(String labelTitle, String buttonTitle) {
	Label label = new Label(labelTitle);
	Panel panel = new Panel();
	Button button = new Button(buttonTitle);
	panel.add(button);
	this.add(label, "Center");
	this.add(panel, "South");
	ActionListener listener = new ActionListener() {
	    public void actionPerformed(ActionEvent event) {
		FatalErrorAWTDialog.this.performQuit();
	    }
	};
	button.addActionListener(listener);
	java.awt.event.KeyListener listener2 = new KeyAdapter() {
	    public void keyPressed(KeyEvent event) {
		if (event.getKeyCode() == 10)
		    FatalErrorAWTDialog.this.performQuit();
	    }
	};
	button.addKeyListener(listener2);
	this.pack();
    }
    
    private void performQuit() {
	this.setVisible(false);
	this.dispose();
	System.exit(0);
    }
}
