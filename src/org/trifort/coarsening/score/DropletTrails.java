package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;

public class DropletTrails {

  private List<List<Droplet>> m_trails;
  
  public void parse(OfCoarseMovie movie){
    m_trails = new ArrayList<List<Droplet>>();
    
    List<Droplet> frame0 = movie.getFrame(0).getDroplets();
    
    for(Droplet drop : frame0){
      List<Droplet> trail = new ArrayList<Droplet>();
      trail.add(drop);
      m_trails.add(trail);
      
      ClosestDropletById closest = new ClosestDropletById();
      
      for(int i = 1; i < movie.size(); ++i){
        List<Droplet> frameN = movie.getFrame(i).getDroplets();
        Droplet match = closest.find(drop.getId(), frameN);
        
        if(match == null){
          break;
        } else {
          trail.add(match);
        }
      }
    }
    
  }
  
  public int size(){
    return m_trails.size();
  }
  
  public List<Droplet> getTrail(int index){
    return m_trails.get(index);
  }
}