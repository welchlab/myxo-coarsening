package org.trifort.coarsening.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeStep {

  private Map<String, Double> m_values;
  
  public TimeStep(){
    m_values = new HashMap<String, Double>();
    m_values.put("1pwt-mic1-3.21.11", 30300.0);
    m_values.put("1pwt-mic1-3.3.11",14700.0);
    m_values.put("1pwt-mic1-5.19.11",23200.0);
    m_values.put("1pwt-mic3-5.12.11",39800.0);
    m_values.put("1pwt-mic5-3.3.11",16700.0);
    m_values.put("1pwt-mic5-3.8.11",7400.0);
    m_values.put("1pwt-mic5-5.12.11",8300.0);
    m_values.put("1pwt-mic5-5.27.11",37400.0);
    m_values.put("1pwt-mic6-2.21.11",12000.0);
    m_values.put("1pwt-mic7-2.21.11",900.0);
    m_values.put("1pwt-mic7-2.25.11",4100.0);
    m_values.put("1pwt-mic7-3.1.11",2500.0);
    m_values.put("1pwt-mic7-3.3.11",37300.0);
    m_values.put("1pwt-mic8-2.25.11",1000.0);
    m_values.put("1pwt-mic8-3.1.11",6200.0);
    m_values.put("1pwt-mic8-3.21.11",38800.0);
    m_values.put("1pwt-mic8-3.3.11",15800.0);
    m_values.put("1pwt-mic8-5.12.11",4600.0);
    m_values.put("1pwt-mic8-5.18.11",28600.0);
    m_values.put("1pwt-mic8-5.27.11",28600.0);
  }
  
  public List<Double> get(String movie_name){
    //return m_values.get(movie_name);
    List<Double> ret = new ArrayList<Double>();
    ret.add(160.0);
    ret.add(700.0);
    return ret;
  }
}