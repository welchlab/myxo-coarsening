package org.trifort.coarsening.score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.Options;

public class MovementScore2 {
  
  private List<Double> m_maxMovement;
  private OfCoarseMovie m_physMovie;
  
  private List<Double> m_moveScore;
  private List<Double> m_thetaScore;
  
  public MovementScore2(OfCoarseMovie phys_movie){
    m_physMovie = phys_movie;
    m_maxMovement = findMaximumMovement(phys_movie);
  }
  
  public List<Double> score(OfCoarseMovie sim_movie){
    List<Double> ret = new ArrayList<Double>();
    
    m_moveScore = new ArrayList<Double>();
    m_thetaScore = new ArrayList<Double>();
    
    List<Droplet> phys_frame0 = m_physMovie.getFrame(0).getDroplets();
    Map<Integer, Double> last_scores = new HashMap<Integer, Double>();
    ClosestDropletById closest = new ClosestDropletById();
    InsideRect inside_rect = new InsideRect();
    
    ret.add(100.0);
    
    for(int i = 1; i < m_physMovie.size(); ++i){
      List<Droplet> prev_phys_frame = m_physMovie.getFrame(0).getDroplets();
      List<Droplet> prev_sim_frame = sim_movie.getFrame(0).getDroplets();
      List<Droplet> phys_frame = m_physMovie.getFrame(i).getDroplets();
      List<Droplet> sim_frame = sim_movie.getFrame(i).getDroplets();

      double frame_sum = 0;
      double frame_count = 0;
      
      double move_sum = 0;
      double rad_sum = 0;
      
      for(Droplet drop0 : phys_frame0){
        if(inside_rect.check(drop0) == false){
          continue;
        }
        
        Droplet prev_phys_drop = closest.find(drop0.getId(), prev_phys_frame);
        Droplet prev_sim_drop = closest.find(drop0.getId(), prev_sim_frame);
        Droplet phys_drop = closest.find(drop0.getId(), phys_frame);
        Droplet sim_drop = closest.find(drop0.getId(), sim_frame);
        
        if(phys_drop == null || sim_drop == null || prev_phys_drop == null || prev_sim_drop == null){
          //double last_score = last_scores.get(drop0.getId());
          //frame_sum += last_score;
          //frame_count++;
        } else {
          try {
            double move_score = scoreDropletsMove(drop0, phys_drop, sim_drop, 
                prev_phys_drop, prev_sim_drop, m_maxMovement.get(i));
            double rad_score = scoreDropletsRad(drop0, phys_drop, sim_drop, 
                prev_phys_drop, prev_sim_drop, m_maxMovement.get(i));
            
            double score = (move_score + rad_score) / 2;
            
            phys_drop.setScore(score);
            sim_drop.setScore(score);
            
            frame_sum += score;
            move_sum += move_score;
            rad_sum += rad_score;
            frame_count++;
            last_scores.put(drop0.getId(), score);
          } catch(Exception ex){
            //droplet did not move
            continue;
          }
        }
      }
      
      if(frame_count == 0){
        continue;
      } else {
        double average = frame_sum / frame_count;
        ret.add(average);
        double move_avg = move_sum / frame_count;
        m_moveScore.add(move_avg);
        double rad_avg = rad_sum / frame_count;
        m_thetaScore.add(rad_avg);
      }
      
    }
    
    return ret;
  }
  
  public List<Double> getMoveScore(){
    return m_moveScore;
  }
  
  public List<Double> getThetaScore(){
    return m_thetaScore;
  }

  private List<Double> findMaximumMovement(OfCoarseMovie phys_movie) {
    MaximumMovement generator = new MaximumMovement();
    return generator.create(phys_movie);
  }

  private double scoreDropletsMove(Droplet drop0, Droplet phys_drop, Droplet sim_drop,
      Droplet prev_phys_drop, Droplet prev_sim_drop, double max_movement) {
   
    //add one to remove 0/0 errors and 0/x errors.
    double dist_phys = prev_phys_drop.distance(phys_drop);
    double dist_sim = prev_sim_drop.distance(sim_drop);
    
    double distance = Math.abs(dist_phys - dist_sim);
    double dist_ratio = (max_movement - distance) / max_movement;
    
    if(dist_ratio > 1){
      dist_ratio = 1;
    }
    
    double dist_avg = dist_ratio * 100.0;
    return dist_avg;
  }

  private double scoreDropletsRad(Droplet drop0, Droplet phys_drop, Droplet sim_drop,
      Droplet prev_phys_drop, Droplet prev_sim_drop, double max_movement) {

    double rad_phys = phys_drop.getRotation(prev_phys_drop);
    double rad_sim = sim_drop.getRotation(prev_sim_drop);
    
    double rad_diff = Math.abs(rad_phys - rad_sim);
    rad_diff = normalizeDiff(rad_diff);
    
    double rad_ratio = (Math.PI - rad_diff) / Math.PI;
    double rad_avg = rad_ratio * 100.0;
    return rad_avg;
  }

  private double normalizeDiff(double rad_diff) {
    if(rad_diff > Math.PI){
      rad_diff = (2*Math.PI) - rad_diff;
    }
    return rad_diff;
  }
}
