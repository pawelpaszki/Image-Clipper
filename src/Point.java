/**
 * 
 * @author Pawel Paszki
 * @version 19/06/2016
 * 
 * This is a custom Point class representing individual pixels in 
 * a BufferedImage. Hash code of each pixel with the same x and y values
 * has to be unique to make sure, that when dealing with these pixels 
 * there are no duplicates
 */
public class Point implements Comparable<Point> {

	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * compareTo method compares x and y values of two Points
	 */
	@Override
	public int compareTo(Point other) {
		if (this.getX() == ((Point) other).getX() && this.getY() == ((Point) other).getY()) {
			return 0;
		} else {
			return -1;
		}
	}
	
	/**
	 * this method checks, whether two pixels are the same based on their 
	 * x and y values
	 */
	@Override
	public boolean equals(Object other) {
		if (this.getX() == ((Point) other).getX() && this.getY() == ((Point) other).getY()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * this method generates hashCode for a Point (pixel) based on its 
	 * x and y values multiplied by a two different prime numbers
	 */
	@Override
	public int hashCode() {
		int code = 0;
		code = ((getX() + 1) * 41) + ((getY() + 1) * 59); 
		return code;
	}
}
