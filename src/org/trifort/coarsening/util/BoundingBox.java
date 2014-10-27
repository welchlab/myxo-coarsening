package org.trifort.coarsening.util;

import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.IntPoint;

public class BoundingBox {

  private IntPoint m_topLeft;
  private IntPoint m_botLeft;
  private IntPoint m_topRight;
  private IntPoint m_botRight;
  
  public BoundingBox(Droplet droplet) {
    int center_x = (int) droplet.getCenter().x;
    int center_y = (int) droplet.getCenter().y;
    double radius = droplet.getRadius();
    
    m_topLeft = new IntPoint((int) (center_x - radius), (int) (center_y - radius));
    m_botLeft = new IntPoint((int) (center_x - radius), (int) (center_y + radius));
    m_topRight = new IntPoint((int) (center_x + radius), (int) (center_y - radius));
    m_botRight = new IntPoint((int) (center_x + radius), (int) (center_y + radius));
  }

  public BoundingBox(Droplet droplet, int border) {
    int center_x = (int) droplet.getCenter().x;
    int center_y = (int) droplet.getCenter().y;
    double radius = droplet.getRadius();
    
    m_topLeft = new IntPoint((int) (center_x - radius) - border, (int) (center_y - radius) - border);
    m_botLeft = new IntPoint((int) (center_x - radius) - border, (int) (center_y + radius) + border);
    m_topRight = new IntPoint((int) (center_x + radius) + border, (int) (center_y - radius) - border);
    m_botRight = new IntPoint((int) (center_x + radius) + border, (int) (center_y + radius) + border);
  }

  /**
   * Returns true if this is inside other
   * @param box2
   * @return
   */
  public boolean inside(BoundingBox other) {
    if(m_topLeft.equals(other.m_topLeft) || 
       m_botLeft.equals(other.m_botLeft) || 
       m_topRight.equals(other.m_topRight) || 
       m_botRight.equals(other.m_botRight)
        ){
      return true;
    } 
    if(other.containsPoint(m_topLeft) ||
       other.containsPoint(m_botLeft) ||
       other.containsPoint(m_topRight) ||
       other.containsPoint(m_botRight)){
      return true;
    }
    return false;
  }

  private boolean containsPoint(IntPoint point) {
    if(point.getX() >= m_topLeft.getX() &&
       point.getY() >= m_topLeft.getY() &&
       point.getX() <= m_botRight.getX() &&
       point.getY() <= m_botRight.getY()){
      return true;
    }
    return false;
  }

}
