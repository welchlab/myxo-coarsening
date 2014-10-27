package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Options;

public class MovementScore {

  public List<Double> score(OfCoarseMovie phys_movie, OfCoarseMovie sim_movie){
    List<Double> ret = new ArrayList<Double>();
    
    List<Droplet> phys_frame0 = phys_movie.getFrame(0).getDroplets();
    Map<Integer, Double> last_scores = new HashMap<Integer, Double>();
    ClosestDropletById closest = new ClosestDropletById();
    InsideRect inside_rect = new InsideRect();
    
    ret.add(100.0);
    
    for(int i = 1; i < phys_movie.size(); ++i){
      List<Droplet> prev_phys_frame = phys_movie.getFrame(i-1).getDroplets();
      List<Droplet> prev_sim_frame = sim_movie.getFrame(i-1).getDroplets();
      List<Droplet> phys_frame = phys_movie.getFrame(i).getDroplets();
      List<Droplet> sim_frame = sim_movie.getFrame(i).getDroplets();

      double frame_sum = 0;
      double frame_count = 0;
      
      for(Droplet drop0 : phys_frame0){
        if(inside_rect.check(drop0) == false){
          continue;
        }
        Droplet prev_phys_drop = closest.find(drop0.getId(), prev_phys_frame);
        Droplet prev_sim_drop = closest.find(drop0.getId(), prev_sim_frame);
        Droplet phys_drop = closest.find(drop0.getId(), phys_frame);
        Droplet sim_drop = closest.find(drop0.getId(), sim_frame);
        
        if(phys_drop == null || sim_drop == null || prev_phys_drop == null || prev_sim_drop == null){
          //double last_score = last_scores.get(drop0.getId());
          //frame_sum += last_score;
          //frame_count++;
        } else {
          try {
            double score = scoreDroplets(drop0, phys_drop, sim_drop, prev_phys_drop, prev_sim_drop);
            
            phys_drop.setScore(score);
            sim_drop.setScore(score);
            
            frame_sum += score;
            frame_count++;
            last_scores.put(drop0.getId(), score);
          } catch(Exception ex){
            //droplet did not move
            continue;
          }
        }
      }
      
      if(frame_count == 0){
        continue;
      } else {
        double average = frame_sum / frame_count;
        ret.add(average);
      }
      
    }
    
    return ret;
  }

  private double scoreDroplets(Droplet drop0, Droplet phys_drop, Droplet sim_drop,
      Droplet prev_phys_drop, Droplet prev_sim_drop) {
   
    //add one to remove 0/0 errors and 0/x errors.
    double dist_phys = prev_phys_drop.distance(phys_drop);
    double dist_sim = prev_sim_drop.distance(sim_drop);
    
    double combined_dist = (dist_phys + dist_sim) / 2.0;
    
    if(combined_dist < 1){
      throw new RuntimeException();
    }
    
    double distance = Math.abs(dist_phys - dist_sim);
    double max_movement = Options.v().getMaxDistanceTravelPerFrame();
    double dist_ratio = (max_movement - distance) / max_movement;
    
    double dist_avg = dist_ratio * 100.0;
    
    double rad_phys = phys_drop.getRotation(prev_phys_drop);
    double rad_sim = sim_drop.getRotation(prev_sim_drop);
    
    double rad_diff = Math.abs(rad_phys - rad_sim);
    rad_diff = normalizeDiff(rad_diff);
    
    double rad_ratio = (Math.PI - rad_diff) / Math.PI;
    double rad_avg = rad_ratio * 100.0;
    
    double total_avg = (dist_avg + rad_avg) / 2;
    return total_avg;
  }

  private double normalizeDiff(double rad_diff) {
    if(rad_diff > Math.PI){
      rad_diff = (2*Math.PI) - rad_diff;
    }
    return rad_diff;
  }
}
