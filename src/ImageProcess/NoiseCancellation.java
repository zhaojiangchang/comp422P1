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

public class NoiseCancellation {
	private BufferedImage inputImg,outputImg;
	private Color[] pixelMatrix=new Color[9];

	public NoiseCancellation(String filename) throws IOException {
		// TODO Auto-generated constructor stub
		this.inputImg = ImageIO.read(NoiseCancellation.class.getResource(filename));
		this.outputImg = new BufferedImage(inputImg.getWidth(),inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
	}
	
	public BufferedImage filterConvolution(){
		int width = inputImg.getWidth();
		int height = outputImg.getHeight();
		System.out.println(width +"   "+height);
		for (int i = 1; i < width-1; i++) {
			for (int j = 1; j < height-1; j++) {
				pixelMatrix[0]=new Color(inputImg.getRGB(i-1,j-1));
				pixelMatrix[1]=new Color(inputImg.getRGB(i-1,j));
				pixelMatrix[2]=new Color(inputImg.getRGB(i-1,j+1));
				pixelMatrix[3]=new Color(inputImg.getRGB(i,j-1));
				pixelMatrix[4]=new Color(inputImg.getRGB(i,j));
				pixelMatrix[5]=new Color(inputImg.getRGB(i,j+1));
				pixelMatrix[6]=new Color(inputImg.getRGB(i+1,j-1));
				pixelMatrix[7]=new Color(inputImg.getRGB(i+1,j));
				pixelMatrix[8]=new Color(inputImg.getRGB(i+1,j+1));
				int edge=convolution(pixelMatrix);
				outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
			}
		}
		return outputImg;
	}
	public BufferedImage medianFilter(){
		int[] R=new int[9];
		int[] B=new int[9];
		int[] G=new int[9];
		for (int i = 1; i < inputImg.getWidth()-1; i++) {
			for (int j = 1; j < outputImg.getHeight()-1; j++) {
				pixelMatrix[0]=new Color(inputImg.getRGB(i-1,j-1));
				pixelMatrix[1]=new Color(inputImg.getRGB(i-1,j));
				pixelMatrix[2]=new Color(inputImg.getRGB(i-1,j+1));
				pixelMatrix[3]=new Color(inputImg.getRGB(i,j-1));
				pixelMatrix[4]=new Color(inputImg.getRGB(i,j));
				pixelMatrix[5]=new Color(inputImg.getRGB(i,j+1));
				pixelMatrix[6]=new Color(inputImg.getRGB(i+1,j-1));
				pixelMatrix[7]=new Color(inputImg.getRGB(i+1,j));
				pixelMatrix[8]=new Color(inputImg.getRGB(i+1,j+1));
				for(int k=0;k<9;k++){
					R[k]=pixelMatrix[k].getRed();
					B[k]=pixelMatrix[k].getBlue();
					G[k]=pixelMatrix[k].getGreen();
				}
				Arrays.sort(R);
				Arrays.sort(G);
				Arrays.sort(B);
				outputImg.setRGB(i,j,new Color(R[4],B[4],G[4]).getRGB());
			}
		}
		return outputImg;

	}
	public int convolution(Color[] pixelMatrix){
		float[] data = {
				(float)1/9,(float)1/9,(float)1/9,
				(float)1/9,(float)1/9,(float)1/9,
				(float)1/9,(float)1/9,(float)1/9,
		};
		int g=(int)((pixelMatrix[0].getRed()*data[0])+(pixelMatrix[1].getRed()*data[1])+(pixelMatrix[2].getRed()*data[2])+
				(pixelMatrix[3].getRed()*data[3])+(pixelMatrix[4].getRed()*data[4])+(pixelMatrix[5].getRed()*data[5])+
				(pixelMatrix[6].getRed()*data[6])+(pixelMatrix[7].getRed()*data[7])+(pixelMatrix[8].getRed()*data[8]));
		return g;

	}
}