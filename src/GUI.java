/**
 * Created by JackyChang on 16/8/18.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/*

 */
public class GUI extends JPanel
        implements ActionListener {
    static String q1 = "q1";
    static String q2 = "q2";
    static String q3 = "q3";
    private static BufferedImage outputImg;
    File file = null;
    String [] imageProcessList = {"edge","median","mean","enhancement","mining"};
    JComboBox cmbList = new JComboBox(imageProcessList);
    JLabel lblText = new JLabel();
    String selected = "q1";
    String imageProcessAction = "edge";
    JTextArea commentTextArea =
            new JTextArea("Training dataset Result and Test dataset Result",5,40);
    public GUI() {
        super(new BorderLayout());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //Create the radio buttons.
        JRadioButton q1b = new JRadioButton(q1);
        q1b.setMnemonic(KeyEvent.VK_B);
        q1b.setActionCommand(q1);
        q1b.setText("Image Processing(Q1(1,2,3) and Q2(1)");
        q1b.setSelected(true);

        JRadioButton q2b = new JRadioButton(q2);
        q2b.setMnemonic(KeyEvent.VK_B);
        q2b.setActionCommand(q2);
        q2b.setText("Face Detection Q2(2)");
        q2b.setSelected(true);

        JRadioButton q3b = new JRadioButton(q3);
        q3b.setMnemonic(KeyEvent.VK_B);
        q3b.setActionCommand(q3);
        q3b.setText("Data Mining Q3");
        q3b.setSelected(true);

        JButton runButton = new JButton();
        runButton.setBounds(0,0,20,50);
        runButton.setText("run");
        ButtonGroup group = new ButtonGroup();
        group.add(q1b);
        group.add(q2b);
        group.add(q3b);
        group.add(runButton);

        //Register a listener for the radio buttons.
        q1b.addActionListener(this);
        q2b.addActionListener(this);
        q3b.addActionListener(this);
        runButton.addActionListener(this);

        cmbList.setSelectedIndex(0);
        cmbList.addActionListener(this);

        JLabel  resultlabel= new JLabel("Classification Result For Q2 and Q3: ", JLabel.LEFT);
        commentTextArea.disable();
        JScrollPane scrollPane = new JScrollPane(commentTextArea);



        JPanel topPanel = new JPanel();
//        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));
        topPanel.add(q1b);
        cmbList.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(cmbList);
        topPanel.add(q2b);
        topPanel.add(q3b);
        topPanel.add(runButton,CENTER_ALIGNMENT);

        JPanel resultPanel = new JPanel(new GridLayout(0, 1));
        resultPanel.add(resultlabel);
        resultPanel.add(scrollPane);

        topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JMenuBar mb = new JMenuBar();
        mb.setSize(100,20);
        JMenu file = new JMenu("Ex");
        mb.add(file);
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        file.add(exit);
        add(mb);
        add(topPanel);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(resultPanel);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }


    /** Listens to the radio buttons. */
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==cmbList){
            JComboBox cb = (JComboBox)e.getSource();
            imageProcessAction = (String)cb.getSelectedItem();
        }

        String command = e.getActionCommand();
        if(command.equals("q1")) {
            selected = "q1";
        }else if(command.equals("q2")) {
            selected = "q2";
        }else if(command.equals("q3")) {
            selected = "q3";
        }else if(command.equals("Exit")){
            System.exit(0);
        }
        else if(command.equals("run")) {
            if (selected.equals("q1")) {
                JFileChooser chooser = new JFileChooser("Image Chooser");
                chooser.setCurrentDirectory(new File("./images"));
                int f = chooser.showOpenDialog(null);
                if (JFileChooser.APPROVE_OPTION == f) {
                    file = chooser.getSelectedFile();
                } else {
//                    System.exit(0);
                }
                ImageProcess preprocessing = null;
                try {
                    preprocessing = new ImageProcess(file);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                outputImg = preprocessing.filterImage(imageProcessAction);
                File outputfile = new File("result/imageProcess/" + imageProcessAction + "-" + file.getName());
                try {
                    ImageIO.write(outputImg, "tif", outputfile);
                    Desktop.getDesktop().open(new File("result/imageProcess"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (selected.equals("q2")) {
                FaceDetection faceDetection = new FaceDetection();
                String result = faceDetection.resultsTraining_summary + "\n"+ faceDetection.resultTraining_details + "\n"+faceDetection.resultsTraining_tpfp+
                        faceDetection.resultsTest_summary+"\n"+faceDetection.resultsTest_details+"\n"+faceDetection.resultsTest_tpfp;
                commentTextArea.setText(result);
            } else if (selected.equals("q3")) {
                JFileChooser chooser2 = new JFileChooser("Files Chooser");
                chooser2.setMultiSelectionEnabled(true);
                chooser2.setCurrentDirectory(new File("./images"));
                int f2 = chooser2.showOpenDialog(null);
                File[] files = null;
                if (JFileChooser.APPROVE_OPTION == f2) {
                    files = chooser2.getSelectedFiles();

                } else {
//                    System.exit(0);
                }
                DataMining dataMining = new DataMining(files);
                String result = dataMining.resultsTraining_summary + "\n"+ dataMining.resultTraining_details + "\n"+dataMining.resultsTraining_tpfp+
                        dataMining.resultsTest_summary+"\n"+dataMining.resultsTest_details+"\n"+dataMining.resultsTest_tpfp;
                commentTextArea.setText(result);
            }
        }
    }

    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("COMP422 PROJECT 1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new GUI();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        frame.setLocation(400,400);
        frame.setSize(100,300);
        //Display the window.
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }



}