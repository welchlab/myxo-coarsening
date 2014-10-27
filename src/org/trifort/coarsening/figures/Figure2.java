package org.trifort.coarsening.figures;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.simulators.SizePredictor;
import org.trifort.coarsening.simulators.SizePredictorEngine;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;

public class Figure2 {

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
    
    InterpolateSignal interopolater = new InterpolateSignal();
    int max_len = interopolater.findMaxLength(movies);  

    List<OfCoarseMovie> trainingMovies = movies.subList(0,  15);
    List<OfCoarseMovie> predictionMovies = movies.subList(15,  movies.size());
    
    FigureSignal signal = interpolateAccuracy(predictionMovies, max_len);
    FigureSignal size_signal = interpolateAccuracySize(predictionMovies, max_len);
    
    try {
      PrintWriter writer = new PrintWriter("matlab/create_figure2.m");
      
      writer.println(signal.writeAverage("ostwald_average"));
      writer.println(signal.writeStdDev("ostwald_stddev"));
      
      writer.println(size_signal.writeAverage("size_average"));
      writer.println(size_signal.writeStdDev("size_stddev"));
      
      writer.println("figure2_data(1,1) = ostwald_average(1);");
      writer.println("figure2_data(2,1) = ostwald_average(21);");
      writer.println("figure2_data(3,1) = ostwald_average(41);");
      writer.println("figure2_data(4,1) = ostwald_average(61);");
      writer.println("figure2_data(5,1) = ostwald_average(81);"); 
      writer.println("figure2_data(6,1) = ostwald_average(101);");
      writer.println("figure2_data(7,1) = ostwald_average(121);");

      writer.println("figure2_stddev(1,1) = ostwald_stddev(1);");
      writer.println("figure2_stddev(2,1) = ostwald_stddev(21);");
      writer.println("figure2_stddev(3,1) = ostwald_stddev(41);");
      writer.println("figure2_stddev(4,1) = ostwald_stddev(61);");
      writer.println("figure2_stddev(5,1) = ostwald_stddev(81);");
      writer.println("figure2_stddev(6,1) = ostwald_stddev(101);");
      writer.println("figure2_stddev(7,1) = ostwald_stddev(121);");

      writer.println("figure2_data(1,2) = size_average(1);");
      writer.println("figure2_data(2,2) = size_average(21);");
      writer.println("figure2_data(3,2) = size_average(41);");
      writer.println("figure2_data(4,2) = size_average(61);");
      writer.println("figure2_data(5,2) = size_average(81);");
      writer.println("figure2_data(6,2) = size_average(101);");
      writer.println("figure2_data(7,2) = size_average(121);");

      writer.println("figure2_stddev(1,2) = size_stddev(1);");
      writer.println("figure2_stddev(2,2) = size_stddev(21);");
      writer.println("figure2_stddev(3,2) = size_stddev(41);");
      writer.println("figure2_stddev(4,2) = size_stddev(61);");
      writer.println("figure2_stddev(5,2) = size_stddev(81);");
      writer.println("figure2_stddev(6,2) = size_stddev(101);");
      writer.println("figure2_stddev(7,2) = size_stddev(121);");

      writer.println("x2(1,1) = 0;");
      writer.println("x2(1,2) = 20;");
      writer.println("x2(1,3) = 40;");
      writer.println("x2(1,4) = 60;");
      writer.println("x2(1,5) = 80;");
      writer.println("x2(1,6) = 100;");
      writer.println("x2(1,7) = 120;");
      
      writer.println("figure;");
      String barweb = "barweb(figure2_data, figure2_stddev, 1, x2, ";
      barweb += "'Ostwald Ripening Simulation versus Size Threshold Simulation', ";
      barweb += "'Normalized Frame', 'Percent Accuracy', [], [], {'Ostwald Ripening', 'Size Threshold'}, [], []);";
      writer.println(barweb);
      writer.println("legend boxon;");
      
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  private FigureSignal interpolateAccuracy(List<OfCoarseMovie> movies,
      int max_len) {

    OstwaldRipeningPercent or_percent = new OstwaldRipeningPercent();
    return or_percent.create(movies, max_len);
    
  }

  private FigureSignal interpolateAccuracySize(List<OfCoarseMovie> movies,
      int max_len) {

    FigureSignal ret = new FigureSignal();
    for(OfCoarseMovie movie : movies){

      System.out.println("interpolateAccuracySize: "+movie.getName());
      
      //SizePredictorEngine size_engine = new SizePredictorEngine();
      //int radius = size_engine.findRadius(movie);

      int radius = 28;
      SizePredictor size_predictor = new SizePredictor(radius);
      OfCoarseMovie size_movie = size_predictor.simulate(movie);
      
      MovieAccuracy accuracy = new MovieAccuracy();
      accuracy.compute(movie, size_movie);
      List<Double> size_signal = accuracy.getAccuracies();
      
      InterpolateSignal interopolater = new InterpolateSignal();
      List<Double> interpolated = interopolater.interpolate(size_signal, max_len);
      ret.addAll(interpolated);
    }
    return ret;
  }
  
  
  public static void main(String[] args){
    Figure2 figure = new Figure2();
    figure.create();
  }
}
