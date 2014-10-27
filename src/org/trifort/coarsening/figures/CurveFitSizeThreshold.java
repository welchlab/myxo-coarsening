package org.trifort.coarsening.figures;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.simulators.SizePredictor;
import org.trifort.coarsening.simulators.SizePredictorEngine;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Setup;

public class CurveFitSizeThreshold {

  private void create() {
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
    
    List<OfCoarseMovie> trainingMovies = movies.subList(0,  15);
    List<OfCoarseMovie> predictionMovies = movies.subList(15,  movies.size());
    
    //double size = findSize(movies);
    double size = findSize(trainingMovies);
    double meanScore = meanScore(predictionMovies, (int) size);
    System.out.println("meanScore: "+meanScore);
  }

  private double meanScore(List<OfCoarseMovie> predictionMovies, int size) {
    List<Double> scores = new ArrayList<Double>();
    for(OfCoarseMovie movie : predictionMovies){
      SizePredictor predictor = new SizePredictor(size);
      OfCoarseMovie sim_movie = predictor.simulate(movie);
      
      MovieAccuracy accuracy = new MovieAccuracy();
      accuracy.compute(movie, sim_movie);
      List<Double> values = accuracy.getAccuracies();
      scores.add(values.get(values.size()-1));
    }
    
    double ret = mean(scores);
    return ret;
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
  
  private double findSize(List<OfCoarseMovie> trainingMovies) {
    Set<Integer> sizes = new TreeSet<Integer>();
    for(OfCoarseMovie movie : trainingMovies){
      for(int i = 0; i < movie.size(); ++i){
        OfCoarseMovieFrame frame = movie.getFrame(i);
        List<Droplet> droplets = frame.getDroplets();
        sizes.addAll(createRadiusHistogram(droplets));
      }
    }
    List<Integer> sizesList = new ArrayList<Integer>();
    sizesList.addAll(sizes);
    Collections.sort(sizesList);
    
    double retSize = -1;
    double retScore = -1;
    
    for(int size : sizesList){
      List<Double> scores = new ArrayList<Double>();
      for(OfCoarseMovie movie : trainingMovies){
        SizePredictor predictor = new SizePredictor(size);
        OfCoarseMovie sim_movie = predictor.simulate(movie);
        
        MovieAccuracy accuracy = new MovieAccuracy();
        accuracy.compute(movie, sim_movie);
        List<Double> values = accuracy.getAccuracies();
        scores.add(values.get(values.size()-1));
      }
      
      double meanScore = mean(scores);
      if(meanScore > retScore){
        System.out.println("new best size: "+meanScore+" "+size);
        retScore = meanScore;
        retSize = size;
      } else {
        System.out.println("score/size: "+meanScore+" "+size);
      }
    }
    
    System.out.println("best_size: "+retSize+" mean_score: "+retScore);
    return retSize;
  }

  private double mean(List<Double> scores){
    if(scores.size() % 2 == 0){
      //even count
      int halfPoint = scores.size() / 2;
      double score1 = scores.get(halfPoint);
      double score2 = scores.get(halfPoint-1);
      return (score1 + score2) / 2.0;
    } else {
      //odd count
      return scores.get(scores.size() / 2);
    }
  }
  
  public static void main(String[] args){
    CurveFitSizeThreshold curveFit = new CurveFitSizeThreshold();
    curveFit.create();
  }
}
