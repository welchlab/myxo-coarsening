package org.trifort.coarsening.simulators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;

public class CoarseningPredictor { 
  
  private double m_timeStep;
  private boolean m_timeStepSet;
  private boolean m_multiTimeStepSet;
  private double m_cnum;
  private boolean m_cnumSet;
  private boolean m_movementReversed;
  private List<Double> timeStep;
    
  public CoarseningPredictor(){
    m_timeStepSet = false;
    m_cnumSet = false;
    m_movementReversed = false;
  }
  
  public void setTimeStep(double time_step){
    m_timeStep = time_step;
    m_timeStepSet = true;
  }
  
  public void setMultiTimeStep(List<Double> timeSteps){
    this.timeStep = timeSteps;
    m_multiTimeStepSet = true;
  }
  
  public void setCnum(double cnum){
    m_cnum = cnum;
    m_timeStep = 8400;
    m_cnumSet = true;
  }


  public void setCnum(double cnum, int time_step) {
    m_cnum = cnum;
    m_timeStep = time_step;
    m_cnumSet = true;
  }
  
  public void reverseMovement(){
    m_movementReversed = true;
  }
  
  public OfCoarseMovie simulate(OfCoarseMovie physical_movie) {

    List<OfCoarseMovieFrame> frames = new ArrayList<OfCoarseMovieFrame>();
    frames.add(physical_movie.getFrame(0).copy());
    
    Equations equations = new Equations(physical_movie.getName());
    if(m_timeStepSet){
      equations.setTimeStep(m_timeStep);
    } else if(m_multiTimeStepSet == false){
      equations.setCnum(m_timeStep, m_cnum);
    }
    if(m_movementReversed){
      equations.reverseMovement();
    }
    
    //equations.setTimeStep(timeStep.get(0));
    
    long startTime = System.currentTimeMillis();
    for(int i = 1; i < physical_movie.size(); ++i){
      //equations.setTimeStep(timeStep.get(i));
      
      OfCoarseMovieFrame prev = frames.get(i-1);
      OfCoarseMovieFrame curr = physical_movie.getFrame(i);
      List<Droplet> prev_droplets = prev.getDroplets();
      List<Droplet> curr_phys_droplets = curr.getDroplets();
      List<Droplet> next_droplets = equations.makePrediction(prev_droplets, curr_phys_droplets, i);
      
      OfCoarseMovieFrame next = new OfCoarseMovieFrame();
      next.setDroplets(next_droplets);
      frames.add(next);
    }
    long stopTime = System.currentTimeMillis();
    long diffTime = stopTime - startTime;
    System.out.println("diffTime1: "+diffTime);
    
    OfCoarseMovie ret = new OfCoarseMovie();
    ret.setFrames(frames);
    return ret;
  }
}
