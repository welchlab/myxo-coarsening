package org.trifort.coarsening.figures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.simulators.CoarseningPredictor;
import org.trifort.coarsening.simulators.RandomMovementPredictor;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;
import org.trifort.coarsening.util.MakeMpeg4;

public class RandomMovie {

  public void create(){
    Setup setup = new Setup();
    String annot_folder = setup.getAnnotationFolder();
    File folder = new File(annot_folder);
    File[] children = folder.listFiles();
    
    List<String> moving_movies = new ArrayList<String>();
    moving_movies.add("1pwt-mic8-5.12.11"); //lots
    
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
    
    OfCoarseMovie movie0 = movies.get(0);

    RandomMovementPredictor predictor = new RandomMovementPredictor();
    OfCoarseMovie random = predictor.simulate(movie0);
    
    try {
      MakeMpeg4 maker = new MakeMpeg4(false);
      maker.setRedTitle("red: r-frame");
      maker.setMovement(true);
      maker.create(movie0, random, random, "ts: "+3400.0, "r-movie", true);
    } catch(Exception ex){
      ex.printStackTrace();
    }
    
    CoarseningPredictor coarse_predictor = new CoarseningPredictor();
    coarse_predictor.setCnum(1.0/10.0, 8400);
    OfCoarseMovie migration = coarse_predictor.simulate(movie0);
    
    try {
      MakeMpeg4 maker = new MakeMpeg4(false);
      maker.setRedTitle("red: m-frame");
      maker.setMovement(true);
      maker.create(movie0, migration, migration, "ts: "+3400.0, "m-movie", true);
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    RandomMovie creator = new RandomMovie();
    creator.create();
  }
}
