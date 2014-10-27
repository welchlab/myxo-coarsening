package org.trifort.coarsening.simulators;

public class MergeEvent {

  private int id1;
  private int id2;
  
  public MergeEvent(int id1, int id2){
    this.id1 = id1;
    this.id2 = id2;
  }
  
  public int getId1(){
    return id1;
  }
  
  public int getId2(){
    return id2;
  }
}
