package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;

/**
 * Scores movement via distance to pair droplet
 * @author pcpratts
 *
 */
public class MovementScore3 {

  public List<Double> score(OfCoarseMovie phys_movie, OfCoarseMovie sim_movie){
    List<Double> ret = new ArrayList<Double>();
    
    List<Droplet> phys_frame0 = phys_movie.getFrame(0).getDroplets();
    Map<Integer, Double> last_scores = new HashMap<Integer, Double>();
    ClosestDropletById closest = new ClosestDropletById();
    InsideRect inside_rect = new InsideRect();
    
    for(int i = 0; i < phys_movie.size(); ++i){
      List<Droplet> phys_frame = phys_movie.getFrame(i).getDroplets();
      List<Droplet> sim_frame = sim_movie.getFrame(i).getDroplets();

      double frame_sum = 0;
      double frame_count = 0;
      
      for(Droplet drop0 : phys_frame0){        
        if(inside_rect.check(drop0) == false){
          continue;
        }
      
        Droplet phys_drop = closest.find(drop0.getId(), phys_frame);

        Droplet sim_drop = closest.find(drop0.getId(), sim_frame);
        
        if(phys_drop == null || sim_drop == null){
          //double last_score = last_scores.get(drop0.getId());
          //frame_sum += last_score;
          //frame_count++;
        } else {
          try {
            
            double score = phys_drop.distance(sim_drop);
            
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
}
