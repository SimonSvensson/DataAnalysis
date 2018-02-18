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
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingConstants;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class Window {

	private JFrame frmDataanalyzer;
	private JTextField textField;
	private JTextField textField_1;
	
	private JTextField textField_2;
	private JTextField textField_3;
	
	//Lists to hold accelerometer data
	List<Double> gsrData = new ArrayList<Double>();
	List<Double> headsetData = new ArrayList<Double>();
	
	//Path variable for headset file and GSR folder
	String gsrPath;
	String headsetPath;

	//The data that is to be analyzed
	List<Double> data = new ArrayList<Double>();
	//For choosing data
	JScrollPane dataScrollPane;
	JList dataList;
	DefaultListModel dataListModel;
	//For choosing data
	JScrollPane selectScrollPane;
	JList selectList;
	DefaultListModel selectListModel;
	
	private JTextField txtS;
	private JTextField txtS_1;
	private JTextField txtS_2;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	
	int offset = 0;
	private JTextField textField_8;
	
	//Calculate ready
	boolean headsetRead = false;
	boolean gsrRead = false;
	
	boolean averageEnabled = true;
	boolean minValEnabled = false;
	boolean maxValEnabled = false;
	boolean stdevEnabled = false;
	boolean skewnessEnabled = false;
	
	//Average parameter, around 5000
	int stepLength = 5000;
	
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
	
	//Function that calculates and updates the offset
	public void populateListview() throws IOException {
		
		dataListModel.removeAllElements();
		
		//For Headset
		File headsetFile = new File(headsetPath);
		
		String[] line;
		String encoding = "UTF-8", delimiter = ","; // Default is .csv
		if(headsetFile.getName().contains(".tsv")){
			encoding = "UTF-16";
			delimiter = "\t";
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(headsetFile), encoding));

		line = in.readLine().split(delimiter);
		
		//Add Recording Time, which in the export will be converted to 11:03:35.787 format
		dataListModel.addElement("Recording Time");
		
		//Add all rows to dataList
		for (String columnName : line) {
			
			//Acc X, Y and Z is unnecessary
			if (!columnName.contains("Acc") && !columnName.contains("time")) {
				dataListModel.addElement(columnName);
				
			}

		}		
		
		in.close();
		
		//For GSR
		dataListModel.addElement("BVP.csv");
		dataListModel.addElement("EDA.csv");
		dataListModel.addElement("HR.csv");
		dataListModel.addElement("TEMP.csv");
		
		
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
	
	//Function that calculates and updates the offset
	public double calculateOffset() {
		
		//Extracts features from data
		List<Double> extractedHead = (new DataHandler()).extract(headsetData, stepLength);
		List<Double> extractedGSR = (new DataHandler()).extract(gsrData, stepLength);
		
		System.out.println("headsetData: " + headsetData.size());
		System.out.println("extractedHead: " + extractedHead.size());
		System.out.println("gsrData: " + gsrData.size());
		System.out.println("extractedGSR: " + extractedGSR.size());
		
		
		int headStart = 0;
		int gsrStart = 0;
		
		//Finds start moving point for bothhttp://marketplace.eclipse.org/marketplace-client-intro?mpc_install=1403812 devices and updates offset value
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
		
		//Draws movement start line for both types of data
		List<Double> movePoint = new ArrayList<Double>();
		for (int i = 0; i < (headStart - 1)/(stepLength/1000); i++) movePoint.add(0.0);
		movePoint.add(maxVal);
		drawDiagram(movePoint, "Headset movement", false);
		movePoint = new ArrayList<Double>();
		for (int i = 0; i < (gsrStart - 1)/(stepLength/1000); i++) movePoint.add(0.0);
		movePoint.add(maxVal);
		drawDiagram(movePoint, "GSR movement", false);
		
		//Updates offset
		offset = gsrStart - headStart;
		return offset;
		
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
		frmDataanalyzer.getContentPane().setBackground(UIManager.getColor("Button.background"));
		frmDataanalyzer.setType(Type.UTILITY);
		frmDataanalyzer.setTitle("DataExtractor");
		frmDataanalyzer.setBounds(100, 100, 1214, 652);
		frmDataanalyzer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDataanalyzer.getContentPane().setLayout(null);
		
		final JLabel lblOffset = new JLabel("Offset: Unknown");
		lblOffset.setVerticalAlignment(SwingConstants.TOP);
		lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
		lblOffset.setBounds(367, 80, 118, 16);
		frmDataanalyzer.getContentPane().add(lblOffset);
		
		JLabel lblSynkronisering = new JLabel("Calculate offset");
		lblSynkronisering.setHorizontalAlignment(SwingConstants.CENTER);
		lblSynkronisering.setBounds(187, 18, 121, 16);
		frmDataanalyzer.getContentPane().add(lblSynkronisering);
		
		JButton btnNewButton = new JButton("Headset File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					//GSR data has been read
					headsetRead = true;
					File file = fileChooser.getSelectedFile();
					
					//Update gsrPath to folder
				    headsetPath = fileChooser.getSelectedFile().toString();

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
					
					//Updates offset if both files have been read
					if (headsetRead && gsrRead) {
						//calculateOffset();
						lblOffset.setText("Offset: " + offset + " s");
						headsetRead = false;
						gsrRead = false;
						//Resets graph
						datasets = new XYSeriesCollection();
						//Update data selection list
						try {
							populateListview();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					System.out.println("Headset data read...");
					
				}
				
			}
		});
		btnNewButton.setBounds(12, 76, 121, 25);
		frmDataanalyzer.getContentPane().add(btnNewButton);
		
		JButton btnGSRFile = new JButton("GSR Folder");
		btnGSRFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select GSR folder");
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fileChooser.setAcceptAllFileFilterUsed(false);
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					
					//GSR data has been read
					gsrRead = true;
				    
					//Update gsrPath to folder
				    gsrPath = fileChooser.getSelectedFile().toString();
				    
				    //gsrFile is pointing to acceleration data on the GSR
				    File gsrFile = new File(gsrPath + "\\ACC.csv");
				    
					
					try {
						gsrData = (new DataHandler()).accMag(gsrFile, Integer.parseInt(textField_3.getText()), Integer.parseInt(textField_1.getText()));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					drawDiagram((new DataHandler()).extract(gsrData, 5000), "GSR", false);
					
					//Updates offset if both files have been read
					if (headsetRead && gsrRead) {
						calculateOffset();
						lblOffset.setText("Offset: " + offset + " s");
						headsetRead = false;
						gsrRead = false;
						//Resets graph
						datasets = new XYSeriesCollection();
						//Update data selection list
						try {
							populateListview();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
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
		
		JLabel lblNewLabel = new JLabel("Data to export");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 160, 473, 22);
		frmDataanalyzer.getContentPane().add(lblNewLabel);
		
		JLabel lblPeriod = new JLabel("Duration");
		lblPeriod.setHorizontalAlignment(SwingConstants.CENTER);
		lblPeriod.setBounds(9, 365, 121, 25);
		frmDataanalyzer.getContentPane().add(lblPeriod);
		
		txtS = new JTextField();
		txtS.setHorizontalAlignment(SwingConstants.CENTER);
		txtS.setText("00:00:10");
		txtS.setBounds(142, 364, 121, 25);
		frmDataanalyzer.getContentPane().add(txtS);
		txtS.setColumns(10);
		
		JLabel lblTo = new JLabel("to");
		lblTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTo.setBounds(269, 365, 93, 25);
		frmDataanalyzer.getContentPane().add(lblTo);
		
		txtS_1 = new JTextField();
		txtS_1.setText("00:10:02");
		txtS_1.setHorizontalAlignment(SwingConstants.CENTER);
		txtS_1.setColumns(10);
		txtS_1.setBounds(364, 364, 121, 25);
		frmDataanalyzer.getContentPane().add(txtS_1);
		
		JLabel lblChunkSize = new JLabel("Chunk Size");
		lblChunkSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblChunkSize.setBounds(9, 400, 121, 25);
		frmDataanalyzer.getContentPane().add(lblChunkSize);
		
		txtS_2 = new JTextField();
		txtS_2.setText("00:00:05");
		txtS_2.setHorizontalAlignment(SwingConstants.CENTER);
		txtS_2.setColumns(10);
		txtS_2.setBounds(142, 404, 121, 25);
		frmDataanalyzer.getContentPane().add(txtS_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.BLACK);
		panel_2.setBounds(12, 326, 476, 1);
		frmDataanalyzer.getContentPane().add(panel_2);
		
		JLabel lblExportSettings = new JLabel("Export Settings");
		lblExportSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblExportSettings.setBounds(9, 449, 476, 22);
		frmDataanalyzer.getContentPane().add(lblExportSettings);
		
		JLabel lblFileInput = new JLabel("Timing");
		lblFileInput.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileInput.setBounds(9, 332, 476, 22);
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
		chckbxNewCheckBox.setBounds(9, 509, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxNewCheckBox);
		
		JLabel lblDataType = new JLabel("Data type");
		lblDataType.setHorizontalAlignment(SwingConstants.CENTER);
		lblDataType.setBounds(12, 478, 121, 22);
		frmDataanalyzer.getContentPane().add(lblDataType);
		
		JLabel lblColumn = new JLabel("Relative Column");
		lblColumn.setHorizontalAlignment(SwingConstants.CENTER);
		lblColumn.setBounds(145, 478, 121, 22);
		frmDataanalyzer.getContentPane().add(lblColumn);
		
		textField_4 = new JTextField();
		textField_4.setHorizontalAlignment(SwingConstants.CENTER);
		textField_4.setText("1");
		textField_4.setBounds(142, 511, 121, 22);
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
		chckbxMaxiumValue.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxMaxiumValue.setBounds(9, 569, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxMaxiumValue);
		
		textField_5 = new JTextField();
		textField_5.setText("3");
		textField_5.setHorizontalAlignment(SwingConstants.CENTER);
		textField_5.setColumns(10);
		textField_5.setBounds(142, 571, 121, 22);
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
		chckbxSkewness.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxSkewness.setBounds(275, 539, 87, 25);
		frmDataanalyzer.getContentPane().add(chckbxSkewness);
		
		textField_6 = new JTextField();
		textField_6.setText("5");
		textField_6.setHorizontalAlignment(SwingConstants.CENTER);
		textField_6.setColumns(10);
		textField_6.setBounds(364, 542, 121, 22);
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
		chckbxStandardDeviation.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxStandardDeviation.setBounds(275, 509, 87, 25);
		frmDataanalyzer.getContentPane().add(chckbxStandardDeviation);
		
		textField_7 = new JTextField();
		textField_7.setText("4");
		textField_7.setHorizontalAlignment(SwingConstants.CENTER);
		textField_7.setColumns(10);
		textField_7.setBounds(364, 512, 121, 22);
		frmDataanalyzer.getContentPane().add(textField_7);
		
		JButton btnExportData = new JButton("Export Data");
		btnExportData.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
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
			    	
			    	//Fetching saving information
					newFile = chooser.getSelectedFile();
			    	path = newFile.getAbsolutePath();
			    	path = path.substring(0, path.lastIndexOf(File.separator)) + "\\";
			    	fileName = newFile.getName() + ".csv";
				
					//Loop through all selected items and write each to output
					System.out.println("Looping");
					int writeColumn = 0;
					for(int i = 0; i < selectList.getModel().getSize(); i++) {
						
						String name = (String) selectList.getModel().getElementAt(i);
						if (name.equals("Recording Time")) name = "Recording start time";
						
						String columnName = name;
						
						//Sequence time restrains
						int start = (new DataHandler()).convertTimestamp(txtS.getText());
						int end = (new DataHandler()).convertTimestamp(txtS_1.getText());;
						int chunkSize = (new DataHandler()).convertTimestamp(txtS_2.getText());
						
			            if (!name.contains("csv")) {
			            	
			            	//Retrieves path and file
			            	String tempPath = headsetPath;
			            	System.out.println(tempPath);
			            	File tempFile = new File(tempPath);
			            	
				            try {
				            	//Get column number from name
								name = Integer.toString((new FileHandler()).getColumnNumber(name, headsetPath));
								//Read column to check if it contains strings or numbers
								List<String> tempData = new ArrayList<String>();
								tempData = (new FileHandler()).getColumn(tempFile, Integer.parseInt(name));
								//Check first value (second row) to check if it contains number or string
								if (tempData.get(1).matches(".*\\d+.*")) {
									
									//Writes latest read string to appropriate row
									/*
									 * Code to handle string column
									 */
									/*List<String> finalData = new ArrayList<String>();
									String lastString = "";
									for (int a = start; a < end; a += chunkSize) {
										
										//If string not is empty write to finalData
										if (!tempData.get(a).equals("")) lastString = tempData.get(a);
										finalData.add(lastString);
										
									}
									
									//Writes time to file
									try {
										(new FileHandler()).exportData(finalData, path, fileName, columnName, writeColumn);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									//Updates write column
									writeColumn++;
									*/
									continue;
									
								} else {
									//Number array export using getRecColumn which returns an appropriate double dataList
									data = (new FileHandler()).getRecColumn(tempFile, Integer.parseInt(name));
								}
								continue;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            } else {
			            	
			            	//Retrieves path and file
			            	String tempPath = gsrPath + "\\" + name;
			            	File tempFile = new File(tempPath);
			            	
			            	try {
								data = (new DataHandler()).dataFetch(tempFile);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		
			            }
			            
			            System.out.println("Writing: " + columnName);
						
						//If time data
						if (columnName.equals("Recording start time")) {
							
							//Get start time in milliseconds
							File headsetFile = new File(headsetPath);
							List<String> time = new ArrayList<String>();
							try {
								time = (new FileHandler()).getColumn(headsetFile, Integer.parseInt(name), 2);
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							String timeString = time.get(2);
							
							int currentTime = 0;
							currentTime += Integer.parseInt(timeString.split(":")[0])*60*60*1000;
							currentTime += Integer.parseInt(timeString.split(":")[1])*60*1000;
							currentTime += (int) (Double.parseDouble(timeString.split(":")[2])*1000.0);
							
							System.out.println(currentTime);
							
							//Reset time array
							time = new ArrayList<String>();
								
							int currentChunkEnd = 0;
							for (int a = start; a < end; a += chunkSize) {
								//Formats millisecond time to XX:XX:XX.xx format
								int hours = (int) Math.floor(currentTime/(60*60*1000));
								int seconds = currentTime % (60*1000);
								int minutes = (currentTime - seconds - hours*60*60*1000)/(1000*60);
								timeString = "";
								if (hours % 24 < 10) timeString += "0";
								timeString += Integer.toString(hours % 24);	
								timeString += ":";
								if (minutes % 60 < 10) timeString += "0";
								timeString += Integer.toString(minutes % 60);	
								timeString += ":";
								if ((seconds/1000.0) < 10) timeString += "0";
								System.out.println(seconds);
								timeString += Double.toString(seconds/1000.0);	
								time.add(timeString);
								currentTime += chunkSize;
								System.out.println(timeString);
							}
							
							//Writes time to file
							try {
								(new FileHandler()).exportData(time, path, fileName, "Recording Time", writeColumn);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							//Updates write column
							writeColumn++;
							
							continue;
							
						}
						
						List<Double> average = new ArrayList<Double>();
						List<Double> minVal = new ArrayList<Double>();
						List<Double> maxVal = new ArrayList<Double>();
						List<Double> stdev = new ArrayList<Double>();
						List<Double> skewness = new ArrayList<Double>();
						
						//Goes through the chunks and extract all types of values for each chunk
						int currentChunkEnd = 0;
						for (int a = start; a < end; a += chunkSize) {
							
							if ((a + chunkSize) < end) {
								currentChunkEnd = a + chunkSize;
							} else {
								currentChunkEnd = end;
							}
							
							try {
								
								//Fetches all values and takes offset into consideration
								if (averageEnabled) average.add((new DataHandler()).average(data, a - offset, currentChunkEnd));
								if (minValEnabled) minVal.add((new DataHandler()).minVal(data, a - offset, currentChunkEnd));
								if (maxValEnabled) maxVal.add((new DataHandler()).maxVal(data, a - offset, currentChunkEnd));
								if (stdevEnabled) stdev.add((new DataHandler()).stdev(data, a - offset, currentChunkEnd));
								if (skewnessEnabled) skewness.add((new DataHandler()).skewness(data, a - offset, currentChunkEnd));
								
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
				    	
				    	//Exports data to specificed columns in specified file
						try {
							boolean firstDraw = true;  
							if (averageEnabled) {
								(new FileHandler()).exportData(average, path, fileName, columnName + " (average)", Integer.parseInt(textField_4.getText()) - 1 + writeColumn);
								//drawDiagram(average, "Average", firstDraw);
								firstDraw = false;
							}
							if (minValEnabled) {
								(new FileHandler()).exportData(minVal, path, fileName, columnName + " (minVal)", Integer.parseInt(textField_8.getText())  - 1 + writeColumn);
								//drawDiagram(minVal, "Min Value", firstDraw);
								firstDraw = false;
							}
							if (maxValEnabled) {
								(new FileHandler()).exportData(maxVal, path, fileName, columnName + " (maxVal)",  Integer.parseInt(textField_5.getText())  - 1 + writeColumn);
								//drawDiagram(maxVal, "Max Value", firstDraw);
								firstDraw = false;
							}
							if (stdevEnabled) {
								(new FileHandler()).exportData(stdev, path, fileName, columnName + " (stdev)", Integer.parseInt(textField_7.getText())  - 1 + writeColumn);
								//drawDiagram(stdev, "Standard Deviation", firstDraw);
								firstDraw = false;
							}
							if (skewnessEnabled) {
								(new FileHandler()).exportData(skewness, path, fileName, columnName + " (skewness)", Integer.parseInt(textField_6.getText())  - 1 + writeColumn);
								//drawDiagram(skewness, "Skewness", firstDraw);
								firstDraw = false;
							}
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//Correct column number when handling multiple files
						writeColumn += (averageEnabled) ? 1 : 0;
						writeColumn += (minValEnabled) ? 1 : 0;
						writeColumn += (maxValEnabled) ? 1 : 0;
						writeColumn += (stdevEnabled) ? 1 : 0;
						writeColumn += (skewnessEnabled) ? 1 : 0;
						
				    	System.out.println("File exported...");
							
					}
						
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
		chckbxMinxValue.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxMinxValue.setBounds(9, 539, 121, 25);
		frmDataanalyzer.getContentPane().add(chckbxMinxValue);
		
		textField_8 = new JTextField();
		textField_8.setText("2");
		textField_8.setHorizontalAlignment(SwingConstants.CENTER);
		textField_8.setColumns(10);
		textField_8.setBounds(142, 541, 121, 22);
		textField_8.getDocument().addDocumentListener(new DocumentListener() {
			
			public void changedUpdate(DocumentEvent e) {
				changed();
			}
			public void removeUpdate(DocumentEvent e) {
				changed();
			}
			public void insertUpdate(DocumentEvent e) {
				changed();
			}

			public void changed() {
				/*if (!textField_8.getText().equals("") && (textField_8.getText().equals("-")) || Integer.parseInt(textField_8.getText()) < 1) {
						JOptionPane.showMessageDialog(null, "Column number needs to be greater than 0.");
				}*/
			}
			  
		});
		frmDataanalyzer.getContentPane().add(textField_8);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.BLACK);
		panel_3.setBounds(9, 440, 476, 1);
		frmDataanalyzer.getContentPane().add(panel_3);
		
		JButton btnNewButton_1 = new JButton("\u2192");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//If some data is selected in data list
				if (dataList.getSelectedValue() != null) {
	
					//Remove "Empty" item from list if it exists
					if (selectListModel.getElementAt(0).equals("Empty")) selectListModel.removeAllElements();
					
					//Add element to select list
					selectListModel.addElement(dataList.getSelectedValue().toString());
					
					//Remove item from data list
					dataListModel.remove(dataList.getSelectedIndex());
					dataList.setModel(dataListModel);
					
				}
				
			}
		});
		
		btnNewButton_1.setBounds(219, 239, 59, 25);
		frmDataanalyzer.getContentPane().add(btnNewButton_1);
		
		//Create data list
		dataListModel = new DefaultListModel();
		dataListModel.addElement("Empty");
		dataList = new JList(dataListModel); //data has type Object[]
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dataList.setLayoutOrientation(JList.VERTICAL);
		dataList.setVisibleRowCount(-1);
		dataScrollPane = new JScrollPane(dataList);
		dataScrollPane.setBounds(12, 190, 195, 117);
		frmDataanalyzer.getContentPane().add(dataScrollPane);
		
		dataList.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent e) {
	            if (e.getValueIsAdjusting()) {
	            	System.out.println(dataList.getSelectedValue().toString());
	        	}
	        }
	    });
		
		//Create selection list
		selectListModel = new DefaultListModel();
		selectListModel.addElement("Empty");
		selectScrollPane = new JScrollPane((Component) null);
		selectScrollPane.setBounds(290, 190, 195, 117);
		frmDataanalyzer.getContentPane().add(selectScrollPane);
		
		selectList = new JList(selectListModel);
		selectList.setVisibleRowCount(-1);
		selectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectList.setLayoutOrientation(JList.VERTICAL);
		selectList.setVisibleRowCount(-1);
		selectScrollPane.setViewportView(selectList);
		
	}
}
