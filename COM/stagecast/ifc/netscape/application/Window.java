/* Window - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.ifc.netscape.application;

public interface Window extends Target
{
    public static final int BLANK_TYPE = 0;
    public static final int TITLE_TYPE = 1;
    public static final String SHOW = "show";
    public static final String HIDE = "hide";
    
    public Size contentSize();
    
    public void addSubview(View view);
    
    public void show();
    
    public void showModally();
    
    public void hide();
    
    public void moveToFront();
    
    public void moveToBack();
    
    public boolean isVisible();
    
    public void setTitle(String string);
    
    public String title();
    
    public void setOwner(WindowOwner windowowner);
    
    public WindowOwner owner();
    
    public void setMenuView(MenuView menuview);
    
    public MenuView menuView();
    
    public View viewForMouse(int i, int i_0_);
    
    public void setBounds(int i, int i_1_, int i_2_, int i_3_);
    
    public void setBounds(Rect rect);
    
    public void sizeTo(int i, int i_4_);
    
    public void sizeBy(int i, int i_5_);
    
    public void moveBy(int i, int i_6_);
    
    public void moveTo(int i, int i_7_);
    
    public void center();
    
    public Size windowSizeForContentSize(int i, int i_8_);
    
    public Rect bounds();
    
    public void setMinSize(int i, int i_9_);
    
    public Size minSize();
    
    public void setResizable(boolean bool);
    
    public boolean isResizable();
    
    public void setContainsDocument(boolean bool);
    
    public boolean containsDocument();
    
    public void didBecomeCurrentDocument();
    
    public void didResignCurrentDocument();
    
    public boolean isCurrentDocument();
}
