package ImageProcess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
	public static void main(String[] args) throws IOException {
      //Edge detection
      EdgeDetection edgeDetection =  new EdgeDetection("test-pattern.tif");
      BufferedImage outputImgEdgeDetction = edgeDetection.processImage();
      File outputfileEd = new File("/Users/JackyChang/Documents/workspace/Comp422/result/test-pattern-EdgeDetection.tif");
      ImageIO.write(outputImgEdgeDetction,"tif", outputfileEd);
      
      //noise cancellation cnvolution filter
      NoiseCancellation noiseCancellation =  new NoiseCancellation("ckt-board-saltpep.tif");
      BufferedImage outputImgNoiseConvFilter = noiseCancellation.filterConvolution();
      File outputfileNc = new File("result/ckt-board-saltpep-noisecancellation-convolution-filter.tif");
      ImageIO.write(outputImgNoiseConvFilter,"tif", outputfileNc);
      
      //noise cancellation median filter
      BufferedImage outputImgNoiseMedianFilter = noiseCancellation.medianFilter();
      outputfileNc = new File("result/ckt-board-saltpep-noisecancellation-median-filter.tif");
      ImageIO.write(outputImgNoiseMedianFilter,"tif", outputfileNc);
   }

}
