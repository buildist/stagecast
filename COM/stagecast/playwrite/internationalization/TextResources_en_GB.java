/* TextResources_en_GB - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite.internationalization;

public class TextResources_en_GB extends TextResources
{
    private static final Object[][] contents
	= { { "WCVarID", "window colour" },
	    { "BGColorVarID", "background colour" },
	    { "TXTcolorID", "text colour" },
	    { "TXTbgcolorID", "background colour" },
	    { "NewColorID", "new colour" }, { "ThisColorID", "this colour" },
	    { "Def CID", "default colour" }, { "darkGray CID", "dark grey" },
	    { "gray CID", "grey" }, { "lt Gray CID", "light grey" },
	    { "dialog cct", "Colour Chooser" },
	    { "dialog cd1t",
	      "Your display is set to 16 Colour. Please change your display settings to High Colour (16-bit) or 24-bit True Colour and try again. If you continue, {0} may not operate correctly. " },
	    { "dialog cd2t",
	      "Your display is set to {1, choice, 8#16 or 256 Colour|32#True Colour (32-bit)}. To display colours in {0} correctly, please change your display settings to {2, choice, 0#High Colour (16-bit) or 24-bit True Colour |1#True Colour (24-bit or 32-bit)|2#High Colour (16-bit) or True Colour (24-bit or 32-bit)} before using {0}." },
	    { "dialog cd3t",
	      "Your display is set to High Colour (16 bit). Testing has shown that this version of Java causes some machines to freeze unexpectedly. We strongly recommend you change your display settings to True Colour (24-bit or 32-bit) before using {0}." },
	    { "gray splat", "grey splat" } };
    
    public Object[][] getContents() {
	return contents;
    }
}
