/**
 * A location comprised of two bytes
 * @author Micah Mundy
 *
 */
public class LocB{ //Location in the form of a pair of integers
	private byte x;
	private byte y;
	/**
	 * the default constructor for a location byte
	 * @param location x
	 * @param location y
	 */
	public LocB(byte location_x, byte location_y){
		x = location_x;
		y = location_y;
	}
	/**
	 * the default constructor for a location byte
	 * @param location x
	 * @param location y
	 */
	public LocB(int location_x, int location_y) {
		x = (byte)location_x;
		y = (byte)location_y;
	}
	/**
	 * returns true if the locations are equal
	 * @param l - the other location
	 * @return true if the two locations are equal
	 */
	public boolean equals(LocB l){
		if(l == null) return false;
		return l.x==x&&l.y==y;
	}
	/**
	 * returns true if the locations are equal
	 * @param x - the x-coordinate of the other location
	 * @param y - the y-coordinate of the other location
	 * @return true if the two locations are equal
	 */
	public boolean equals(byte x, byte y){
		return this.x==x&&this.y==y;
	}
	/**
	 * translates the location to the given coordinates
	 * @param location x
	 * @param location y
	 */
	public void jumpTo(byte location_x, byte location_y){
		x = location_x;
		y = location_y;
	}
	/**
	 * translates this instance of LocI to equal "location"
	 * @param the location to jump to
	 */
	public void jumpTo(LocB location){
		x = location.x;
		y = location.y;
	}
	/**
	 * translates this instance of LocI by (x,y)
	 * @param x - a change in the x-coordinate
	 * @param y - a change in the y-coordinate
	 */
	public void translate(byte x, byte y){
		this.x += x;
		this.y += y;
	}
	/**
	 * translates this instance of LocI by (x,y)
	 * @param x - a change in the x-coordinate
	 * @param y - a change in the y-coordinate
	 */
	public void translate(double x, double y){
		this.x += (byte)x;
		this.y += (byte)y;
	}

	/**
	 * Creates a <i>LocI</i> identical to <b>this</b>.
	 * @return an instance of <i>LocI</i> that equals <b>this</b>.
	 */
	@Override
	public LocB clone(){
		LocB l = new LocB(x, y);
		return l;
	}
	/**
	 * @return the x-value of the location
	 */
	public byte getX() {
		return x;
	}
	/**
	 * Sets the x-value of this location to <i>x</i>.
	 * @param x - the new x-value of this location
	 */
	public void setX(byte x) {
		this.x = x;
	}
	/**
	 * @return the x-value of the location
	 */
	public byte getY() {
		return y;
	}
	/**
	 * Sets the y-value of this location to <i>y</i>.
	 * @param y - the new y-value of this location
	 */
	public void setY(byte y) {
		this.y = y;
	}
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
	/**
	 * This method uses the distance formula to 
	 * return a precise distance between <b>o</b>.
	 * The formula is <code>Math.sqrt(Math.pow(x-o.x,2)+Math.pow(y-o.y,2))</code>
	 * @param o - the other LocI to compare distances
	 * @return the distance to the this point and <b>o</b>.
	 */
	public double distanceTo(LocB o){
		return Math.sqrt(Math.pow(x-o.x,2)+Math.pow(y-o.y,2));
	}
	
	
	/**
	 * This method uses the distance formula to 
	 * return an estimated distance between <b>o</b>.
	 * The formula is <code>Math.abs(x-o.x)+Math.abs(y-o.y)</code>
	 * @param o - the other LocI to compare distances
	 * @return the estimated distance to the this point and <b>o</b>. 
	 */
	public byte distanceEstimate(LocB o){
		return (byte) (Math.abs(x-o.x)+Math.abs(y-o.y));
	}
}