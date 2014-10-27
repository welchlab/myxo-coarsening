/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.storage;

public class Options {
  
  public static Options m_instance;
  
  public static Options v(){
    if(m_instance == null){
      m_instance = new Options();
    }
    return m_instance;
  }
  
  private boolean m_minimizeScore;
  
  public Options(){
    m_minimizeScore = true;
  }
  
  public boolean getMinimizeScore(){
    return m_minimizeScore;
  }
  
  public int getBorderSize(){
    return 250;
  }
  
  public int getWidth(){
    return 1600;
  }
  
  public int getHeight(){
    return 1200;
  }
  
  public int getDebugId(){
    return 47;
  }
  
  public double getMaxDistanceTravelPerFrame(){
    return 10;
  }
}
