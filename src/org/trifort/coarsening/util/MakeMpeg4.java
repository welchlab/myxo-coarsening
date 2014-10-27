/* 
 * Copyright 2012 Phil Pratt-Szeliga and other contributors
 * http://trifort.org/
 * 
 * See the file LICENSE for copying permission.
 */

package org.trifort.coarsening.util;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

import edu.syr.pcpratts.image.Bitmap;
import edu.syr.pcpratts.image.BitmapFactory;
import edu.syr.pcpratts.util.FileOrderer2;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.io.File;
import java.util.List;

import org.apache.commons.imaging.formats.tiff.TiffImageParser;
import org.trifort.coarsening.closest.ClosestDropletById;
import org.trifort.coarsening.score.MovementScore3;
import org.trifort.coarsening.score.MovieAccuracy;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Options;
import org.trifort.coarsening.storage.Point;
import org.trifort.coarsening.storage.Setup;

public class MakeMpeg4 {
  
  private Setup setup;
  private boolean fullImage;
  private MovieAccuracy accuracy;
  private List<Double> movementScore;
  private int startFrame;
  private int fontSize;
  private float borderStroke;
  private float dropletStroke;
  private String movieName;
  private String redTitle;
  private boolean movementMovie;
  
  public MakeMpeg4(boolean fullImage){
    this.setup = new Setup();
    this.fullImage = fullImage;
    this.accuracy = new MovieAccuracy();
    this.movementMovie = false;
  }
  
  public void create(OfCoarseMovie physical_movie, OfCoarseMovie coarse_movie, 
      OfCoarseMovie size_movie, String status_str, String output_name,
      boolean training_movie) throws Exception {

    fontSize = 18;
    borderStroke = 1.0f;
    dropletStroke = 2.0f;
    movieName = physical_movie.getName();
    
    int beginMovie = 0;
    boolean show_stats = true;
    boolean show_red = true;
    
    String filename = physical_movie.getName();
    IMediaWriter writer = ToolFactory.makeWriter(output_name+".mp4");
     
    if(fullImage){
      writer.addVideoStream(0, 0, 1600, 1200);
    } else {
      writer.addVideoStream(0, 0, 320, 240);
    }
    
    accuracy.compute(physical_movie, coarse_movie);
    MovementScore3 movement_score = new MovementScore3();
    movementScore = movement_score.score(physical_movie, coarse_movie);

    long curr_time = 0;
    long frame_rate = DEFAULT_TIME_UNIT.convert(15, MILLISECONDS);

    boolean use_bg = true;
    boolean all_frames = true;
    
    BufferedImage prev = null;
    
    FileOrderer2 orderer = new FileOrderer2();
    List<String> files = null;
    if(use_bg){
      files = orderer.order(setup.getBmpsFolder()+filename+"/bmps/");
      OfCoarseMovieFrame frame0 = physical_movie.getFrame(0);
      String name = frame0.getName();
      
      startFrame = findStartFrame(name, files);
      beginMovie = Integer.valueOf(removeExtension(files.get(0)));
    }
    
    if(all_frames){
      for(int i = 0; i < files.size(); i += 5){
        System.out.println("frame: ["+i+"/"+files.size()+"]");
        
        BufferedImage img;
        if(i < startFrame){
          String image_filename = files.get(i);
          img = getImage(image_filename);
          
          if(fullImage == false){
            img = scaleImage(img, 320, 240, i);
          }
          
          if(show_stats){
            writeStats(img, i, show_red, training_movie);
          }
          
        } else {
          int sim_index = (i - startFrame)/5;
          String image_filename = files.get(i);
          BufferedImage image = getImage(image_filename);
          OfCoarseMovieFrame phys_frame0 = physical_movie.getFrame(0);
          
          if(sim_index >= physical_movie.size()){
            continue;
          }
          
          OfCoarseMovieFrame phys_frame = physical_movie.getFrame(sim_index);
          OfCoarseMovieFrame coarsening_frame = coarse_movie.getFrame(sim_index);
          OfCoarseMovieFrame size_frame = size_movie.getFrame(sim_index);
          
          if(fullImage == false){
            image = scaleImage(image, 320, 240, i);
          }
          
          img = createImage(phys_frame0, phys_frame, coarsening_frame, size_frame, 
            status_str, i, physical_movie.size(), image, show_red);
          if(show_stats){
            writeStats(img, i, show_red, training_movie);
          }
        }
        
        writer.encodeVideo(0, img, curr_time, DEFAULT_TIME_UNIT);
        prev = img;
        curr_time += frame_rate;
      }
    } else {
      for(int i = 0; i < physical_movie.size(); ++i){
        System.out.println("frame: ["+i+"/"+physical_movie.size()+"]");

        OfCoarseMovieFrame phys_frame0 = physical_movie.getFrame(0);
        OfCoarseMovieFrame phys_frame = physical_movie.getFrame(i);
        OfCoarseMovieFrame coarsening_frame = coarse_movie.getFrame(i);
        OfCoarseMovieFrame size_frame = size_movie.getFrame(i);
        
        BufferedImage image;
        if(use_bg){
          int frame_index = startFrame+i*5;
          if(frame_index < files.size()){
            String image_filename = files.get(frame_index);
            image = getImage(image_filename);
          } else {
            image = new BufferedImage(1600, 1200, BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D g = image.createGraphics();
            g.setPaint(Color.white);
            g.fillRect(0, 0, 1600, 1200);
            g.dispose();
          }
        } else {
          image = new BufferedImage(1600, 1200, BufferedImage.TYPE_3BYTE_BGR);
          Graphics2D g = image.createGraphics();
          g.setPaint(Color.white);
          g.fillRect(0, 0, 1600, 1200);
          g.dispose();
        }

        if(fullImage == false){
          image = scaleImage(image, 320, 240, i);
        }
        BufferedImage img = createImage(phys_frame0, phys_frame, coarsening_frame, size_frame, 
          status_str, i, physical_movie.size(), image, show_red);
        if(show_stats){
          writeStats(img, i, show_red, training_movie);
        }
        
        writer.encodeVideo(0, img, curr_time, DEFAULT_TIME_UNIT);
        prev = img;
        curr_time += frame_rate;
      }
    
    }
    
    //bug in xuggle: last two frames are not written
    writer.encodeVideo(0, prev, curr_time, DEFAULT_TIME_UNIT);
    
    writer.flush();
    writer.close();
  }
  
  private void writeStats(BufferedImage img, int index, boolean show_red,
      boolean training_movie){
    
    int white_color = 0xFFFFFF;
    int black_color = 0x000000;
    int red_color = 0xFF0000;
    int blue_color = 0x0000FF;
    
    Graphics2D graphics = img.createGraphics();
    int stat_index = (index/5) - (startFrame / 5);
    System.out.println("stat_index: "+stat_index);
    
    Color c = new Color(white_color);
    graphics.setPaint(c);
    
    //fill top banner
    graphics.fillRect(0, 0, 320, 20);
    graphics.fillRect(0, 20, 320, 20);
    c = new Color(black_color);
    graphics.setPaint(c);
    graphics.setFont(new Font("Arial", Font.BOLD, fontSize));
    graphics.setPaint(c);
    if(training_movie){
      graphics.drawString("training movie", 100, 15);
    } else {
      graphics.drawString("prediction movie", 85, 15);
    }
    graphics.drawString("blue: e-frame", 35, 35);
    if(show_red){
      if(redTitle == null){
      graphics.drawString("red: o-frame", 195, 35);
      } else {
      graphics.drawString(redTitle, 195, 35);
      }
    }
    
    //fill bottom banner
    c = new Color(white_color);
    graphics.setPaint(c);
    graphics.fillRect(0, 240-20, 320, 20);
    c = new Color(black_color);
    graphics.setPaint(c);
    graphics.drawString("f: "+intPad4(index), 10, 237);
    if(stat_index >= 0 && movementMovie == false && show_red){
      graphics.drawString("p: "+doubleTrunc2(accuracy.getAccuracy(stat_index)), 90, 237);
      graphics.drawString("m: "+intPad2(accuracy.getMatching(stat_index)), 190, 237);
      graphics.drawString("s: "+intPad2(accuracy.getStartSize()), 260, 237);
    }
    if(stat_index >= 0 && movementMovie){
      graphics.drawString("d: "+doubleTrunc2(movementScore.get(stat_index)), 90, 237);
    }
    graphics.dispose();
  }
  
  private String intPad4(int value){
    String ret = ""+value;
    while(ret.length() < 4){
      ret = "0" + ret;
    }
    return ret;
  }
  
  private String intPad3(int value){
    String ret = ""+value;
    while(ret.length() < 3){
      ret = "0" + ret;
    }
    return ret;
  }
  
  private String intPad2(int value){
    String ret = ""+value;
    while(ret.length() < 2){
      ret = "0" + ret;
    }
    return ret;
  }
  
  private String doubleTrunc2(double value){
    String str = ""+value;
    int dot_index = str.indexOf(".");
    String whole_part;
    String fract_part;
    if(dot_index == -1){
      whole_part = intPad3((int) value);
      fract_part = "00";
    } else {
      whole_part = intPad3((int) value);
      int end_index = dot_index+3;
      if(end_index >= str.length()){
        end_index = str.length();
      }
      fract_part = str.substring(dot_index+1, end_index);
      while(fract_part.length() < 2){
        fract_part += "0";
      }
    }
    return whole_part + "." + fract_part;
  }
  
  private BufferedImage getImage(String image_filename) throws Exception {
    BitmapFactory factory = new BitmapFactory();
    BufferedImage image;
    if(image_filename.endsWith(".tiff") || image_filename.endsWith(".tif")){
      TiffImageParser parser = new TiffImageParser();
      List<BufferedImage> images = parser.getAllBufferedImages(new File(image_filename));
      image = images.get(0);

      BufferedImage bmp_image = new BufferedImage(1600, 1200, BufferedImage.TYPE_3BYTE_BGR);
      for(int x = 0; x < 1600; ++x){
        for(int y = 0; y < 1200; ++y){
          int color = image.getRGB(x, y);
          bmp_image.setRGB(x, y, color);
        }
      }
      image = bmp_image;
    } else {
      Bitmap bmp = factory.create(image_filename);
      image = bmp.createBufferedImage();
    }
    return image;
  }

  private BufferedImage scaleImage(BufferedImage before, int width, int height, int index) {
    BufferedImage after = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    AffineTransform at = new AffineTransform();
    at.scale(0.2, 0.2);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
    BufferedImage ret = scaleOp.filter(before, after);
    return ret;
  }

  private int findStartFrame(String name, List<String> files) {
    String target = removeExtension(name);
    int index = Integer.valueOf(removeExtension(files.get(0)));
    for(String file : files){
      String curr = removeExtension(file);
      if(target.equals(curr)){
        return index;
      }
      ++index;
    }
    throw new RuntimeException("cannot find start_frame");
  }

  private String removeExtension(String path){
    File file = new File(path);
    String filename = file.getName();
    int index = filename.indexOf(".");
    return filename.substring(0, index);
  }
  
  public BufferedImage createImage(OfCoarseMovieFrame phys_frame0, OfCoarseMovieFrame phys_frame, 
      OfCoarseMovieFrame coarsening_frame, OfCoarseMovieFrame size_frame, String status_str,
      int index, int movie_size, BufferedImage ret, boolean show_red){
    int width = scale(Options.v().getWidth());
    int height = scale(Options.v().getHeight());
    int border = scale(Options.v().getBorderSize());

    int phys_color = 0x0000FF;
    int coarse_color = 0xFF0000;
    int phys_color_alpha = 0x1F0000FF;
    int coarse_color_alpha = 0x1FFF0000;
    int size_color = 0x00F700;
    int grey_color = 0x333333;
    int black_color = 0x000000;
    int white_color = 0xFFFFFF;
    
    boolean show_drops = true;
    boolean show_border = true;
    
    Graphics2D g = ret.createGraphics();
    
    List<Droplet> phys_drops = phys_frame.getDroplets();
    List<Droplet> coarsening_drops = coarsening_frame.getDroplets();

    Color c;
    
    if(show_drops){
      if(show_red){
        for(Droplet drop : coarsening_drops){
          
          Point center = drop.getCenter();
          int radius = (int) drop.getRadius();
          
          c = new Color(coarse_color_alpha, true);
          g.setPaint(c);
          g.fillOval(scale(center.x-radius), scale(center.y-radius), scale(2*radius), scale(2*radius));
          
          c = new Color(coarse_color, false);
          g.setPaint(c);
          Stroke stroke = new BasicStroke(dropletStroke);
          g.setStroke(stroke);
          g.drawOval(scale(center.x-radius), scale(center.y-radius), scale(2*radius), scale(2*radius));
        }
      }
      
      c = new Color(phys_color, true);
      g.setPaint(c);
      g.setFont(new Font("Arial", Font.BOLD, fontSize));
      for(Droplet drop : phys_drops){
        Point center = drop.getCenter();
        int radius = (int) drop.getRadius();
        
        c = new Color(phys_color_alpha, true);
        g.setPaint(c);
        g.fillOval(scale(center.x-radius), scale(center.y-radius), scale(2*radius), scale(2*radius));
        
        c = new Color(phys_color, false);
        g.setPaint(c);
        Stroke stroke = new BasicStroke(dropletStroke);
        g.setStroke(stroke);
        
        g.drawOval(scale(center.x-radius), scale(center.y-radius), scale(2*radius), scale(2*radius));
      }
    }

    c = new Color(grey_color);
    g.setPaint(c);

    Stroke stroke = new BasicStroke(borderStroke);
    g.setStroke(stroke);
    
    if(show_border){
      //draw border
      c = new Color(0x000000);
      g.setPaint(c);
      g.drawLine(border, border, border, height-border);
      g.drawLine(border, height-border, width-border, height-border);
      g.drawLine(width-border, height-border, width-border, border);
      g.drawLine(width-border, border, border, border);
    }
    
    
    stroke = new BasicStroke(1.0f);
    g.setStroke(stroke);
    
    boolean show_boxes = true;
    if(show_boxes){
      MovieBoxes boxes = new MovieBoxes();
      if(boxes.contains(movieName)){
        MovieBox box = boxes.get(movieName);
        box.draw(g, index);
      }
    }

    g.dispose();
    return ret;
  }
  
  private int scale(int value){
    if(fullImage){
      return value;
    } else {
      return value / 5;
    }
  }

  private int scale(double value){
    if(fullImage){
      return (int) value;
    } else {
      return (int) (value / 5.0);
    }
  }

  public void setRedTitle(String str) {
    redTitle = str;
  }

  public void setMovement(boolean value) {
    movementMovie = value;
  }
}