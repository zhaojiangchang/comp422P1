package ImageProcess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EdgeDetection {
	BufferedImage inputImg,outputImg;
	private int[][] pixelMatrix=new int[3][3];

   public EdgeDetection(String filename) throws IOException {
		// TODO Auto-generated constructor stub
	   this.inputImg = ImageIO.read(EdgeDetection.class.getResource(filename));
	   this.outputImg=new BufferedImage(this.inputImg.getWidth(),this.inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
   }
   public BufferedImage processImage(){

      int width = inputImg.getWidth();
      int height = outputImg.getHeight();
      System.out.println(width +"   "+height);
      for (int i = 1; i < width-1; i++) {
          for (int j = 1; j < height-1; j++) {
             pixelMatrix[0][0]=new Color(inputImg.getRGB(i-1,j-1)).getRed();
             pixelMatrix[0][1]=new Color(inputImg.getRGB(i-1,j)).getRed();
             pixelMatrix[0][2]=new Color(inputImg.getRGB(i-1,j+1)).getRed();
             pixelMatrix[1][0]=new Color(inputImg.getRGB(i,j-1)).getRed();
             pixelMatrix[1][2]=new Color(inputImg.getRGB(i,j+1)).getRed();
             pixelMatrix[2][0]=new Color(inputImg.getRGB(i+1,j-1)).getRed();
             pixelMatrix[2][1]=new Color(inputImg.getRGB(i+1,j)).getRed();
             pixelMatrix[2][2]=new Color(inputImg.getRGB(i+1,j+1)).getRed();
             int edge=(int) convolution(pixelMatrix);
             outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
          }
       }
       return outputImg;
   }
   public static double convolution(int[][] pixelMatrix){

	    int gy=(pixelMatrix[0][0]*-1)+(pixelMatrix[0][1]*-2)+(pixelMatrix[0][2]*-1)+(pixelMatrix[2][0])+(pixelMatrix[2][1]*2)+(pixelMatrix[2][2]*1);
	    int gx=(pixelMatrix[0][0])+(pixelMatrix[0][2]*-1)+(pixelMatrix[1][0]*2)+(pixelMatrix[1][2]*-2)+(pixelMatrix[2][0])+(pixelMatrix[2][2]*-1);
	    return Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));

	}
}