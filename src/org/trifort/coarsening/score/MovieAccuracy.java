package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Options;
import org.trifort.coarsening.storage.Point;
import org.trifort.coarsening.util.BoundingBox;

public class MovieAccuracy {
  
  private int startSize;
  private List<Double> accuracies;
  private List<Integer> matchings;

  public void compute(OfCoarseMovie phys_movie, OfCoarseMovie sim_movie){
    
    startSize = 0;
    accuracies = new ArrayList<Double>();
    matchings = new ArrayList<Integer>();
        
    InsideRect inside_rect = new InsideRect();
    
    List<Droplet> phys_drops0 = phys_movie.getFrame(0).getDroplets();
    for(Droplet phys_drop0 : phys_drops0){
      if(inside_rect.check(phys_drop0)){
        ++startSize;
      }
    }
    
    //System.out.println("start_size: "+start_size);
    
    for(int i = 0; i < phys_movie.size(); ++i){
      OfCoarseMovieFrame phys_frame = phys_movie.getFrame(i);
      OfCoarseMovieFrame sim_frame = sim_movie.getFrame(i);
      
      List<Droplet> phys_drops = phys_frame.getDroplets();
      List<Droplet> sim_drops = sim_frame.getDroplets();

      ClosestDropletById finder = new ClosestDropletById();
      
      int matching = 0;
      for(Droplet frame0_drop : phys_drops0){
        if(inside_rect.check(frame0_drop) == false){
          continue;
        }
        
        Droplet closest_phys = finder.find(frame0_drop.getId(), phys_drops);
        Droplet closest_sim = finder.find(frame0_drop.getId(), sim_drops);
        
        if(closest_phys != null && closest_sim != null){
          ++matching;
        } else if(closest_phys == null && closest_sim == null){
          ++matching;
        }
      }
      
      double percent = (double) matching / (double) startSize * 100.0;
      accuracies.add(percent);
      matchings.add(matching);
    }
  }
  
  public int getStartSize(){
    return startSize;
  }
  
  public int size(){
    return accuracies.size();
  }
  
  public double getAccuracy(int frame){
    return accuracies.get(frame);
  }
  
  public int getMatching(int frame){
    return matchings.get(frame);
  }

  public List<Double> getAccuracies() {
    return accuracies;
  }
}
