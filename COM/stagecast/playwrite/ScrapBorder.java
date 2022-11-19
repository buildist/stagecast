/* ScrapBorder - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Bitmap;
import COM.stagecast.ifc.netscape.application.Border;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;
import COM.stagecast.ifc.netscape.application.Rect;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public class ScrapBorder extends Border implements ResourceIDs.ScrapBorderIDs
{
    private static ScrapBorder _testBorder = null;
    private static ScrapBorder _testRedBorder = null;
    private static ScrapBorder _testGreenBorder = null;
    private static ScrapBorder _variableAliasBorder = null;
    private final Bitmap topBorder;
    private final Bitmap bottomBorder;
    private final Bitmap leftBorder;
    private final Bitmap rightBorder;
    private Rect bufferRect = new Rect();
    
    public static ScrapBorder getCommentBorder() {
	return getTestBorder();
    }
    
    public static ScrapBorder getRuleBorder() {
	return getTestBorder();
    }
    
    public static ScrapBorder getTestBorder() {
	if (_testBorder == null)
	    _testBorder = new ScrapBorder(Resource.getImage("TestLeft"),
					  Resource.getImage("TestTop"),
					  Resource.getImage("TestRight"),
					  Resource.getImage("TestBottom"));
	return _testBorder;
    }
    
    public static ScrapBorder getGreenTestBorder() {
	if (_testGreenBorder == null)
	    _testGreenBorder
		= new ScrapBorder(Resource.getImage("TESTGLeft"),
				  Resource.getImage("TESTGTop"),
				  Resource.getImage("TESTGRight"),
				  Resource.getImage("TESTGBottom"));
	return _testGreenBorder;
    }
    
    public static ScrapBorder getRedTestBorder() {
	if (_testRedBorder == null)
	    _testRedBorder = new ScrapBorder(Resource.getImage("TESTRLeft"),
					     Resource.getImage("TESTRTop"),
					     Resource.getImage("TESTRRight"),
					     Resource.getImage("TESTRBottom"));
	return _testRedBorder;
    }
    
    public static ScrapBorder getVariableAliasBorder() {
	if (_variableAliasBorder == null)
	    _variableAliasBorder
		= new ScrapBorder(Resource.getImage("AliasLeft"),
				  Resource.getImage("AliasTop"),
				  Resource.getImage("AliasRight"),
				  Resource.getImage("AliasBottom"));
	return _variableAliasBorder;
    }
    
    ScrapBorder(Bitmap left, Bitmap top, Bitmap right, Bitmap bottom) {
	leftBorder = left;
	topBorder = top;
	rightBorder = right;
	bottomBorder = bottom;
    }
    
    public int bottomMargin() {
	return bottomBorder.height();
    }
    
    public int leftMargin() {
	return leftBorder.width();
    }
    
    public int rightMargin() {
	return rightBorder.width();
    }
    
    public int topMargin() {
	return topBorder.height();
    }
    
    Color getBackgroundColor() {
	return Util.ruleScrapColor;
    }
    
    public void drawInRect(Graphics g, int x, int y, int width, int height) {
	g.pushState();
	bufferRect.setBounds(x, y, width, height);
	g.setClipRect(bufferRect);
	drawLeftBorder(g, x, y, width, height);
	drawRightBorder(g, x, y, width, height);
	drawTopBorder(g, x, y, width, height);
	drawBottomBorder(g, x, y, width, height);
	g.popState();
    }
    
    public void drawTopBorder(Graphics g, int x, int y, int width,
			      int height) {
	Rect clipRect = g.clipRect();
	int ourX = 0;
	int ourY = 0;
	int ourWidth = width;
	int ourHeight = topMargin();
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    ourWidth = topBorder.width();
	    for (ourX = 0; ourX < width; ourX += ourWidth) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    topBorder.drawAt(g, ourX, ourY);
	    }
	}
    }
    
    public void drawBottomBorder(Graphics g, int x, int y, int width,
				 int height) {
	Rect clipRect = g.clipRect();
	int ourX = 0;
	int ourY = height - bottomMargin();
	int ourWidth = width;
	int ourHeight = bottomMargin();
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    ourWidth = bottomBorder.width();
	    for (ourX = 0; ourX < width; ourX += ourWidth) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    bottomBorder.drawAt(g, ourX, ourY);
	    }
	}
    }
    
    public void drawLeftBorder(Graphics g, int x, int y, int width,
			       int height) {
	Rect clipRect = g.clipRect();
	int ourX = 0;
	int ourY = 0;
	int ourWidth = leftMargin();
	int ourHeight = height;
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    ourHeight = leftBorder.height();
	    for (ourY = 0; ourY < height; ourY += ourHeight) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    leftBorder.drawAt(g, ourX, ourY);
	    }
	}
    }
    
    public void drawRightBorder(Graphics g, int x, int y, int width,
				int height) {
	Rect clipRect = g.clipRect();
	int ourX = width - rightMargin();
	int ourY = 0;
	int ourWidth = rightMargin();
	int ourHeight = height;
	if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight)) {
	    ourHeight = rightBorder.height();
	    for (ourY = 0; ourY < height; ourY += ourHeight) {
		if (clipRect.intersects(ourX, ourY, ourWidth, ourHeight))
		    rightBorder.drawAt(g, ourX, ourY);
	    }
	}
    }
}
