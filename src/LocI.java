
public class LocI{ //Location in the form of a pair of integers
	private int x;
	private int y;
	/**
	 * the default constructor for a location integer
	 * @param location x
	 * @param location y
	 */
	public LocI(int location_x, int location_y){
		x = location_x;
		y = location_y;
	}
	/**
	 * returns true if the locations are equal
	 * @param l - the other location
	 * @return true if the two locations are equal
	 */
	public boolean equals(LocI l){
		if(l == null) return false;
		return l.x==x&&l.y==y;
	}
	/**
	 * returns true if the locations are equal
	 * @param x - the x-coordinate of the other location
	 * @param y - the y-coordinate of the other location
	 * @return true if the two locations are equal
	 */
	public boolean equals(int x, int y){
		return this.x==x&&this.y==y;
	}
	/**
	 * translates the location to the given coordinates
	 * @param location x
	 * @param location y
	 */
	public void jumpTo(int location_x, int location_y){
		x = location_x;
		y = location_y;
	}
	/**
	 * translates this instance of LocI to equal "location"
	 * @param the location to jump to
	 */
	public void jumpTo(LocI location){
		x = location.x;
		y = location.y;
	}
	/**
	 * translates this instance of LocI by (x,y)
	 * @param x - a change in the x-coordinate
	 * @param y - a change in the y-coordinate
	 */
	public void translate(int x, int y){
		this.x += x;
		this.y += y;
	}
	/**
	 * translates this instance of LocI by (x,y)
	 * @param x - a change in the x-coordinate
	 * @param y - a change in the y-coordinate
	 */
	public void translate(double x, double y){
		this.x += (int)x;
		this.y += (int)y;
	}

	/**
	 * Creates a <i>LocI</i> identical to <b>this</b>.
	 * @return an instance of <i>LocI</i> that equals <b>this</b>.
	 */
	@Override
	public LocI clone(){
		LocI l = new LocI(x, y);
		return l;
	}
	/**
	 * @return the x-value of the location
	 */
	public int getX() {
		return x;
	}
	/**
	 * Sets the x-value of this location to <i>x</i>.
	 * @param x - the new x-value of this location
	 */
	public void setX(int x) {
		this.x = x;
	}
	/**
	 * @return the x-value of the location
	 */
	public int getY() {
		return y;
	}
	/**
	 * Sets the y-value of this location to <i>y</i>.
	 * @param y - the new y-value of this location
	 */
	public void setY(int y) {
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
	public double distanceTo(LocI o){
		return Math.sqrt(Math.pow(x-o.x,2)+Math.pow(y-o.y,2));
	}
	
	
	/**
	 * This method uses the distance formula to 
	 * return an estimated distance between <b>o</b>.
	 * The formula is <code>Math.abs(x-o.x)+Math.abs(y-o.y)</code>
	 * @param o - the other LocI to compare distances
	 * @return the estimated distance to the this point and <b>o</b>. 
	 */
	public int distanceEstimate(LocI o){
		return Math.abs(x-o.x)+Math.abs(y-o.y);
	}
}