


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;



public class Main {
	public static void main(String[] args) throws IOException {
		BufferedImage outputImg;

		String file = "hubble.tif";
		String filterMethod = "mining";
		Preprocessing  preprocessing  =  new Preprocessing (file);
		if(filterMethod.equalsIgnoreCase("edge")){
			Preprocessing  p  =  new Preprocessing (file);
			BufferedImage img = p.filterImage("median");
			preprocessing.setInputImg(img);
			outputImg = preprocessing.filterImage(filterMethod);
		}else{
			outputImg = preprocessing.filterImage(filterMethod);
		}
		File outputfile = new File("result/filter-"+file);
		ImageIO.write(outputImg,"tif", outputfile);

	}
	public Main(){


	}
}
