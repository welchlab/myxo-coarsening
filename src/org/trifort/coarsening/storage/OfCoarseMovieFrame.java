/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class OfCoarseMovieFrame {

  private List<Droplet> m_droplets;
  private Map<Integer, Droplet> m_idToDroplets;
  private String m_name;
  
  public void open(String path){
    m_droplets = new ArrayList<Droplet>();
    m_idToDroplets = new TreeMap<Integer, Droplet>();
    
    Set<Droplet> visited = new HashSet<Droplet>();
    
    File file = new File(path);
    String filename = file.getName();
    m_name = filename;
    
    try {
      BufferedReader reader = new BufferedReader(new FileReader(path));
      String first_line = reader.readLine();
      boolean read_type = false;
      if(first_line.equals("MyxoFinder 2.1")){
        read_type = true;
      }
      int num = readInt(reader);
      for(int i = 0; i < num; ++i){
        if(read_type){
          reader.readLine();
        }
        int id = readInt(reader);
        int x = readInt(reader);
        int y = readInt(reader);
        int radius = readInt(reader);
        
        Droplet droplet = new Droplet(id, x, y, radius);
        if(droplet.getVolume() < 500){
          continue;
        }
        if(visited.contains(droplet)){
          continue;
        }
        visited.add(droplet);
        
        m_droplets.add(droplet);
        m_idToDroplets.put(id, droplet);
      }
      reader.close();
    } catch(Exception ex){
      System.out.println("error in frame: "+path);
      throw new RuntimeException(ex);
    }
  }

  public void save(String filename) {
    try {
      PrintWriter writer = new PrintWriter(filename);
      writer.println("MyxoFinder 2.1");
      writer.println(m_droplets.size());
      for(int i = 0; i < m_droplets.size(); ++i){
        Droplet droplet = m_droplets.get(i);
        writer.println("circle");
        writer.println(droplet.getId());
        writer.println((int) droplet.getCenter().x);
        writer.println((int) droplet.getCenter().y);
        writer.println((int) droplet.getRadius());
      }
      writer.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
  }
  
  public String getName(){
    return m_name;
  }
  
  private int readInt(BufferedReader reader) throws Exception {
    String line = reader.readLine();
    return Integer.parseInt(line);
  }

  public List<Droplet> getDroplets() {
    return m_droplets;
  }
  
  public Map<Integer, Droplet> getIdToDroplets(){
    return m_idToDroplets;
  } 

  public void setDroplets(List<Droplet> droplets) {
    m_droplets = droplets;
  }

  public OfCoarseMovieFrame copy() {
    OfCoarseMovieFrame ret = new OfCoarseMovieFrame();
    ret.m_droplets = new ArrayList<Droplet>();
    for(Droplet droplet : m_droplets){
      Droplet new_droplet = new Droplet(droplet);
      ret.m_droplets.add(new_droplet);
    }
    return ret;
  }
}
