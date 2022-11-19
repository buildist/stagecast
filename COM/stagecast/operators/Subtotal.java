/* Subtotal - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.operators;
import COM.stagecast.ifc.netscape.application.Color;
import COM.stagecast.ifc.netscape.application.Font;
import COM.stagecast.playwrite.Util;
import COM.stagecast.playwrite.internationalization.ResourceIDs;

public interface Subtotal extends ResourceIDs.OperationIDs, Expression
{
    public static final int PAD = 4;
    public static final Font FONT = Util.ruleFont;
    public static final int ASCENT = FONT.fontMetrics().maxAscent();
    public static final int SEPARATOR_Y = ASCENT + 4;
    public static final Color BORDER_COLOR = Color.darkGray;
    public static final Color LIGHTER_BACKGROUND_COLOR = Color.lightGray;
    public static final Color DARKER_BACKGROUND_COLOR = Color.gray;
    
    public static interface Creator extends Expression
    {
	public Expression[] subexpressions();
	
	public Subtotal getSubtotal();
	
	public Subtotal createSubtotal();
	
	public void setSubtotal(Subtotal subtotal);
	
	public void clearCaches();
    }
    
    public Object getResult();
    
    public String getResultAsString();
    
    public Creator getExpression();
    
    public void reevaluate();
    
    public void clearCache();
    
    public void expressionChanged();
}
