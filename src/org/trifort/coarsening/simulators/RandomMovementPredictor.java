package org.trifort.coarsening.simulators;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Point;

public class RandomMovementPredictor {

  public OfCoarseMovie simulate(OfCoarseMovie physical_movie) {
    OfCoarseMovie ret = new OfCoarseMovie();
    
    OfCoarseMovieFrame phys_frame0 = physical_movie.getFrame(0);
    OfCoarseMovieFrame sim_frame0 = phys_frame0.copy();
    
    List<OfCoarseMovieFrame> sim_frames = new ArrayList<OfCoarseMovieFrame>();
    sim_frames.add(sim_frame0);    
    
    for(int i = 1; i < physical_movie.size(); ++i){
      List<Droplet> phys_droplets = physical_movie.getFrame(i).getDroplets();
      OfCoarseMovieFrame prev_frame = sim_frames.get(i-1);
      List<Droplet> prev_droplets = prev_frame.getDroplets();
      List<Droplet> next_droplets = randomSimulate(phys_droplets, prev_droplets);
      OfCoarseMovieFrame new_frame = new OfCoarseMovieFrame();
      new_frame.setDroplets(next_droplets);
      sim_frames.add(new_frame);
    }
    
    ret.setFrames(sim_frames);
    
    return ret;
  }

  private List<Droplet> randomSimulate(List<Droplet> phys_droplets, List<Droplet> prev_droplets) {
    List<Droplet> ret = new ArrayList<Droplet>();
    
    ClosestDropletById closest = new ClosestDropletById();
    for(Droplet drop : prev_droplets){
      Droplet next_drop = new Droplet(drop);
      Droplet phys_drop = closest.find(drop.getId(), phys_droplets);
      
      if(phys_drop == null || phys_drop.getVolume() < 500){
        continue;
      }
      
      next_drop.setVolume(phys_drop.getVolume());
      
      double angle = Math.random() * Math.PI;
      
      //reverse angle half the time
      if(Math.random() > 0.5){
        angle *= -1;
      }
      
      double cell_movement_per_frame_pixels = 1;
      double distance = rand() * cell_movement_per_frame_pixels;
      
      double x_change = distance * Math.cos(angle);
      double y_change = distance * Math.sin(angle);
      
      double x_new = next_drop.getCenter().x + x_change;
      double y_new = next_drop.getCenter().y + y_change;
      
      Point new_center = new Point(x_new, y_new);
      next_drop.setCenter(new_center);
      
      ret.add(next_drop);
    }
    
    return ret;
  }
  
  private double rand(){
    return -Math.log(Math.random());
  }
}
