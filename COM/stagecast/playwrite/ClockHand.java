/* ClockHand - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Graphics;

class ClockHand
{
    static final Color handColor = Color.yellow;
    private int[] baseX;
    private int[] baseY;
    private int[] rotatedX;
    private int[] rotatedY;
    private int numberOfPoints;
    private int time;
    
    ClockHand(int originX, int originY, int length, int thickness,
	      int points) {
	baseX = new int[points];
	baseY = new int[points];
	rotatedX = new int[points];
	rotatedY = new int[points];
	numberOfPoints = points;
	baseX[0] = originX;
	baseY[0] = originY;
	baseX[1] = baseX[0] - thickness / 2;
	baseY[1] = baseY[0] + thickness / 2;
	baseX[2] = baseX[1];
	baseY[2] = baseY[0] + length - thickness;
	baseX[3] = baseX[0];
	baseY[3] = baseY[0] + length;
	baseX[4] = baseX[0] + thickness / 2;
	baseY[4] = baseY[2];
	baseX[5] = baseX[4];
	baseY[5] = baseY[1];
    }
    
    void rotate(double angle) {
	double sine = Math.sin(angle);
	double cosine = Math.cos(angle);
	for (int i = 0; i < numberOfPoints; i++) {
	    rotatedX[i] = (int) ((double) baseX[0]
				 + (double) (baseX[0] - baseX[i]) * cosine
				 - (double) (baseY[0] - baseY[i]) * sine);
	    rotatedY[i] = (int) ((double) baseY[0]
				 + (double) (baseX[0] - baseX[i]) * sine
				 + (double) (baseY[0] - baseY[i]) * cosine);
	}
    }
    
    public void draw(double angle, Graphics g) {
	rotate(angle);
	g.setColor(handColor);
	g.fillPolygon(rotatedX, rotatedY, numberOfPoints);
    }
}
