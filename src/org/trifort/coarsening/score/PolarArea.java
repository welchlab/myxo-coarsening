/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.score;

public class PolarArea {
  
  public double calculate(double x, double y){
    double xx = x * x;
    double yy = y * y;
    double radius = Math.sqrt(xx + yy);
    double angle = Math.asin(y / radius);
      
    double angle_deg = Math.toDegrees(angle);
    double angle_calc = Math.abs(angle_deg - 45);
    angle = Math.toRadians(angle_calc);
    double ret = polarArea(radius, angle);
    return ret;
  }

  private double polarArea(double radius, double angle){
    return 0.5 * angle * radius * radius;
  }

}
