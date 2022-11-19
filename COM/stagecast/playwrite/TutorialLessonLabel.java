/* TutorialLessonLabel - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.MouseEvent;

public class TutorialLessonLabel extends ButtonLabel
{
    String _worldName;
    
    TutorialLessonLabel(PlaywriteButton button, PlaywriteView nameView,
			String worldName) {
	super(button, nameView, 0);
	_worldName = worldName;
    }
    
    public int referenceX() {
	return _nameView.x();
    }
    
    public void moveByReferencePoint(int x, int y) {
	int ref = referenceX();
	this.moveTo(x - ref, y);
    }
    
    public void mouseUp(MouseEvent event) {
	if (this.localBounds().contains(event.x, event.y)
	    && this.window() instanceof TutorialSplashScreen)
	    ((TutorialSplashScreen) this.window()).openWorld(_worldName);
    }
}
