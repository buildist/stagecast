/* UniqueID - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package COM.stagecast.playwrite;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

class UniqueID implements Externalizable
{
    static final int storeVersion = 1;
    static final long serialVersionUID = -3819410108754429234L;
    private double part1 = Math.random();
    private long part2 = System.currentTimeMillis();
    
    public UniqueID() {
	/* empty */
    }
    
    boolean equals(UniqueID id) {
	return (id == this
		|| id != null && id.part1 == part1 && id.part2 == part2);
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeDouble(part1);
	out.writeLong(part2);
    }
    
    public void readExternal(ObjectInput in)
	throws IOException, ClassNotFoundException {
	part1 = in.readDouble();
	part2 = in.readLong();
    }
    
    public String toString() {
	return "<UniqueID (" + part1 + ", " + part2 + ")>";
    }
}
