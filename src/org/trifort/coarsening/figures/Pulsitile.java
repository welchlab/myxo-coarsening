package org.trifort.coarsening.figures;

import static com.xuggle.xuggler.Global.DEFAULT_TIME_UNIT;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.apache.commons.imaging.formats.tiff.TiffImageParser;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

import edu.syr.pcpratts.image.Bitmap;
import edu.syr.pcpratts.image.BitmapFactory;
import edu.syr.pcpratts.util.FileOrderer2;

public class Pulsitile {

  private BufferedImage scaleImage(BufferedImage before, int width, int height, int index) {
    BufferedImage after = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    AffineTransform at = new AffineTransform();
    at.scale(0.2, 0.2);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
    BufferedImage ret = scaleOp.filter(before, after);
    return ret;
  }

  public void create(){
    String path = "/Volumes/TOSHIBA EXT/Mutant Movies/Pulsitale/MXAN_0180-mic5-11.04.11";
    FileOrderer2 orderer = new FileOrderer2();
    List<String> files = orderer.order(path);
    
    BitmapFactory factory = new BitmapFactory();
    IMediaWriter writer = ToolFactory.makeWriter("pulseitile_big.mp4");
    
    writer.addVideoStream(0, 0, 320, 240);
    
    long curr_time = 0;
    long frame_rate = DEFAULT_TIME_UNIT.convert(15, MILLISECONDS);
    
    for(String file : files){
      try {
        TiffImageParser parser = new TiffImageParser();
        List<BufferedImage> images = parser.getAllBufferedImages(new File(file));
        BufferedImage image = images.get(0);
  
        BufferedImage bmp_img = new BufferedImage(1600, 1200, BufferedImage.TYPE_3BYTE_BGR);
        for(int x = 0; x < 1600; ++x){
          for(int y = 0; y < 1200; ++y){
            int color = image.getRGB(x, y);
            bmp_img.setRGB(x, y, color);
          }
        }
  
        //bmp_img = scaleImage(bmp_img, 320, 240, 0);
      
        writer.encodeVideo(0, bmp_img, curr_time, DEFAULT_TIME_UNIT);
        curr_time += frame_rate;
      } catch(Exception ex){
        ex.printStackTrace();
      }
    }
    
    writer.close();
  }
  
  public static void main(String[] args){
    Pulsitile engine = new Pulsitile();
    engine.create();
  }
}
