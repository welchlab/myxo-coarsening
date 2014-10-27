package org.trifort.coarsening.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Setup;

import edu.syr.pcpratts.util.FileOrderer2;

public class ValidateInputs {

  public void create(){
    Setup setup = new Setup();
    String annot_folder = setup.getAnnotationFolder();
    File folder = new File(annot_folder);
    File[] children = folder.listFiles();
    
    List<OfCoarseMovie> movies = new ArrayList<OfCoarseMovie>();
    for(File child : children){
      if(child.isDirectory()){
        String name = child.getName();
        if(name.startsWith(".")){
          continue;
        }
        OfCoarseMovie movie = new OfCoarseMovie();
        movie.open(child);
        movies.add(movie);
      }
    }
    
    for(OfCoarseMovie movie : movies){
      FileOrderer2 orderer = new FileOrderer2();
      List<String> files = orderer.order(setup.getBmpsFolder()+movie.getName()+"/bmps/");
      validate(movie, files);
    }
  }
  
  private void validate(OfCoarseMovie movie, List<String> files) {
    System.out.println("validate: "+movie.getName()+" size: "+files.size());
    for(int i = 1; i < files.size(); ++i){
      String prev = files.get(i-1);
      String curr = files.get(i);
      
      int prev_num = getNumber(prev);
      int curr_num = getNumber(curr);
      
      if(curr_num != prev_num + 1){
        System.out.println("error: ");
        System.out.println("prev: "+prev);
        System.out.println("curr: "+curr);
      }
    }
  }
  
  private int getNumber(String filename){
    File file = new File(filename);
    String name = file.getName();
    int index = name.indexOf('.');
    if(index == -1){
      return Integer.valueOf(name);
    } else {
      name = name.substring(0, index);
      return Integer.valueOf(name);
    }
  }

  public static void main(String[] args){
   ValidateInputs validator = new ValidateInputs(); 
   validator.create();
  }
}
