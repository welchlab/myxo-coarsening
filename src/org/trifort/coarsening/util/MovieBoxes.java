package org.trifort.coarsening.util;

import java.util.HashMap;
import java.util.Map;

public class MovieBoxes {

  private Map<String, MovieBox> m_boxes;
  
  public MovieBoxes(){
    m_boxes = new HashMap<String, MovieBox>();
    m_boxes.put("1pwt-mic8-3.3.11", new MovieBox(155, 60, 80, 90, 1180, 1441, "F5b"));
    m_boxes.put("1pwt-mic8-5.27.11", new MovieBox(210, 55, 80, 80, 800, 1000, "F5a"));
  }
  
  public boolean contains(String movie_name){
    return m_boxes.containsKey(movie_name);
  }
  
  public MovieBox get(String movie_name){
    return m_boxes.get(movie_name);
  }
}
