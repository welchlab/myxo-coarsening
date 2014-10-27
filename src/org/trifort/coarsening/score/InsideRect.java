package org.trifort.coarsening.score;

import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.Options;
import org.trifort.coarsening.storage.Point;

public class InsideRect {

  public boolean check(Droplet drop){    
    if(drop == null){
      return false;
    }
    
    int rect_border = Options.v().getBorderSize();
    Point center = drop.getCenter();
    if(center.x < rect_border){
      return false;
    }
    if(center.y < rect_border){
      return false;
    }
    if(center.x >= (Options.v().getWidth() - rect_border)){
      return false;
    }
    if(center.y >= (Options.v().getHeight() - rect_border)){
      return false;
    }
    return true;
  }

}
