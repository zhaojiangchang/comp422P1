import com.sun.deploy.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

/**
 * Created by JackyChang on 16/8/4.
 */
public class DataMining {
    private File[] files = null;
    private final String comma = ",";
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
            trainingInst.setClassIndex(trainingInst.numAttributes()-1);

            //setup test dataset
            DataSource testSource = new DataSource("result/dataMining/testDataset.csv");
            Instances testInst = testSource.getDataSet();
            testInst.setClassIndex(testInst.numAttributes()-1);

            NumericToNominal convertToNominal = new NumericToNominal();
            convertToNominal.setInputFormat(trainingInst);
//
            Instances newTrainingInst = Filter.useFilter(trainingInst, convertToNominal);
            Instances newTestInst = Filter.useFilter(testInst, convertToNominal);

            J48 tree = new J48();
            String[] options = new String[1];
            options[0] = "-R";
            tree.setOptions(options);
            tree.buildClassifier(newTrainingInst);
//            System.out.println(tree);
            Evaluation eval = new Evaluation(newTrainingInst);
//            System.out.println(trainingInst.numAttributes());
            eval.evaluateModel(tree, newTrainingInst);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            System.out.println(eval.toMatrixString("Confusion Matrix"));

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
                String classLable = i / 200+"";
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