/* GraphicsState - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

class GraphicsState implements Cloneable
{
    java.awt.Graphics awtGraphics;
    Font font;
    Color color;
    Rect clipRect;
    Rect absoluteClipRect;
    int xOffset;
    int yOffset;
    Color xorColor;
    int debugOptions;
    static final int LOG_OPTION = 1;
    static final int FLASH_OPTION = 2;
    static final int BUFFERED_OPTION = 4;
    static final int NONE_OPTION = -1;
    
    public Object clone() {
	GraphicsState graphicsstate_0_ = null;
	try {
	    graphicsstate_0_ = (GraphicsState) super.clone();
	} catch (Exception exception) {
	    /* empty */
	}
	if (graphicsstate_0_ != null) {
	    graphicsstate_0_.clipRect = null;
	    graphicsstate_0_.absoluteClipRect = null;
	    graphicsstate_0_.awtGraphics = null;
	}
	return graphicsstate_0_;
    }
    
    public String toString() {
	StringBuffer stringbuffer = new StringBuffer();
	stringbuffer.append("Font: " + font + ", ");
	stringbuffer.append("Color: " + color + ", ");
	stringbuffer
	    .append("Translation: (" + xOffset + ", " + yOffset + "), ");
	stringbuffer.append("xor: " + xorColor + ", ");
	stringbuffer.append("absoluteClipRect: " + absoluteClipRect + ", ");
	stringbuffer.append("debugOption: " + debugOptions);
	return stringbuffer.toString();
    }
    
    boolean debugLog() {
	return (debugOptions & 0x1) == 1;
    }
    
    boolean debugFlash() {
	return (debugOptions & 0x2) == 2;
    }
    
    boolean debugBuffered() {
	return (debugOptions & 0x4) == 4;
    }
}
