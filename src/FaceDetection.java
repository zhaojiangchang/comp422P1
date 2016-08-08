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
    private final int featureSize = 10;

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
//        left eyebrow
        features[0] = getFeature(img.getSubimage(0,0,8,2));
        //right eyebrow
        features[1] = getFeature(img.getSubimage(0,11,8,2));
        //left eye
        features[2] = getFeature(img.getSubimage(0,0,7,7));
        //right eye
        features[3] = getFeature(img.getSubimage(6,0,7,7));
        //nose
        features[4] = getFeature(img.getSubimage(11,6,6,3));
        //mouth
        features[5] = getFeature(img.getSubimage(4,14,6,3));
        //nose_bridge
        features[6] = getFeature(img.getSubimage(8,2,2,9));
        //cheek
        features[7] = getFeature(img.getSubimage(0,7,6,6));
        //face
        features[8] = getFeature(img.getSubimage(0,0,19,19));
        //lower face
        features[9] = getFeature(img.getSubimage(0,9,19,10));
        return features;
    }
    private int getFeature(BufferedImage img){
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
        double evarage = total/(img.getHeight()*img.getWidth());
        double sumSquare = 0;
        for(Double d: allPixRed){
            sumSquare = (evarage-d)*(evarage-d);
        }
        double sumSquareRoot = Math.sqrt(sumSquare);
        return (int)sumSquareRoot;
    }
}
