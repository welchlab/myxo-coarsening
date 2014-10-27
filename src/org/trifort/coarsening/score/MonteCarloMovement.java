package org.trifort.coarsening.score;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.figures.FigureSignal;
import org.trifort.coarsening.simulators.RandomMovementPredictor;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;

public class MonteCarloMovement {

  public void create(){
    Setup setup = new Setup();
    String annot_folder = setup.getAnnotationFolder();
    File folder = new File(annot_folder);
    File[] children = folder.listFiles();
    
    List<OfCoarseMovie> movies = new ArrayList<OfCoarseMovie>();
    for(File child : children){
      if(child.isDirectory()){
        String name = child.getName();
        if(name.startsWith(".")){
          continue;
        }
        if(name.equals("1pwt-mic8-5.12.11") == false){
          continue;
        }
        OfCoarseMovie movie = new OfCoarseMovie();
        movie.open(child);
        movies.add(movie);
        
        break;
      }
    }
    
    OfCoarseMovie movie0 = movies.get(0);

    FigureSignal signal = new FigureSignal();
    
    for(int i = 0; i < 100; ++i){
      System.out.println("i="+i);
      RandomMovementPredictor predictor = new RandomMovementPredictor();
      OfCoarseMovie random = predictor.simulate(movie0);
   
      MovementScore movement_score = new MovementScore();
      List<Double> move_score = movement_score.score(movie0, random);
    
      for(int j = 0; j < move_score.size(); ++j){
        signal.add(j, move_score.get(j));
      }
    }
    
    try {
      PrintWriter writer = new PrintWriter("random_monte_carlo.txt");
      writer.println("median,stddev");
      for(int i = 0; i < signal.size(); ++i){
        double median = signal.getAverage(i);
        double stddev = signal.getStdDev(i);
        writer.println(median+","+stddev);
      }
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
    
    //System.out.println("move_scores: ");
    //for(Double value : move_score){
    //  System.out.println(value);
    //} 
    
    //try {
    //  MakeMpeg4 maker = new MakeMpeg4();
    //  maker.create(movie0, random, random, 3400, "random0");
    //} catch(Exception ex){
    //  ex.printStackTrace();
    //}
  }
  
  public static void main(String[] args){
    MonteCarloMovement simulator = new MonteCarloMovement();
    simulator.create();
  }
}
