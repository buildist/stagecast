/* Graphics - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;
import COM.stagecast.ifc.netscape.util.InconsistencyException;
import COM.stagecast.ifc.netscape.util.Vector;

public class Graphics
{
    Vector graphicsStates;
    java.awt.Graphics primaryAwtGraphics;
    java.awt.Graphics currentAwtGraphics;
    static int graphicsCount = 0;
    int graphicsID;
    Rect primaryClipRect;
    Bitmap buffer;
    public static final int LEFT_JUSTIFIED = 0;
    public static final int CENTERED = 1;
    public static final int RIGHT_JUSTIFIED = 2;
    
    public Graphics(View view) {
	graphicsStates = new Vector();
	graphicsID = graphicsCount++;
	Rect rect = view.convertRectToView(null, view.bounds);
	rect.x -= view.bounds.x;
	rect.y -= view.bounds.y;
	buffer = null;
	init(rect, view.rootView().panel.getGraphics());
    }
    
    public Graphics(Bitmap bitmap) {
	graphicsStates = new Vector();
	graphicsID = graphicsCount++;
	buffer = bitmap;
	init(new Rect(0, 0, bitmap.width(), bitmap.height()),
	     bitmap.awtImage().getGraphics());
    }
    
    Graphics(Rect rect, java.awt.Graphics graphics_0_) {
	graphicsStates = new Vector();
	graphicsID = graphicsCount++;
	init(new Rect(rect), graphics_0_);
    }
    
    static Graphics newGraphics(View view) {
	return new Graphics(view);
    }
    
    static Graphics newGraphics(Bitmap bitmap) {
	return new Graphics(bitmap);
    }
    
    void init(Rect rect, java.awt.Graphics graphics_1_) {
	GraphicsState graphicsstate = new GraphicsState();
	graphicsStates.addElement(graphicsstate);
	graphicsstate.absoluteClipRect = rect;
	primaryAwtGraphics = graphics_1_;
	currentAwtGraphics = primaryAwtGraphics;
	primaryAwtGraphics.clipRect(rect.x, rect.y, rect.width, rect.height);
	primaryClipRect = rect;
	graphicsstate.xOffset = rect.x;
	graphicsstate.yOffset = rect.y;
    }
    
    public void pushState() {
	GraphicsState graphicsstate = state();
	GraphicsState graphicsstate_2_;
	if (graphicsstate != null)
	    graphicsstate_2_ = (GraphicsState) graphicsstate.clone();
	else
	    graphicsstate_2_ = new GraphicsState();
	graphicsStates.addElement(graphicsstate_2_);
    }
    
    final void restoreAwtGraphics(java.awt.Graphics graphics_3_,
				  GraphicsState graphicsstate) {
	if (graphicsstate.font != null) {
	    if (graphicsstate.font.wasDownloaded())
		graphics_3_.setFont(null);
	    else
		graphics_3_.setFont(graphicsstate.font._awtFont);
	}
	if (graphicsstate.color == null)
	    graphics_3_.setColor(null);
	else {
	    graphics_3_.setColor(graphicsstate.color._color);
	    if (graphicsstate.xorColor != null)
		graphics_3_.setXORMode(graphicsstate.xorColor._color);
	    else
		graphics_3_.setPaintMode();
	}
    }
    
    public void popState() {
	GraphicsState graphicsstate = state();
	graphicsStates.removeLastElement();
	if (graphicsstate.awtGraphics != null) {
	    graphicsstate.awtGraphics.dispose();
	    currentAwtGraphics = awtGraphics();
	}
	graphicsstate = state();
	if (graphicsstate != null)
	    restoreAwtGraphics(currentAwtGraphics, graphicsstate);
    }
    
    final GraphicsState state() {
	return (GraphicsState) graphicsStates.lastElement();
    }
    
    java.awt.Graphics awtGraphics() {
	int i = graphicsStates.count();
	while (i-- > 0) {
	    GraphicsState graphicsstate
		= (GraphicsState) graphicsStates.elementAt(i);
	    if (graphicsstate.awtGraphics != null)
		return graphicsstate.awtGraphics;
	}
	return primaryAwtGraphics;
    }
    
    public void setFont(Font font) {
	GraphicsState graphicsstate = state();
	graphicsstate.font = font;
	if (font == null || font.wasDownloaded())
	    currentAwtGraphics.setFont(null);
	else
	    currentAwtGraphics.setFont(font._awtFont);
    }
    
    public Font font() {
	return state().font;
    }
    
    public void setColor(Color color) {
	GraphicsState graphicsstate = state();
	graphicsstate.color = color;
	currentAwtGraphics.setColor(color == null ? null : color._color);
    }
    
    public Color color() {
	return state().color;
    }
    
    public void translate(int i, int i_4_) {
	GraphicsState graphicsstate = state();
	graphicsstate.xOffset += i;
	graphicsstate.yOffset += i_4_;
	graphicsstate.clipRect = null;
    }
    
    public int xTranslation() {
	GraphicsState graphicsstate = state();
	return graphicsstate.xOffset;
    }
    
    public int yTranslation() {
	GraphicsState graphicsstate = state();
	return graphicsstate.yOffset;
    }
    
    public Point translation() {
	GraphicsState graphicsstate = state();
	return new Point(graphicsstate.xOffset, graphicsstate.yOffset);
    }
    
    public void setClipRect(Rect rect, boolean bool) {
	GraphicsState graphicsstate = state();
	Rect rect_5_ = absoluteClipRect();
	Rect rect_6_;
	if (rect == null) {
	    rect_6_ = primaryClipRect;
	    Rect rect_7_ = rect_6_;
	    rect_6_ = new Rect(rect_6_);
	} else {
	    Rect rect_8_ = rect;
	    rect_6_ = new Rect(rect);
	    rect_6_.moveBy(graphicsstate.xOffset, graphicsstate.yOffset);
	}
	if (bool)
	    rect_6_.intersectWith(rect_5_);
	if (!rect_6_.equals(rect_5_)) {
	    if (!bool && graphicsstate.awtGraphics != null) {
		graphicsstate.awtGraphics.dispose();
		graphicsstate.awtGraphics = null;
	    }
	    if (graphicsstate.awtGraphics == null) {
		graphicsstate.awtGraphics = primaryAwtGraphics.create();
		currentAwtGraphics = graphicsstate.awtGraphics;
		if (graphicsstate.color != null) {
		    currentAwtGraphics.setColor(Color.white._color);
		    currentAwtGraphics.setColor(graphicsstate.color._color);
		}
	    }
	    currentAwtGraphics.clipRect(rect_6_.x, rect_6_.y, rect_6_.width,
					rect_6_.height);
	    graphicsstate.absoluteClipRect = rect_6_;
	    graphicsstate.clipRect = null;
	    restoreAwtGraphics(currentAwtGraphics, graphicsstate);
	}
    }
    
    public void setClipRect(Rect rect) {
	setClipRect(rect, true);
    }
    
    public Rect clipRect() {
	GraphicsState graphicsstate = state();
	if (graphicsstate.clipRect != null)
	    return graphicsstate.clipRect;
	int i = graphicsStates.count();
	while (i-- > 0) {
	    GraphicsState graphicsstate_9_
		= (GraphicsState) graphicsStates.elementAt(i);
	    if (graphicsstate_9_.absoluteClipRect != null) {
		graphicsstate.clipRect
		    = new Rect(graphicsstate_9_.absoluteClipRect);
		graphicsstate.clipRect.moveBy(-graphicsstate.xOffset,
					      -graphicsstate.yOffset);
		return graphicsstate.clipRect;
	    }
	}
	return null;
    }
    
    Rect absoluteClipRect() {
	int i = graphicsStates.count();
	while (i-- > 0) {
	    GraphicsState graphicsstate
		= (GraphicsState) graphicsStates.elementAt(i);
	    if (graphicsstate.absoluteClipRect != null)
		return graphicsstate.absoluteClipRect;
	}
	return null;
    }
    
    public void clearClipRect() {
	setClipRect(null, false);
    }
    
    public Bitmap buffer() {
	return buffer;
    }
    
    public boolean isDrawingBuffer() {
	return buffer != null;
    }
    
    public void dispose() {
	int i = graphicsStates.count();
	while (i-- > 0) {
	    GraphicsState graphicsstate
		= (GraphicsState) graphicsStates.elementAt(i);
	    if (graphicsstate.awtGraphics != null)
		graphicsstate.awtGraphics.dispose();
	}
	graphicsStates.removeAllElements();
	primaryAwtGraphics.dispose();
	primaryAwtGraphics = null;
	currentAwtGraphics = null;
    }
    
    public void sync() {
	Application.application().syncGraphics();
    }
    
    public void setPaintMode() {
	GraphicsState graphicsstate = state();
	graphicsstate.xorColor = null;
	currentAwtGraphics.setPaintMode();
    }
    
    public void setXORMode(Color color) {
	if (color == null)
	    setPaintMode();
	else {
	    GraphicsState graphicsstate = state();
	    graphicsstate.xorColor = color;
	    currentAwtGraphics.setXORMode(color._color);
	}
    }
    
    public void drawRect(Rect rect) {
	drawRect(rect.x, rect.y, rect.width, rect.height);
    }
    
    public void drawRect(int i, int i_10_, int i_11_, int i_12_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_10_ += graphicsstate.yOffset;
	currentAwtGraphics.drawRect(i, i_10_, i_11_ - 1, i_12_ - 1);
    }
    
    public void fillRect(Rect rect) {
	fillRect(rect.x, rect.y, rect.width, rect.height);
    }
    
    public void fillRect(int i, int i_13_, int i_14_, int i_15_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_13_ += graphicsstate.yOffset;
	currentAwtGraphics.fillRect(i, i_13_, i_14_, i_15_);
    }
    
    public void drawRoundedRect(Rect rect, int i, int i_16_) {
	drawRoundedRect(rect.x, rect.y, rect.width, rect.height, i, i_16_);
    }
    
    public void drawRoundedRect(int i, int i_17_, int i_18_, int i_19_,
				int i_20_, int i_21_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_17_ += graphicsstate.yOffset;
	currentAwtGraphics.drawRoundRect(i, i_17_, i_18_ - 1, i_19_ - 1, i_20_,
					 i_21_);
    }
    
    public void fillRoundedRect(Rect rect, int i, int i_22_) {
	fillRoundedRect(rect.x, rect.y, rect.width, rect.height, i, i_22_);
    }
    
    public void fillRoundedRect(int i, int i_23_, int i_24_, int i_25_,
				int i_26_, int i_27_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_23_ += graphicsstate.yOffset;
	currentAwtGraphics.fillRoundRect(i, i_23_, i_24_, i_25_, i_26_, i_27_);
    }
    
    public void drawLine(int i, int i_28_, int i_29_, int i_30_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_28_ += graphicsstate.yOffset;
	i_29_ += graphicsstate.xOffset;
	i_30_ += graphicsstate.yOffset;
	currentAwtGraphics.drawLine(i, i_28_, i_29_, i_30_);
    }
    
    public void drawPoint(int i, int i_31_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_31_ += graphicsstate.yOffset;
	currentAwtGraphics.drawLine(i, i_31_, i, i_31_);
    }
    
    public void drawOval(Rect rect) {
	drawOval(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }
    
    public void drawOval(int i, int i_32_, int i_33_, int i_34_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_32_ += graphicsstate.yOffset;
	currentAwtGraphics.drawOval(i, i_32_, i_33_, i_34_);
    }
    
    public void fillOval(Rect rect) {
	fillOval(rect.x, rect.y, rect.width, rect.height);
    }
    
    public void fillOval(int i, int i_35_, int i_36_, int i_37_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_35_ += graphicsstate.yOffset;
	currentAwtGraphics.fillOval(i, i_35_, i_36_, i_37_);
    }
    
    public void drawArc(Rect rect, int i, int i_38_) {
	drawArc(rect.x, rect.y, rect.width, rect.height, i, i_38_);
    }
    
    public void drawArc(int i, int i_39_, int i_40_, int i_41_, int i_42_,
			int i_43_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_39_ += graphicsstate.yOffset;
	currentAwtGraphics.drawArc(i, i_39_, i_40_, i_41_, i_42_, i_43_);
    }
    
    public void fillArc(Rect rect, int i, int i_44_) {
	fillArc(rect.x, rect.y, rect.width, rect.height, i, i_44_);
    }
    
    public void fillArc(int i, int i_45_, int i_46_, int i_47_, int i_48_,
			int i_49_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_45_ += graphicsstate.yOffset;
	currentAwtGraphics.fillArc(i, i_45_, i_46_, i_47_, i_48_, i_49_);
    }
    
    public void drawPolygon(int[] is, int[] is_50_, int i) {
	GraphicsState graphicsstate = state();
	if (graphicsstate.xOffset != 0 || graphicsstate.yOffset != 0) {
	    currentAwtGraphics.translate(graphicsstate.xOffset,
					 graphicsstate.yOffset);
	    currentAwtGraphics.drawPolygon(is, is_50_, i);
	    currentAwtGraphics.translate(-graphicsstate.xOffset,
					 -graphicsstate.yOffset);
	} else
	    currentAwtGraphics.drawPolygon(is, is_50_, i);
    }
    
    public void drawPolygon(Polygon polygon) {
	drawPolygon(polygon.xPoints, polygon.yPoints, polygon.numPoints);
    }
    
    public void fillPolygon(int[] is, int[] is_51_, int i) {
	GraphicsState graphicsstate = state();
	if (graphicsstate.xOffset != 0 || graphicsstate.yOffset != 0) {
	    currentAwtGraphics.translate(graphicsstate.xOffset,
					 graphicsstate.yOffset);
	    currentAwtGraphics.fillPolygon(is, is_51_, i);
	    currentAwtGraphics.translate(-graphicsstate.xOffset,
					 -graphicsstate.yOffset);
	} else
	    currentAwtGraphics.fillPolygon(is, is_51_, i);
    }
    
    public void fillPolygon(Polygon polygon) {
	fillPolygon(polygon.xPoints, polygon.yPoints, polygon.numPoints);
    }
    
    public void drawBitmapAt(Bitmap bitmap, int i, int i_52_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_52_ += graphicsstate.yOffset;
	if (bitmap != null) {
	    if (!bitmap.loadsIncrementally())
		bitmap.loadData();
	    if (!bitmap.isValid())
		System.err.println("Graphics.drawBitmapAt() - Invalid bitmap: "
				   + bitmap.name());
	    else
		currentAwtGraphics.drawImage(bitmap.awtImage(), i, i_52_,
					     bitmap.bitmapObserver());
	}
    }
    
    public void drawBitmapScaled(Bitmap bitmap, int i, int i_53_, int i_54_,
				 int i_55_) {
	GraphicsState graphicsstate = state();
	if (bitmap != null) {
	    i += graphicsstate.xOffset;
	    i_53_ += graphicsstate.yOffset;
	    bitmap.createScaledVersion(i_54_, i_55_);
	    if (!bitmap.isValid())
		System.err.println
		    ("Graphics.drawBitmapScaled() - Invalid bitmap: "
		     + bitmap.name());
	    else
		currentAwtGraphics.drawImage(bitmap.awtImage(), i, i_53_,
					     i_54_, i_55_,
					     bitmap.bitmapObserver());
	}
    }
    
    static int computeStringWidth(FontMetrics fontmetrics, String string) {
	return fontmetrics.stringWidth(string);
    }
    
    public void drawStringInRect(String string, int i, int i_56_, int i_57_,
				 int i_58_, int i_59_) {
	if (font() == null)
	    throw new InconsistencyException("No font set");
	FontMetrics fontmetrics = font().fontMetrics();
	if (fontmetrics == null)
	    throw new InconsistencyException("No metrics for Font " + font());
	int i_60_;
	if (i_59_ == 1) {
	    int i_61_ = computeStringWidth(fontmetrics, string);
	    if (i_61_ > i_57_)
		i_61_ = i_57_;
	    i_60_ = i + (i_57_ - i_61_) / 2;
	} else if (i_59_ == 2) {
	    int i_62_ = computeStringWidth(fontmetrics, string);
	    if (i_62_ > i_57_)
		i_62_ = i_57_;
	    i_60_ = i + i_57_ - i_62_;
	} else
	    i_60_ = i;
	int i_63_ = (i_58_ - fontmetrics.ascent() - fontmetrics.descent()) / 2;
	if (i_63_ < 0)
	    i_63_ = 0;
	int i_64_ = i_56_ + i_58_ - i_63_ - fontmetrics.descent();
	drawString(string, i_60_, i_64_);
    }
    
    public void drawStringInRect(String string, Rect rect, int i) {
	if (rect == null)
	    throw new InconsistencyException
		      ("Null Rect passed to drawStringInRect.");
	drawStringInRect(string, rect.x, rect.y, rect.width, rect.height, i);
    }
    
    public void drawString(String string, int i, int i_65_) {
	if (string == null)
	    throw new InconsistencyException
		      ("Null String passed to drawString.");
	Font font = font();
	if (font == null || !font.wasDownloaded()) {
	    GraphicsState graphicsstate = state();
	    i += graphicsstate.xOffset;
	    i_65_ += graphicsstate.yOffset;
	    currentAwtGraphics.drawString(string, i, i_65_);
	} else {
	    FontMetrics fontmetrics = font.fontMetrics();
	    int[] is = fontmetrics.widthsArray();
	    int i_66_ = fontmetrics.widthsArrayBase();
	    Vector vector = font.glyphVector();
	    i_65_ -= fontmetrics.ascent();
	    for (int i_67_ = 0; i_67_ < string.length(); i_67_++) {
		int i_68_ = string.charAt(i_67_) - i_66_;
		if (i_68_ < 0 || i_68_ >= is.length - i_66_) {
		    if (string.charAt(i_67_) == ' ')
			i += is[32];
		} else {
		    drawBitmapAt((Bitmap) vector.elementAt(i_68_), i, i_65_);
		    i += is[string.charAt(i_67_)];
		}
	    }
	}
    }
    
    public void drawBytes(byte[] is, int i, int i_69_, int i_70_, int i_71_) {
	Font font = font();
	if (font == null || !font.wasDownloaded()) {
	    GraphicsState graphicsstate = state();
	    i_70_ += graphicsstate.xOffset;
	    i_71_ += graphicsstate.yOffset;
	    currentAwtGraphics.drawBytes(is, i, i_69_, i_70_, i_71_);
	} else {
	    FontMetrics fontmetrics = font.fontMetrics();
	    int[] is_72_ = fontmetrics.widthsArray();
	    int i_73_ = fontmetrics.widthsArrayBase();
	    Vector vector = font.glyphVector();
	    i_71_ -= fontmetrics.ascent();
	    for (int i_74_ = 0; i_74_ < i_69_; i_74_++) {
		int i_75_ = is[i_74_] - i_73_;
		if (i_75_ < 0 || i_75_ >= is_72_.length - i_73_) {
		    if ((char) is[i_74_] == ' ')
			i_70_ += is_72_[32];
		} else {
		    drawBitmapAt((Bitmap) vector.elementAt(i_75_), i_70_,
				 i_71_);
		    i_70_ += is_72_[is[i_74_]];
		}
	    }
	}
    }
    
    public void drawChars(char[] cs, int i, int i_76_, int i_77_, int i_78_) {
	Font font = font();
	if (font != null) {
	    if (!font.wasDownloaded()) {
		GraphicsState graphicsstate = state();
		i_77_ += graphicsstate.xOffset;
		i_78_ += graphicsstate.yOffset;
		currentAwtGraphics.drawChars(cs, i, i_76_, i_77_, i_78_);
	    } else {
		FontMetrics fontmetrics = font.fontMetrics();
		int[] is = fontmetrics.widthsArray();
		int i_79_ = fontmetrics.widthsArrayBase();
		Vector vector = font.glyphVector();
		i_78_ -= fontmetrics.ascent();
		for (int i_80_ = 0; i_80_ < i_76_; i_80_++) {
		    int i_81_ = cs[i_80_] - i_79_;
		    if (i_81_ < 0 || i_81_ >= is.length - i_79_) {
			if (cs[i_80_] == 32)
			    i_77_ += is[32];
		    } else {
			drawBitmapAt((Bitmap) vector.elementAt(i_81_), i_77_,
				     i_78_);
			i_77_ += is[cs[i_80_]];
		    }
		}
	    }
	}
    }
    
    void copyArea(int i, int i_82_, int i_83_, int i_84_, int i_85_,
		  int i_86_) {
	GraphicsState graphicsstate = state();
	i += graphicsstate.xOffset;
	i_82_ += graphicsstate.yOffset;
	i_85_ += graphicsstate.xOffset;
	i_86_ += graphicsstate.yOffset;
	currentAwtGraphics.copyArea(i, i_82_, i_83_, i_84_, i_85_ - i,
				    i_86_ - i_82_);
    }
    
    public String toString() {
	int i = graphicsStates.count();
	String string = buffer != null ? " for Bitmap " : " ";
	StringBuffer stringbuffer
	    = new StringBuffer("Graphics" + string + i + " states:\n");
	while (i-- > 0) {
	    GraphicsState graphicsstate
		= (GraphicsState) graphicsStates.elementAt(i);
	    stringbuffer.append(graphicsstate.toString());
	    stringbuffer.append("\n");
	}
	return stringbuffer.toString();
    }
    
    String toShortString() {
	StringBuffer stringbuffer
	    = new StringBuffer("Graphics" + (isDrawingBuffer() ? "<B>" : "")
			       + "(" + graphicsID + ")");
	int i = graphicsStates.count();
	while (i-- > 0)
	    stringbuffer.append("-");
	stringbuffer.append(">");
	return stringbuffer.toString();
    }
    
    public void setDebugOptions(int i) {
	if (i != 0)
	    throw new InconsistencyException
		      ("Can't set non zero debugOptions on a Graphics.  Use DebugGraphics instead.");
    }
    
    public int debugOptions() {
	return 0;
    }
    
    void setDebug(View view) {
	/* empty */
    }
    
    static void setViewDebug(View view, int i) {
	/* empty */
    }
    
    static int shouldViewDebug(View view) {
	return 0;
    }
    
    static int viewDebug(View view) {
	return 0;
    }
    
    static int debugViewCount() {
	return 0;
    }
}
