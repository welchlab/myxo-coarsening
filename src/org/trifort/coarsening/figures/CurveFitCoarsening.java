package org.trifort.coarsening.figures;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.simulators.CoarseningPredictor;
import org.trifort.coarsening.simulators.PhasedCoarseningPredictor;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;

public class CurveFitCoarsening {
  
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
    
    List<OfCoarseMovie> trainingMovies = movies.subList(0,  15);
    List<OfCoarseMovie> predictionMovies = movies.subList(15,  movies.size());
    
    List<Double> taus = findTaus(trainingMovies);
    System.out.println("taus: "+toString(taus));
    double score = findSingleScore(taus.get(0), taus.get(1), predictionMovies);
    System.out.println("score: "+score);
  }
  
  private List<Double> findTaus(List<OfCoarseMovie> trainingMovies) {
    double maxScore = 0;
    int bestTau1 = 0;
    int bestTau2 = 0;
    
    List<Integer> tau2 = new ArrayList<Integer>();
    for(int tau2Value = 0; tau2Value < 1500; tau2Value += 10){
      tau2.add(tau2Value);
    }
    
    for(int tau1 = 0; tau1 < 1500; tau1 += 10){
      List<Double> scores = findScore(tau1, trainingMovies);
      for(int i = 0; i < scores.size(); ++i){
        double score = scores.get(i);
        if(score > maxScore){
          maxScore = score;
          bestTau1 = tau1;
          bestTau2 = tau2.get(i);
          System.out.println("new max score: "+maxScore+" "+bestTau1+" "+bestTau2);
        } else {
          System.out.println("score: "+score+" "+tau1+" "+tau2.get(i));
        }
      }
    }
    
    List<Double> ret = new ArrayList<Double>();
    ret.add((double) bestTau1);
    ret.add((double) bestTau2);
    return ret;
  }

  private String toString(List<Double> tauItem) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for(Double value : tauItem){
      builder.append(value);
      builder.append(" ");
    }
    builder.append("]");
    return builder.toString();
  }

  private double summation(List<Double> values) {
    double ret = 0;
    for(Double value : values){
      ret += value;
    }
    return ret;
  }
  
  private double mean(List<Double> values){
    Collections.sort(values);
    if(values.size() % 2 == 0){
      //even count
      int halfPoint = values.size() / 2;
      double score1 = values.get(halfPoint);
      double score2 = values.get(halfPoint-1);
      return (score1 + score2) / 2.0;
    } else {
      //odd count
      return values.get(values.size() / 2);
    }
  }
  
  private double execScoringMechanism(List<Double> values){
    return summation(values);
  }
  
  private double findSingleScore(double tau1, double tau2, List<OfCoarseMovie> trainingMovies){
    List<Double> scores = new ArrayList<Double>();
    int movieIndex = 0;
    for(OfCoarseMovie movie : trainingMovies){
      PhasedCoarseningPredictor predictor = new PhasedCoarseningPredictor();
      List<OfCoarseMovie> simulated = predictor.simulate(movie, tau1, movieIndex, trainingMovies.size()-1);
      
      for(OfCoarseMovie simulatedItem : simulated){
        MovieAccuracy accuracy = new MovieAccuracy();
        accuracy.compute(movie, simulatedItem);
        List<Double> values = accuracy.getAccuracies();
        double score = execScoringMechanism(values);
        scores.add(score);
      }
      ++movieIndex;
    }
    return mean(scores);
  }
  
  private double maxFromRange(List<Double> values, int startIndex, int endIndex) {
    double max = 0;
    for(int i = startIndex; i < endIndex; ++i){
      double value = values.get(i);
      if(value > max){
        max = value;
      }
    }
    return max;
  }

  private List<Double> findScore(double tau1, List<OfCoarseMovie> trainingMovies) {
    List<List<Double>> scores = new ArrayList<List<Double>>();
    
    //seed with tau2 count
    for(int i = 0; i < 1500; i += 10){
      scores.add(new ArrayList<Double>());
    }
 
    long startTime = System.currentTimeMillis();
    int movieIndex = 0;
    for(OfCoarseMovie movie : trainingMovies){
      PhasedCoarseningPredictor predictor = new PhasedCoarseningPredictor();
      List<OfCoarseMovie> simulated = predictor.simulate(movie, tau1, movieIndex, trainingMovies.size()-1);
      
      int index = 0;
      for(OfCoarseMovie simulatedItem : simulated){
        MovieAccuracy accuracy = new MovieAccuracy();
        accuracy.compute(movie, simulatedItem);
        List<Double> values = accuracy.getAccuracies();
        double score = execScoringMechanism(values);
        scores.get(index).add(score);
        ++index;
      }
      ++movieIndex;
    }
    
    List<Double> ret = new ArrayList<Double>();
    for(int i = 0; i < scores.size(); ++i){
      List<Double> scoreList = scores.get(i);
      ret.add(mean(scoreList));
    }
    
    long stopTime = System.currentTimeMillis();
    long diffTime = stopTime - startTime;
    System.out.println("diffTime2: "+diffTime);
    return ret;
  }

  public static void main(String[] args){
    CurveFitCoarsening curveFit = new CurveFitCoarsening();
    curveFit.create();
  }
}
