package org.trifort.coarsening.figures;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.trifort.coarsening.simulators.CoarseningPredictor;
import org.trifort.coarsening.simulators.PhasedCoarseningPredictor;
import org.trifort.coarsening.simulators.TwoPhaseCoarseningPredictor;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;
import org.trifort.coarsening.storage.TimeStep;
import org.trifort.coarsening.util.MakeMpeg4;
import org.trifort.coarsening.util.MovieBoxes;

public class Mpeg4 {

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
        
        OfCoarseMovie movie = new OfCoarseMovie();
        movie.open(child);
        movies.add(movie);
      }
    }

    List<OfCoarseMovie> trainingMovies = movies.subList(0, 15);
    List<OfCoarseMovie> predictionMovies = movies.subList(15, movies.size());
  
    try {
      PrintWriter writer = new PrintWriter("sup_materials4/ids.txt");
      writer.println("movie_name,stack_id");
      int stack_id = 1;
      for(OfCoarseMovie movie : trainingMovies){
        TwoPhaseCoarseningPredictor predictor = new TwoPhaseCoarseningPredictor();
        OfCoarseMovie sim_movie = predictor.simulate(movie, 160, 700);
        
        String status_str = "tau: [160, 700]";
        MakeMpeg4 maker = new MakeMpeg4(false);
        maker.create(movie, sim_movie, sim_movie, status_str, 
            "sup_materials4/stack"+stack_id, true);
        
        writer.println(movie.getName()+","+stack_id);
        ++stack_id;
      }
      for(OfCoarseMovie movie : predictionMovies){
        TwoPhaseCoarseningPredictor predictor = new TwoPhaseCoarseningPredictor();
        OfCoarseMovie sim_movie = predictor.simulate(movie, 160, 700);
        
        String status_str = "tau: [160, 700]";
        MakeMpeg4 maker = new MakeMpeg4(false);
        maker.create(movie, sim_movie, sim_movie, status_str, 
            "sup_materials4/stack"+stack_id, false);
        
        writer.println(movie.getName()+","+stack_id);
        ++stack_id;
      }
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    Mpeg4 creator = new Mpeg4();
    creator.create();
  }
}
