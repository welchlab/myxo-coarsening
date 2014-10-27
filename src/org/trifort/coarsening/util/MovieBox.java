package org.trifort.coarsening.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class MovieBox {

  private int startX;
  private int startY;
  private int width;
  private int height;
  private int startFrame;
  private int stopFrame;
  private String caption;
  
  public MovieBox(int startX, int startY, int width, int height, int startFrame, 
      int stopFrame, String caption){
    
    this.startX = startX;
    this.startY = startY;
    this.width = width;
    this.height = height;
    this.startFrame = startFrame;
    this.stopFrame = stopFrame;
    this.caption = caption;
  }
  
  public void draw(Graphics2D graphics, int frame){
    if(frame < startFrame || frame > stopFrame){
      return;
    }
    
    Color color = new Color(0xFF00FF);
    graphics.setColor(color);
    Stroke stroke = new BasicStroke(2.0f);
    graphics.setStroke(stroke);
    
    graphics.drawLine(startX, startY, startX+width, startY);
    graphics.drawLine(startX+width, startY, startX+width, startY+height);
    graphics.drawLine(startX+width, startY+height, startX, startY+height);
    graphics.drawLine(startX, startY+height, startX, startY);
    
    graphics.fillRect(startX, startY, 35, 20);
    
    color = new Color(0xFFFFFF);
    graphics.setColor(color);
    int fontSize = 16;
    graphics.setFont(new Font("Arial", Font.BOLD, fontSize));
    graphics.drawString(caption, startX+3, startY+15);
  }
}
