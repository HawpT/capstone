/**
 * Edge.java
 * 
 * Helen Hu and Greg Gagne
 * 
 * September 2015
 * 
 * This class represents an edge between a pair of points.
 */

import java.awt.Point;

public class Edge {
  // the pair of points
  private Point point1, point2;
  
  // line equation ax + by = c
  private double a, b, c;  
  
  // constructor
  public Edge(Point first, Point second){
    point1 = new Point(first);
    point2 = new Point(second);
    initLineEquation();
  }
  
  /**
   * From page 113 of your textbook.
   * Notice the x and y values of a Point are public.
   */
  private void initLineEquation(){
    a = point2.y - point1.y;
    b = point1.x - point2.x;
    c = point1.x*point2.y - point1.y*point2.x; 
  }
  
  /**
   * From page 113 of your textbook.
   * Plugs in point to the general line equation and returns the result 
   */
  public double plugInPoint(Point other){
    return a*other.x + b*other.y - c;
  }
  
  /**
   * @return the distance between the two points
   */
  public double distance() {
    return Math.sqrt(distanceSquared());
  }
  
  /**
   * @return the distance squared between the two points
   */
  public double distanceSquared() {
    return ((point1.x - point2.x)*(point1.x - point2.x) +
            (point1.y - point2.y)*(point1.y - point2.y));
  }
}

