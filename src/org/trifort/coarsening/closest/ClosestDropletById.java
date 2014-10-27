package org.trifort.coarsening.closest;

import java.util.List;

import org.trifort.coarsening.storage.Droplet;

public class ClosestDropletById {

  public Droplet find(int id, List<Droplet> droplets){
    for(Droplet droplet : droplets){
      int curr_id = droplet.getId();
      if(id == curr_id){
        return droplet;
      }
    }
    return null;
  }
}
