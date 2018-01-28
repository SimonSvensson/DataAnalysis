import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
	
	public List<String> getColumn(File file, int columnNumber) throws FileNotFoundException, IOException {
		
		List<String> columnData = new ArrayList<String>();
		
		String line, encoding = "UTF-8", delimiter = ","; // Default is .csv
		if(file.getName().contains(".tsv")){
			encoding = "UTF-16";
			delimiter = "\t";
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

		while ((line = in.readLine()) != null){
			if (line.split(delimiter).length > columnNumber) {
				columnData.add(line.split(delimiter)[columnNumber]);
			} else {
				columnData.add(null);
			}
		}
		
		in.close();
		return columnData;
		
    }
	
	//Same function as getColumn but it also lets the user to specify how many item to fetch
	public List<String> getColumn(File file, int columnNumber, int fetchSize) throws FileNotFoundException, IOException {
		
		List<String> columnData = new ArrayList<String>();
		
		String line, encoding = "UTF-8", delimiter = ","; // Default is .csv
		if(file.getName().contains(".tsv")){
			encoding = "UTF-16";
			delimiter = "\t";
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

		int count = 0;
		while ((line = in.readLine()) != null){
			if (line.split(delimiter).length > columnNumber) {
				columnData.add(line.split(delimiter)[columnNumber]);
			} else {
				columnData.add(null);
			}
			count++;
			if (count > fetchSize) {
				in.close();
				return columnData;
			}
		}
		
		in.close();
		return columnData;
		
    }
	
	/* --------------------------------------------------------------------- */
	/* File functions */
	
	//Deletes a file
	public void deleteFile(String Path, String fileName) throws IOException {
		
		File file = new File(Path + fileName);
		Files.deleteIfExists(file.toPath());
		
	}
	
	//Exports data list to specificed column in a CSV file with given parameters 
	public void exportData(List<Double> data, String path, String fileName, int columnNumber) throws IOException {
		
		//Reads in old data to be able to determine how to insert data into the correct column
		List<String> oldData = new ArrayList<String>();
		File file = new File(path + fileName);
		
		//Creates a new empty file if no exist
		if (!file.exists()) file.createNewFile();
		
		String line, encoding = "UTF-8";
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));

		//Gets number of seperators, or ,
		while ((line = in.readLine()) != null){
			oldData.add(line);
		}
		
		in.close();
		
		try {
		
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			
			//Writes til the longest list is complete
			for (int i = 0; i < Math.max(oldData.size(), data.size()); i++) {
				
				//For each row 
				int numberOfColumns = 0;
				String currentLine = "";
				String newData = "";
				
				//Get old line data
				if (i >= 0 && i < oldData.size() && oldData.get(i) != null) {
					currentLine += oldData.get(i);
				} else {
					currentLine += "";
				}
				
				//Get new data
				if (i >= 0 && i < data.size() && data.get(i) != null) {
					newData += data.get(i);
				} else {
					newData += "";
				}
				
				//Gets number of columns at given line
				if (currentLine == "") {
					numberOfColumns = 0;
				} else {
					numberOfColumns = currentLine.length() - currentLine.replace(",", "").length();
				}
				
				//Inserts data into line 
				String newString = "";
				if (columnNumber > numberOfColumns) {
					//If columnNumber is larger than number of existing number, then you just add right amount of ,
					int seperatorsToAdd = columnNumber - numberOfColumns;
					newString = currentLine + new String(new char[seperatorsToAdd]).replace("\0", ",") + newData;
				} else {
					if (columnNumber == 0) {
						//Adds data value first and the the rest from old
						newString += newData;
						if (currentLine.indexOf(",") != -1) newString += currentLine.substring(currentLine.indexOf(","), currentLine.length());
					} else if (columnNumber == data.size()) {
						//Adds up to last , from old and then data value
						newString += currentLine.substring(i, currentLine.lastIndexOf(","));
						newString += newData;
					} else {
						//Finds index of Nth occurence of , and then insert and then adds the rest
						//Finds end index of first part
						String tempString = currentLine;
						int index = 0;
						for (int a = 0; a < columnNumber; a++) {
							int tempLength = tempString.length();
							tempString = tempString.substring(tempString.indexOf(",") + 1, tempString.length());
							index += tempLength - tempString.length();
						}
						newString += currentLine.substring(0, index);
						//Adds data
						newString += newData;
						//Finds start index of second part
						int tempLength = tempString.length();
						tempString = tempString.substring(tempString.indexOf(","), tempString.length());
						index += tempLength - tempString.length();
						newString += currentLine.substring(index, currentLine.length());
						
					}
				}
				
				//Writes final string to file
				writer.println(newString);
			
			}
			
			writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
