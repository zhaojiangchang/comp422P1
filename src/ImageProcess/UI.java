package ImageProcess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Test.BinaryFile;

public class UI extends JFrame implements ActionListener, ChangeListener, ItemListener {
	private JButton btnBrowse = new JButton("Browse...");   
	private JButton btnRunEdgeDection = new JButton("Run Edge Dection");
	private JButton btnRunNoiseCancellation = new JButton("Run Noise Cancellation");
	private JButton btnRunImageEnhancement = new JButton("Run Image Enhancement");
//	private JTextArea txtAreaMessages = new JTextArea();
//	private JScrollPane scrollPaneMessages = null;    //    Instantiated in constructor frmChess()
//	private JPanel pnlMessages = new JPanel(new BorderLayout());
	BufferedImage outputImgEdgeDetction;
	private JLabel lblSelectImage = new JLabel("Select an image:");
	private JLabel lblGlobalStatistics = new JLabel();
	private JTextField txtSelectImage = new JTextField();
	private ImagePanel imagePanel = new ImagePanel();

	private JProgressBar pbEnhancer = new JProgressBar(SwingConstants.HORIZONTAL, 100, 100);   

	short[][] originalImage;
	short[][] finalImage; 

	// set the endian mode to run the test in
	final short endian = BinaryFile.BIG_ENDIAN;
	// set the signed mode to run the test in
	private Container c;

	public UI(){
		c = getContentPane();
		setBounds(50, 50, 555, 595);
		setBackground(new Color(204, 204, 204));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Image Processing");
		setResizable(false);
		c.setLayout(null);

		// add buttons to their event listener
		btnBrowse.addActionListener(this);       
		btnRunEdgeDection.addActionListener(this); 
		btnRunImageEnhancement.addActionListener(this);   
		btnRunNoiseCancellation.addActionListener(this);   

		
//		txtAreaMessages.setEditable(true);    // message box is read-only
//		scrollPaneMessages = new JScrollPane(txtAreaMessages);       
//		pnlMessages.setBounds(10, 120, 530, 440);
//		pnlMessages.setPreferredSize(new Dimension(0, 235));
//		pnlMessages.add(scrollPaneMessages, BorderLayout.CENTER);

	
		pbEnhancer.setBounds(10, 205, 530, 25);

		// setting UI of buttons
		lblSelectImage.setBounds(10, 20, 100, 25);               
		txtSelectImage.setBounds(110, 20, 335, 25);       
		btnBrowse.setBounds(450, 20, 90, 25);
		btnRunEdgeDection.setBounds(5, 55, 185, 25);		
		btnRunNoiseCancellation.setBounds(182, 55, 185, 25);
		btnRunImageEnhancement.setBounds(360, 55, 185, 25);
		lblGlobalStatistics.setBounds(210, 162, 500, 30);        

		txtSelectImage.setEditable(false);
		txtSelectImage.setBackground(Color.WHITE);
		pbEnhancer.setStringPainted(true);

		// adding buttons to the main window
		c.add(btnBrowse);       
		c.add(btnRunEdgeDection); 		
		c.add(btnRunNoiseCancellation);           
		c.add(btnRunImageEnhancement);           
          
		c.add(lblGlobalStatistics);           
		c.add(lblSelectImage);
		c.add(txtSelectImage);
		c.add(imagePanel);
		c.add(pbEnhancer);        

		show();       
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if (source == btnBrowse)
			btnBrowse_Click();
		else if (source == btnRunEdgeDection)
			try {
				btnRunEdgeDectionRun_Click();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}   
	private void btnRunEdgeDectionRun_Click() throws IOException {
		// TODO Auto-generated method stub
		EdgeDetection edgeDetection =  new EdgeDetection("test-pattern.tif");
		outputImgEdgeDetction = edgeDetection.processImage();
		imagePanel.setImage(outputImgEdgeDetction);
//		File outputfileEd = new File("result/test-pattern-EdgeDetection.tif");
//		ImageIO.write(outputImgEdgeDetction,"tif", outputfileEd);
		
	}
	public void btnBrowse_Click()
	{   
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("/Users/JackyChang/Documents/workspace/Comp422/"));

		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".tif")
						|| f.isDirectory();
			}

			public String getDescription() {
				return "tif Images";
			}
		});

		int r = chooser.showOpenDialog(this);
		if (r == JFileChooser.APPROVE_OPTION)
		{   
			if(chooser.getSelectedFile().getAbsolutePath().endsWith(".tif"))
			{
				String imageName = chooser.getSelectedFile().getAbsolutePath();
				this.txtSelectImage.setText(imageName);
				this.viewImage(imageName);   
			}
			else
				JOptionPane.showMessageDialog(this, "Only PGM image files are supported in this version. Please select a valid PGM image file.", "Warning: Image File Not Supported", JOptionPane.WARNING_MESSAGE);                           
		}
	}
	// This method opens up an image in ImageJ Viewer
	private void viewImage(String imageName)
	{       
		Runtime load = Runtime.getRuntime();           
		String programC = "/Users/JackyChang/Documents/workspace/Comp422/ImageJ/imageJ";           
		try
		{
			if(new File(programC).exists())
				load.exec(programC + " " + imageName);
		}
			
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(this, "ImageJ could not be found on this computer. This will not stop Image Enhancement process.\nHowever, application will not be able to display original and enhanced images." +
					" Please select\n'Run Image Enhancement' button in order to continue.\n\n", "Warning: ImageJ Not Found", JOptionPane.WARNING_MESSAGE);                           
		}           
	}        
}
class ImagePanel extends JPanel{

    private BufferedImage image;

   public ImagePanel(){
   }
   public void setImage(BufferedImage image){
		this.image = image;   

   }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }

}