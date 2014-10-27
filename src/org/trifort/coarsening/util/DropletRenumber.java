package org.trifort.coarsening.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.closest.ClosestDropletHistogram;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Setup;

public class DropletRenumber {

  public void run(File child) {
    OfCoarseMovie movie = new OfCoarseMovie();
    movie.open(child);
    
    OfCoarseMovieFrame prev = movie.getFrame(0);
    List<Droplet> droplets = prev.getDroplets();
    for(int i = 0; i < droplets.size(); ++i){
      Droplet curr = droplets.get(i);
      curr.setId(i+1);
    }
    
    for(int i = 1; i < movie.size(); ++i){
      OfCoarseMovieFrame next = movie.getFrame(i);
      List<Droplet> prev_droplets = prev.getDroplets();
      List<Droplet> next_droplets = next.getDroplets();
      
      ClosestDropletHistogram histogram = 
          new ClosestDropletHistogram(prev_droplets, next_droplets);
      for(Droplet curr : prev_droplets){
        Droplet next_drop = histogram.getClosest(curr);
        if(next_drop == null){
          continue;
        }
        next_drop.setId(curr.getId());
      }
      
      prev = next;
    }
    movie.save(child);
  }
  
  public void create(){
    Setup setup = new Setup();
    String annot_folder = setup.getAnnotationFolder();
    File folder = new File(annot_folder);
    File[] children = folder.listFiles();
    
    for(File child : children){
      if(child.isDirectory()){
        String name = child.getName();
        if(name.startsWith(".")){
          continue;
        }
        run(child);
      }
    }  
  }
  
  public static void main(String[] args){
    DropletRenumber renumberer = new DropletRenumber();
    renumberer.create();
  }
}
