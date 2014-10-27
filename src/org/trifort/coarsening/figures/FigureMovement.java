package org.trifort.coarsening.figures;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.score.MovementScore2;
import org.trifort.coarsening.simulators.CoarseningPredictor;
import org.trifort.coarsening.simulators.CoarseningPredictorEngine;
import org.trifort.coarsening.simulators.RandomMovementPredictor;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;
import org.trifort.coarsening.storage.TimeStep;

public class FigureMovement {

  public void create(){
    Setup setup = new Setup();
    String annot_folder = setup.getAnnotationFolder();
    File folder = new File(annot_folder);
    File[] children = folder.listFiles();
    
    List<String> moving_movies = new ArrayList<String>();
    moving_movies.add("1pwt-mic5-5.12.11"); //lots 7
    moving_movies.add("1pwt-mic8-5.27.11"); //lots 20
    moving_movies.add("1pwt-mic8-5.12.11"); //lots 18
    
    List<OfCoarseMovie> movies = new ArrayList<OfCoarseMovie>();
    for(File child : children){
      if(child.isDirectory()){
        String name = child.getName();
        if(name.startsWith(".")){
          continue;
        }
        
        if(moving_movies.contains(name) == false){
          continue;
        }
        
        OfCoarseMovie movie = new OfCoarseMovie();
        movie.open(child);
        movies.add(movie);
      }
    }

    InterpolateSignal interopolater = new InterpolateSignal();
    int max_len = interopolater.findMaxLength(movies);
    
    FigureSignal coarse_signal = interpolateCoarse(movies, max_len);
    FigureSignal random_signal = interpolateRandom(movies, max_len);
    
    coarse_signal.scale50Percent(random_signal);

    try {
      PrintWriter writer = new PrintWriter("matlab/create_figure_random.m");
      
      writer.println(coarse_signal.writeAverage("coarse_average"));
      writer.println(coarse_signal.writeStdDev("coarse_stddev"));

      writer.println("figure; hold;");
      writer.println("x1 = (1:1:"+max_len+")';");
      writer.println("h0 = shadedErrorBar(x1, coarse_average, coarse_stddev);");
      writer.println("legend([h0.mainLine h0.patch], 'Migration Accuracy', 'Movement Std. Dev.');");
      
      writer.println("xlabel('Normalized Frame');");
      writer.println("ylabel('Percent Accuracy');");
      writer.println("title('Migration Accracy');");
      
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  private FigureSignal interpolateRandom(List<OfCoarseMovie> movies, int max_len) {
    FigureSignal ret = new FigureSignal();
    
    for(OfCoarseMovie movie : movies){
      MovementScore2 ms2 = new MovementScore2(movie);
      
      for(int i = 0; i < 100; ++i){
        RandomMovementPredictor predictor = new RandomMovementPredictor();
        OfCoarseMovie random = predictor.simulate(movie);
        
        List<Double> signal = ms2.score(random);
        
        InterpolateSignal interopolater = new InterpolateSignal();
        List<Double> interpolated = interopolater.interpolate(signal, max_len);
        ret.addAll(interpolated);
      }
    }

    return ret;
  }

  private FigureSignal interpolateCoarse(List<OfCoarseMovie> movies, int max_len) {
    FigureSignal ret = new FigureSignal();
    
    //TODO: implement this for multi-tau
    /*
    for(OfCoarseMovie movie : movies){

      MovementScore2 ms2 = new MovementScore2(movie);
      
      CoarseningPredictorEngine engine = new CoarseningPredictorEngine();
      double cnum = engine.findCnum(movie);
      
      CoarseningPredictor predictor = new CoarseningPredictor();
      predictor.setCnum(cnum, (int) new TimeStep().get(movie.getName()));
      OfCoarseMovie sim_movie = predictor.simulate(movie);
      
      List<Double> signal = ms2.score(sim_movie);
      
      InterpolateSignal interopolater = new InterpolateSignal();
      List<Double> interpolated = interopolater.interpolate(signal, max_len);
      ret.addAll(interpolated);
    }
    */
    
    return ret;
  }

  public static void main(String[] args){
    FigureMovement figure = new FigureMovement();
    figure.create();
  }
}
