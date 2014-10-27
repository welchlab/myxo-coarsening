package org.trifort.coarsening.figures;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;

public class PercentRemaining {

  private List<Double> m_percents;
  
  public void calculate(OfCoarseMovie phys_movie){
    m_percents = new ArrayList<Double>();
    double start_count = phys_movie.getFrame(0).getDroplets().size();
    
    for(int i = 0; i < phys_movie.size(); ++i){
      OfCoarseMovieFrame frame = phys_movie.getFrame(i);
      double curr_count = frame.getDroplets().size();
      
      double percent = curr_count / start_count * 100.0;
      m_percents.add(percent);
    }
    
  }
  
  public double get(int frame){
    return m_percents.get(frame);
  }
  
  public List<Double> getSignal(){
    return m_percents;
  }
}
