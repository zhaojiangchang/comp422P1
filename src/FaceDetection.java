/**
 * Created by JackyChang on 16/8/3.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.awt.image.WritableRaster;
import java.util.List;

import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instance;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

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
    private final int featureSize =20;

    public FaceDetection(){
        this.allFiles = new HashMap<String, List<File>>();
        loadFiles();
        naiveBayesClassfier();
    }

    public void loadFiles(){
        try {
            trainingDatasetWriter = new PrintWriter(trainingDataset);
            testDatasetWriter = new PrintWriter(testDataset);
            allFiles.put("trainFace", loadFolder(trainingDatasetWriter, new File("images/2.2/train/face"), "face"));
            allFiles.put("trainNonFace", loadFolder(trainingDatasetWriter, new File("images/2.2/train/non-face"), "non-face"));
            allFiles.put("testFace", loadFolder(testDatasetWriter, new File("images/2.2/test/face"), "face"));
            allFiles.put("testNonFace", loadFolder(testDatasetWriter, new File("images/2.2/test/non-face"), "non-face"));
            trainingDatasetWriter.close();
            testDatasetWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private List<File> loadFolder(PrintWriter writer, File folderPath, String classlable){
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
//            trainingInst.setClassIndex(trainingInst.numAttributes()-1);
            ArffSaver training_saver = new ArffSaver();
            training_saver.setInstances(trainingInst);
            training_saver.setFile(new File("result/faceDetection/trainingDataset.arff"));
            training_saver.writeBatch();
            trainSource = new DataSource("result/faceDetection/trainingDataset.arff");
            trainingInst = trainSource.getDataSet();
            trainingInst.setClassIndex(trainingInst.numAttributes()-1);

            //setup test dataset
            testSource = new DataSource("result/faceDetection/testDataset.csv");
             Instances testInst = testSource.getDataSet();
            ArffSaver test_saver = new ArffSaver();
            test_saver.setInstances(testInst);
            test_saver.setFile(new File("result/faceDetection/testDataset.arff"));
            test_saver.writeBatch();
            testSource = new DataSource("result/faceDetection/testDataset.arff");
            testInst = testSource.getDataSet();
            testInst.setClassIndex(testInst.numAttributes()-1);
            NaiveBayes cls = new NaiveBayes();
            cls.buildClassifier(trainingInst);
            Evaluation eval = new Evaluation(trainingInst);
            eval.evaluateModel(cls, testInst);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));

            //plot ROC curve
            plotROCcurve(eval);



        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void plotROCcurve(Evaluation eval){
        // generate curve
        ThresholdCurve tc = new ThresholdCurve();
        int classIndex = 0;
        Instances result = tc.getCurve(eval.predictions(), classIndex);
        // plot curve
        ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
        vmc.setROCString("(Area under ROC = " +
                Utils.doubleToString(tc.getROCArea(result), 4) + ")");
        vmc.setName(result.relationName());
        PlotData2D tempd = new PlotData2D(result);
        tempd.setPlotName(result.relationName());
        tempd.addInstanceNumberAttribute();
        // specify which points are connected
        boolean[] cp = new boolean[result.numInstances()];
        for (int n = 1; n < cp.length; n++)
            cp[n] = true;
        try {
            tempd.setConnectPoints(cp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // add plot
        try {
            vmc.addPlot(tempd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // display curve
        String plotName = vmc.getName();
        final javax.swing.JFrame jf =
                new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
        jf.setSize(500,400);
        jf.getContentPane().setLayout(new BorderLayout());
        jf.getContentPane().add(vmc, BorderLayout.CENTER);
        jf.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                jf.dispose();
            }
        });
        jf.setVisible(true);
    }
    private void createCsvFile(PrintWriter fileWriter, List<File>files, String classlable) {
        if(classlable.equals("face")){
            String line = getFeaturesLine();
            fileWriter.append(line);
            fileWriter.append("\n");
        }

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
        line.append("Class");
        return line.toString();
    }

    private double[] featureList(BufferedImage img){
        double [] features = new double[featureSize];
        //big square
        features[0] = getFeatureMean(img.getSubimage(0,0,19,19));

        features[1] = getFeatureSd(img.getSubimage(0,0,19,19));
        //upper left
        features[2] = getFeatureMean(img.getSubimage(0,0,9,9));

        features[3] = getFeatureSd(img.getSubimage(0,0,9,9));
        //upper right
        features[4] = getFeatureMean(img.getSubimage(10,0,9,9));

        features[5] = getFeatureSd(img.getSubimage(10,0,9,9));
        //lower left
        features[6] = getFeatureMean(img.getSubimage(0,10,9,9));

        features[7] = getFeatureSd(img.getSubimage(0,10,9,9));
        //lower right
        features[8] = getFeatureMean(img.getSubimage(10,10,9,9));

        features[9] = getFeatureSd(img.getSubimage(10,10,9,9));
        //small central square
        features[10] = getFeatureMean(img.getSubimage(5,5,9,9));

        features[11] = getFeatureSd(img.getSubimage(5,5,9,9));
        //central row of the big square
        features[12] = getFeatureMean(img.getSubimage(0,9,19,1));

        features[13] = getFeatureSd(img.getSubimage(0,9,19,1));
        //central column of the big square
        features[14] = getFeatureMean(img.getSubimage(9,0,1,19));

        features[15] = getFeatureSd(img.getSubimage(9,0,1,19));
        //central row of the small square
        features[16] = getFeatureMean(img.getSubimage(4,9,9,1));

        features[17] = getFeatureSd(img.getSubimage(4,9,9,1));
        //central column of the small square
        features[18] = getFeatureMean(img.getSubimage(9,4,1,9));

        features[19] = getFeatureSd(img.getSubimage(9,4,1,9));


        return features;
    }

    private int getFeatureSd(BufferedImage img){
        double total = 0;
        List<Double>allPixRed = new ArrayList<Double>();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i,j);
                Color c = new Color(img.getRGB(i,j));
                allPixRed.add((double)c.getRed());
                total += c.getRed();
            }
        }
        double mean = total/(img.getHeight()*img.getWidth()-1);
        double sumSquare = 0;
        for(Double d: allPixRed){
            sumSquare += (mean-d)*(mean-d);
        }
        double sd = Math.sqrt(sumSquare/(img.getHeight()*img.getWidth()-1));
        sd = new BigDecimal(sd).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return (int)sd;
    }
    private int getFeatureMean(BufferedImage img){
        double total = 0;
        List<Double>allPixRed = new ArrayList<Double>();
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int rgb = img.getRGB(i,j);
                Color c = new Color(img.getRGB(i,j));
                allPixRed.add((double)c.getRed());
                total += c.getRed();
            }
        }
        double mean = total/((img.getHeight()*img.getWidth()-1));
        mean = new BigDecimal(mean).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return (int)mean;
    }
}
