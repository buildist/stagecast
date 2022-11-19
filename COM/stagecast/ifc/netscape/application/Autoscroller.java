/* Autoscroller - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class Autoscroller implements Target
{
    private MouseEvent _event;
    
    public void performCommand(String string, Object object) {
	View view = (View) object;
	view.mouseDragged(view.rootView().convertEventToView(view, _event));
    }
    
    public void setEvent(MouseEvent mouseevent) {
	_event = mouseevent;
    }
}
