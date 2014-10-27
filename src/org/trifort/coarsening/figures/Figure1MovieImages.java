package org.trifort.coarsening.figures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.formats.tiff.TiffImageParser;
import org.trifort.coarsening.simulators.TwoPhaseCoarseningPredictor;
import org.trifort.coarsening.storage.Droplet;
import org.trifort.coarsening.storage.OfCoarseMovie;
import org.trifort.coarsening.storage.OfCoarseMovieFrame;
import org.trifort.coarsening.storage.Options;
import org.trifort.coarsening.storage.Point;
import org.trifort.coarsening.storage.Setup;
import org.trifort.coarsening.util.MakeMpeg4;

import edu.syr.pcpratts.image.Bitmap;
import edu.syr.pcpratts.image.BitmapFactory;

public class Figure1MovieImages {

  public void create(){
    Setup setup = new Setup();
    String annot_folder = setup.getAnnotationFolder();
    File folder = new File(annot_folder);
    File[] children = folder.listFiles();
    
    OfCoarseMovie movie = null;
    for(File child : children){
      if(child.isDirectory()){
        String name = child.getName();
        if(name.startsWith(".")){
          continue;
        }
        
        if(name.equals("1pwt-mic8-5.18.11")){
          movie = new OfCoarseMovie();
          movie.open(child);
        }
      }
    }
    
    TwoPhaseCoarseningPredictor predictor = new TwoPhaseCoarseningPredictor();
    OfCoarseMovie sim_movie = predictor.simulate(movie, 160, 700);
    
    try {
      createImage(movie, sim_movie, "1.bmp", -1, "image1.bmp", "0h");
      createImage(movie, sim_movie, "360.bmp", -1, "image2.bmp", "6h");
      createImage(movie, sim_movie, "600.bmp", 0, "image3.bmp", "10h");
      createImage(movie, sim_movie, "900.bmp", 60, "image4.bmp", "15h");
      createImage(movie, sim_movie, "1440.bmp", 157, "image5.bmp", "24h");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  private void createImage(OfCoarseMovie movie, OfCoarseMovie sim_movie, String bmpFilename,
      int simIndex, String filename, String time) throws Exception {
    
    String baseFolder = "/Users/pcpratts/Desktop/ofcoarse_bmps/1pwt-mic8-5.18.11/bmps/";
    String fullFilename = baseFolder + bmpFilename;
    BufferedImage image = getImage(fullFilename);
    
    Graphics2D g = image.createGraphics();
    
    int phys_color = 0x0000FF;
    int coarse_color = 0xFF0000;
    int phys_color_alpha = 0x1F0000FF;
    int coarse_color_alpha = 0x1FFF0000;
    int size_color = 0x00F700;
    int grey_color = 0x333333;
    int black_color = 0x000000;
    int white_color = 0xFFFFFF;
    float borderStroke = 4.0f;
    float dropletStroke = 4.0f;
    
    //draw border
    int width = Options.v().getWidth();
    int height = Options.v().getHeight();
    int border = Options.v().getBorderSize();

    Stroke stroke = new BasicStroke(borderStroke);
    g.setStroke(stroke);
    Color c = new Color(0x000000);
    g.setPaint(c);
    g.drawLine(border, border, border, height-border);
    g.drawLine(border, height-border, width-border, height-border);
    g.drawLine(width-border, height-border, width-border, border);
    g.drawLine(width-border, border, border, border);
    
    if(simIndex != -1){
      OfCoarseMovieFrame physFrame = movie.getFrame(simIndex);
      OfCoarseMovieFrame simFrame = sim_movie.getFrame(simIndex);
      
      for(Droplet drop : simFrame.getDroplets()){
        Point center = drop.getCenter();
        int radius = (int) drop.getRadius();
        
        c = new Color(coarse_color_alpha, true);
        g.setPaint(c);
        g.fillOval((int) (center.x-radius), (int) (center.y-radius), 2*radius, 2*radius);
        
        c = new Color(coarse_color, false);
        g.setPaint(c);
        stroke = new BasicStroke(dropletStroke);
        g.setStroke(stroke);
        g.drawOval((int) (center.x-radius), (int) (center.y-radius), 2*radius, 2*radius);
      }
      
      for(Droplet drop : physFrame.getDroplets()){
        Point center = drop.getCenter();
        int radius = (int) drop.getRadius();
        
        c = new Color(phys_color_alpha, true);
        g.setPaint(c);
        g.fillOval((int) (center.x-radius), (int) (center.y-radius), 2*radius, 2*radius);
        
        c = new Color(phys_color, false);
        g.setPaint(c);
        stroke = new BasicStroke(dropletStroke);
        g.setStroke(stroke);
        
        g.drawOval((int) (center.x-radius), (int) (center.y-radius), 2*radius, 2*radius);
      }
    }
    
    ImageIO.write(image, "bmp", new File(filename));
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

  public static void main(String[] args){
    Figure1MovieImages creator = new Figure1MovieImages();
    creator.create();
  }
}
