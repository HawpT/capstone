/**
 * ConvexHull.java
 * 
 * Kevin Haupt and Colette Bedoya
 * 
 * September 2015
 */

import java.util.*;
import java.awt.Point;
import java.awt.Polygon;

public class ConvexHull {
	// all the points in the collection
	private ArrayList<Point> points;

	// the polygon that makes up the convex hull
	private Polygon hull;

	// a boolean to check if the convex hull needs to be recalculated
	private boolean hullCalculated = true;

	// constructor
	public ConvexHull() {
		points = new ArrayList<Point>();
		hull = new Polygon();
	}

	/**
	 * Adds a single point to the collection
	 * @param point
	 */
	public void addPoint(Point point) {
		this.points.add(point);
		hullCalculated = false;
	}  

	/** returns a specific point from the collection
	 * 
	 * @param i: a number between 0 and the number of points
	 * @return the Point indexed
	 */
	public Point getPoint(int i){
		if (0 <= i && i < points.size())
			return points.get(i);
		else
			throw new NoSuchElementException();
	}

	/** 
	 * 
	 * @return an ArrayList with all the Points in the collection 
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 * removes all the points from the collection
	 */
	public void clear() {
		points.clear();
		hull.reset();
		hullCalculated = true;
	}

	/** returns the number of points in the current collection */
	public int getNumber() {
		return points.size();
	}

	/** returns the convex hull for the current set of points */
	public Polygon getHull() {
		if (!hullCalculated)
			calculateConvexHull();
		return hull;
	}

	/** returns all points' positions as a String */
	public String toString() {
		String returnString = "Points:\n";
		for (int i=0; i<hull.npoints; i++){
			returnString = returnString + " Point " + i + ": (" +
					points.get(i).x + "," + points.get(i).y;
		}
		return returnString;
	}


	/** returns convex hull points */
	public String convexHullToString() {
		String hullPoints = "Convex Hull:\n";
		if (!hullCalculated)
			calculateConvexHull();
		for (int i=0; i<hull.npoints; i++){
			hullPoints = hullPoints + "\t(" + hull.xpoints[i] +"," 
					+ hull.ypoints[i] + ")\n"; 
		}
		return hullPoints;
	} 



	/**
	 * Calculates the convex hull from the current set of points
	 *
	 * This method currently does not work!
	 *
	 * You may NOT use the Polygon contains method in your final solution.
	 */
	private void calculateConvexHull() {
		hull.reset();
		hullCalculated = true;
		Point minX = points.get(0);
		Point maxX = points.get(0);

		//start from the smallest X value
		for (Point pt : points)
		{
			if (pt.x < minX.x)
				minX = pt;
		}
		for (Point pt : points)
		{
			if (pt.x > maxX.x)
				maxX = pt;
		}

		//the first best point is minX
		Point startPoint = minX;
		Edge saveEdge = new Edge(minX,maxX);
		boolean smaller = false;
		boolean larger = false;
		boolean pointsLeft = true;
		double c = 0;
		ArrayList<Point> hullPoints = new ArrayList();


		//draw a line from each point to each other point

		hull.addPoint(startPoint.x, startPoint.y);
		hullPoints.add(startPoint);

		while(pointsLeft){
			//points left will remain false if we add no new points to the hull
			pointsLeft = false;

			for (int i = 0; i < points.size(); i++) 
			{

				Point pt1 = points.get(i);
				if(!hullPoints.contains(pt1)){
					if(!pt1.equals(startPoint)){
						saveEdge = new Edge(startPoint, pt1);

						for (Point pt2 : points){
							if (saveEdge.plugInPoint(pt2) < 0)
								smaller = true;
							if (saveEdge.plugInPoint(pt2) > 0)
								larger = true;
						}
						
						//if no c values are < AND >, add the point
						if (!( smaller && larger)){
							hull.addPoint(pt1.x, pt1.y);
							hullPoints.add(pt1);
							startPoint = pt1;
							i = points.size();
							pointsLeft = true;
						}

						smaller = false;
						larger = false;
					}
				}

			}
		}
		hullCalculated = false;
	}



	// extra credit method
	public Point[] getClosestPoints() {
		if (points.size() >= 2) {
			Point[] closest = new Point[2];
			closest[0] = points.get(0);
			closest[1] = points.get(1);
			return closest;
		}
		else 
			return null;
	}
}

