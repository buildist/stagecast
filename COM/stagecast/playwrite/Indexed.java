/* Indexed - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

interface Indexed extends Contained, Viewable, Deletable
{
    public IndexedContainer getIndexedContainer();
    
    public int getIndex();
    
    public void setIndex(int i);
    
    public boolean removeFromContainer();
    
    public PlaywriteView getView();
    
    public void setView(PlaywriteView playwriteview);
}
