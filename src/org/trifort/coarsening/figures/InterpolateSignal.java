package org.trifort.coarsening.figures;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.storage.OfCoarseMovie;

public class InterpolateSignal {

  public int findMaxLength(List<OfCoarseMovie> movies) {
    int max = Integer.MIN_VALUE;
    for(OfCoarseMovie movie : movies){
      if(movie.size() > max){
        max = movie.size();
      }
    }
    return max;
  }
  
  public List<Double> interpolate(List<Double> signal, int max_len){
    
    //0 0 0 1 1 1 2 2
    List<Integer> signal_index = createIndexMap(signal.size(), max_len);
    
    List<Double> ret = new ArrayList<Double>();
    for(int index : signal_index){
      double value = signal.get(index);
      ret.add(value);
    }
  
    //printArray(ret);
    return ret;
  }
  
  private List<Integer> createIndexMap(int signal_len, int max_len){
    List<Integer> ret = new ArrayList<Integer>();
    
    int mod = max_len % signal_len;
    int div = max_len / signal_len;
   
    boolean mod_over_half = false;
    
    if(mod > signal_len / 2){
      mod = div;
      ++div;
      mod_over_half = true;
    }
    
    for(int i = 0; i < signal_len; ++i){
      if(i == signal_len - 1){
        if(mod_over_half){
          for(int j = 0; j < mod; ++j){
            ret.add(i);
          }
        } else {
          for(int j = 0; j < div; ++j){
            ret.add(i);
          }
          for(int j = 0; j < mod; ++j){
            ret.add(i);
          }
        }
      } else {
        for(int j = 0; j < div; ++j){
          ret.add(i);
        }
      }
    }
    
    if(ret.size() > max_len){
      ret = ret.subList(0, max_len);
    }
    
    return ret;
  }
  
  private void printArray(List values){
    System.out.println("printArray");
    for(Object value : values){
      System.out.println(value.toString());
    }
  }
  
  public static void main(String[] args){
    List<Double> values = new ArrayList<Double>();
    values.add(0.0);
    values.add(1.0);
    values.add(4.0);
    
    InterpolateSignal test = new InterpolateSignal();
    test.interpolate(values, 4);
    test.interpolate(values, 5);
    test.interpolate(values, 6);
    test.interpolate(values, 7);
    test.interpolate(values, 8);
    test.interpolate(values, 9);
    test.interpolate(values, 10);
    test.interpolate(values, 11);
    //test.interpolate(values, 12);
    //test.interpolate(values, 13);
    //List<Double> interp = test.interpolate(values, 14);
    //for(double value : interp){
    //  System.out.println(value);
    //}
  }
}
