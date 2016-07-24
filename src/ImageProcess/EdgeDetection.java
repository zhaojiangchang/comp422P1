package ImageProcess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class EdgeDetection {
	BufferedImage inputImg,outputImg,tempImg;
	private Color[] pixelMatrix=new Color[9];
	private int[] R=new int[9];
	private int[] B=new int[9];
	private int[] G=new int[9];
	private String fileName;

	public EdgeDetection(String fileName) throws IOException {
		// TODO Auto-generated constructor stub
		this.fileName = fileName;
		this.inputImg = ImageIO.read(EdgeDetection.class.getResource(fileName));
		this.outputImg=new BufferedImage(this.inputImg.getWidth(),this.inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
		this.tempImg=new BufferedImage(this.inputImg.getWidth(),this.inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
	}
	public BufferedImage processImage() throws IOException{
		NoiseCancellation noiseCancellation = new NoiseCancellation(fileName);
		tempImg = noiseCancellation.medianFilter();
		for (int i = 1; i < inputImg.getWidth()-1; i++) {
			for (int j = 1; j < outputImg.getHeight()-1; j++) {
				pixelMatrix[0]=new Color(tempImg.getRGB(i-1,j-1));
				pixelMatrix[1]=new Color(tempImg.getRGB(i-1,j));
				pixelMatrix[2]=new Color(tempImg.getRGB(i-1,j+1));
				pixelMatrix[3]=new Color(tempImg.getRGB(i,j-1));
				pixelMatrix[4]=new Color(tempImg.getRGB(i,j));
				pixelMatrix[5]=new Color(tempImg.getRGB(i,j+1));
				pixelMatrix[6]=new Color(tempImg.getRGB(i+1,j-1));
				pixelMatrix[7]=new Color(tempImg.getRGB(i+1,j));
				pixelMatrix[8]=new Color(tempImg.getRGB(i+1,j+1));
				int edge=(int) convolution(pixelMatrix);
				outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
			}
		}
		return outputImg;
	}
	public static double convolution(Color[] pixelMatrix){

		int gx=(pixelMatrix[0].getRed()*-1)+(pixelMatrix[2].getRed())+(pixelMatrix[3].getRed()*-2)+(pixelMatrix[5].getRed()*2)+(pixelMatrix[6].getRed()*-1)+(pixelMatrix[8].getRed()*1);
		int gy=(pixelMatrix[0].getRed()+(pixelMatrix[1].getRed()*2)+(pixelMatrix[2].getRed()*1)+(pixelMatrix[6].getRed()*-1)+(pixelMatrix[7].getRed()*-2)+(pixelMatrix[8].getRed())*-1);
		return Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));

	}
}