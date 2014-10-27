package org.trifort.coarsening.figures;

import java.util.ArrayList;
import java.util.List;

public class FigureSignal {

  private List<FigurePoint> m_points;
  
  public FigureSignal(){
    m_points = new ArrayList<FigurePoint>();
  }

  public void add(int index, double distance) {
    while(m_points.size() <= index){
      m_points.add(new FigurePoint());
    }
    m_points.get(index).addPoint(distance);
  }
  
  public void addAll(List<Double> signal){
    if(m_points.size() == 0){
      //add all
      for(Double value : signal){
        FigurePoint new_point = new FigurePoint();
        new_point.addPoint(value);
        m_points.add(new_point);
      }
    } else if(m_points.size() != signal.size()){
      throw new RuntimeException("interpolation incorrect: "+m_points.size()+" "+signal.size());
    } else {
      for(int i = 0; i < signal.size(); ++i){ 
        double value = signal.get(i);
        FigurePoint point = m_points.get(i);   
        point.addPoint(value);
      }
    }
  }
  
  public int size(){
    return m_points.size();
  }
  
  public double getAverage(int frame){
    return m_points.get(frame).getAverage();
  }
  
  public double getStdDev(int frame){
    double avg = m_points.get(frame).getAverage();
    return m_points.get(frame).getStdDev(avg);
  }

  public String writeAverage(String variable_name){
    StringBuilder ret = new StringBuilder();
    ret.append(variable_name+" = [");
    for(int i = 0; i < size(); ++i){
      ret.append(getAverage(i));
      if(i < size() - 1){
        ret.append(";");
      }
    }
    ret.append("];\n");
    return ret.toString();
  }
  
  public String writeStdDev(String variable_name){
    StringBuilder ret = new StringBuilder();
    ret.append(variable_name+" = [");
    for(int i = 0; i < size(); ++i){
      ret.append(getStdDev(i));
      if(i < size() - 1){
        ret.append(";");
      }
    }
    ret.append("];\n");
    return ret.toString();
  }

  public List<Double> getAverages() {
    List<Double> ret = new ArrayList<Double>();
    for(int i = 0; i < size(); ++i){
      ret.add(getAverage(i));
    }
    return ret;
  }
  
  
  public void scale50Percent(FigureSignal random_signal) {
    for(int i = 1; i < size(); ++i){
      m_points.get(i).scale50Percent(random_signal.getAverage(i));
    }
  }
  
}
