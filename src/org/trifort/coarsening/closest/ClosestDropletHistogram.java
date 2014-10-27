package org.trifort.coarsening.closest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.trifort.coarsening.storage.Droplet;

public class ClosestDropletHistogram {

  private Map<Droplet, Droplet> m_matchings;
  
  public ClosestDropletHistogram(List<Droplet> droplets1,
      List<Droplet> droplets2) {

    List<HistogramEntry> histogram = new ArrayList<HistogramEntry>();

    //enter all distances
    for(int i = 0; i < droplets1.size(); ++i){
      for(int j = 0; j < droplets2.size(); ++j){
        Droplet droplet1 = droplets1.get(i);
        Droplet droplet2 = droplets2.get(j);
        
        HistogramEntry entry = new HistogramEntry(droplet1, droplet2);
        histogram.add(entry);
      }
    }
    
    //sort by distance
    Collections.sort(histogram);
        
    //going from smallest to largest, assign
    m_matchings = new HashMap<Droplet, Droplet>();
    Set<Droplet> taken_targets = new HashSet<Droplet>();
    
    for(int i = 0; i < histogram.size(); ++i){
      HistogramEntry entry = histogram.get(i);
      Droplet source = entry.getDroplet1();
      Droplet target = entry.getDroplet2();
      
      if(m_matchings.containsKey(source) == false){
        if(taken_targets.contains(target) == false){
          m_matchings.put(source, target);
          taken_targets.add(target);
        }
      }
    }
  }

  public Droplet getClosest(Droplet source){
    return m_matchings.get(source);
  }
  
  private class HistogramEntry implements Comparable<HistogramEntry> {

    private Droplet m_droplet1;
    private Droplet m_droplet2;
    private Double m_distance;
    
    public HistogramEntry(Droplet droplet1, Droplet droplet2){
      m_droplet1 = droplet1;
      m_droplet2 = droplet2;
      m_distance = droplet1.distance(droplet2);
    }

    public Droplet getDroplet1(){
      return m_droplet1;
    }
    
    public Droplet getDroplet2(){
      return m_droplet2;
    }
    
    @Override
    public int compareTo(HistogramEntry other) {
      return m_distance.compareTo(other.m_distance);
    }
  }
}
