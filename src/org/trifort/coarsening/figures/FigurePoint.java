package org.trifort.coarsening.figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FigurePoint {

  private List<Double> m_values;
  
  public FigurePoint(){
    m_values = new ArrayList<Double>();
  }
  
  public void addPoint(double point){
    m_values.add(point);
  }
  
  public double getAverage(){
    double sum = 0;
    double count = 0;
    for(Double value : m_values){
      sum += value;
      ++count;
    }
    return sum / count;
  }
  
  public double getStdDev(double average){
    double error_sum = 0;
    double count = 0;
    
    for(Double value : m_values){
      double error = average - value;
      double sqrd_error = error * error;
      error_sum += sqrd_error;
      ++count;
    }
    
    if(count == 0){
      return 0;
    }
    double std_dev = Math.sqrt(error_sum / count);
    return std_dev;
  }
  
  public void scale50Percent(double other_average) {
    double scaler = 50.0 / other_average;
    for(int i = 0; i < m_values.size(); ++i){
      double value = m_values.get(i);
      value *= scaler;
      m_values.set(i, value);
    }
  }
}

