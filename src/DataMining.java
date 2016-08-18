import com.sun.deploy.util.StringUtils;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.Exchanger;

import weka.core.converters.ArffSaver;
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
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

/**
 * Created by JackyChang on 16/8/4.
 */
public class DataMining {
    private File[] files = null;
    private final String comma = ",";
    public String resultsTraining_summary= "";
    public String resultsTraining_tpfp= "";
    public String resultTraining_details = "";
    public String resultTraining_tree = "";

    public String resultsTest_summary= "";
    public String resultsTest_tpfp= "";
    public String resultsTest_details= "";
    public String resultsTest_tree= "";

    public DataMining(File[] files){
        this.files = files;
        loadFiles();
        classfier();
    }
    private void classfier(){
        try{
            //setup training dataset
            DataSource trainSource = new DataSource("result/dataMining/trainingDataset.csv");
            Instances trainingInst = trainSource.getDataSet();

            ArffSaver training_saver = new ArffSaver();
            training_saver.setInstances(trainingInst);
            training_saver.setFile(new File("result/dataMining/trainingDataset.arff"));
            training_saver.writeBatch();

            trainSource = new DataSource("result/dataMining/trainingDataset.arff");
            trainingInst = trainSource.getDataSet();
            trainingInst.setClassIndex(trainingInst.numAttributes()-1);

            //setup test dataset
            DataSource testSource = new DataSource("result/dataMining/testDataset.csv");
            Instances testInst = testSource.getDataSet();
            ArffSaver test_saver = new ArffSaver();
            test_saver.setInstances(testInst);
            test_saver.setFile(new File("result/dataMining/testDataset.arff"));
            test_saver.writeBatch();
            testSource = new DataSource("result/dataMining/testDataset.arff");
            testInst = testSource.getDataSet();
            testInst.setClassIndex(testInst.numAttributes()-1);

            NumericToNominal convertToNominal = new NumericToNominal();
            convertToNominal.setInputFormat(trainingInst);
//
            Instances newTrainingInst = Filter.useFilter(trainingInst, convertToNominal);

            J48 tree = new J48();
//            String[] options = new String[1];
//            options[0] = "-R";
//            tree.setOptions(options);
            tree.buildClassifier(newTrainingInst);
//            System.out.println(tree);
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("====================================Evaluation Training Dataset==================================================");
            System.out.println("evaluation training dataset:  ");
            System.out.println();
            System.out.println();
            System.out.println();
            //evaluate training dataset
            Evaluation evalTraining = new Evaluation(newTrainingInst);
//            System.out.println(trainingInst.numAttributes());
            evalTraining.evaluateModel(tree, newTrainingInst);
            resultsTraining_summary = evalTraining.toSummaryString("\nSummary\n======\n", false);
            resultTraining_details = evalTraining.toClassDetailsString("\nClass Details\n======\n");
            resultsTraining_tpfp = evalTraining.toMatrixString("\nConfusion Matrix: false positives and false negatives\n======\n");
            resultTraining_tree = tree.toString();
            System.out.println(evalTraining.toSummaryString("\nResults\n======\n", false));
            System.out.println(evalTraining.toMatrixString("Confusion Matrix"));
            System.out.println(tree.toString());
            // display classifier
            final javax.swing.JFrame jf =
                    new javax.swing.JFrame("Weka Classifier Tree Visualizer(Training): J48");
            jf.setSize(1000,800);
            jf.getContentPane().setLayout(new BorderLayout());
            TreeVisualizer tv = new TreeVisualizer(null,
                    tree.graph(),
                    new PlaceNode2());
            jf.getContentPane().add(tv, BorderLayout.CENTER);
            jf.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    jf.dispose();
                }
            });

            jf.setVisible(true);
            tv.fitToScreen();

            System.out.println("==========================================Done============================================================");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("====================================Evaluation Test Dataset==================================================");
            System.out.println("evaluation test dataset:  ");
            System.out.println();
            System.out.println();
            System.out.println();


            Instances newTestInst= Filter.useFilter(testInst, convertToNominal);
//            J48 tree2 = new J48();
//            String[] options2 = new String[1];
//            options2[0] = "-R";
//            tree2.setOptions(options2);
//            tree2.buildClassifier(newTrainingInst);
            //evaluate test dataset
            evalTraining = new Evaluation(newTrainingInst);
//            System.out.println(trainingInst.numAttributes());
            evalTraining.evaluateModel(tree, newTestInst);
            resultsTest_summary = evalTraining.toSummaryString("\nSummary\n======\n", false);
            resultsTest_details = evalTraining.toClassDetailsString("\nClass Details\n======\n");
            resultsTest_tpfp = evalTraining.toMatrixString("\nConfusion Matrix: false positives and false negatives\n======\n");
            resultsTest_tree = evalTraining.toString();

            System.out.println(evalTraining.toSummaryString("\nResults\n======\n", false));
            System.out.println(evalTraining.toMatrixString("Confusion Matrix"));


            // display classifier
            final javax.swing.JFrame jf2 =
                    new javax.swing.JFrame("Weka Classifier Tree Visualizer(Test): J48");
            jf2.setSize(1000,800);
            jf2.getContentPane().setLayout(new BorderLayout());
            TreeVisualizer tv2 = new TreeVisualizer(null,
                    tree.graph(),
                    new PlaceNode2());
            jf2.getContentPane().add(tv2, BorderLayout.CENTER);
            jf2.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent e) {
                    jf2.dispose();
                }
            });

            jf2.setVisible(true);
            tv2.fitToScreen();

            System.out.println("=======================================Done===========================================================");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void loadFiles(){
        for(File file: files){
            System.out.println(file.getName());
        }
        List<BufferedReader>bufferedReaders = getBufferedReaders();

        PrintWriter trainingDatasetWriter = null;
        PrintWriter testDataseWriter = null;
        File trainingDataset = new File("result/dataMining/trainingDataset.csv");
        File testDataset = new File("result/dataMining/testDataset.csv");
        try{
            trainingDatasetWriter = new PrintWriter(trainingDataset);
            testDataseWriter = new PrintWriter(testDataset);
            String featuresLine = getTopFeatureLine(bufferedReaders);
            trainingDatasetWriter.append(featuresLine);
            trainingDatasetWriter.append("\n");
            testDataseWriter.append(featuresLine);
            testDataseWriter.append("\n");
            LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(files[0]));
            lineNumberReader.skip(Long.MAX_VALUE);
            int totalLines = lineNumberReader.getLineNumber();
            for(int i = 0; i<totalLines-2; i++){
                StringBuilder line = new StringBuilder();
                String classLable = "c"+i / 200+"";
                String text = null;
                for(int j = 0; j<bufferedReaders.size(); j++){
                    text = bufferedReaders.get(j).readLine();
                    if(text!=null){
                        List<String>str = Arrays.asList(text.trim().split("\\s+"));
                        line.append(StringUtils.join(str,comma));
                        line.append(comma);
                    }
                }
                if(text!=null){
                    if(i%2==0){
                        line.append(classLable);
                        trainingDatasetWriter.append(line.toString());
                        trainingDatasetWriter.append("\n");
                    }else if(i%2==1){
                        line.append(classLable);
                        testDataseWriter.append(line.toString());
                        testDataseWriter.append("\n");
                    }
                }

            }
            for(int rd = 0; rd<bufferedReaders.size();rd++){
                bufferedReaders.get(rd).close();
            }
            trainingDatasetWriter.close();
            testDataseWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private List<BufferedReader> getBufferedReaders(){
        List<BufferedReader> bufferedReaders = new ArrayList<BufferedReader>();
        for(File file: files){
            try{
                bufferedReaders.add(new BufferedReader(new FileReader(file)));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return bufferedReaders;
    }
    private String getTopFeatureLine(List<BufferedReader> rds) {
        StringBuffer line = new StringBuffer();
        String strArray[] = null;
        for (int i = 0; i < rds.size(); i++) {
            try {
                String text = rds.get(i).readLine();
                strArray = text.trim().split("\\s+");
                for (int j = 0; j < strArray.length; j++) {
                    line.append(files[i].getName().substring(6) + j);
                    line.append(comma);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        line.append("class");
        return line.toString();
    }
}