import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JToolBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import java.awt.Canvas;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JCheckBox;


public class Window {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	
	private FileHandler fileHandler;
	private JTextField textField_2;
	private JTextField textField_3;
	
	List<Double> gsvData = new ArrayList<Double>();
	List<Double> headsetData = new ArrayList<Double>();
	private JTextField txtS;
	private JTextField txtS_1;
	private JTextField txtS_2;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;

	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1214, 871);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblSynkronisering = new JLabel("Calculate offset");
		lblSynkronisering.setHorizontalAlignment(SwingConstants.CENTER);
		lblSynkronisering.setBounds(187, 18, 121, 16);
		frame.getContentPane().add(lblSynkronisering);
		
		JButton btnNewButton = new JButton("Headset File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					try {
						headsetData = (new DataHandler()).headsetAccMag(file, Integer.parseInt(textField_2.getText()));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("Headset data done..");
					
				}
				
			}
		});
		btnNewButton.setBounds(12, 76, 121, 25);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnGsvFile = new JButton("GSV File");
		btnGsvFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					
					try {
						gsvData = (new DataHandler()).gsrAccMag(file, Integer.parseInt(textField_3.getText()), Integer.parseInt(textField_1.getText()));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					System.out.println("GSV data done..");
					
				}
				
			}
		});
		btnGsvFile.setBounds(12, 113, 121, 25);
		frame.getContentPane().add(btnGsvFile);
		
		JLabel lblSampleRate = new JLabel("Sample rate");
		lblSampleRate.setHorizontalAlignment(SwingConstants.CENTER);
		lblSampleRate.setBounds(145, 46, 87, 16);
		frame.getContentPane().add(lblSampleRate);
		
		final JLabel lblOffset = new JLabel("Offset: ");
		lblOffset.setVerticalAlignment(SwingConstants.TOP);
		lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
		lblOffset.setBounds(367, 116, 118, 16);
		frame.getContentPane().add(lblOffset);
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setText("120");
		textField.setBounds(145, 74, 87, 25);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setHorizontalAlignment(SwingConstants.CENTER);
		textField_1.setText("32");
		textField_1.setColumns(10);
		textField_1.setBounds(145, 111, 87, 25);
		frame.getContentPane().add(textField_1);
		
		JButton btnNewButton_1 = new JButton("Calculate");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				File file = new File("C:/Users/Rasmus Lindahl/Desktop/data.csv");
				try {
					
					boolean result = Files.deleteIfExists(file.toPath());
					
					PrintWriter writer = new PrintWriter(file, "UTF-8");
					for (int i = 0; i < 50000; i++) {
						
						Double hData = headsetData.get(i);
						Double gData = gsvData.get(i);
						
						writer.println(hData+","+gData);
					}
					writer.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("Writing data done..");
				
				try {
					
					double offset = (new DataHandler()).calcOffset(headsetData, gsvData);
					lblOffset.setText(lblOffset.getText() + offset);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		btnNewButton_1.setBounds(367, 76, 121, 25);
		frame.getContentPane().add(btnNewButton_1);
		
		JLabel lblStartColumnAcc = new JLabel("Acc Start Column");
		lblStartColumnAcc.setHorizontalAlignment(SwingConstants.CENTER);
		lblStartColumnAcc.setBounds(244, 46, 111, 16);
		frame.getContentPane().add(lblStartColumnAcc);
		
		textField_2 = new JTextField();
		textField_2.setText("7");
		textField_2.setHorizontalAlignment(SwingConstants.CENTER);
		textField_2.setColumns(10);
		textField_2.setBounds(244, 74, 111, 25);
		frame.getContentPane().add(textField_2);
		
		textField_3 = new JTextField();
		textField_3.setText("0");
		textField_3.setHorizontalAlignment(SwingConstants.CENTER);
		textField_3.setColumns(10);
		textField_3.setBounds(244, 111, 111, 25);
		frame.getContentPane().add(textField_3);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setBounds(521, 42, 635, 362);
		frame.getContentPane().add(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.BLACK);
		panel_1.setBounds(12, 151, 476, 1);
		frame.getContentPane().add(panel_1);
		
		JLabel lblNewLabel = new JLabel("Export file");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(12, 270, 121, 22);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton_2 = new JButton("Choose");
		btnNewButton_2.setBounds(145, 270, 121, 25);
		frame.getContentPane().add(btnNewButton_2);
		
		JLabel lblPeriod = new JLabel("Duration");
		lblPeriod.setHorizontalAlignment(SwingConstants.CENTER);
		lblPeriod.setBounds(12, 198, 121, 25);
		frame.getContentPane().add(lblPeriod);
		
		txtS = new JTextField();
		txtS.setHorizontalAlignment(SwingConstants.CENTER);
		txtS.setText("10 s");
		txtS.setBounds(145, 197, 121, 25);
		frame.getContentPane().add(txtS);
		txtS.setColumns(10);
		
		JLabel lblTo = new JLabel("to");
		lblTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTo.setBounds(272, 198, 93, 25);
		frame.getContentPane().add(lblTo);
		
		txtS_1 = new JTextField();
		txtS_1.setText("100 s");
		txtS_1.setHorizontalAlignment(SwingConstants.CENTER);
		txtS_1.setColumns(10);
		txtS_1.setBounds(367, 197, 121, 25);
		frame.getContentPane().add(txtS_1);
		
		JLabel lblChunkSize = new JLabel("Chunk Size");
		lblChunkSize.setHorizontalAlignment(SwingConstants.CENTER);
		lblChunkSize.setBounds(12, 233, 121, 25);
		frame.getContentPane().add(lblChunkSize);
		
		txtS_2 = new JTextField();
		txtS_2.setText("10 s");
		txtS_2.setHorizontalAlignment(SwingConstants.CENTER);
		txtS_2.setColumns(10);
		txtS_2.setBounds(145, 237, 121, 25);
		frame.getContentPane().add(txtS_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.BLACK);
		panel_2.setBounds(12, 307, 476, 1);
		frame.getContentPane().add(panel_2);
		
		JLabel lblExportSettings = new JLabel("Export Settings");
		lblExportSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblExportSettings.setBounds(12, 307, 476, 22);
		frame.getContentPane().add(lblExportSettings);
		
		JLabel lblFileInput = new JLabel("File input");
		lblFileInput.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileInput.setBounds(12, 164, 476, 22);
		frame.getContentPane().add(lblFileInput);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Average");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxNewCheckBox.setBounds(12, 375, 127, 25);
		frame.getContentPane().add(chckbxNewCheckBox);
		
		JLabel lblDataType = new JLabel("Data type");
		lblDataType.setHorizontalAlignment(SwingConstants.CENTER);
		lblDataType.setBounds(12, 341, 121, 22);
		frame.getContentPane().add(lblDataType);
		
		JLabel lblColumn = new JLabel("Column");
		lblColumn.setHorizontalAlignment(SwingConstants.CENTER);
		lblColumn.setBounds(145, 341, 121, 22);
		frame.getContentPane().add(lblColumn);
		
		textField_4 = new JTextField();
		textField_4.setHorizontalAlignment(SwingConstants.CENTER);
		textField_4.setText("1");
		textField_4.setBounds(145, 377, 121, 22);
		frame.getContentPane().add(textField_4);
		textField_4.setColumns(10);
		
		JCheckBox chckbxMaxiumValue = new JCheckBox("Max Value");
		chckbxMaxiumValue.setSelected(true);
		chckbxMaxiumValue.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxMaxiumValue.setBounds(12, 405, 127, 25);
		frame.getContentPane().add(chckbxMaxiumValue);
		
		textField_5 = new JTextField();
		textField_5.setText("2");
		textField_5.setHorizontalAlignment(SwingConstants.CENTER);
		textField_5.setColumns(10);
		textField_5.setBounds(145, 407, 121, 22);
		frame.getContentPane().add(textField_5);
		
		JCheckBox chckbxSkewness = new JCheckBox("Skewness ");
		chckbxSkewness.setSelected(true);
		chckbxSkewness.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxSkewness.setBounds(12, 439, 127, 25);
		frame.getContentPane().add(chckbxSkewness);
		
		textField_6 = new JTextField();
		textField_6.setText("3");
		textField_6.setHorizontalAlignment(SwingConstants.CENTER);
		textField_6.setColumns(10);
		textField_6.setBounds(145, 441, 121, 22);
		frame.getContentPane().add(textField_6);
		
		JCheckBox chckbxStandardDeviation = new JCheckBox("Std Dev");
		chckbxStandardDeviation.setSelected(true);
		chckbxStandardDeviation.setHorizontalAlignment(SwingConstants.LEFT);
		chckbxStandardDeviation.setBounds(12, 472, 127, 25);
		frame.getContentPane().add(chckbxStandardDeviation);
		
		textField_7 = new JTextField();
		textField_7.setText("4");
		textField_7.setHorizontalAlignment(SwingConstants.CENTER);
		textField_7.setColumns(10);
		textField_7.setBounds(145, 474, 121, 22);
		frame.getContentPane().add(textField_7);
		
		JButton btnExportData = new JButton("Export Data");
		btnExportData.setBounds(903, 426, 253, 43);
		frame.getContentPane().add(btnExportData);
	}
}
