import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		return columnData;
    }
}
