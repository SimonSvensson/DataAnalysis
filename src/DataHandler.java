import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataHandler {

	public List<Double> accelerometerMagnitude(List<String> time, List<String> x, List<String> y, List<String> z, double conversionFactor) { //headset
		
		List<Double> accMag = new ArrayList<Double>();
		
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
	
public List<Double> accelerometerMagnitude(List<String> x, List<String> y, List<String> z, int sampleRate, double conversionFactor) { //gsr
		
		List<Double> accMag = new ArrayList<Double>();
		
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
	
	public List<Double> accMag(File file, int accStartColumn) throws FileNotFoundException, IOException { //headset
		
		return accMag(file, accStartColumn, -1);
	}
	
	public List<Double> accMag(File file, int accStartColumn, int sampleRate) throws FileNotFoundException, IOException { //gsr
		
		List<String> time = null;
		List<Double> accelerationMag;
		
		if(sampleRate == -1){
			time = (new FileHandler()).getColumn(file, 3);
		}
		
		List<String> x = (new FileHandler()).getColumn(file, accStartColumn);		
		List<String> y = (new FileHandler()).getColumn(file, accStartColumn + 1);
		List<String> z = (new FileHandler()).getColumn(file, accStartColumn + 2);
		
		if(sampleRate > -1) // gsr
			accelerationMag = accelerometerMagnitude(x,y,z, sampleRate, (9.81/64));
		else 				// headset
			accelerationMag = accelerometerMagnitude(time, x,y,z, 1);
		
		return accelerationMag;
		
	}
	
	public double calcOffset(List<Double> head, List<Double> hand) throws FileNotFoundException, IOException {
		
		double headThreshold = 0.7;
		double handThreshold = 0.7;
		
		double headAverage = average(head, 0, head.size() - 1);
		//System.out.println("head.size: " + head.size());
		//System.out.println("Average head: " + headAverage);
		double handAverage = average(hand, 0, hand.size() - 1);
		//System.out.println("Average hand: " + handAverage);
		
		int pos = 0;
		int searchRange = 500;
		int still = 0;
		double sens = 0.15;
		int stillCount = 0;
		
		while (pos < hand.size()) {
			
			double avg = 0;
			int count = 0;
			double largest = 0;
			for (int i = (pos - searchRange); i < (pos + searchRange); i++) {
				if (i >= 0 && i < hand.size()) {
					if (hand.get(i) != null) {
						//avg += Math.abs(headAverage-head.get(i));
						//count++;
						double tempDiff = Math.abs(handAverage-hand.get(i));
						if (tempDiff > largest) largest = tempDiff;
					}
				}
			}
			//avg/=count;
			
			double diff = largest;
			//double diff = Math.abs(headAverage-avg);
			if (diff < sens && still == 0) {
				still = 1;
				stillCount++;
				System.out.println(pos);
				System.out.println("stillcount: " + stillCount);
			} 
			if (diff > sens && still == 1) {
				still = 0;
				//System.out.println(pos);
				//System.out.println("move");
			}
			
			pos++;
			
		}
		
		return 0.0;
		
		//In milliseconds
		/*int searchRange = 10;
		
		double averageValHead = 0;
		int headPos = searchRange;
		while (averageValHead < headThreshold && headPos < 1000*10) {
			
			double sum = 0;
			int count = 0;
			for (int i = (headPos - searchRange); i < (headPos + searchRange); i++) {
				
				if (head.get(i) != null) {
					sum += Math.abs(head.get(i) - headAverage);
					count++;
				}
				
			}
			
			averageValHead = sum/((double) count);
			headPos++;
			System.out.println("sum: " + sum);
			System.out.println("averageValHead: " + averageValHead);
			System.out.println("Headpos: " + headPos);
			
		}
		
		double averageValHand = 0;
		int handPos = searchRange;
		while (averageValHand < handThreshold && handPos < 1000*10) {
			
			double sum = 0;
			int count = 0;
			for (int i = (handPos - searchRange); i < (handPos + searchRange); i++) {
				
				if (hand.get(i) != null) {
					sum += Math.abs(hand.get(i) - handAverage);
					count++;
				}
				
			}
			
			averageValHand = sum/count;
			handPos++;
			
		}
		
		//Returns hand offset compared to headpos
		return handPos - headPos;*/
		
	}
	
	public double average(List<Double> data, int a, int b) throws FileNotFoundException, IOException {
		
		double sum = 0;
		int count = 0;
		for(int i = a; i < b; i++) {
			
			if (data.get(i) != null) {
				sum += data.get(i);
				count++;
			}
			
		}
		
		return sum/count;
		
	}
	
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

}
