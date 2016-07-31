

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JScrollPane;

import Q1.EdgeDetection;
import Q1.ImageEnhancement;
import Q1.NoiseCancellation;
import Q2.MiningImageData;
import Test.frmImageEnhancer;

public class Main {
	public static void main(String[] args) throws IOException {
//		final UI app = new UI();


		//Project Question 1
		
		//1.1 Edge detection
		EdgeDetection edgeDetection =  new EdgeDetection("test-pattern.tif");
		BufferedImage outputImgEdgeDetction = edgeDetection.processImage();
		File outputfileEd = new File("result/q1/test-pattern-EdgeDetection.tif");
		ImageIO.write(outputImgEdgeDetction,"tif", outputfileEd);

		//1.2 noise cancellation cnvolution filter
		NoiseCancellation noiseCancellation =  new NoiseCancellation("ckt-board-saltpep.tif");
		BufferedImage outputImgNoiseConvFilter = noiseCancellation.meanFilter();
		File outputfileNc = new File("result/q1/ckt-board-saltpep-noisecancellation-convolution-filter.tif");
		ImageIO.write(outputImgNoiseConvFilter,"tif", outputfileNc);

		//noise cancellation median filter
		BufferedImage outputImgNoiseMedianFilter = noiseCancellation.medianFilter();
		outputfileNc = new File("result/q1/ckt-board-saltpep-noisecancellation-median-filter.tif");
		ImageIO.write(outputImgNoiseMedianFilter,"tif", outputfileNc);

		//1.3 image enhancement
		ImageEnhancement  imageEnhancement  =  new ImageEnhancement ("blurry-moon.tif");
		BufferedImage outputImgImageEnhancement = imageEnhancement.enhancement();
		File outputfileImageEnhancement = new File("result/q1/blurry-moon-enhancement.tif");
		ImageIO.write(outputImgImageEnhancement,"tif", outputfileImageEnhancement);
		
		//Project Question 2
		//2.1 mining space image
		MiningImageData  miningImageData  =  new MiningImageData ("hubble.tif");
				BufferedImage outputImgMiningImageData = miningImageData.meanFilter();
				File outputfileMiningImageData = new File("result/q2/maining-image-data.tif");
				ImageIO.write(outputImgMiningImageData,"tif", outputfileMiningImageData);
	}
	public Main(){


	}
}
