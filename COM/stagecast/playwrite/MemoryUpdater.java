/* MemoryUpdater - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.TextField;
import COM.stagecast.ifc.netscape.application.View;

class MemoryUpdater implements Runnable
{
    TextField _text;
    TextField _percentText;
    View _percentView;
    
    MemoryUpdater(TextField text, TextField percentText, View percentView) {
	_text = text;
	_percentText = percentText;
	_percentView = percentView;
	new Thread(new FatalErrorNotifier(this), "MemoryUpdater").start();
    }
    
    private String kb(long bytes) {
	return (int) (bytes / 1024L) + "K";
    }
    
    public void run() {
	for (;;) {
	    long fm = Runtime.getRuntime().freeMemory();
	    long tm = Runtime.getRuntime().totalMemory();
	    long used = tm - fm;
	    int ratio = (int) (used * 100L / tm);
	    _text.setStringValue(kb(used) + "/" + kb(tm));
	    _percentText.setStringValue(ratio + "%");
	    _percentView.sizeTo(ratio * _percentText.width() / 100,
				_percentView.height());
	    _percentView.superview().setDirty(true);
	    try {
		Thread.sleep(2000L);
	    } catch (InterruptedException interruptedexception) {
		/* empty */
	    }
	}
    }
}
