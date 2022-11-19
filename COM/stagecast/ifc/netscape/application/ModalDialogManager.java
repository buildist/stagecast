/* ModalDialogManager - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import java.awt.Dialog;

class ModalDialogManager implements Runnable
{
    private Dialog modalDialog;
    
    ModalDialogManager(Dialog dialog) {
	modalDialog = dialog;
    }
    
    void show() {
	Thread thread = new Thread(this);
	thread.start();
    }
    
    public void run() {
	modalDialog.show();
    }
}
