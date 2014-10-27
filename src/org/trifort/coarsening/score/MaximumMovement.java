package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.figures.FigureSignal;
import org.trifort.coarsening.simulators.RandomMovementPredictor;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;

public class MaximumMovement {

  public List<Double> create(OfCoarseMovie phys_movie){
    FigureSignal ret = new FigureSignal();
    
    for(int i = 0; i < 100; ++i){
      RandomMovementPredictor predictor = new RandomMovementPredictor();
      OfCoarseMovie random = predictor.simulate(phys_movie);
      
      List<Double> curr_list = create(phys_movie, random);
      ret.addAll(curr_list);
    }
    
    return ret.getAverages();
  }
  
  private List<Double> create(OfCoarseMovie phys_movie, OfCoarseMovie sim_movie){
    List<Double> ret = new ArrayList<Double>();
    List<Droplet> phys_frame0 = phys_movie.getFrame(0).getDroplets();
    for(int i = 0; i < phys_movie.size(); ++i){
      double phys_max_movement = findMaxMovement(phys_frame0, phys_movie, i);
      double sim_max_movement = findMaxMovement(phys_frame0, sim_movie, i);
      
      if(phys_max_movement > sim_max_movement){
        ret.add(phys_max_movement);
      } else {
        ret.add(sim_max_movement);
      }
    }
    return ret;
  }

  private double findMaxMovement(List<Droplet> phys_frame0, OfCoarseMovie movie, int frame) {
    List<Droplet> other_droplets = movie.getFrame(frame).getDroplets();
    ClosestDropletById closest = new ClosestDropletById();
    InsideRect inside_rect = new InsideRect();
    
    double max_value = Double.MIN_VALUE;
    for(Droplet phys_drop : phys_frame0){
      if(inside_rect.check(phys_drop) == false){
        continue;
      }
      Droplet other_drop = closest.find(phys_drop.getId(), other_droplets);
      if(other_drop == null){
        continue;
      }
      double distance = phys_drop.distance(other_drop);
      if(distance > max_value){
        max_value = distance;
      }
    }
    return max_value; 
  }
}
