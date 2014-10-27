/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.storage;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
public class OfCoarseMovie implements Comparable<OfCoarseMovie> {

  private List<OfCoarseMovieFrame> m_frames;
  private String m_name;
  
  public OfCoarseMovie(){
  }
  
  public void setName(String name){
    m_name = name;
  }
  
  public void open(File movie_folder) {
    m_frames = new ArrayList<OfCoarseMovieFrame>();
    m_name = movie_folder.getName();
    
    File full_path = new File(movie_folder.getAbsoluteFile()+"/fbodies/");
    FileOrderer2 orderer = new FileOrderer2();
    List<String> files = orderer.order(full_path.getAbsolutePath());

    int start_frame = 0;
    int stop_frame = files.size();

    for(int i = start_frame; i < stop_frame; ++i){
      String file = files.get(i);
      try {
        OfCoarseMovieFrame frame = new OfCoarseMovieFrame();
        frame.open(file);
        m_frames.add(frame);
      } catch(Exception ex){
        //bad file. choose previous file to add
        m_frames.add(m_frames.get(m_frames.size()-1));
      }
    }
  }
  
  public void setFrames(List<OfCoarseMovieFrame> frames){
    m_frames = frames;
  }

  public int size() {
    return m_frames.size();
  }

  public String getName() {
    return m_name;
  }

  public OfCoarseMovieFrame getFrame(int index) {
    return m_frames.get(index);
  }

  public void save(File movie_folder) {
    File full_path = new File(movie_folder.getAbsoluteFile()+"/fbodies/");
    FileOrderer2 orderer = new FileOrderer2();
    List<String> files = orderer.order(full_path.getAbsolutePath());
    
    for(int i = 0; i < files.size(); ++i){
      String file = files.get(i);
      OfCoarseMovieFrame frame = m_frames.get(i);
      frame.save(file);
    }
  }

  @Override
  public int compareTo(OfCoarseMovie other) {
    return m_name.compareTo(other.m_name);
  }

  public OfCoarseMovie submovie(double startFrame, double endFrame) {
    List<OfCoarseMovieFrame> subframes = m_frames.subList((int) startFrame, (int) endFrame);
    OfCoarseMovie ret = new OfCoarseMovie();
    ret.setFrames(subframes);
    return ret;
  }
}
