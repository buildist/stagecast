/* OffsetDirectAccessor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

class OffsetDirectAccessor extends Variable.NumberDirectAccessor
{
    public Object constrainDirectValue(Variable variable, VariableOwner owner,
				       Object value) {
	Object result = super.constrainDirectValue(variable, owner, value);
	if (result != Variable.ILLEGAL_VALUE && result != Variable.UNBOUND) {
	    int offset = ((Number) result).intValue();
	    int squareSize = 2147483647;
	    if (owner instanceof CharacterInstance
		&& ((Contained) owner).getContainer() instanceof Board)
		squareSize = ((Board) ((Contained) owner).getContainer())
				 .getSquareSize();
	    if (offset < 0 || offset >= squareSize)
		result = Variable.ILLEGAL_VALUE;
	}
	return result;
    }
}
