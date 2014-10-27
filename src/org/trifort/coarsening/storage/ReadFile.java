package org.trifort.coarsening.storage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {

  public List<String> readLines(String filename, boolean skip_first) {
    List<String> ret = new ArrayList<String>();
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
      if(skip_first){
        reader.readLine();
      }
      while(true){
        String line = reader.readLine();
        if(line == null){
          break;
        }
        ret.add(line);
      }
      reader.close();
    } catch(Exception ex){
      ex.printStackTrace();
    }
    return ret;
  }
}
