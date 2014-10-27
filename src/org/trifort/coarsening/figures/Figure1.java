package org.trifort.coarsening.figures;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.Time;
import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.simulators.CoarseningPredictor;
import org.trifort.coarsening.simulators.CoarseningPredictorEngine;
import org.trifort.coarsening.simulators.SizePredictor;
import org.trifort.coarsening.simulators.SizePredictorEngine;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Setup;
import org.trifort.coarsening.storage.TimeStep;

public class Figure1 {

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
    
    FigureSignal signal = interpolateAccuracy(movies, max_len);
    FigureSignal remaining = interpolateRemaining(movies, max_len);
    
    if(signal.size() != remaining.size()){
      throw new RuntimeException("signal sizes do not match");
    }
    
    try {
      PrintWriter writer = new PrintWriter("matlab/create_figure1.m");
      
      writer.println(signal.writeAverage("ostwald_average"));
      writer.println(signal.writeStdDev("ostwald_stddev"));
      
      writer.println(remaining.writeAverage("remaining_average"));

      writer.println("figure1 = figure('Renderer','painters');");
      writer.println("axes1 = axes('Parent',figure1);");
      writer.println("xlim(axes1,[0 167]);");
      writer.println("ylim(axes1,[0 110]);");
      writer.println("hold(axes1,'all');");
      writer.println("x1 = (1:1:"+max_len+")';");
      writer.println("h0 = shadedErrorBar(x1, ostwald_average, ostwald_stddev);");
      writer.println("h1 = plot(remaining_average);");
      writer.println("set(h1(1), 'Color', [0 0 1]);");
      writer.println("legend([h0.mainLine h0.patch h1], 'Ostwald Ripening Accuracy', 'Ostwald Ripening Std. Dev.', 'Experiment Percent Remaining', 'Orientation', 'Horizontal');");
      
      writer.println("xlabel('Normalized Frame');");
      writer.println("ylabel('Percent Accuracy');");
      writer.println("title('Ostwald Ripening Mean Percent Accuracy and Mean Percent Remaining Domes');");
      
      writer.close();
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

  private FigureSignal interpolateRemaining(List<OfCoarseMovie> movies,
      int max_len) {
    
    FigureSignal ret = new FigureSignal();
    
    for(OfCoarseMovie movie : movies){
      
      System.out.println("interpolateRemaining: "+movie.getName());
      
      PercentRemaining remaining = new PercentRemaining();
      remaining.calculate(movie);
      List<Double> signal = remaining.getSignal();
      
      InterpolateSignal interopolater = new InterpolateSignal();
      List<Double> interpolated = interopolater.interpolate(signal, max_len);
      ret.addAll(interpolated);
      
    }
    return ret;
  }
  
  public static void main(String[] args){
    Figure1 figure = new Figure1();
    figure.create();
  }
}
