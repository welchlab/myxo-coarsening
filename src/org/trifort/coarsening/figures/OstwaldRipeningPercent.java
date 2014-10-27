package org.trifort.coarsening.figures;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.simulators.CoarseningPredictor;
import org.trifort.coarsening.simulators.TwoPhaseCoarseningPredictor;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.TimeStep;

public class OstwaldRipeningPercent {

  public FigureSignal create(List<OfCoarseMovie> movies, int max_len){
    
    FigureSignal ret = new FigureSignal();
    TimeStep time_step = new TimeStep();
    
    for(OfCoarseMovie movie : movies){
      System.out.println("interpolateAccuracy: "+movie.getName());
      List<Double> taus = time_step.get(movie.getName());
      double tau1 = taus.get(0);
      double tau2 = taus.get(1);
      
      TwoPhaseCoarseningPredictor predictor = new TwoPhaseCoarseningPredictor();
      OfCoarseMovie sim_movie = predictor.simulate(movie, tau1, tau2);
      
      MovieAccuracy accuracy = new MovieAccuracy();
      accuracy.compute(movie, sim_movie);
      List<Double> values = accuracy.getAccuracies();
      
      InterpolateSignal interopolater = new InterpolateSignal();
      List<Double> interpolated = interopolater.interpolate(values, max_len);
      ret.addAll(interpolated);
    }
    
    return ret;
  }
}