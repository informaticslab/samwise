package gov.cdc.irdu.math;

/**
 * This class represents a line on a 2D graph. It can have specific end points (default) 
 * or infinite end points.
 * 
 * Linear equation: Ax + By + C = 0 ==> slope * x - y + linearConstant = 0
 *  
 * @author joel
 *
 */

public class Line {
	private Coordinate pointA;
	private Coordinate pointB;
	private double slope;
	private double linearConstant;
	private boolean coordinateEndpoints;
	
	public Line(Coordinate pointA, Coordinate pointB) {
		this(pointA, pointB, true);
	}
	
	public Line(Coordinate pointA, Coordinate pointB, boolean hasCoordinateEndpoints) {
		this.pointA = pointA;
		this.pointB = pointB;
		this.slope = calculateSlope(pointA, pointB);
		this.linearConstant = (-1 * (slope * pointA.getX())) + pointA.getY();
		this.coordinateEndpoints = hasCoordinateEndpoints;
	}
	
	public Line (double x1, double y1, double x2, double y2) {
		this(new Coordinate(x1, y1), new Coordinate(x2, y2));
	}
	
	public Line (double x1, double y1, double x2, double y2, boolean hasCoordinateEndpoints) {
		this(new Coordinate(x1, y1), new Coordinate(x2, y2), hasCoordinateEndpoints);
	}
	
	/**
	 * This method returns a value representing the relationship of the given
	 * point to the line. If the point is above the line, it returns a positive
	 * value representing the distance from the line. If the point is below the
	 * line, it returns a negative number which is the distance from the line.
	 * If it is on the line, it returns 0.0.
	 * 
	 * @param point
	 * @return
	 */
	public double compare(Coordinate point) {
		double result = (slope * point.getX()) - point.getY() + linearConstant;
		return result;
	}

	public double getLinearConstant() {
		return this.linearConstant;
	}
	
	public Coordinate getPointA() {
		return pointA;
	}

	public Coordinate getPointB() {
		return pointB;
	}

	public double getSlope() {
		return slope;
	}

	public boolean isCoordinateEndpoints() {
		return coordinateEndpoints;
	}

	public void setCoordinateEndpoints(boolean coordinateEndpoints) {
		this.coordinateEndpoints = coordinateEndpoints;
	}

	private double calculateSlope(Coordinate a, Coordinate b) {
		double diffX = b.getX() - a.getX();
		double diffY = b.getY() - a.getY();
		
		// This is a special case. The slope of a vertical line is undefined.
		if (diffX == 0)
			return Float.NaN;
		
		return diffY / diffX;
	}
	
}
