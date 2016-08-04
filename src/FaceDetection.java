/**
 * Created by JackyChang on 16/8/3.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.concurrent.Exchanger;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class FaceDetection {
private Map<String, List<File>> allFiles;
    private final File trainingDataset = new File("result/faceDetection/trainingDataset.csv");
    private final File testDataset = new File("result/faceDetection/testDataset.csv");
    private PrintWriter trainingDatasetWriter;
    private PrintWriter testDatasetWriter;
    private final String comma = ",";
    private static WritableRaster raster;
    private final int featureSize = 8;

    public FaceDetection(){
        this.allFiles = new HashMap<String, List<File>>();
        loadFiles();
        naiveBayesClassfier();
    }

    public void loadFiles(){
        try {
            trainingDatasetWriter = new PrintWriter(trainingDataset);
            testDatasetWriter = new PrintWriter(testDataset);
            allFiles.put("trainFace", loadFolder(trainingDatasetWriter, new File("images/2.2/train/face"), 0));
            allFiles.put("trainNonFace", loadFolder(trainingDatasetWriter, new File("images/2.2/train/non-face"), 1));
            allFiles.put("testFace", loadFolder(testDatasetWriter, new File("images/2.2/test/face"), 0));
            allFiles.put("testNonFace", loadFolder(testDatasetWriter, new File("images/2.2/test/non-face"), 1));
            trainingDatasetWriter.close();
            testDatasetWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private List<File> loadFolder(PrintWriter writer, File folderPath, int classlable){
        List<File>files = new ArrayList<File>();
        for(File file: folderPath.listFiles()){
            files.add(file);
        }
        createCsvFile(writer, files, classlable);

        return files;
    }
    private void naiveBayesClassfier(){
        DataSource trainSource = null;
        DataSource testSource = null;
        try{
            //setup training dataset
            trainSource = new DataSource("result/faceDetection/trainingDataset.csv");
            Instances trainingInst = trainSource.getDataSet();
            trainingInst.setClassIndex(trainingInst.numAttributes()-1);
            NaiveBayes cls = new NaiveBayes();
            cls.buildClassifier(trainingInst);

            //setup test dataset
            testSource = new DataSource("result/faceDetection/testDataset.csv");
             Instances testInst = testSource.getDataSet();
            testInst.setClassIndex(testInst.numAttributes()-1);

            Evaluation eval = new Evaluation(trainingInst);
            eval.evaluateModel(cls, trainingInst);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));


        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void createCsvFile(PrintWriter fileWriter, List<File>files, int classlable){
        String line = getFeaturesLine();
        fileWriter.append(line);
        fileWriter.append("\n");
        for(int i = 0; i<files.size();i++){
            BufferedImage img = getImage(files.get(i));
            int[] features = featureList(img);
            StringBuffer sb = new StringBuffer();
//            sb.append(files.get(i).getName()+comma);
            for(int j = 0; j<features.length; j++){
                sb.append(features[j]+comma);
            }
            sb.append(classlable);
            fileWriter.append(sb);
            fileWriter.append("\n");


        }



    }
    private BufferedImage getImage(File file){
        BufferedImage img = null;
        try{
            img = ImageIO.read(file);
            img = new BufferedImage(img.getWidth(),img.getHeight(),img.TYPE_INT_RGB);
        }catch (Exception e){
            e.printStackTrace();
        }

        return img;
    }
    private String getFeaturesLine(){
        StringBuffer line = new StringBuffer();
//        line.append("filename"+comma);
        for(int i = 0; i<featureSize; i++){
            line.append("feature"+i+comma);
        }
        line.append("class");
        return line.toString();
    }

    private int[] featureList(BufferedImage img){
        int [] features = new int[featureSize];
        //left eyebrow
        features[0] = getFeature(0,0,2,8, img);
        //right eyebrow
        features[1] = getFeature(0,11,2,8,img);
        //left eye
        features[2] = getFeature(2,0,5,7, img);
        //right eye
        features[3] = getFeature(2,12,5,7,img);
        //nose
        features[4] = getFeature(6,11,4,6, img);
        //mouth
        features[5] = getFeature(5,14,4,10, img);
        //nose_bridge
        features[6] = getFeature(8,2,8,6,img);
        //cheek
        features[7] = getFeature(0,8,5,6,img);
        return features;
    }
    private int getFeature(int x, int y, int rows, int cols, BufferedImage img){
        int[] pixelMatrix = new int[rows*cols];
//        pixelMatrix = raster.getPixels(x,y,rows,cols,pixelMatrix);
        for(int i =0; i<pixelMatrix.length; i++){
//            System.out.println(pixelMatrix[i]);
            pixelMatrix[i] = 1;
        }
        return 1;
    }
}
