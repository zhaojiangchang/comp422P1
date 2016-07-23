package ImageProcess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {
	public static void main(String[] args) throws IOException {
      
      EdgeDetction edgeDetction =  new EdgeDetction("./test-pattern.tif");
      BufferedImage outputImgEdgeDetction = edgeDetction.processImage();
      File outputfile = new File("/Users/JackyChang/Documents/workspace/Comp422/res/test-pattern-EdgeDetection.tif");
      ImageIO.write(outputImgEdgeDetction,"tif", outputfile);
      
//      NoiseCancellation noiseCancellation =  new NoiseCancellation("");

   }

}
