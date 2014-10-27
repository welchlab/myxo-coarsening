package org.trifort.coarsening.simulators;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;

public class TwoPhaseCoarseningPredictor {

  public OfCoarseMovie simulate(OfCoarseMovie physical_movie, double tau1,
      double tau2) {
    
    List<OfCoarseMovieFrame> frames = new ArrayList<OfCoarseMovieFrame>();
    frames.add(physical_movie.getFrame(0).copy());
    
    int step1 = physical_movie.size() / 2;
    
    Equations equations = new Equations(physical_movie.getName());
    equations.setTimeStep(tau1);
    
    for(int i = 1; i < step1; ++i){
      OfCoarseMovieFrame prev = frames.get(i-1);
      OfCoarseMovieFrame curr = physical_movie.getFrame(i);
      List<Droplet> prev_droplets = prev.getDroplets();
      List<Droplet> curr_phys_droplets = curr.getDroplets();
      List<Droplet> next_droplets = equations.makePrediction(prev_droplets, curr_phys_droplets, i);
      
      OfCoarseMovieFrame next = new OfCoarseMovieFrame();
      next.setDroplets(next_droplets);
      frames.add(next);
    }
    
    equations.setTimeStep(tau2);
    
    for(int i = step1; i < physical_movie.size(); ++i){
      OfCoarseMovieFrame prev = frames.get(i - 1);
      OfCoarseMovieFrame curr = physical_movie.getFrame(i);
      List<Droplet> prev_droplets = prev.getDroplets();
      List<Droplet> curr_phys_droplets = curr.getDroplets();
      List<Droplet> next_droplets = equations.makePrediction(prev_droplets, curr_phys_droplets, i);
      
      OfCoarseMovieFrame next = new OfCoarseMovieFrame();
      next.setDroplets(next_droplets);
      frames.add(next);
    }
      
    OfCoarseMovie ret = new OfCoarseMovie();
    ret.setFrames(frames);
    
    return ret;
  }
}
