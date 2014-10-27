/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.storage;


public class Droplet {

  private int m_id;
  private double m_x;
  private double m_y;
  private double m_volume;
  
  private double m_xMovement;
  private double m_yMovement;
  
  private double m_sumVolume;
  private int m_sumVolumeCount;
  private double m_sumX;
  private double m_sumY;
  private int m_sumCenterCount;
  
  private int m_startRadius;
  private double m_score;
  
  public Droplet(int id, double x, double y, int radius){
    m_id = id;
    m_x = x;
    m_y = y;
    m_startRadius = radius;
    m_volume = toVolume(radius);
    
    m_score = -100;
    
    resetNextData();
  }
  
  public Droplet(Droplet other){
    m_id = other.m_id;
    m_x = other.m_x;
    m_y = other.m_y;
    m_volume = other.m_volume;
    m_startRadius = other.m_startRadius;

    m_score = -100;
    
    resetNextData();
  }
  
  public int getStartRadius(){
    return m_startRadius;
  }
  
  public void finalizePrediction() {
    if(m_sumVolumeCount == 0){
      m_volume = 0;
      return;
    }
    double x_change = (m_sumX / (double) m_sumCenterCount);
    double y_change = (m_sumY / (double) m_sumCenterCount);
    double sum_change = (m_sumVolume / (double) m_sumVolumeCount);
    
    double old_x = m_x;
    double old_y = m_y;
    
    m_x += x_change;
    m_y += y_change;
    
    double old_volume = m_volume;
    //BUG1: AS PER REVIEWER COMMENT, don't average dV
    //double new_volume = old_volume + sum_change;
    double new_volume = old_volume + m_sumVolume;
    m_volume = new_volume;
    
    resetNextData();
  }

  public double getVolume() {
    return m_volume;
  }
  
  public Point getCenter(){
    Point ret = new Point(m_x, m_y);
    return ret;
  }

  public void addNextVolume(double volume) {
    m_sumVolume += volume;
    m_sumVolumeCount++;
  }
  
  public void addNextData(double volume, Point center){
    m_sumVolume += volume;
    m_sumVolumeCount++;
    
    m_sumX += center.x;
    m_sumY += center.y;
    m_sumCenterCount++;
  }
  
  public double getRadius() {
    return toRadius(m_volume);
  }

  public void resetNextData() {
    m_sumVolumeCount = 0;
    m_sumVolume = 0;
    m_sumCenterCount = 0;
    m_sumX = 0;
    m_sumY = 0;
  }

  public void setCenter(Point center) {
    m_x = center.x;
    m_y = center.y;
  }

  public int getId(){
    return m_id;
  }
  
  public void setVolume(double volume) {
    m_volume = volume;
  }

  private double toRadius(double volume) {
    if(volume < 0.0000000001){
      return 0;
    }
    return Math.pow(volume / (Math.PI * 4.0) * 6.0, 1.0/3.0);
  }

  private double toVolume(double radius) {
    return Math.PI * radius * radius * radius * 4.0 / 6.0;
  }

  public void setId(int value) {
    m_id = value;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(m_volume);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(m_x);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(m_y);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Droplet other = (Droplet) obj;
    if (Double.doubleToLongBits(m_volume) != Double
        .doubleToLongBits(other.m_volume))
      return false;
    if (Double.doubleToLongBits(m_x) != Double.doubleToLongBits(other.m_x))
      return false;
    if (Double.doubleToLongBits(m_y) != Double.doubleToLongBits(other.m_y))
      return false;
    return true;
  }

  public Double distance(Droplet other) {
    Point lhs_point = getCenter();
    Point rhs_point = other.getCenter();
    
    return lhs_point.distance(rhs_point);
  }

  public void setMovement(double xdiff, double ydiff) {
    m_xMovement = xdiff;
    m_yMovement = ydiff;
  }

  public double getMovementTheta() {
    return Math.atan2(m_yMovement, m_xMovement);
  }

  public double getMovementRho() {
    double xsqrd = m_xMovement * m_xMovement;
    double ysqrd = m_yMovement * m_yMovement;
    return Math.sqrt(xsqrd + ysqrd);
  }

  public double getRotation(Droplet drop2) {
    double xdiff = m_x - drop2.m_x;
    double ydiff = m_y - drop2.m_y;
    return Math.atan2(ydiff, xdiff);
  }
  
  public void setScore(double score){
    m_score = score;
  }
  
  public double getScore(){
    return m_score;
  }
}
