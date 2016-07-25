package ImageProcess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JScrollPane;

import Test.frmImageEnhancer;

public class Main {
	public static void main(String[] args) throws IOException {
//		final UI app = new UI();


		//Edge detection
		EdgeDetection edgeDetection =  new EdgeDetection("test-pattern.tif");
		BufferedImage outputImgEdgeDetction = edgeDetection.processImage();
		File outputfileEd = new File("result/test-pattern-EdgeDetection.tif");
		ImageIO.write(outputImgEdgeDetction,"tif", outputfileEd);

		//noise cancellation cnvolution filter
		NoiseCancellation noiseCancellation =  new NoiseCancellation("ckt-board-saltpep.tif");
		BufferedImage outputImgNoiseConvFilter = noiseCancellation.meanFilter();
		File outputfileNc = new File("result/ckt-board-saltpep-noisecancellation-convolution-filter.tif");
		ImageIO.write(outputImgNoiseConvFilter,"tif", outputfileNc);

		//noise cancellation median filter
		BufferedImage outputImgNoiseMedianFilter = noiseCancellation.medianFilter();
		outputfileNc = new File("result/ckt-board-saltpep-noisecancellation-median-filter.tif");
		ImageIO.write(outputImgNoiseMedianFilter,"tif", outputfileNc);

		//image enhancement
		ImageEnhancement  imageEnhancement  =  new ImageEnhancement ("blurry-moon.tif");
		BufferedImage outputImgImageEnhancement = imageEnhancement.enhancement();
		File outputfileImageEnhancement = new File("result/blurry-moon-enhancement.tif");
		ImageIO.write(outputImgImageEnhancement,"tif", outputfileImageEnhancement);
	}
	public Main(){


	}
}
