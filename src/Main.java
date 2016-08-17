


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Main {
	private static BufferedImage outputImg;
	static int question =3;

	public static void main(String[] args) throws IOException {
		File file = null;
		if(question ==1){
			JFileChooser chooser = new JFileChooser("Image Chooser");
			chooser.setCurrentDirectory(new File("./images"));
			int f = chooser.showOpenDialog(null);
			if(JFileChooser.APPROVE_OPTION == f){
				file = chooser.getSelectedFile();
			}else{
				System.exit(0);
			}
			String filterMethod = "enhancement";
			ImageProcess preprocessing  =  new ImageProcess(file);
			outputImg = preprocessing.filterImage(filterMethod);
			File outputfile = new File("result/imageProcess/"+filterMethod+"-"+ file.getName());
			ImageIO.write(outputImg,"tif", outputfile);
		}else if(question==2){
			FaceDetection faceDetection = new FaceDetection();
		}
		else if(question == 3){
            JFileChooser chooser = new JFileChooser("Files Chooser");
            chooser.setMultiSelectionEnabled(true);
            chooser.setCurrentDirectory(new File("./images"));
            int f = chooser.showOpenDialog(null);
            File[] files = null;
            if(JFileChooser.APPROVE_OPTION == f){
                files = chooser.getSelectedFiles();

            }else{
                System.exit(0);
            }
		    DataMining dataMining = new DataMining(files);
        }


	}
}
