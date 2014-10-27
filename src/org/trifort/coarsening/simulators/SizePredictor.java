package org.trifort.coarsening.simulators;

import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;

public class SizePredictor {

  private int m_radius;
  
  public SizePredictor(int radius){
    m_radius = radius;
  }
  
  public OfCoarseMovie simulate(OfCoarseMovie physical_movie) {
    OfCoarseMovieFrame frame = physical_movie.getFrame(0);
    
    List<OfCoarseMovieFrame> ret_frames = new ArrayList<OfCoarseMovieFrame>();
    ret_frames.add(frame.copy());
    
    OfCoarseMovieFrame frame2 = frame.copy();
    List<Droplet> droplets = frame2.getDroplets();
    for(int i = 0; i < droplets.size(); ++i){
      Droplet curr_droplet = droplets.get(i);
      if(curr_droplet.getStartRadius() <= m_radius){
        droplets.remove(i);
        --i;
      }
    }
    
    for(int i = 1; i < physical_movie.size(); ++i){
      ret_frames.add(frame2);
    }
    
    OfCoarseMovie ret = new OfCoarseMovie();
    ret.setFrames(ret_frames);
    return ret;
  }
}
