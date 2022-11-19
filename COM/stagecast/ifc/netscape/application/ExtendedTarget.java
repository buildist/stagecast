/* ExtendedTarget - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface ExtendedTarget extends Target
{
    public static final String SET_FONT = "setFont";
    public static final String NEW_FONT_SELECTION = "newFontSelection";
    public static final String SHOW_FONT_CHOOSER = "showFontChooser";
    public static final String SHOW_COLOR_CHOOSER = "showColorChooser";
    public static final String COPY = "copy";
    public static final String CUT = "cut";
    public static final String PASTE = "paste";
    
    public boolean canPerformCommand(String string);
}
