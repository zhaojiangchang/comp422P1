package Test;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import javax.swing.event.*;

// Main class that handles most UI related issues
public class frmImageEnhancer extends JFrame implements ActionListener, ChangeListener, ItemListener
{
	// Image Enhancer class, which handles all of the image related methods
	public class clsImageEnhancer implements Runnable
	{
		public float Mean_g, SD_g;       
		public short[][] originalImage;

		public clsImageEnhancer(short[][] originalImage)
		{
			this.originalImage = originalImage;
		}

		// This method does the adaptive enhancement
		public short[][] autoEnhance(float E, float Ko)
		{
			short[][] finalImage = new short[this.originalImage.length][this.originalImage.length];

			for(int y = 0; y < this.originalImage.length; y++)
				for(int x = 0; x < this.originalImage[0].length; x++)
				{                           
					if(this.originalImage[y][x] <= (this.Mean_g * Ko))
						finalImage[y][x] = this.clamp((short)((float)this.originalImage[y][x] * (1 - E)));               
					else
						finalImage[y][x] = this.clamp((short)((float)this.originalImage[y][x] * (1 + E)));               
				}

			return finalImage;
		}

		// This method does the regular enhancement given in the textbook
		public short[][] manualEnhance(float E, float Ko, float K1, float K2)
		{
			short[][] finalImage = new short[this.originalImage.length][this.originalImage.length];

			int neighborhood = 3, innerNeighborhood = 3;                   
			neighborhood = (neighborhood % 2 > 0 ? neighborhood - 1 : neighborhood - 2);
			neighborhood /= 2;
			innerNeighborhood = (innerNeighborhood % 2 > 0 ? innerNeighborhood - 1 : innerNeighborhood - 2);
			innerNeighborhood /= 2;                    

			for(int y = 0; y < this.originalImage.length; y++)
				for(int x = 0; x < this.originalImage[0].length; x++)
				{                           
					float Mean_l = this.calculateMean(this.Convert2DTo1D(this.originalImage, x, y, neighborhood));
					float SD_l = this.calculateStandardDeviation(this.Convert2DTo1D(this.originalImage, x, y, neighborhood), Mean_l);

					if(Mean_l <= (this.Mean_g * Ko))
						if((SD_g * K1) <= SD_l && SD_l <= (SD_g * K2))
							finalImage[y][x] = (short)this.clamp(this.originalImage[y][x] * E);
						else
							finalImage[y][x] = this.originalImage[y][x];   
					else
						finalImage[y][x] = this.originalImage[y][x];
				}        

			return finalImage;
		}

		public short clamp(short c) { return (short)this.clamp((float)c); }
		public int clamp(int c) { return (int)this.clamp((float)c); }
		public float clamp(float c)
		{
			if (c < 0) c = 0;
			else if (c > 255) c = 255;       
			return c;
		}

		// Converts a 2-D array into 1-D array of the size of neighborhood from x and y coordinates
		public short[] Convert2DTo1D(short[][] array2D, int x, int y, int neighborhood)
		{
			short[] array1D = new short[(neighborhood * 2 + 1) * (neighborhood * 2 + 1)];
			int index = 0;

			for(int j = y - neighborhood; j <= y + neighborhood; j++)
				for(int i = x - neighborhood; i <= x + neighborhood; i++)
					if((j < 0 || j >= array2D.length || i < 0 || i >= array2D[0].length))
						array1D[index++] = 0;
					else
						array1D[index++] = array2D[j][i];

			return array1D;
		}          

		// Converts a 2-D array into 1-D array
		public short[] Convert2DTo1D(short[][] array2D)
		{
			int index = 0;
			short[] array1D = new short[(array2D.length * array2D[0].length)];

			for(int y = 0; y < array2D.length; y++)
				for(int x = 0; x < array2D[0].length; x++)
					array1D[index++] = array2D[y][x];

			return array1D;
		}

		// This method calculates mean of intensity values of a 2-D array
		public float calculateMean(short[][] array2D)
		{
			return this.calculateMean(this.Convert2DTo1D(array2D));       
		}

		// This method calculates mean of intensity values of a 1-D array
		public float calculateMean(short[] array1D)
		{
			float tmpSum = 0;

			for(int x = 0; x < array1D.length; x++)
				tmpSum += array1D[x];

			return (tmpSum / array1D.length);
		}

		// This method calculates standard deviation of intensity values of a 2-D array
		public float calculateStandardDeviation(short[][] array2D, float mean)
		{
			return this.calculateStandardDeviation(this.Convert2DTo1D(array2D), mean);
		}    

		// This method calculates standard deviation of intensity values of a 1-D array
		public float calculateStandardDeviation(short[] array1D, float mean)       
		{
			float tmpSum = 0;

			for(int x = 0; x < array1D.length; x++)     
			{
				tmpSum += (int)Math.pow((array1D[x] - mean), 2);
			}

			return (float)Math.sqrt((tmpSum / array1D.length));
		}

		// This method runs a new thread and does all of the enhancement process
		public void run()
		{   
			c.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));    

			String imageName = txtSelectImage.getText();
			setProgressBar(30);

			try
			{   
				int neighborhood = 3, innerNeighborhood = 3;                   
				neighborhood = (neighborhood % 2 > 0 ? neighborhood - 1 : neighborhood - 2);
				neighborhood /= 2;
				innerNeighborhood = (innerNeighborhood % 2 > 0 ? innerNeighborhood - 1 : innerNeighborhood - 2);
				innerNeighborhood /= 2;                    

				if(!chkAutoEnhance.isSelected())
					finalImage = manualEnhance(((float)(sldrE.getValue())/100), ((float)(sldrK0.getValue())/100), ((float)(sldrK1.getValue())/100), ((float)(sldrK2.getValue())/100));
				else
				{
					finalImage = imageEnhancer.manualEnhance(((float)(sldrE.getValue())/100), ((float)(sldrK0.getValue())/100), ((float)(sldrK1.getValue())/100), ((float)(sldrK2.getValue())/100));
					imageEnhancer.originalImage = finalImage;
					finalImage = imageEnhancer.autoEnhance(((float)(sldrAutoE.getValue())/100), ((float)(sldrAutoKo.getValue())/100));
				}                

				setProgressBar(60);

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("MMddyyyy_hhmmss");

				String DoValue = sdf.format(cal.getTime());
				File tFile;    

				tFile = new File(DoValue);
				if(tFile.exists())
					deleteDirectory(tFile);
				if(!tFile.exists())
					tFile.mkdir();

				writeToTextFile(originalImage, "Original", M, N, DoValue);   
				imageName = writeToTextFile(finalImage, (chkAutoEnhance.isSelected() ? "AdaptiveImage" : "FinalImage"), M, N, DoValue);
				setProgressBar(70);

				float Mean_result = imageEnhancer.calculateMean(finalImage);
				float SD_result = imageEnhancer.calculateStandardDeviation(finalImage, Mean_result);

				lblGlobalStatistics.setText("<html><Font Name='Ariel' Bold='True' Size='2'>Original Image: Mean = " + dfSlider.format(this.Mean_g) + ", Standard Deviation = " + dfSlider.format(this.SD_g) + 
						"<BR>Resultant Image: Mean = " + dfSlider.format(Mean_result) + ", Standard Deviation = " + dfSlider.format(SD_result) + "</Font></html>");
				setProgressBar(90);
			}
			catch (Exception exc)
			{
				//JOptionPane.showMessageDialog(this, exc.toString(), "Error", JOptionPane.ERROR_MESSAGE);                           
			}
			finally
			{
				c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

			}

			viewImage(imageName);
			// waiting for viewImage method to complete open the image in background
			for(int i = 92; i <= 102; i += 2)
				setProgressBar(i);
		}
	}

	public static void main(String[] args)
	{       
		final frmImageEnhancer app = new frmImageEnhancer();
	}

	// The whole constructor is for setting up the UI of the form
	public frmImageEnhancer()
	{
		// setting UI of the window
		c = getContentPane();
		setBounds(50, 50, 555, 595);
		setBackground(new Color(204, 204, 204));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Image & Contrast Enhancement");
		setResizable(false);
		c.setLayout(null);

		// add buttons to their event listener
		btnBrowse.addActionListener(this);       
		btnRun.addActionListener(this);   
		sldrK0.addChangeListener(this);
		sldrK1.addChangeListener(this);
		sldrK2.addChangeListener(this);
		sldrE.addChangeListener(this);
		sldrAutoKo.addChangeListener(this);
		sldrAutoE.addChangeListener(this);
		chkAutoEnhance.addChangeListener(this);

		txtAreaMessages.setEditable(true);    // message box is read-only
		scrollPaneMessages = new JScrollPane(txtAreaMessages);       
		pnlMessages.setBounds(10, 240, 530, 315);
		pnlMessages.setPreferredSize(new Dimension(0, 235));
		pnlMessages.add(scrollPaneMessages, BorderLayout.CENTER);

		lblK0.setBounds(50, 60, 100, 25);
		sldrK0.setBounds(10, 80, 125, 20);
		lblK1.setBounds(178, 60, 100, 25);
		sldrK1.setBounds(143, 80, 125, 20);
		lblK2.setBounds(315, 60, 100, 25);
		sldrK2.setBounds(280, 80, 125, 20);
		lblE.setBounds(455, 60, 100, 25);
		sldrE.setBounds(415, 80, 125, 20);       
		lblAutoKo.setBounds(25, 110, 100, 25);
		sldrAutoKo.setBounds(10, 130, 125, 20);
		lblAutoE.setBounds(165, 110, 100, 25);
		sldrAutoE.setBounds(143, 130, 125, 20);
		pbEnhancer.setBounds(10, 205, 530, 25);

		// setting UI of buttons
		lblSelectImage.setBounds(10, 20, 100, 25);               
		txtSelectImage.setBounds(110, 20, 335, 25);       
		btnBrowse.setBounds(450, 20, 90, 25);
		chkAutoEnhance.setBounds(280, 122, 200, 25);
		btnRun.setBounds(10, 165, 185, 25);
		lblGlobalStatistics.setBounds(210, 162, 500, 30);        

		txtSelectImage.setEditable(false);
		txtSelectImage.setBackground(Color.WHITE);
		pbEnhancer.setStringPainted(true);

		// adding buttons to the main window
		c.add(btnBrowse);       
		c.add(btnRun);           
		c.add(lblGlobalStatistics);           
		c.add(lblSelectImage);
		c.add(lblK0);
		c.add(sldrK0);
		c.add(lblK1);
		c.add(sldrK1);
		c.add(lblK2);
		c.add(sldrK2);
		c.add(lblE);
		c.add(sldrE);
		c.add(lblAutoKo);
		c.add(sldrAutoKo);
		c.add(lblAutoE);
		c.add(sldrAutoE);
		c.add(txtSelectImage);
		c.add(pnlMessages);
		c.add(chkAutoEnhance);
		c.add(pbEnhancer);        

		show();       
	}

	public void itemStateChanged(ItemEvent e) { }

	// This method decides which method to call for any particular slider motion
	public void stateChanged(ChangeEvent e)
	{       
		Object source = e.getSource();

		if (source == this.sldrK0)
			this.lblK0.setText("Ko"+ dfSlider.format(((float)this.sldrK0.getValue())/100));
					else if (source == this.sldrK1)
						this.lblK1.setText("K1"+ dfSlider.format(((float)this.sldrK1.getValue())/100));
								else if (source == this.sldrK2)
									this.lblK2.setText("K2"+ dfSlider.format(((float)this.sldrK2.getValue())/100));
											else if (source == this.sldrE)
												this.lblE.setText("E"+ dfSlider.format(((float)this.sldrE.getValue())/100));
														else if (source == this.sldrAutoE)
															this.lblAutoE.setText("Adapt E = " + dfSlider.format(((float)this.sldrAutoE.getValue())/100));
														else if (source == this.sldrAutoKo)
															this.lblAutoKo.setText("Adapt Ko = " + dfSlider.format(((float)this.sldrAutoKo.getValue())/100));
	}        

	// This method decides which method to call for any particular button click
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		if (source == btnBrowse)
			btnBrowse_Click();
		else if (source == btnRun)
			btnRun_Click();
	}        

	// This method opens up a file dialogue box and lets user select a source pgm
	// image file for enhancement. It also sets hard coded weight values and open
	// an input source image.
	public void btnBrowse_Click()
	{   
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("images/"));

		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".pgm")
						|| f.isDirectory();
			}

			public String getDescription() {
				return "PGM Images";
			}
		});

		int r = chooser.showOpenDialog(this);
		if (r == JFileChooser.APPROVE_OPTION)
		{   
			if(chooser.getSelectedFile().getAbsolutePath().endsWith(".pgm"))
			{
				this.setProgressBar(0);
				String imageName = chooser.getSelectedFile().getAbsolutePath();
				this.txtSelectImage.setText(imageName);
				this.loadImageStatistics(imageName);
				this.setHardCodedWeights();
				this.viewImage(imageName);   
			}
			else
				JOptionPane.showMessageDialog(this, "Only PGM image files are supported in this version. Please select a valid PGM image file.", "Warning: Image File Not Supported", JOptionPane.WARNING_MESSAGE);                           
		}
	}

	// This method sets the slider values against some of the most commonly used images
	private void setHardCodedWeights()
	{
		String imageName = this.txtSelectImage.getText().trim();

		if(imageName.endsWith("florida_satellite.pgm"))
		{
			this.sldrK0.setValue(100);
			this.sldrK1.setValue(5);
			this.sldrK2.setValue(250);
			this.sldrE.setValue(180);
			this.sldrAutoKo.setValue(70);
			this.sldrAutoE.setValue(25);
		}
		else if(imageName.endsWith("SEM-filament.pgm") || imageName.endsWith("Tungsten.pgm"))
		{
			this.sldrK0.setValue(65);
			this.sldrK1.setValue(0);
			this.sldrK2.setValue(250);
			this.sldrE.setValue(150);
			this.sldrAutoKo.setValue(0);
			this.sldrAutoE.setValue(15);
		}
		else if(imageName.endsWith("MRI-spine.pgm"))
		{
			this.sldrK0.setValue(180);
			this.sldrK1.setValue(1);
			this.sldrK2.setValue(225);
			this.sldrE.setValue(140);
			this.sldrAutoKo.setValue(75);
			this.sldrAutoE.setValue(25);
		}
		else if(imageName.endsWith("lena.pgm"))
		{
			this.sldrK0.setValue(25);
			this.sldrK1.setValue(5);
			this.sldrK2.setValue(250);
			this.sldrE.setValue(75);
			this.sldrAutoKo.setValue(35);
			this.sldrAutoE.setValue(35);
		} 

		this.lblK0.setText("Ko"+ dfSlider.format(((float)this.sldrK0.getValue())/100));
				this.lblK1.setText("K1"+ dfSlider.format(((float)this.sldrK1.getValue())/100));
						this.lblK2.setText("K2"+ dfSlider.format(((float)this.sldrK2.getValue())/100));
								this.lblE.setText("E"+ dfSlider.format(((float)this.sldrE.getValue())/100));
										this.lblAutoE.setText("Adapt E = " + dfSlider.format(((float)this.sldrAutoE.getValue())/100));
										this.lblAutoKo.setText("Adapt Ko = " + dfSlider.format(((float)this.sldrAutoKo.getValue())/100));
	}

	// This method sets progress bar's values
	private void setProgressBar(int value)
	{         
		pbEnhancer.setValue(value);
		pbEnhancer.repaint();
		try{Thread.sleep(250);}
		catch (InterruptedException err){}
	}

	// This method loads input image's statistics and starts the thread which actually
	// does the enhancement part
	public void btnRun_Click()
	{
		if(this.txtSelectImage.getText().trim().length() > 0)
		{
			loadImageStatistics(txtSelectImage.getText());
			this.trdImageEnhancer.start();
		}
		else
			JOptionPane.showMessageDialog(this, "No image was selected for enhancement. Please select the 'Browse' button to locate an image file.",
					"Warning: No Image to Process", JOptionPane.WARNING_MESSAGE);                           
	}    

	// This method calls some other methods to calculate mean, standard deviation and
	// other statistics of the input image passed to it
	private void loadImageStatistics(String imageName)
	{
		this.originalImage = this.loadImage(imageName);       
		this.imageEnhancer = new clsImageEnhancer(this.originalImage);   
		this.trdImageEnhancer = new Thread(this.imageEnhancer);
		this.trdImageEnhancer.setPriority(Thread.MAX_PRIORITY);
		this.finalImage = new short[this.originalImage.length][this.originalImage[0].length];
		this.imageEnhancer.Mean_g = this.Mean_g = this.imageEnhancer.calculateMean(this.originalImage);
		this.imageEnhancer.SD_g = this.SD_g = this.imageEnhancer.calculateStandardDeviation(this.originalImage, Mean_g);
		lblGlobalStatistics.setText("<html><Font Name='Ariel' Bold='True' Size='2'>Original Image: Mean = " + dfSlider.format(this.Mean_g) +
				", Standard Deviation = " + dfSlider.format(this.SD_g) + "</Font></html>");
	}

	// This method loads the input image into a 2-D array
	private short[][] loadImage(String fileName)
	{
		short[][] fileImage = new short[1][1];
		String header = "";
		try
		{
			file = new RandomAccessFile(fileName, "r");
			bin = new BinaryFile(file);
			bin.setEndian(endian);
			bin.setSigned(signed);
			while(true)
			{
				header += bin.readFixedZeroString(1);
				if(header.endsWith("255"))
				{
					header += bin.readFixedZeroString(1);
					break;
				}
			}

			// get dimensions from its header
			String dimensions, strM, strN;
			dimensions = header.substring(header.indexOf('\n')+1);
			dimensions = dimensions.substring(dimensions.indexOf('\n')+1);
			dimensions = dimensions.substring(0, dimensions.indexOf('\n'));
			strM = dimensions.substring(0, dimensions.indexOf(' ')).trim();
			strN = dimensions.substring(dimensions.indexOf(' ')+1).trim();

			int actualM, actualN;

			// set M, N, P and Q

			actualM = Integer.parseInt(strM);
			actualN = Integer.parseInt(strN);

			M = actualM;
			N = actualN;            

			// create arrays
			fileImage = new short[N][M];

			// populate array from original image
			try
			{
				for(int y = 0; y < N; y++)               
					for(int x = 0; x < M; x++)
						fileImage[y][x] = bin.readUnsignedByte();
			}
			catch(Exception exc)
			{
				JOptionPane.showMessageDialog(this, "Error while reading the image.\n" + exc.toString(),  "Error", JOptionPane.ERROR_MESSAGE);             
			}
			finally         
			{
				file.close();             
			}   
		}
		catch(Exception exc)
		{
			JOptionPane.showMessageDialog(this, exc.toString(),  "Error", JOptionPane.ERROR_MESSAGE);             
		}    

		return fileImage;
	}    

	// This method deletes a directory and all sub-directories and files in it
	public boolean deleteDirectory(File path)
	{
		if( path.exists() )
		{
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++)
				if(files[i].isDirectory())
					deleteDirectory(files[i]);
				else
					files[i].delete();
		}
		return( path.delete() );
	}

	// This method opens up an image in ImageJ Viewer
	private void viewImage(String imageName)
	{       
		Runtime load = Runtime.getRuntime();           
		String programC = "C:\\Program Files\\ImageJ\\ImageJ.exe";           
		String programD = "D:\\Program Files\\ImageJ\\ImageJ.exe";     

		try
		{
			if(new File(programC).exists())
				load.exec(programC + " " + imageName);
			else
				load.exec(programD + " " + imageName);
		}
		catch (Exception exc)
		{
			JOptionPane.showMessageDialog(this, "ImageJ could not be found on this computer. This will not stop Image Enhancement process.\nHowever, application will not be able to display original and enhanced images." +
					" Please select\n'Run Image Enhancement' button in order to continue.\n\n", "Warning: ImageJ Not Found", JOptionPane.WARNING_MESSAGE);                           
		}           
	}        

	// This method writes a 2-D array into the text box
	private void writeImageInTextbox(String title, short[][] imageArray)
	{
		txtAreaMessages.append(title + "\n");
		for(int y = 0; y < imageArray.length; y++)
		{
			for(int x = 0; x < imageArray[0].length; x++)
			{
				txtAreaMessages.append(imageArray[y][x] + " ");
			}
			txtAreaMessages.append("\n");
		}
		txtAreaMessages.append("\n");
		txtAreaMessages.setCaretPosition(txtAreaMessages.getText().length());
	}

	// This method writes a 2-D image into a file in Text format
	private String writeToTextFile(short[][] imageArray, String imgName, int X, int Y, String DoValue)
	{   
		String strImageFile = "";       
		FileOutputStream fout;
		PrintStream ps;
		File file;    

		try
		{           
			// if the image already exists, delete it and re-create it
			strImageFile = DoValue + "/Image_" + imgName + X + Y + ".pgm";
			file = new File(strImageFile);
			if(file.exists())
				file.delete();

			fout = new FileOutputStream(strImageFile, true);   
			ps = new PrintStream(fout);            

			// adding predefined header of image
			ps.println("P2\n# Written by Image/Contrast Enhacement Tool (coded by Mukarram Mukhtar)\n" + X + " " + Y + "\n255");

			for(int y = 0; y < Y; y++)
				for(int x = 0; x < X; x++)
					ps.print(dfImage.format(imageArray[y][x]) + " ");

			ps.close();
			fout.close();           
		}           
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}        

		return strImageFile;       
	}    

	// This method writes a 2-D image into a file in Binary format
	private String writeToBinaryFile(short[][] imageArray, String imgName, int X, int Y, String DoValue)
	{   
		String strImageFile = "";

		try
		{
			// if the image already exists, delete it and re-create it
			strImageFile = DoValue + "/Image_" + imgName + X + Y + ".pgm";
			File tempFile = new File(strImageFile);
			if(tempFile.exists())
				tempFile.delete();

			file = new RandomAccessFile(strImageFile, "rw");
			bin = new BinaryFile(file);

			bin.setEndian(endian);
			bin.setSigned(signed);
			bin.writeZeroString("P5\n# Written by Image/Contrast Enhacement Tool (coded by Mukarram Mukhtar)\n" + X + " " + Y + "\n255");

			for(int y = 0; y < Y; y++)
				for(int x = 0; x < X; x++)
					bin.writeByte((short)imageArray[y][x]);

			file.close();
		}           
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(this, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		return strImageFile;
	}

	private JButton btnBrowse = new JButton("Browse...");       
	private JButton btnRun = new JButton("Run Image Enhancement");

	private JTextArea txtAreaMessages = new JTextArea();
	private JScrollPane scrollPaneMessages = null;    //    Instantiated in constructor frmChess()
	private JPanel pnlMessages = new JPanel(new BorderLayout());

	private JLabel lblSelectImage = new JLabel("Select an image:");
	private JLabel lblGlobalStatistics = new JLabel();
	private JTextField txtSelectImage = new JTextField();
	private JCheckBox chkAutoEnhance = new JCheckBox("Adaptive Enhancement", false);

	private JLabel lblK0 = new JLabel("Ko = 0.00");
	private JSlider sldrK0 = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
	private JLabel lblK1 = new JLabel("K1 = 0.00");
	private JSlider sldrK1 = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
	private JLabel lblK2 = new JLabel("K2 = 0.00");
	private JSlider sldrK2 = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
	private JLabel lblE = new JLabel("E = 0.00");
	private JSlider sldrE = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
	private JLabel lblAutoKo = new JLabel("Adapt Ko = 0.00");
	private JSlider sldrAutoKo = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
	private JLabel lblAutoE = new JLabel("Adapt E = 0.00");
	private JSlider sldrAutoE = new JSlider(JSlider.HORIZONTAL, 0, 500, 0);
	private JProgressBar pbEnhancer = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);

	private clsImageEnhancer imageEnhancer;   
	private Thread trdImageEnhancer;

	private DecimalFormat dfImage = new DecimalFormat("000");   
	private DecimalFormat dfSlider = new DecimalFormat("0.00");    

	private int M, N;
	private float Mean_g, SD_g;        

	short[][] originalImage;
	short[][] finalImage; 

	private RandomAccessFile file;
	private BinaryFile bin;
	// set the endian mode to run the test in
	final short endian = BinaryFile.BIG_ENDIAN;
	// set the signed mode to run the test in
	private final boolean signed = true;
	private final double TWO = 2;

	private Random rnd = new Random();   
	private Container c;
}