/* DimensionDirectAccessor - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;

class DimensionDirectAccessor extends Variable.NumberDirectAccessor
{
    public Object constrainDirectValue(Variable variable, VariableOwner owner,
				       Object value) {
	Object result = super.constrainDirectValue(variable, owner, value);
	if (result != Variable.ILLEGAL_VALUE && result != Variable.UNBOUND) {
	    double dimension = ((Number) result).doubleValue();
	    if (dimension <= 0.0)
		result = Variable.ILLEGAL_VALUE;
	    if (((CocoaCharacter) owner).getContainer() instanceof Board) {
		Board board = (Board) ((CocoaCharacter) owner).getContainer();
		if (dimension > (double) Math.max(board.getSquareHeight(),
						  board.getSquareWidth()))
		    result = Variable.ILLEGAL_VALUE;
	    }
	}
	return result;
    }
}
