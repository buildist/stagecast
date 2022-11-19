/* ColorGrid - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;

public class ColorGrid
{
    public static final int TABLE_SIZE = 10;
    private static Color[][] colors;
    
    static {
	int[][] rgbArray
	    = { { 255, 255, 255 }, { 255, 204, 255 }, { 230, 204, 255 },
		{ 204, 204, 255 }, { 204, 255, 255 }, { 204, 255, 204 },
		{ 230, 255, 204 }, { 255, 255, 204 }, { 255, 230, 204 },
		{ 255, 204, 204 }, { 255, 255, 255 }, { 255, 179, 255 },
		{ 217, 179, 255 }, { 179, 179, 255 }, { 179, 255, 255 },
		{ 179, 255, 179 }, { 217, 255, 179 }, { 255, 255, 179 },
		{ 255, 217, 179 }, { 255, 179, 179 }, { 226, 226, 226 },
		{ 255, 153, 255 }, { 204, 153, 255 }, { 153, 153, 255 },
		{ 153, 255, 255 }, { 153, 255, 153 }, { 204, 255, 153 },
		{ 255, 255, 153 }, { 255, 204, 153 }, { 255, 153, 153 },
		{ 196, 196, 196 }, { 255, 127, 255 }, { 191, 127, 255 },
		{ 127, 127, 255 }, { 127, 255, 255 }, { 127, 255, 127 },
		{ 191, 255, 127 }, { 255, 255, 127 }, { 255, 191, 127 },
		{ 255, 127, 127 }, { 166, 166, 166 }, { 255, 102, 255 },
		{ 179, 102, 255 }, { 102, 102, 255 }, { 102, 255, 255 },
		{ 102, 255, 102 }, { 179, 255, 102 }, { 255, 255, 102 },
		{ 255, 179, 102 }, { 255, 102, 102 }, { 136, 136, 136 },
		{ 255, 76, 255 }, { 166, 76, 255 }, { 76, 76, 255 },
		{ 76, 255, 255 }, { 76, 255, 76 }, { 166, 255, 76 },
		{ 255, 255, 76 }, { 255, 166, 76 }, { 255, 76, 76 },
		{ 106, 106, 106 }, { 255, 0, 255 }, { 127, 0, 255 },
		{ 0, 0, 255 }, { 0, 255, 255 }, { 0, 255, 0 }, { 127, 255, 0 },
		{ 255, 255, 0 }, { 255, 127, 0 }, { 255, 0, 0 },
		{ 76, 76, 76 }, { 204, 0, 204 }, { 102, 0, 204 },
		{ 0, 0, 204 }, { 0, 204, 204 }, { 0, 204, 0 }, { 102, 204, 0 },
		{ 204, 204, 0 }, { 204, 102, 0 }, { 204, 0, 0 },
		{ 38, 38, 38 }, { 153, 0, 153 }, { 76, 0, 153 }, { 0, 0, 153 },
		{ 0, 153, 153 }, { 0, 153, 0 }, { 76, 153, 0 },
		{ 153, 153, 0 }, { 153, 76, 0 }, { 153, 0, 0 }, new int[3],
		{ 102, 0, 102 }, { 51, 0, 102 }, { 0, 0, 102 },
		{ 0, 102, 102 }, { 0, 102, 0 }, { 51, 102, 0 },
		{ 102, 102, 0 }, { 102, 51, 0 }, { 102, 0, 0 } };
	colors = new Color[10][10];
	int[] pixels = new int[100];
	int count = 0;
	for (int i = 0; i < rgbArray.length; i++) {
	    int rgbValue
		= new Color(rgbArray[i][0], rgbArray[i][1], rgbArray[i][2])
		      .rgb();
	    pixels[count] = rgbValue;
	    count++;
	}
	COM.stagecast.ifc.netscape.application.Bitmap b
	    = BitmapManager.createBitmapManager(pixels, 10, 10);
	COM.stagecast.ifc.netscape.application.Bitmap b2
	    = BitmapManager.createBitmapManager(10, 10);
	Graphics graphics2 = b2.createGraphics();
	b.drawAt(graphics2, 0, 0);
	graphics2.dispose();
	boolean success = b2.grabPixels(pixels);
	ASSERT.isTrue(success, "grabPixels");
	b.flush();
	b2.flush();
	count = 0;
	for (int y = 0; y < 10; y++) {
	    for (int x = 0; x < 10; x++) {
		colors[x][y] = new Color(pixels[count]);
		count++;
	    }
	}
    }
    
    public static Color[][] getColors() {
	return colors;
    }
    
    private ColorGrid() {
	/* empty */
    }
}
