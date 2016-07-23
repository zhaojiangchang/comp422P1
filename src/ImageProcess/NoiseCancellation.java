package ImageProcess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class NoiseCancellation {
	private BufferedImage inputImgEdgeDetction,outputImgEdgeDetction;
	private int[][] pixelMatrix=new int[3][3];

   public NoiseCancellation(String filename) throws IOException {
		// TODO Auto-generated constructor stub
	   this.inputImgEdgeDetction = ImageIO.read(NoiseCancellation.class.getResource(filename));
	   this.outputImgEdgeDetction=new BufferedImage(inputImgEdgeDetction.getWidth(),inputImgEdgeDetction.getHeight(),inputImgEdgeDetction.TYPE_INT_RGB);
   }
   public BufferedImage processImage(){

      int width = inputImgEdgeDetction.getWidth();
      int height = outputImgEdgeDetction.getHeight();
      System.out.println(width +"   "+height);
      for (int i = 1; i < width-1; i++) {
          for (int j = 1; j < height-1; j++) {
            
          }
       }
       return outputImgEdgeDetction;
   }
}