
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by JackyChang on 16/8/2.
 */
public class Preprocessing {
    private BufferedImage inputImg,outputImg;
    private Color[] pixelMatrix=new Color[9];
    private int[] R=new int[9];
    private int[] B=new int[9];
    private int[] G=new int[9];
    private int width = 0;
    private int height = 0;
    private int thresholder = 200;

    private static final float []sobelx = {
            -1, 0, 1,
            -2, 0, 2,
            -1, 0, 1
    };
    private static final float []sobely = {
            -1, -2, -1,
            0,  0,  0,
            1,  2,  1
    };
    private static final float[] enhancementFilter ={
            0,-1,0,
            -1,5,-1,
            0,-1,0,
    };
    private static final float[] meanFilter = {
            (float)1/9,(float)1/9,(float)1/9,
            (float)1/9,(float)1/9,(float)1/9,
            (float)1/9,(float)1/9,(float)1/9,
    };
    private String filepath;

    public Preprocessing(String filepath) throws IOException{
        this.filepath = filepath;
        init();

    }
    public void setThresholder(int value){
        this.thresholder =  value;
    }
    public void init()throws IOException{
        pixelMatrix=new Color[9];
        R=new int[9];
        B=new int[9];
        G=new int[9];
        this.inputImg = ImageIO.read(Preprocessing.class.getResource(this.filepath));
        this.outputImg=new BufferedImage(this.inputImg.getWidth(),this.inputImg.getHeight(),this.inputImg.TYPE_INT_RGB);
        this.width = this.inputImg.getWidth();
        this.height = this.inputImg.getHeight();

    }
    public void setInputImg(BufferedImage image){
        this.inputImg = image;
    }
    public void setOutputImg(BufferedImage image){
        this.outputImg = image;
    }
    public BufferedImage getInputImg(){
        return this.inputImg;
    }
    public BufferedImage getOutputImg(){
        return this.outputImg;
    }

    public BufferedImage filterImage(String method){
        for (int i = 1; i < inputImg.getWidth()-1; i++) {
            for (int j = 1; j < inputImg.getHeight()-1; j++) {
                pixelMatrix[0]=new Color(inputImg.getRGB(i-1,j-1));
                pixelMatrix[1]=new Color(inputImg.getRGB(i-1,j));
                pixelMatrix[2]=new Color(inputImg.getRGB(i-1,j+1));
                pixelMatrix[3]=new Color(inputImg.getRGB(i,j-1));
                pixelMatrix[4]=new Color(inputImg.getRGB(i,j));
                pixelMatrix[5]=new Color(inputImg.getRGB(i,j+1));
                pixelMatrix[6]=new Color(inputImg.getRGB(i+1,j-1));
                pixelMatrix[7]=new Color(inputImg.getRGB(i+1,j));
                pixelMatrix[8]=new Color(inputImg.getRGB(i+1,j+1));
                int edge= 0;
                switch (method) {
                    case "edge":
                        int gx = convolution(pixelMatrix, sobelx);
                        int gy = convolution(pixelMatrix, sobely);

                        edge = (int)Math.sqrt(Math.pow(gy,2)+Math.pow(gx,2));
                        if(edge<0){
                            edge = 0;
                        }
                        else if(edge>255){
                            edge = 255;
                        }
                        outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
                        break;
                    case "median":
                        for(int k=0;k<9;k++){
                            R[k]=pixelMatrix[k].getRed();
                            B[k]=pixelMatrix[k].getBlue();
                            G[k]=pixelMatrix[k].getGreen();
                        }
                        Arrays.sort(R);
                        Arrays.sort(G);
                        Arrays.sort(B);
                        outputImg.setRGB(i,j,new Color(R[4],B[4],G[4]).getRGB());
                        break;
                    case "mean":
                        edge = convolution(pixelMatrix,meanFilter);
                        outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
                        break;
                    case "enhancement":
                        edge = convolution(pixelMatrix,enhancementFilter);
                        if(edge>255){
                            edge = 255;
                        }
                        else if(edge<0){
                            edge = 0;
                        }
                        outputImg.setRGB(i,j,((edge<<16 | edge<<8 | edge)));
                        break;
                    case "mining":
                        edge=convolution(pixelMatrix, meanFilter);
                        if(edge>thresholder){
                            edge = 255;
                        }else{
                            edge = 0;
                        }
                        outputImg.setRGB(i,j,(edge<<16 | edge<<8 | edge));
                        break;
                    default:
                        break;

                }
            }
        }
        return outputImg;
    }
    private int convolution(Color[] pixelMatrix, float[]filter){
        double toReturn = 0;
        for(int i = 0; i<pixelMatrix.length; i++){
                toReturn += ((float)pixelMatrix[i].getRed())*filter[i];
        }
        return (int)toReturn;
    }

}


