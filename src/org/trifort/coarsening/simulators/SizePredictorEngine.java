package org.trifort.coarsening.simulators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;

public class SizePredictorEngine {

  private int m_maxFrame;
  
  public int findRadius(OfCoarseMovie movie) {
    OfCoarseMovieFrame frame0 = movie.getFrame(0);
    List<Integer> radius_histogram = createRadiusHistogram(frame0.getDroplets());
    int max_radius = -1;
    double max_average = Double.MIN_VALUE;
    for(int radius : radius_histogram){
      SizePredictor predictor = new SizePredictor(radius);
      OfCoarseMovie sim_movie = predictor.simulate(movie);
      
      MovieAccuracy accuracy = new MovieAccuracy();
      accuracy.compute(movie, sim_movie);
      List<Double> values = accuracy.getAccuracies();
      for(int frame = 100; frame < movie.size(); ++frame){
        double percent = values.get(frame);
        if(percent > max_average){
          max_average = percent;
          max_radius = radius;
          m_maxFrame = frame;
        }
      }
    }
    
    return max_radius;
  }

  public int getMaxFrame(){
    return m_maxFrame;
  }
  
  private List<Integer> createRadiusHistogram(List<Droplet> droplets) {
    Set<Integer> radius_set = new TreeSet<Integer>();
    for(Droplet droplet : droplets){
      radius_set.add(droplet.getStartRadius());
    }
    List<Integer> ret = new ArrayList<Integer>();
    for(Integer radius : radius_set){
      ret.add(radius);
    }
    Collections.sort(ret);
    return ret;
  }

}
