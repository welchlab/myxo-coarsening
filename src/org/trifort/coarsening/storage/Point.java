/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.storage;

public class Point {
  public double x;
  public double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double distance(Point other) {
    double xdiff = x - other.x;
    double ydiff = y - other.y;
    return Math.sqrt((xdiff*xdiff)+(ydiff*ydiff));
  }
  
  @Override
  public String toString(){
    StringBuilder ret = new StringBuilder();
    ret.append(x);
    ret.append(",");
    ret.append(y);
    return ret.toString();
  }
}
