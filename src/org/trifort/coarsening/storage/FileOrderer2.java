/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.storage;

import java.io.File;
import java.util.*;

public class FileOrderer2 {

  private Set<Integer> m_Filenumbers;
  private int m_MaxNumber;
  private String m_Prefix;
  
  public List<String> order(File[] files){
    m_Filenumbers = new HashSet<Integer>();
    m_MaxNumber = Integer.MIN_VALUE;

    findPrefix(files);
    findFileNumbers(files);
    List<String> ret = new ArrayList<String>();
    if(m_Filenumbers.isEmpty())
      return ret;
    String base_path = justFolder(files[0].getAbsolutePath());
    String extension = findExtension(files);
    for(int i = 0; i <= m_MaxNumber; ++i){
      if(m_Filenumbers.contains(i)){
        ret.add(base_path+m_Prefix+i+extension);
      }
    }
    return ret;
  }
  
  public String justFolder(String input){
    File f = new File(input);
    if(f.isDirectory())
      return input;

    //now we know there is a . in the filename
    String s = File.separator;
    if(s.equals("\\"))
      s = "\\\\";
    String[] tokens = input.split(s);
    String ret = "";
    for(int i = 0; i < tokens.length - 1; ++i){
      ret += tokens[i] + File.separator;
    }
    return ret;
  }

  public List<String> order(String[] files){
    File[] typed_files = new File[files.length];
    int i = 0;
    for(String file : files){
      File f = new File(file);
      typed_files[i] = f;
      ++i;
    }
    return order(typed_files);
  }

  private String findExtension(File[] files){
    for(File f : files){
      if(f.isDirectory())
        continue;
      if(f.getName().startsWith("."))
        continue;
      return findExtension(f);
    }
    throw new RuntimeException("Cannot figure out file extensions");
  }

  public String findExtension(File file) {
    String name = file.getName();
    int index = name.lastIndexOf(".");
    return name.substring(index);
  }
  
  public List<String> order(String path){
    File f = new File(path);
    File[] files = f.listFiles();
    return order(files);
  }

  private void findFileNumbers(File[] files) {
    for(File f : files){
      if(f.isDirectory())
        continue;
      String name = f.getName();

      //stupid mac os makes hidden files
      if(name.startsWith(".")){
        continue;
      }
      
      String base_num = trimExtension(name);
      base_num = base_num.substring(m_Prefix.length());
      try {
        int num = Integer.parseInt(base_num);
        m_Filenumbers.add(num);
        if(num > m_MaxNumber){
          m_MaxNumber = num;
        }
      } catch(Exception ex){
        //continue
      }
    }
  }

  public static String trimExtension(String name) {
    int index = name.lastIndexOf(".");
    return name.substring(0, index);
  }
  
  private void findPrefix(File[] files) {    
    List<String> prefixes = new ArrayList<String>();
    for(File f : files){
      if(f.isDirectory())
        continue;
      String name = f.getName();

      //stupid mac os makes hidden files
      if(name.startsWith(".")){
        continue;
      }
      
      String base_name = trimExtension(name);
      String prefix = "";
      for(int i = 0; i < base_name.length(); ++i){
        char c = base_name.charAt(i);
        if(Character.isDigit(c)){
          break;
        }
        prefix += c;
      }
      
      prefixes.add(prefix);
      if(prefixes.size() > 40){
        break;
      }
    }
    m_Prefix = mostCommon(prefixes);
  }

  private String mostCommon(List<String> prefixes) {
    Map<String, Integer> count = new HashMap<String, Integer>();
    for(String prefix : prefixes){
      if(count.containsKey(prefix)){
        int cnt = count.get(prefix);
        cnt++;
        count.put(prefix, cnt);
      } else {
        count.put(prefix, 1);
      }
    }
    String ret = "";
    int max_count = 0;
    Iterator<String> iter = count.keySet().iterator();
    while(iter.hasNext()){
      String curr = iter.next();
      int cnt = count.get(curr);
      if(cnt > max_count){
        ret = curr;
        max_count = cnt;
      }
    }
    return ret;
  }
  
  public static void main(String[] args){
    FileOrderer2 orderer = new FileOrderer2();
    List<String> names = orderer.order("fo_test");
    for(String name : names){
      System.out.println(name);
    }
  }
}