package org.trifort.coarsening.simulators;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;

public class PhasedCoarseningPredictor {

  public List<OfCoarseMovie> simulate(OfCoarseMovie physical_movie, double tau1,
      int movieIndex, int movieCount) {
    List<OfCoarseMovie> ret = new ArrayList<OfCoarseMovie>();
    List<OfCoarseMovieFrame> frames = new ArrayList<OfCoarseMovieFrame>();
    frames.add(physical_movie.getFrame(0).copy());
    
    int stepSize = physical_movie.size() / 2;
    
    Equations equations = new Equations(physical_movie.getName());
    equations.setTimeStep(tau1);
    
    for(int i = 1; i < physical_movie.size() - stepSize; ++i){
      OfCoarseMovieFrame prev = frames.get(i-1);
      OfCoarseMovieFrame curr = physical_movie.getFrame(i);
      List<Droplet> prev_droplets = prev.getDroplets();
      List<Droplet> curr_phys_droplets = curr.getDroplets();
      List<Droplet> next_droplets = equations.makePrediction(prev_droplets, curr_phys_droplets, i);
      
      OfCoarseMovieFrame next = new OfCoarseMovieFrame();
      next.setDroplets(next_droplets);
      frames.add(next);
    }
    
    for(double tau2 = 0; tau2 < 1500; tau2 += 10){
      System.out.println("movie: ["+movieIndex+"/"+movieCount+"] tau1: "+tau1+" tau2: "+tau2);
      List<OfCoarseMovieFrame> retFrames = new ArrayList<OfCoarseMovieFrame>();
      for(OfCoarseMovieFrame frame : frames){
        retFrames.add(frame);
      }
      
      OfCoarseMovieFrame prev = retFrames.get(retFrames.size()-1);
      equations.setTimeStep(tau2);
      for(int i = 0; i < stepSize; ++i){
        OfCoarseMovieFrame curr = physical_movie.getFrame(physical_movie.size() - stepSize + i);
        List<Droplet> prev_droplets = prev.getDroplets();
        List<Droplet> curr_phys_droplets = curr.getDroplets();
        List<Droplet> next_droplets = equations.makePrediction(prev_droplets, curr_phys_droplets, i);
        
        OfCoarseMovieFrame next = new OfCoarseMovieFrame();
        next.setDroplets(next_droplets);
        retFrames.add(next);
      }
      
      OfCoarseMovie retMovie = new OfCoarseMovie();
      retMovie.setFrames(retFrames);
      
      ret.add(retMovie);
    }
    
    return ret;
  }
}
