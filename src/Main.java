


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Main {
	private static BufferedImage outputImg;

	public static void main(String[] args) throws IOException {
		File file = null;
		JFileChooser chooser = new JFileChooser("Image Chooser");
		chooser.setCurrentDirectory(new File("./images"));
		int f = chooser.showOpenDialog(null);
		if(JFileChooser.APPROVE_OPTION == f){
			file = chooser.getSelectedFile();
		}else{
			System.exit(0);
		}
		String filterMethod = "edge";
		Preprocessing  preprocessing  =  new Preprocessing (file);
		outputImg = preprocessing.filterImage(filterMethod);
		File outputfile = new File("result/filter-");
		ImageIO.write(outputImg,"tif", outputfile);

	}
	public Main(){


	}
}