import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class DataHandler {

	/* --------------------------------------------------------------------- */
	/* Following code is to extract both accelerometer 
	 * data from headset and the GSR. The "Calculate Offset" 
	 * section */

	//Headset accelermeter data extraction
	public List<Double> accelerometerMagnitude(List<String> time, List<String> x, List<String> y, List<String> z, double conversionFactor) { //headset
		
		List<Double> accMag = new ArrayList<Double>();
		
		//Using time to put the total magnitute data in the right place. 
		double xAcc, yAcc, zAcc, totalAcc;
		for (int i = 1; i < x.size(); i++) {
			
			if (Integer.parseInt(time.get(i)) != 0) {
				
				for (int j = 0; j < (Integer.parseInt(time.get(i)) - Integer.parseInt(time.get(i-1)) -1); j++) {
					accMag.add(null);
				}
			}
			
			if (x.get(i) == null || y.get(i) == null || z.get(i)  == null) {
				
				accMag.add(null);
				
			} else if (x.get(i).length() > 0 && y.get(i).length() > 0 && z.get(i).length() > 0) {
				
				xAcc = Double.parseDouble(x.get(i).replaceAll(",", "."))*conversionFactor;
				yAcc = Double.parseDouble(y.get(i).replaceAll(",", "."))*conversionFactor;
				zAcc = Double.parseDouble(z.get(i).replaceAll(",", "."))*conversionFactor;
				
				totalAcc = Math.sqrt(Math.pow(xAcc, 2) + Math.pow(yAcc, 2) + Math.pow(zAcc, 2));
				
				accMag.add(totalAcc);
				
			} 

		}
		
		return accMag;
		
	}
	
	//GSR extraction of accelerometer data
	public List<Double> accelerometerMagnitude(List<String> x, List<String> y, List<String> z, int sampleRate, double conversionFactor) { //gsr
		
		List<Double> accMag = new ArrayList<Double>();
		
		//Same as with the headset only that you have to multiply the acceleration with 9.81/64
		double xAcc, yAcc, zAcc, totalAcc;
		double pos = 0;
		int oldArrPos, arrPos;
		for (int i = 2; i < x.size(); i++) {
			
			if (x.get(i) == null || y.get(i) == null || z.get(i)  == null) {
				accMag.add(null);
			} else if (x.get(i).length() > 0 && y.get(i).length() > 0 && z.get(i).length() > 0) {
				xAcc = Double.parseDouble(x.get(i).replaceAll(",", "."))*conversionFactor;
				yAcc = Double.parseDouble(y.get(i).replaceAll(",", "."))*conversionFactor;
				zAcc = Double.parseDouble(z.get(i).replaceAll(",", "."))*conversionFactor;
				
				totalAcc = Math.sqrt(Math.pow(xAcc, 2) + Math.pow(yAcc, 2) + Math.pow(zAcc, 2));
				
				if (pos > 0) {
					
					oldArrPos = (int) Math.round(pos - (1000.0/sampleRate));
					arrPos = (int) Math.round(pos);
				
					for (int j = oldArrPos; j < (arrPos - 1); j++) {
						
						accMag.add(null);	
					}
				}
				accMag.add(totalAcc);
				pos += (1000.0/sampleRate);	
			}
		}
		return accMag;
	}
	
	//First function called when handling headset data. -1 is for clarifying that it is headset data
	public List<Double> accMag(File file, int accStartColumn) throws FileNotFoundException, IOException { 
		
		return accMag(file, accStartColumn, -1);
	}
	
	//This function handles both headset and GSR data. 
	public List<Double> accMag(File file, int accStartColumn, int sampleRate) throws FileNotFoundException, IOException { 
		
		List<String> time = null;
		List<Double> accelerationMag;

		if(sampleRate == -1){
			time = (new FileHandler()).getColumn(file, 3);
		}
		
		List<String> x = (new FileHandler()).getColumn(file, accStartColumn);
		List<String> y = (new FileHandler()).getColumn(file, accStartColumn + 1);
		List<String> z = (new FileHandler()).getColumn(file, accStartColumn + 2);
		
		//First case is for GSR and second for Headset
		if(sampleRate > -1) {
			accelerationMag = accelerometerMagnitude(x,y,z, sampleRate, (9.81/64));
		} else {
			accelerationMag = accelerometerMagnitude(time, x,y,z, 1);
		}
		
		return accelerationMag;
		
	}
	
	/* End of "Calculate Offset" section */
	/* --------------------------------------------------------------------- */
	
	/* --------------------------------------------------------------------- */
	/* This section contains general data extraction functions */
	
	//Returns average value between two pivots (indexes)
	public double average(List<Double> data, int a, int b) throws FileNotFoundException, IOException {
		
		double sum = 0;
		int count = 0;
		for(int i = a; i < b; i++) {
			
			//Only reads valid data
			if (i >= 0 && i < data.size() && data.get(i) != null) {
				sum += data.get(i);
				count++;
			}
			
		}
		
		return sum/count;
		
	}
	
	//Return value of max element between two pivots
	public double maxVal(List<Double> data, int a, int b) throws FileNotFoundException, IOException {
		
		int count = 0;
		double max = 0;
		for(int i = a; i < b; i++) {
			
			if (i >= 0 && i < data.size() && data.get(i) != null && data.get(i) > max) {
				max = data.get(i);
				count ++;
			}
			
		}
		
		return max;
		
	}
	
	//Return value of min element between two pivots
	public double minVal(List<Double> data, int a, int b) throws FileNotFoundException, IOException {
		
		double sum = 0;
		int count = 0;
		double min = 100000;
		for(int i = a; i < b; i++) {
			
			if (i >= 0 && i < data.size() && data.get(i) != null && data.get(i) < min) {
				min = data.get(i);
				count++;
			}
			
		}
		
		return min;
		
	}
	
	//Returns stdev value between two pivots
	public double stdev(List<Double> data, int a, int b) throws FileNotFoundException, IOException {
		
		double stdev = 0;
		
		double avg = average(data, a, b);
		int dataPoints = numberOfDatapoints(data, a, b);
		
		//Calculate sum of |x - avg|^2
		double distSum = 0;
		for(int i = a; i < b; i++) {
			if (i >= 0 && i < data.size() && data.get(i) != null) {
				distSum += Math.pow(Math.abs(data.get(i) - avg), 2);
			}
		}
		
		//Divides distSum by (n - 1) or n if value exists and takes the square root out of the result
		if (dataPoints > 1) {
			stdev = Math.sqrt(distSum/(dataPoints - 1));
		} else if (dataPoints == 1) {
			stdev = Math.sqrt(distSum);
		} else {
			stdev = 0.0;
		}
		
		return stdev;
		
	}
	
	//Returns skewness from sample between two pivots using this formula:
	//http://www.macroption.com/images/formulas/sample-skewness-formula-standard-deviation.png
	public double skewness(List<Double> data, int a, int b) throws FileNotFoundException, IOException {
		
		double skewness = 0;
		double avg = average(data, a, b);
		int dataPoints = numberOfDatapoints(data, a, b);
		
		//Calculates n/((n-1)(n-2))
		double firstFactor = 0;
		if (dataPoints > 2) firstFactor = dataPoints/((dataPoints-1)*(dataPoints-2));
		
		////Calculate distSum of |x - avg|^3
		double distSum = 0;
		for(int i = a; i < b; i++) {
			if (i >= 0 && i < data.size() && data.get(i) != null) {
				distSum += Math.pow(Math.abs(data.get(i) - avg), 3);
			}
		}
		
		//Divides distSum by stdev^3 to get second factor
		double secondFactor = distSum/Math.pow(stdev(data, a, b), 3);
		
		skewness = firstFactor * secondFactor;
		
		return secondFactor;
		
	}
	
	//Returns actual number of datapoints between two pivots
	public int numberOfDatapoints (List<Double> data, int a, int b) {
		
		int count = 0;
		for(int i = a; i < b; i++) {
			
			if (i >= 0 && i < data.size() && data.get(i) != null) {
				count++;
			}
			
		}
		
		return count;
		
	}
	
	//Finds the approximate time of when the studied user starts to move
	//searchRange works best at 10
	//diffLimit should be 0.6 for head and 0.8 for gsr
	//stepLength is used to calculate real position in array
	public int findStartMove(List<Double> data, int searchRange, double diffLimit, int stepLength) throws FileNotFoundException, IOException {
			
		for (int i = 5; i < data.size(); i++) {
			
			//In this case 12 will equal twelve pair of 5 second units = 60 sek
			double backward = (new DataHandler()).maxVal(data, i - searchRange, i);
			double forward = (new DataHandler()).maxVal(data, i, i + searchRange);
			double diff = forward - backward;
			
			//Around 0.3 when still and over 0.8 when starting to move
			if (diff > diffLimit) {
				return i*((int) (stepLength/1000));
			}
			
		}
		
		return 0;
		
	}
	
	//Extracts deviation from average and averages that data. Used when finding start of movement
	//stepLength should be 5000 for both devices
	public List<Double> extract(List<Double> data, int stepLength) {
		
		//List that will contain the data
		List<Double> extractedData = new ArrayList<Double>();
		
		//Gets average
		double avg = 0;
		try {
			avg = average(data, 0, data.size() - 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int averageRange = (int) stepLength/2;
		for (int i = 0; i < data.size(); i+=stepLength) {
			
			//Average data from averageRange back and forth relative to pivot point
			int count = 0;
			double sum = 0;
			for (int a = (i - averageRange); a < (i + averageRange); a++) {
				
				if (a >= 0 && a < data.size() && data.get(a) != null) {
					count++;
					sum += Math.abs(avg-data.get(a)); 
				}
				
			}
			
			//Adds extraced info to extracted data
			extractedData.add(sum/count);
			
		}
		
		return extractedData;
		
	}
	
	//Downsamples a given list with values from one sample rate to another
	public List<Double> downsample(List<Double> list, int from, int to) {
		
		List<Double> downsampled = new ArrayList<Double>();
		
		double offset = (double) from/to;
		double pos = 0;
		int position = 0;
		
		while (Math.round(pos) < list.size()) {
			position = (int) Math.round(pos);
			double sum = 0;
			double count = 0;
			
			for (int i = 0; i < Math.round(offset); i++) {
				int newPos = (int) Math.round(position - offset/2 + i);
				if (newPos >= 0 && list.get(newPos) != null) {
					sum += list.get(newPos);
					count++;
				}
			}
			
			if (count > 0) {
				downsampled.add((double) (sum/count));
			} else {
				downsampled.add(null);
			}
			pos+=offset;
			
		}
		
		return downsampled;
		
	}
	
	//Converts the timestamp to milliseconds and returns the results
	public int convertTimestamp(String time) {
		
		double timeInMilli = 0;
		
		String[] timeParts = time.split(":");
		
		//Starts at end of 00:00:00, multiplies by 60^(2-2), then reads second part and multiply by 60^(2-1)
		for (int i = timeParts.length - 1; i >= 0; i--) {
			timeInMilli += (Double.parseDouble(timeParts[i]) * Math.pow(60, timeParts.length - 1 - i))*1000;
		}
		
		return (int) timeInMilli;
		
	}
	
	/* End of general data extraction functions */
	/* --------------------------------------------------------------------- */
		
	/* --------------------------------------------------------------------- */
	/* This section contains data fetch functions */
	
	//General function to read all files. Reads file and converts data to correct format
	//where array position equal to milliseconds
	public List<Double> dataFetch(File file) throws FileNotFoundException, IOException { 
		
		List<String> tempArr = new ArrayList<String>();
		List<Double> finalArr = new ArrayList<Double>();
		int sampleRate = 0;
		int arrayPos = 0;
		double samplePos = 0;
		int startRow = 0;
		
		//Reads the correct parameters to handle file (BVP)
		if(file.getName().contains("ACC")){
			tempArr = (new FileHandler()).getColumn(file, 0);	
			sampleRate = (int) Double.parseDouble(tempArr.get(1));
			startRow = 2;
		}
		
		//Reads the correct parameters to handle file (BVP)
		if(file.getName().contains("BVP")){
			tempArr = (new FileHandler()).getColumn(file, 0);	
			sampleRate = (int) Double.parseDouble(tempArr.get(1));
			startRow = 2;
		}
		
		//Reads the correct parameters to handle file (EDA)
		if(file.getName().contains("EDA")){
			tempArr = (new FileHandler()).getColumn(file, 0);	
			sampleRate = (int) Double.parseDouble(tempArr.get(1));
			startRow = 3;
		}
		
		//Reads the correct parameters to handle file (HR) heartrate
		if(file.getName().contains("HR")){
			tempArr = (new FileHandler()).getColumn(file, 0);
			sampleRate = (int) Double.parseDouble(tempArr.get(1));
			startRow = 2;
		}
		
		//Reads the correct parameters to handle file (TEMP) temperature
		if(file.getName().contains("TEMP")){
			tempArr = (new FileHandler()).getColumn(file, 0);	
			//Limit temp to 100 because of 382.18. Sets every elemtent bigger than 100 to element 6
			//Should be approximately right
			for (int i = 0; i < tempArr.size(); i++) {
				if (tempArr.get(i) != null && Double.parseDouble(tempArr.get(i)) > 100) tempArr.set(i, tempArr.get(6));
			}
			sampleRate = (int) Double.parseDouble(tempArr.get(1));
			startRow = 2;
		}
		
		//Converts string to double and to correct format
		int i = startRow;
		while (i < tempArr.size()) {
			if (arrayPos == (int) samplePos) {
				finalArr.add(Double.parseDouble(tempArr.get(i)));
				samplePos+=1000.0/sampleRate;
				i++;
			} else {
				finalArr.add(null);
			}
			
			arrayPos++;
		}

		return finalArr;
		
	}
	
	/* End of general functions section */
	/* --------------------------------------------------------------------- */

}
