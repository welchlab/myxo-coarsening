package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Options;
import org.trifort.coarsening.storage.Point;

public class ScoreMovie2 {

  public List<Double> compute(OfCoarseMovie phys_movie, OfCoarseMovie sim_movie) {

    MovieAccuracy best = new MovieAccuracy();
    best.compute(phys_movie, sim_movie);
    List<Double> percent = best.getAccuracies();
        
    List<Double> ret = new ArrayList<Double>();
    for(int i = 0; i < phys_movie.size(); ++i){
      double curr_score = score(phys_movie, sim_movie, i, percent.get(i));
      ret.add(curr_score);
    }
    return ret;
  }
  
  public double score(OfCoarseMovie phys_movie, OfCoarseMovie sim_movie, int index, double percent) {
    double start_size = phys_movie.getFrame(0).getDroplets().size();
    OfCoarseMovieFrame phys_frame = phys_movie.getFrame(index);
    OfCoarseMovieFrame sim_frame = sim_movie.getFrame(index);
    
    List<Droplet> phys_drops = phys_frame.getDroplets();
    List<Droplet> sim_drops = sim_frame.getDroplets();
    
    //ClosestDropletHistogram histogram1 = new ClosestDropletHistogram(phys_drops, sim_drops);
    //ClosestDropletHistogram histogram2 = new ClosestDropletHistogram(sim_drops, phys_drops);
    
    ClosestDropletById closest = new ClosestDropletById();
    InsideRect inside_rect = new InsideRect();
    
    double sum1 = 0;
    int count1 = 0;
    double sum2 = 0;
    int count2 = 0;
    PolarArea polar_area_comp = new PolarArea();
    for(int i = 0; i < phys_drops.size(); ++i){
      Droplet phys_drop = phys_drops.get(i);
      if(inside_rect.check(phys_drop) == false){
        continue;
      }
      Droplet sim_drop = closest.find(phys_drop.getId(), sim_drops);
      
      if(sim_drop == null){
        continue;
      }
      
      double distance = sim_drop.getCenter().distance(phys_drop.getCenter());
      double volume_diff = sim_drop.getVolume() - phys_drop.getVolume();
      volume_diff *= volume_diff;
      volume_diff = Math.sqrt(volume_diff);
      sum1 += polar_area_comp.calculate(distance, volume_diff);
      ++count1;
    }
    
    for(int i = 0; i < sim_drops.size(); ++i){
      Droplet sim_drop = sim_drops.get(i);
      if(inside_rect.check(sim_drop) == false){
        continue;
      }
      Droplet phys_drop = closest.find(sim_drop.getId(), phys_drops);
      
      if(phys_drop == null){
        continue;
      }
      
      double distance = sim_drop.getCenter().distance(phys_drop.getCenter());
      double volume_diff = sim_drop.getVolume() - phys_drop.getVolume();
      volume_diff *= volume_diff;
      volume_diff = Math.sqrt(volume_diff);
      sum2 += polar_area_comp.calculate(distance, volume_diff);
      ++count2;
    }
    
    double divider = percent * percent;
    
    if(count1 == 0 && count2 == 0){
      throw new RuntimeException();
    } else if(count1 == 0){ 
      double value1 = sum2 / count2;
      return value1 / divider;
    } else if(count2 == 0){
      double value1 = sum1 / count1;
      return value1 / divider;
    } else {
      double value1 = (sum1 / count1) + (sum2 / count2);
      return value1 / divider;
    }
    
  }
}
