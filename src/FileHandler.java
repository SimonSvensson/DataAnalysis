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
		
		if (file.getName().contains(".csv")) {
			
			String dataRow ;
			BufferedReader CSVFile = new BufferedReader(new FileReader(file));
		    while ((dataRow = CSVFile.readLine()) != null) {
		    	columnData.add(dataRow.split(",")[columnNumber]);
		    }
			
		} else if (file.getName().contains(".tsv")) {
		
			BufferedReader in = new BufferedReader(
		           new InputStreamReader(new FileInputStream(file), "UTF-16"));
		
			String line;
			
			while ((line = in.readLine()) != null) {
				
				if (line.split("\t").length > columnNumber) {
					
					columnData.add(line.split("\t")[columnNumber]);
					
				} else {
					
					columnData.add(null);
					
				}
				
			}
			
		}

		return columnData;

    }

}
