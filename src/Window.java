import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Window.Type;

public class Window {

	private JFrame frmDataanalyzer;
	private JTextField textField;
	private JTextField textField_1;
	
	private JTextField textField_2;
	private JTextField textField_3;
	
	List<Double> gsrData = new ArrayList<Double>();
	List<Double> headsetData = new ArrayList<Double>();

	//The data that is to be analyzed
	List<Double> data = new ArrayList<Double>();
	
	private JTextField txtS;
	private JTextField txtS_1;
	private JTextField txtS_2;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	
	int offset = 0;
	private JTextField textField_8;
	
	boolean averageEnabled = true;
	boolean minValEnabled = true;
	boolean maxValEnabled = true;
	boolean stdevEnabled = true;
	boolean skewnessEnabled = true;
	
	//Holder for diagram and dataset for all datasets
	JPanel panel;
	XYSeriesCollection datasets;
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frmDataanalyzer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/* Malplaced function that draws the diagram */
	void drawDiagram(List<Double> data, String name, boolean redraw) {
	
		//Resets diagram if redraw is true
		if (redraw) {
			datasets = new XYSeriesCollection();
		}
		
		
		XYSeries series = new XYSeries(name);
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) != null) series.add(i, data.get(i));
		}
	   
	    //Add the serie to your datasets
   		datasets.addSeries(series);

		//Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart(
			"Data", // Title
			"x-axis", // x-axis Label
			"y-axis", // y-axis Label
			datasets, // Dataset
			PlotOrientation.VERTICAL, // Plot Orientation
			true, // Show Legend
			false, // Use tooltips
			false // Configure chart to generate URLs?
		);
   
   		ChartPanel CP = new ChartPanel(chart);
   		panel.setLayout(new java.awt.BorderLayout());
   		panel.add(CP,BorderLayout.CENTER);
 		panel.validate();
		
	}
	
	/* ---------------------------------
	 * End of diagram funciton
	 */

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
		//Creates empty graph
		List<Double> tempList = new ArrayList<Double>();
		tempList.add(0.0);
		drawDiagram(tempList, "Empty", true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmDataanalyzer = new JFrame();
		frmDataanalyzer.setType(Type.UTILITY);
		frmDataanalyzer.setTitle("DataExtractor");
		frmDataanalyzer.setBounds(100, 100, 1214, 580);
		frmDataanalyzer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDataanalyzer.getContentPane().setLayout(null);
		
		JLabel lblSynkronisering = new JLabel("Calculate offset");
		lblSynkronisering.setHorizontalAlignment(SwingConstants.CENTER);
		lblSynkronisering.setBounds(187, 18, 121, 16);
		frmDataanalyzer.getContentPane().add(lblSynkronisering);
		
		JButton btnNewButton = new JButton("Headset File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					try {
						headsetData = (new DataHandler()).accMag(file, Integer.parseInt(textField_2.getText()));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Draws diagram and updates title
					drawDiagram((new DataHandler()).extract(headsetData, 5000), "Headset", true);
					frmDataanalyzer.setTitle("DataExtractor - " + file.getName());
					
					System.out.println("Headset data read...");
					
				}
				
			}
		});
		btnNewButton.setBounds(12, 76, 121, 25);
		frmDataanalyzer.getContentPane().add(btnNewButton);
		
		JButton btnGSRFile = new JButton("GSR File");
		btnGSRFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					try {
						gsrData = (new DataHandler()).accMag(file, Integer.parseInt(textField_3.getText()), Integer.parseInt(textField_1.getText()));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					drawDiagram((new DataHandler()).extract(gsrData, 5000), "GSR", false);
					
					System.out.println("GSR data read...");
					
				}
				
			}
		});
		btnGSRFile.setBounds(12, 113, 121, 25);
		frmDataanalyzer.getContentPane().add(btnGSRFile);
		
		JLabel lblSampleRate = new JLabel("Sample rate");
		lblSampleRate.setHorizontalAlignment(SwingConstants.CENTER);
		lblSampleRate.setBounds(145, 46, 87, 16);
		frmDataanalyzer.getContentPane().add(lblSampleRate);
		
		final JLabel lblOffset = new JLabel("Offset: Unknown");
		lblOffset.setVerticalAlignment(SwingConstants.TOP);
		lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
		lblOffset.setBounds(367, 116, 118, 16);
		frmDataanalyzer.getContentPane().add(lblOffset);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setText("120");
		textField.setBounds(145, 74, 87, 25);
		frmDataanalyzer.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setText("32");
		textField_1.setColumns(10);
		textField_1.setBounds(145, 111, 87, 25);
		frmDataanalyzer.getContentPane().add(textField_1);
		
		JButton btnNewButton_1 = new JButton("Calculate");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	
				//StepLength 5000 = 5 sek
				int stepLength = 5000;
				
				//Extracts features from data
				List<Double> extractedHead = (new DataHandler()).extract(headsetData, stepLength);
				List<Double> extractedGSR = (new DataHandler()).extract(gsrData, stepLength);
				
				int headStart = 0;
				int gsrStart = 0;
				
				//Finds start moving point for both devices and updates offset value
				try {
					headStart = (new DataHandler()).findStartMove(extractedHead, 10, 0.6, stepLength);
					gsrStart = (new DataHandler()).findStartMove(extractedGSR, 10, 0.8, stepLength);
					System.out.println("Found critical extracted index for headset at: " + headStart + " and for gsr at: " + gsrStart);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//Draws start move points to graph as vertical lines
				double maxVal = 0.0;
				try {
					maxVal = Math.max((new DataHandler()).maxVal(extractedHead, 0, extractedHead.size()), (new DataHandler()).maxVal(extractedGSR, 0, extractedGSR.size()));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				List<Double> movePoint1 = new ArrayList<Double>();
				for (int i = 0; i < (headStart - 1)/(stepLength/1000); i++) movePoint1.add(0.0);
				movePoint1.add(maxVal);
				drawDiagram(movePoint1, "Headset movement", false);
				List<Double> movePoint2 = new ArrayList<Double>();
				for (int i = 0; i < (gsrStart - 1)/(stepLength/1000); i++) movePoint2.add(0.0);
				movePoint2.add(maxVal);
				drawDiagram(movePoint2, "GSR movement", false);
				
				//Updates offset
				offset = gsrStart - headStart;
				lblOffset.setText("Offset: " + offset/1000.0 + " s");
				
			}
		});
		btnNewButton_1.setBounds(367, 76, 121, 25);
		frmDataanalyzer.getContentPane().add(btnNewButton_1);
		
		JLabel lblStartColumnAcc = new JLabel("Acc Start Column");
		lblStartColumnAcc.setHorizontalAlignment(SwingConstants.CENTER);
		lblStartColumnAcc.setBounds(244, 46, 111, 16);
		frmDataanalyzer.getContentPane().add(lblStartColumnAcc);
		
		textField_2 = new JTextField();
		textField_2.setText("7");
		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		textField_2.setColumns(10);
		textField_2.setBounds(244, 74, 111, 25);
		frmDataanalyzer.getContentPane().add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setText("0");
		textField_3.setHorizontalAlignment(SwingConstants.CENTER);
		textField_3.setColumns(10);
		textField_3.setBounds(244, 111, 111, 25);
		frmDataanalyzer.getContentPane().add(textField_3);
		
		panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(521, 42, 635, 362);
		frmDataanalyzer.getContentPane().add(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.BLACK);
		panel_1.setBounds(12, 151, 476, 1);
		frmDataanalyzer.getContentPane().add(panel_1);
		
		JLabel lblNewLabel = new JLabel("Data to analyse");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 269, 121, 22);
		frmDataanalyzer.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton_2 = new JButton("Choose");
		btnNewButton_2.setBounds(145, 269, 121, 25);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (lblOffset.getText().toLowerCase().contains("unknown")) JOptionPane.showMessageDialog(null, "Calculate offset for better precision.");
				
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					File file = fileChooser.getSelectedFile();
					try {
						data = (new DataHandler()).dataFetch(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//Sequence time restrains
					int start = (new DataHandler()).convertTimestamp(txtS.getText());
					int end = (new DataHandler()).convertTimestamp(txtS_1.getText());;
					
					drawDiagram(data.subList(start, end), "Data", false);
					
					System.out.println("Datafile read...");
					
				}
				
			}
		});
		frmDataanalyzer.getContentPane().add(btnNewButton_2);
		
		JLabel lblPeriod = new JLabel("Duration");
		lblPeriod.setHorizontalAlignment(SwingConstants.CENTER);
		lblPeriod.setBounds(12, 197, 121, 25);
		frmDataanalyzer.getContentPane().add(lblPeriod);
		
		txtS = new JTextField();
		txtS.setHorizontalAlignment(SwingConstants.CENTER);
		txtS.setText("00:00:10");
		txtS.setBounds(145, 196, 121, 25);
		frmDataanalyzer.getContentPane().add(txtS);
		txtS.setColumns(10);
		
		JLabel lblTo = new JLabel("to");
		lblTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTo.setBounds(272, 197, 93, 25);
		frmDataanalyzer.getContentPane().add(lblTo);
		
		txtS_1 = new JTextField();
		txtS_1.setText("00:10:02");
		txtS_1.setHorizontalAlignment(SwingConstants.CENTER);
		txtS_1.setColumns(10);
		txtS_1.setBounds(367, 196, 121, 25);
		frmDataanalyzer.getContentPane().add(txtS_1);
		
		JLabel lblChunkSize = new JLabel("Chunk Size");
		lblChunkSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblChunkSize.setBounds(12, 232, 121, 25);
		frmDataanalyzer.getContentPane().add(lblChunkSize);
		
		txtS_2 = new JTextField();
		txtS_2.setText("00:00:05");
		txtS_2.setHorizontalAlignment(SwingConstants.CENTER);
		txtS_2.setColumns(10);
		txtS_2.setBounds(145, 236, 121, 25);
		frmDataanalyzer.getContentPane().add(txtS_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.BLACK);
		panel_2.setBounds(12, 307, 476, 1);
		frmDataanalyzer.getContentPane().add(panel_2);
		
		JLabel lblExportSettings = new JLabel("Export Settings");
		lblExportSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblExportSettings.setBounds(12, 313, 476, 22);
		frmDataanalyzer.getContentPane().add(lblExportSettings);
		
		JLabel lblFileInput = new JLabel("Timing");
		lblFileInput.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileInput.setBounds(12, 159, 476, 22);
		frmDataanalyzer.getContentPane().add(lblFileInput);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Average");
		chckbxNewCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					averageEnabled = true;
		        } else {
		        	averageEnabled = false;
		        };
			}
		});
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxNewCheckBox.setBounds(12, 375, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxNewCheckBox);
		
		JLabel lblDataType = new JLabel("Data type");
		lblDataType.setHorizontalAlignment(SwingConstants.CENTER);
		lblDataType.setBounds(12, 341, 121, 22);
		frmDataanalyzer.getContentPane().add(lblDataType);
		
		JLabel lblColumn = new JLabel("Column");
		lblColumn.setHorizontalAlignment(SwingConstants.CENTER);
		lblColumn.setBounds(145, 341, 121, 22);
		frmDataanalyzer.getContentPane().add(lblColumn);
		
		textField_4 = new JTextField();
		textField_4.setHorizontalAlignment(SwingConstants.CENTER);
		textField_4.setText("1");
		textField_4.setBounds(145, 377, 121, 22);
		frmDataanalyzer.getContentPane().add(textField_4);
		textField_4.setColumns(10);
		
		JCheckBox chckbxMaxiumValue = new JCheckBox("Max Value");
		chckbxMaxiumValue.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					maxValEnabled = true;
		        } else {
		        	maxValEnabled = false;
		        };
			}
		});
		chckbxMaxiumValue.setSelected(true);
		chckbxMaxiumValue.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxMaxiumValue.setBounds(12, 435, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxMaxiumValue);
		
		textField_5 = new JTextField();
		textField_5.setText("3");
		textField_5.setHorizontalAlignment(SwingConstants.CENTER);
		textField_5.setColumns(10);
		textField_5.setBounds(145, 437, 121, 22);
		frmDataanalyzer.getContentPane().add(textField_5);
		
		JCheckBox chckbxSkewness = new JCheckBox("Skewness ");
		chckbxSkewness.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					skewnessEnabled = true;
		        } else {
		        	skewnessEnabled = false;
		        };
			}
		});
		chckbxSkewness.setSelected(true);
		chckbxSkewness.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxSkewness.setBounds(12, 495, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxSkewness);
		
		textField_6 = new JTextField();
		textField_6.setText("5");
		textField_6.setHorizontalAlignment(SwingConstants.CENTER);
		textField_6.setColumns(10);
		textField_6.setBounds(145, 497, 121, 22);
		frmDataanalyzer.getContentPane().add(textField_6);
		
		JCheckBox chckbxStandardDeviation = new JCheckBox("SD Dev");
		chckbxStandardDeviation.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					stdevEnabled = true;
		        } else {
		        	stdevEnabled = false;
		        };
			}
		});
		chckbxStandardDeviation.setSelected(true);
		chckbxStandardDeviation.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxStandardDeviation.setBounds(12, 465, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxStandardDeviation);
		
		textField_7 = new JTextField();
		textField_7.setText("4");
		textField_7.setHorizontalAlignment(SwingConstants.CENTER);
		textField_7.setColumns(10);
		textField_7.setBounds(145, 467, 121, 22);
		frmDataanalyzer.getContentPane().add(textField_7);
		
		JButton btnExportData = new JButton("Export Data");
		btnExportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//Sequence time restrains
				int start = (new DataHandler()).convertTimestamp(txtS.getText());
				int end = (new DataHandler()).convertTimestamp(txtS_1.getText());;
				int chunkSize = (new DataHandler()).convertTimestamp(txtS_2.getText());
				
				List<Double> average = new ArrayList<Double>();
				List<Double> minVal = new ArrayList<Double>();
				List<Double> maxVal = new ArrayList<Double>();
				List<Double> stdev = new ArrayList<Double>();
				List<Double> skewness = new ArrayList<Double>();
				
				//Goes through the chunks and extract all types of values for each chunk
				int currentChunkEnd = 0;
				for (int i = start; i < end; i += chunkSize) {
					
					if ((i + chunkSize) < end) {
						currentChunkEnd = i + chunkSize;
					} else {
						currentChunkEnd = end;
					}
					
					try {
						
						//Fetches all values and takes offset into consideration
						if (averageEnabled) average.add((new DataHandler()).average(data, i - offset, currentChunkEnd));
						if (minValEnabled) minVal.add((new DataHandler()).minVal(data, i - offset, currentChunkEnd));
						if (maxValEnabled) maxVal.add((new DataHandler()).maxVal(data, i - offset, currentChunkEnd));
						if (stdevEnabled)stdev.add((new DataHandler()).stdev(data, i - offset, currentChunkEnd));
						if (skewnessEnabled) skewness.add((new DataHandler()).skewness(data, i - offset, currentChunkEnd));
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				File newFile;
				String path = "";
				String fileName = "";
				
				//Opens save panel and allows user to save data to csv file
				JFileChooser chooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("CSV file", new String[] {"csv"});
				chooser.setFileFilter(filter);
				chooser.addChoosableFileFilter(filter);
			    int retrival = chooser.showSaveDialog(null);
			    if (retrival == JFileChooser.APPROVE_OPTION) {
			    	
			    	newFile = chooser.getSelectedFile();
			    	path = newFile.getAbsolutePath();
			    	path = path.substring(0,path.lastIndexOf(File.separator)) + "\\";
			    	fileName = newFile.getName() + ".csv";
			    	
			    	//Exports data to specificed columns in specified file
					try {
						boolean firstDraw = true; 
						if (averageEnabled) {
							(new FileHandler()).exportData(average, path, fileName, Integer.parseInt(textField_4.getText()) - 1);
							drawDiagram(average, "Average", firstDraw);
							firstDraw = false;
						}
						if (minValEnabled) {
							(new FileHandler()).exportData(minVal, path, fileName, Integer.parseInt(textField_8.getText())  - 1);
							drawDiagram(minVal, "Min Value", firstDraw);
							firstDraw = false;
						}
						if (maxValEnabled) {
							(new FileHandler()).exportData(maxVal, path, fileName, Integer.parseInt(textField_5.getText())  - 1);
							drawDiagram(maxVal, "Max Value", firstDraw);
							firstDraw = false;
						}
						if (stdevEnabled) {
							(new FileHandler()).exportData(stdev, path, fileName, Integer.parseInt(textField_7.getText())  - 1);
							drawDiagram(stdev, "Standard Deviation", firstDraw);
							firstDraw = false;
						}
						if (skewnessEnabled) {
							(new FileHandler()).exportData(skewness, path, fileName, Integer.parseInt(textField_6.getText())  - 1);
							drawDiagram(skewness, "Skewness", firstDraw);
							firstDraw = false;
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			    	System.out.println("File exported...");
					
			    }

			}
		});
		btnExportData.setBounds(903, 420, 253, 43);
		frmDataanalyzer.getContentPane().add(btnExportData);
		
		JCheckBox chckbxMinxValue = new JCheckBox("Min Value");
		chckbxMinxValue.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					minValEnabled = true;
		        } else {
		        	minValEnabled = false;
		        };
			}
		});
		chckbxMinxValue.setSelected(true);
		chckbxMinxValue.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxMinxValue.setBounds(12, 405, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxMinxValue);
		
		textField_8 = new JTextField();
		textField_8.setText("2");
		textField_8.setHorizontalAlignment(SwingConstants.CENTER);
		textField_8.setColumns(10);
		textField_8.setBounds(145, 407, 121, 22);
		frmDataanalyzer.getContentPane().add(textField_8);
	}
}
