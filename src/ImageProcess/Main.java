package ImageProcess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
	public static void main(String[] args) throws IOException {
      
      EdgeDetction edgeDetction =  new EdgeDetction("test-pattern.tif");
      BufferedImage outputImgEdgeDetction = edgeDetction.processImage();
      File outputfileEd = new File("/Users/JackyChang/Documents/workspace/Comp422/result/test-pattern-EdgeDetection.tif");
      ImageIO.write(outputImgEdgeDetction,"tif", outputfileEd);
      
      NoiseCancellation noiseCancellation =  new NoiseCancellation("ckt-board-saltpep.tif");
      BufferedImage outputImgNoise = noiseCancellation.processImage();
      File outputfileNc = new File("/Users/JackyChang/Documents/workspace/Comp422/result/ckt-board-saltpep-noisecancellation.tif");
      ImageIO.write(outputImgNoise,"tif", outputfileNc);
   }

}
