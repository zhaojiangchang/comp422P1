package ImageProcess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EdgeDetction {
	static BufferedImage inputImgEdgeDetction,outputImgEdgeDetction;
	private int[][] pixelMatrix=new int[3][3];

   public EdgeDetction(String filename) throws IOException {
		// TODO Auto-generated constructor stub
	   this.inputImgEdgeDetction = ImageIO.read(EdgeDetction.class.getResource(filename));
	   this.outputImgEdgeDetction=new BufferedImage(this.inputImgEdgeDetction.getWidth(),this.inputImgEdgeDetction.getHeight(),this.inputImgEdgeDetction.TYPE_INT_RGB);
   }
   public BufferedImage processImage(){

      int width = inputImgEdgeDetction.getWidth();
      int height = outputImgEdgeDetction.getHeight();
      System.out.println(width +"   "+height);
      for (int i = 1; i < width-1; i++) {
          for (int j = 1; j < height-1; j++) {
             pixelMatrix[0][0]=new Color(inputImgEdgeDetction.getRGB(i-1,j-1)).getRed();
             pixelMatrix[0][1]=new Color(inputImgEdgeDetction.getRGB(i-1,j)).getRed();
             pixelMatrix[0][2]=new Color(inputImgEdgeDetction.getRGB(i-1,j+1)).getRed();
             pixelMatrix[1][0]=new Color(inputImgEdgeDetction.getRGB(i,j-1)).getRed();
             pixelMatrix[1][2]=new Color(inputImgEdgeDetction.getRGB(i,j+1)).getRed();
             pixelMatrix[2][0]=new Color(inputImgEdgeDetction.getRGB(i+1,j-1)).getRed();
             pixelMatrix[2][1]=new Color(inputImgEdgeDetction.getRGB(i+1,j)).getRed();
             pixelMatrix[2][2]=new Color(inputImgEdgeDetction.getRGB(i+1,j+1)).getRed();
             int edge=(int) convolution(pixelMatrix);
             outputImgEdgeDetction.setRGB(i,j,(edge<<16 | edge<<8 | edge));
          }
       }
       return outputImgEdgeDetction;
   }
   public static double convolution(int[][] pixelMatrix){

	    int gy=(pixelMatrix[0][0]*-1)+(pixelMatrix[0][1]*-2)+(pixelMatrix[0][2]*-1)+(pixelMatrix[2][0])+(pixelMatrix[2][1]*2)+(pixelMatrix[2][2]*1);
	    int gx=(pixelMatrix[0][0])+(pixelMatrix[0][2]*-1)+(pixelMatrix[1][0]*2)+(pixelMatrix[1][2]*-2)+(pixelMatrix[2][0])+(pixelMatrix[2][2]*-1);
	    return Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));

	}
}