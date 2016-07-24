package ImageProcess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBufferByte;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class ImageEnhancement {
	private BufferedImage inputImg,outputImg;
	private int[]pixelMatrix=new int[9];

	public ImageEnhancement(String filename) throws IOException {
		// TODO Auto-generated constructor stub
		this.inputImg = ImageIO.read(ImageEnhancement.class.getResource(filename));
		this.outputImg = new BufferedImage(inputImg.getWidth(),inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
	}
	
	public BufferedImage filterConvolution(){
		int width = inputImg.getWidth();
		int height = outputImg.getHeight();
		System.out.println(width +"   "+height);
		for (int i = 1; i < width-1; i++) {
			for (int j = 1; j < height-1; j++) {
				pixelMatrix[0]=new Color(inputImg.getRGB(i-1,j-1)).getRed();
				pixelMatrix[1]=new Color(inputImg.getRGB(i-1,j)).getRed();
				pixelMatrix[2]=new Color(inputImg.getRGB(i-1,j+1)).getRed();
				pixelMatrix[3]=new Color(inputImg.getRGB(i,j-1)).getRed();
				pixelMatrix[4]=new Color(inputImg.getRGB(i,j)).getRed();
				pixelMatrix[5]=new Color(inputImg.getRGB(i,j+1)).getRed();
				pixelMatrix[6]=new Color(inputImg.getRGB(i+1,j-1)).getRed();
				pixelMatrix[7]=new Color(inputImg.getRGB(i+1,j)).getRed();
				pixelMatrix[8]=new Color(inputImg.getRGB(i+1,j+1)).getRed();
				int edge=convolution(pixelMatrix);
				outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
			}
		}
		return outputImg;
	}
	public BufferedImage medianFilter(){
		int width = inputImg.getWidth();
		int height = outputImg.getHeight();
		System.out.println(width +"   "+height);
		for (int i = 1; i < width-1; i++) {
			for (int j = 1; j < height-1; j++) {
				pixelMatrix[0]=new Color(inputImg.getRGB(i-1,j-1)).getRed();
				pixelMatrix[1]=new Color(inputImg.getRGB(i-1,j)).getRed();
				pixelMatrix[2]=new Color(inputImg.getRGB(i-1,j+1)).getRed();
				pixelMatrix[3]=new Color(inputImg.getRGB(i,j-1)).getRed();
				pixelMatrix[4]=new Color(inputImg.getRGB(i,j)).getRed();
				pixelMatrix[5]=new Color(inputImg.getRGB(i,j+1)).getRed();
				pixelMatrix[6]=new Color(inputImg.getRGB(i+1,j-1)).getRed();
				pixelMatrix[7]=new Color(inputImg.getRGB(i+1,j)).getRed();
				pixelMatrix[8]=new Color(inputImg.getRGB(i+1,j+1)).getRed();
				Arrays.sort(pixelMatrix);
				int edge = pixelMatrix[4];
				outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
			}
		}
		return outputImg;

	}
	public int convolution(int[] pixelMatrix){
		float[] data = {
				(float)1/9,(float)1/9,(float)1/9,
				(float)1/9,(float)1/9,(float)1/9,
				(float)1/9,(float)1/9,(float)1/9,
		};
		int g=(int)((pixelMatrix[0]*data[0])+(pixelMatrix[1]*data[1])+(pixelMatrix[2]*data[2])+
				(pixelMatrix[3]*data[3])+(pixelMatrix[4]*data[4])+(pixelMatrix[5]*data[5])+
				(pixelMatrix[6]*data[6])+(pixelMatrix[7]*data[7])+(pixelMatrix[8]*data[8]));
		return g;

	}
}