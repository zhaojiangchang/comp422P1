package ImageProcess;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageEnhancement {
	private BufferedImage inputImg,outputImg;
	private Color[] pixelMatrix=new Color[9];
	@SuppressWarnings("static-access")
	public ImageEnhancement(String filename) throws IOException {
		// TODO Auto-generated constructor stub
		this.inputImg = ImageIO.read(ImageEnhancement.class.getResource(filename));
		this.outputImg = new BufferedImage(inputImg.getWidth(),inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
	}
	
	public BufferedImage enhancement(){
		int width = inputImg.getWidth();
		int height = outputImg.getHeight();
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
				outputImg.setRGB(i,j,(convolution(pixelMatrix)).getRGB());
			}
		}
		return outputImg;
	}
	
	public Color convolution(Color[] pixelMatrix){
		int[] data = {
				0,-1,0,
				-1,5,-1,
				0,-1,0,
		};
		int r=((pixelMatrix[0].getRed()*data[0])+(pixelMatrix[1].getRed()*data[1])+(pixelMatrix[2].getRed()*data[2])+
				(pixelMatrix[3].getRed()*data[3])+(pixelMatrix[4].getRed()*data[4])+(pixelMatrix[5].getRed()*data[5])+
				(pixelMatrix[6].getRed()*data[6])+(pixelMatrix[7].getRed()*data[7])+(pixelMatrix[8].getRed()*data[8]));
		int b=((pixelMatrix[0].getBlue()*data[0])+(pixelMatrix[1].getBlue()*data[1])+(pixelMatrix[2].getBlue()*data[2])+
				(pixelMatrix[3].getBlue()*data[3])+(pixelMatrix[4].getBlue()*data[4])+(pixelMatrix[5].getBlue()*data[5])+
				(pixelMatrix[6].getBlue()*data[6])+(pixelMatrix[7].getBlue()*data[7])+(pixelMatrix[8].getBlue()*data[8]));
		int g=((pixelMatrix[0].getGreen()*data[0])+(pixelMatrix[1].getGreen()*data[1])+(pixelMatrix[2].getGreen()*data[2])+
				(pixelMatrix[3].getGreen()*data[3])+(pixelMatrix[4].getGreen()*data[4])+(pixelMatrix[5].getGreen()*data[5])+
				(pixelMatrix[6].getGreen()*data[6])+(pixelMatrix[7].getGreen()*data[7])+(pixelMatrix[8].getGreen()*data[8]));
		if(r>255) r = 255;
		else if(r<0) r = 0;
		if(g>255) g = 255;
		else if(g<0) g = 0;
		if(b>255) b = 255;
		else if(b<0) b = 0;
		return new Color(r,g,b);

	}
}