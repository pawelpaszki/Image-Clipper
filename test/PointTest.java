import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.Point;

/**
 * 
 * @author Pawel Paszki
 * version 30/09/2016
 * 
 * Test case for Point class, which basically tests, if Points with 
 * the same coordinates are equal and with different ones are not
 *
 */

public class PointTest {

	private Point point1;
	private Point point2;
	private Point point3;
	
	@Before
	public void setUp() throws Exception {
		point1 = new Point(10,20);
		point2 = new Point(20,10);
		point3 = new Point(10,20);
		
	}
	
	@After
	public void tearDown() {
		point1 = null;
		point2 = null;
		point3 = null;
	}
	
	@Test
	public void testExistence() {
		assertTrue(point1 != null);
		assertTrue(point2 != null);
		assertTrue(point3 != null);
	}
	
	@Test
	public void testEquality() {
		assertTrue(point1.equals(point3));
		assertFalse(point1.equals(point2));
		assertFalse(point2.equals(point3));
	}
	
	

}
