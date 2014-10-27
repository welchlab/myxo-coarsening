package org.trifort.coarsening.storage;

import java.io.File;
import java.util.List;

public class Setup {

  private String m_bmpsFolder;
  
  public Setup(){
    ReadFile read_file = new ReadFile();
    List<String> lines = read_file.readLines("setup.txt", false);
    parseLines(lines);
  }
  
  private void parseLines(List<String> lines) {
    for(String line : lines){
      String[] tokens = line.split("=");
      if(tokens.length != 2){
        throw new RuntimeException("invalid setup file");
      }
      String key = tokens[0].trim();
      String value = tokens[1].trim();
      if(key.equals("bmps_folder")){
        m_bmpsFolder = value;
      } else {
        throw new RuntimeException("invalid setup file");
      }
    }
  }

  public String getAnnotationFolder(){ 
    return normalizePath("exp_frames");
  }
  
  private String normalizePath(String path) {
    if(path.endsWith(File.separator) == false){
      path += File.separator;
    }
    return path;
  }

  public String getBmpsFolder(){
    return normalizePath(m_bmpsFolder);
  }
}
