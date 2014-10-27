package org.trifort.coarsening.simulators;

import java.util.Collections;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.score.MovementScore;
import org.trifort.coarsening.score.MovementScore2;
import org.trifort.coarsening.score.MovementScore3;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.TimeStep;

public class CoarseningPredictorEngine {

  public double findTimeStep(OfCoarseMovie phys_movie) {
    int low = 0;
    int high = 40000;
    
    double max_score = Double.MIN_VALUE;
    double ret = 0;
    double max_frame = 0;
    
    for(int i = low; i < high; i += 100){
      CoarseningPredictor predictor = new CoarseningPredictor();
      predictor.setTimeStep(i);
      OfCoarseMovie sim_movie = predictor.simulate(phys_movie);
      MovieAccuracy accuracy = new MovieAccuracy();
      accuracy.compute(phys_movie, sim_movie);
      List<Double> values = accuracy.getAccuracies();
      
      for(int j = values.size() - 20; j < values.size(); ++j){
        double percent = values.get(j);
        if(percent > max_score){
          System.out.println("  max_score: "+i+" "+percent+" frame: "+j);
          max_score = percent;
          ret = i;
          max_frame = j;
        } else if(percent == max_score){
          if(j > max_frame){
            System.out.println("  max_score_better_frame: "+i+" "+percent+" frame: "+j);
            max_score = percent;
            ret = i;
            max_frame = j;
          }
        }
      }
    }
    
    System.out.println("movie: "+phys_movie.getName()+" accuracy: "+max_score+" frame: "+max_frame+" time_step: "+ret);
    //TotalAccuracy.v().addLine(phys_movie.getName()+","+max_score+","+max_frame);
    return ret;
  }

  public double findCnum(OfCoarseMovie movie) {
    double low = 0.01;
    double high = 2.8;
    
    double max_score = Double.MAX_VALUE;
    double best_cnum = -1;
    double max_frame = 0;
    
    for(double i = low; i < high; i += 0.01)
    {
      //System.out.println("find cnum: "+i);
      
      CoarseningPredictor predictor = new CoarseningPredictor();
      //TODO: implement this for multi-tau
      //predictor.setCnum(i, (int) new TimeStep().get(movie.getName()));
      predictor.setCnum(i, 991);
      OfCoarseMovie sim_movie = predictor.simulate(movie);
      
      MovementScore3 movement_score = new MovementScore3();
      List<Double> values = movement_score.score(movie, sim_movie);
      
      /*
      //double avg_score = average(score);
      double avg_score = mean(score);
      
      if(avg_score < max_score){
        max_score = avg_score;
        best_cnum = i;
        System.out.println("new best cnum: "+best_cnum+" "+max_score);
      } else {
        System.out.println("avg_score: "+avg_score+" cnum: "+i);
      }
      */
      double percent = values.get(values.size()-1);
      //for(int j = values.size() - 20; j < values.size(); ++j)
      {
      //  double percent = values.get(j);
      if(percent < max_score){
        System.out.println("  max_score: "+i+" "+percent);
        max_score = percent;
        best_cnum = i;
       } 
      }
    }
    
    return best_cnum;
  }

  private double average(List<Double> score) {
    
    double sum = 0;
    for(Double value : score){
      sum += value;
    }
    
    return sum / score.size();
  }
  
  private double mean(List<Double> scores){
    Collections.sort(scores);
    return scores.get(scores.size() / 2);
  }
}