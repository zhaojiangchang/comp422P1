/**
 * Created by JackyChang on 16/8/3.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.awt.image.WritableRaster;
import java.util.List;

import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Exchanger;
import javax.swing.*;
import javax.imageio.ImageIO;

public class FaceDetection {
private Map<String, List<File>> allFiles;
    private final File trainingDataset = new File("result/faceDetection/trainingDataset.csv");
    private final File testDataset = new File("result/faceDetection/testDataset.csv");
    private PrintWriter trainingDatasetWriter;
    private PrintWriter testDatasetWriter;
    private final String comma = ",";
    private static WritableRaster raster;
    private final int featureSize =10;

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
            eval.evaluateModel(cls, testInst);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createCsvFile(PrintWriter fileWriter, List<File>files, int classlable) {
        String line = getFeaturesLine();
        fileWriter.append(line);
        fileWriter.append("\n");
        for(int i = 0; i<files.size();i++){
            BufferedImage img = null;
            try {
                img = ImageIO.read(files.get(i));
            }catch(Exception e){
                e.printStackTrace();
            }

            double[] features = featureList(img);
            StringBuffer sb = new StringBuffer();
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

    private double[] featureList(BufferedImage img){
        double [] features = new double[featureSize];
        //lower face
        features[0] = getFeatureMean(img.getSubimage(0,0,10,10));
        //upper face
        features[1] = getFeatureMean(img.getSubimage(10,0,8,10));
        //left eye
        features[2] = getFeatureMean(img.getSubimage(0,10,10,8));
        //right eye
        features[3] = getFeatureMean(img.getSubimage(0,10,10,8));
        //left eye
        features[4] = getFeatureSd(img.getSubimage(10,10,8,8));
        //right eye
        features[5] = getFeatureSd(img.getSubimage(10,10,8,8));
        //nose
        features[6] = getFeatureMean(img.getSubimage(11,6,6,3));
        //nose
        features[7] = getFeatureSd(img.getSubimage(11,6,6,3));
        //lower face
        features[8] = getFeatureSd(img.getSubimage(0,0,10,10));
//        //upper face
        features[9] = getFeatureSd(img.getSubimage(10,0,8,10));
//        //mouth
//        features[10] = getFeatureMean(img.getSubimage(5,5,5,5));
//        //mouth
//        features[11] = getFeatureSd(img.getSubimage(5,5,5,5));
//        //nose_bridge
//        features[12] = getFeatureMean(img.getSubimage(10,5,5,5));
//        //nose_bridge
//        features[13] = getFeatureSd(img.getSubimage(10,5,5,5));
//        //nose_bridge
//        features[14] = getFeatureMean(img.getSubimage(5,10,5,5));
//        //nose_bridge
//        features[15] = getFeatureSd(img.getSubimage(5,10,5,5));
//        //nose_bridge
//        features[16] = getFeatureMean(img.getSubimage(10,10,5,5));
//        //nose_bridge
//        features[17] = getFeatureSd(img.getSubimage(10,10,5,5));


        return features;
    }
    private int getFeatureSd(BufferedImage img){
        double total = 0;
        List<Double>allPixRed = new ArrayList<Double>();
        for (int i = 0; i < img.getWidth()-1; i++) {
            for (int j = 0; j < img.getHeight()-1; j++) {
                int rgb = img.getRGB(i,j);
                Color c = new Color(img.getRGB(i,j));
                allPixRed.add((double)c.getRed());
                total += c.getRed();
            }
        }
        double mean = total/(img.getHeight()*img.getWidth());
        double sumSquare = 0;
        for(Double d: allPixRed){
            sumSquare += (mean-d)*(mean-d);
        }
        double sd = Math.sqrt(sumSquare/(img.getHeight()*img.getWidth()));
        return (int)sd;
    }
    private int getFeatureMean(BufferedImage img){
        double total = 0;
        List<Double>allPixRed = new ArrayList<Double>();
        for (int i = 0; i < img.getWidth()-1; i++) {
            for (int j = 0; j < img.getHeight()-1; j++) {
                int rgb = img.getRGB(i,j);
                Color c = new Color(img.getRGB(i,j));
                allPixRed.add((double)c.getRed());
                total += c.getRed();
            }
        }
        double mean = total/(img.getHeight()*img.getWidth());

        return (int)mean;
    }
}
