/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.simulators;

import java.util.*;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.Options;
import org.trifort.coarsening.storage.Point;

public class Equations {

  private double m_timeStep;
  private boolean m_timeStepSet;
  private double m_cnum;
  private boolean m_cnumSet;

  private double m_minAggregateSize;
  private List<Droplet> m_prevDroplets;
  private List<Droplet> m_nextDroplets;
  private int m_frame;
  private String m_movieName;
  private boolean m_movementReversed;
  private List<MergeEvent> mergeEvents;
  
  public Equations(String movie_name){
    m_minAggregateSize = 12;
    m_movieName = movie_name;
    m_timeStepSet = false;
    m_cnumSet = false;
    m_movementReversed = false;
    mergeEvents = new ArrayList<MergeEvent>();
  }
  
  public void setTimeStep(double time_step){
    m_timeStep = time_step;
    m_timeStepSet = true;
  }
  
  public void setCnum(double time_step, double cnum){
    m_timeStep = time_step;
    m_cnum = cnum;
    m_cnumSet = true;
  }
  
  public void reverseMovement(){
    m_movementReversed = true;
  }

  public List<Droplet> makePrediction(List<Droplet> droplets, 
      List<Droplet> next_frame, int frame) {
    
    m_frame = frame;
    m_prevDroplets = new ArrayList<Droplet>();
    for(Droplet drop : droplets){
      m_prevDroplets.add(new Droplet(drop));
    }
    
    m_nextDroplets = next_frame;
    
    resetDropletAverages();
    doPairDropletPrediction();
    averageValues();
    removeSmallDroplets();
    List<Droplet> ret = mergeDroplets();
    renumberDropletsAfterMerge(ret);

    if(m_cnumSet){
      trackVolumeViaPhysical(ret);
    }
    
    if(m_timeStepSet){
      trackDistanceViaPhysical(ret);
    }
    return ret;
  }
  
  private void renumberDropletsAfterMerge(List<Droplet> ret) {
    Map<Integer, Droplet> retIds = new HashMap<Integer, Droplet>();
    Map<Integer, Droplet> nextIds = new HashMap<Integer, Droplet>();
    
    hashDroplets(ret, retIds);
    hashDroplets(m_nextDroplets, nextIds);
    
    for(MergeEvent event : mergeEvents){
      int id1 = event.getId1();
      int id2 = event.getId2();
      
      if(retIds.containsKey(id1) && retIds.containsKey(id2)){
        continue;
      } else {
        if(retIds.containsKey(id1)){
          //here retIds contains id1 but not id2
          if(nextIds.containsKey(id1)){
            //if the next frame contains id1, we have something to track against
            continue;
          } else {
            //the next frame doesn't contain id1, try id2
            if(nextIds.containsKey(id2)){
              //remap droplet id to id2
              retIds.get(id1).setId(id2);
            }
          }
        }
        if(retIds.containsKey(id2)){
          //here retIds contains id2 but not id1
          if(nextIds.containsKey(id2)){
            //if the next frame contains id2, we have something to track against
            continue;
          } else {
            //the next frame doesn't contains id2, try id1
            if(nextIds.containsKey(id1)){
              //remap droplet id to id1
              retIds.get(id2).setId(id1);
            }
          }
        }
      }
    }
  }

  private void hashDroplets(List<Droplet> dropletList,
      Map<Integer, Droplet> dropletMap) {
    
    for(Droplet droplet : dropletList){
      dropletMap.put(droplet.getId(), droplet);
    }
  }

  private void trackVolumeViaPhysical(List<Droplet> ret){
    ClosestDropletById closest = new ClosestDropletById();
    for(int i = 0; i < m_prevDroplets.size(); ++i){
      Droplet prev = m_prevDroplets.get(i);
      Droplet phys_drop = closest.find(prev.getId(), m_nextDroplets);
      Droplet ret_drop = closest.find(prev.getId(), ret);
      
      if(ret_drop == null){
        continue;
      } else if(phys_drop == null || phys_drop.getVolume() < m_minAggregateSize){
        ret.remove(ret_drop);
        --i;
      } else {
        ret_drop.setVolume(phys_drop.getVolume());
      }
    }
  }
  
  private void trackDistanceViaPhysical(List<Droplet> ret) {
    
    ClosestDropletById closest = new ClosestDropletById();
    for(Droplet curr : ret){
      Droplet match = closest.find(curr.getId(), m_nextDroplets);
      if(match == null){
        continue;
      }
      curr.setCenter(match.getCenter());
    }
  }

  public double volume_update_drop1(int id1, int id2, double v1_k, double v2_k, 
    Point x1_k, Point x2_k, double radius1, double radius2){
        
    double L_k = x1_k.distance(x2_k);
 
		double term2 = m_timeStep * 
                      (4 * Math.pow(Math.PI, 4.0/3.0)) 
                    / 
                      (
                       Math.log(L_k / Math.pow(v1_k, 1.0/3.0)) +
  		                 Math.log(L_k / Math.pow(v2_k, 1.0/3.0))
  		                ) 
		                *
			                (
  			               Math.pow(v1_k, -1.0/3.0) - 
  			               Math.pow(v2_k, -1.0/3.0)
  			              );

		double volume_change = -term2;
    return volume_change;
	}

	public double volume_update_drop2(int id1, int id2, double v1_k, double v2_k, double v1_k1_diff){
	  double volume_change = -v1_k1_diff;
    return volume_change;
	}
	
  public Point center_update_drop1(int id, int id2, Point x1_k, Point x2_k, double v1_k, double v1_k1_diff){

    double g_1 = (2.0 / Math.pow(Math.PI, 2.0/3.0)) * m_cnum * Math.pow(v1_k, 2.0/3.0);
    
    double distance = x1_k.distance(x2_k);
    
    double c_12 = (1.0 / (3 * Math.pow(Math.PI, 1.0/3.0))) * Math.pow(distance, -1) 
      * Math.pow(v1_k, 1.0/3.0) * Math.log10(v1_k);

    double movement = (c_12 / g_1) * v1_k1_diff;
    
    double max_movement = Options.v().getMaxDistanceTravelPerFrame();
    if(movement > max_movement){
      movement = max_movement;
    }
    
    if(movement < -max_movement){
      movement = -max_movement;
    }
    
    if(m_movementReversed){ 
      movement *= -1;
    }
    
    double xdiff = x1_k.x - x2_k.x;
    double ydiff = x1_k.y - x2_k.y;
    
    double angle = Math.atan2(-ydiff, xdiff);
    
    double x_movement = -movement * Math.cos(angle);
    double y_movement = movement * Math.sin(angle);
    
    return new Point(x_movement, y_movement);
  }
  
  public Point center_update_drop2(int id, int id2, Point x1_k, Point x2_k, double v1_k, double v1_k1_diff, double v2_k){

    double g_2 = (2.0 / Math.pow(Math.PI, 2.0/3.0)) * m_cnum * Math.pow(v2_k, 2.0/3.0);
    
    double distance = x1_k.distance(x2_k);
    
    double c_21 = (1.0 / (3 * Math.pow(Math.PI, 1.0/3.0))) * Math.pow(distance, -1) * Math.pow(v2_k, 1.0/3.0) *
      Math.log10(v2_k);

    double movement = (c_21 / g_2) * v1_k1_diff;

    double max_movement = Options.v().getMaxDistanceTravelPerFrame();
    if(movement > max_movement){
      movement = max_movement;
    }
    
    if(movement < -max_movement){
      movement = -max_movement;
    }
    
    if(m_movementReversed){ 
      movement *= -1;
    }
    
    double xdiff = x1_k.x - x2_k.x;
    double ydiff = x1_k.y - x2_k.y;
    
    double angle = Math.atan2(-ydiff, xdiff);
    
    double x_movement = -movement * Math.cos(angle);
    double y_movement = movement * Math.sin(angle);

    return new Point(x_movement, y_movement);
  }

  private void averageValues() {
    for(int i = 0; i < m_prevDroplets.size(); ++i){
      Droplet drop = m_prevDroplets.get(i);
      
      drop.finalizePrediction();
      if(drop.getVolume() < m_minAggregateSize){
        m_prevDroplets.remove(i);
        --i;
      }
    }
  }

  private void doPairDropletPrediction() {
    
    for(int i = 0; i < m_prevDroplets.size(); ++i){
      Droplet drop1 = m_prevDroplets.get(i);
      
      if(drop1.getVolume() < m_minAggregateSize)
        continue;
      
      for(int j = i + 1; j < m_prevDroplets.size(); ++j){

        Droplet drop2 = m_prevDroplets.get(j);   
        
        if(drop2.getVolume() < m_minAggregateSize)
          continue;      
        
        double drop1_volume;
        double drop2_volume;
        Point drop1_center;
        Point drop2_center;
        
        Droplet drop1_calc;
        Droplet drop2_calc;
        
        if(drop1.getVolume() < drop2.getVolume()){
          drop1_calc = drop1;
          drop2_calc = drop2;
        } else {
          drop1_calc = drop2;
          drop2_calc = drop1;
        }
        
        if(m_timeStepSet){
          drop1_volume = volume_update_drop1(drop1_calc.getId(), drop2_calc.getId(), 
            drop1_calc.getVolume(), drop2_calc.getVolume(), drop1_calc.getCenter(), 
            drop2_calc.getCenter(), drop1_calc.getRadius(), drop2_calc.getRadius());
          drop2_volume = volume_update_drop2(drop1_calc.getId(), drop2_calc.getId(), 
            drop1_calc.getVolume(), drop2_calc.getVolume(), drop1_volume);
          
          drop1_center = new Point(0, 0);
          drop2_center = new Point(0, 0);
        } else {
        
          drop1_volume = volume_update_drop1(drop1_calc.getId(), drop2_calc.getId(), 
              drop1_calc.getVolume(), drop2_calc.getVolume(), drop1_calc.getCenter(), 
              drop2_calc.getCenter(), drop1_calc.getRadius(), drop2_calc.getRadius());
          drop2_volume = volume_update_drop2(drop1_calc.getId(), drop2_calc.getId(), 
              drop1_calc.getVolume(), drop2_calc.getVolume(), drop1_volume);
          
          drop1_center = center_update_drop1(drop1_calc.getId(), drop2_calc.getId(), drop1_calc.getCenter(), 
            drop2_calc.getCenter(), drop1_calc.getVolume(), drop1_volume);
          drop2_center = center_update_drop2(drop2_calc.getId(), drop1_calc.getId(), drop1_calc.getCenter(), 
            drop2_calc.getCenter(), drop1_calc.getVolume(), drop1_volume, drop2_calc.getVolume());
        
          drop1_volume = 0;
          drop2_volume = 0;
        }
        
        drop1_calc.addNextData(drop1_volume, drop1_center);
        drop2_calc.addNextData(drop2_volume, drop2_center);
      }
    }
  }

  private double trackedVolumeChange(int id) {
    
    ClosestDropletById closest = new ClosestDropletById();
    Droplet prev_drop = closest.find(id, m_prevDroplets);
    Droplet next_drop = closest.find(id, m_nextDroplets);
    
    double ret;
    
    if(next_drop != null){
      double prev_volume = prev_drop.getVolume();
      double next_volume = next_drop.getVolume();
      
      ret = next_volume - prev_volume;
    } else {
      double prev_volume = prev_drop.getVolume();
      
      ret = -prev_volume;
    }
    
    return ret;
  }

  public Point midpoint(Point p1, Point p2){
    double x = (p1.x + p2.x) / 2.0;
    double y = (p1.y + p2.y) / 2.0;
    return new Point(x, y);
  }

  private List<Droplet> mergeDroplets() {
    List<Droplet> output = new ArrayList<Droplet>();
    for(int i = 0; i < m_prevDroplets.size(); ++i){
      Droplet drop1 = m_prevDroplets.get(i);
      boolean merged = false;
      for(int j = i + 1; j < m_prevDroplets.size(); ++j){
        Droplet drop2 = m_prevDroplets.get(j);
        double distance = drop1.getCenter().distance(drop2.getCenter());
        
        double radius1 = drop1.getStartRadius();
        double radius2 = drop2.getStartRadius();
        if(distance - (radius1 + radius2) <= 0){
          MergeEvent new_event = new MergeEvent(drop1.getId(), drop2.getId());
          mergeEvents.add(new_event);
          
          Point center = midpoint(drop1.getCenter(), drop2.getCenter());
          double volume = drop1.getVolume() + drop2.getVolume();
          
          if(radius1 > radius2){
            drop1.setCenter(center);
            drop1.setVolume(volume);
            m_prevDroplets.remove(j);
            break;
          } else {
            drop2.setCenter(center);
            drop2.setVolume(volume);
            m_prevDroplets.remove(i);
            --i;
            merged = true;
            break;
          }
        }
      }
      if(!merged){
        output.add(new Droplet(drop1));
      }
    }

    return output;
  }

  private void removeSmallDroplets() {
    for(int i = 0; i < m_prevDroplets.size(); ++i){
      Droplet curr = m_prevDroplets.get(i);
      if(curr.getVolume() < m_minAggregateSize){
        m_prevDroplets.remove(i);
        --i;
      }
    }
  }

  private void resetDropletAverages() {
    for(int i = 0; i < m_prevDroplets.size(); ++i){
      m_prevDroplets.get(i).resetNextData();
    }
  }
  
}
