import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Verification {
	public static boolean verify(String fileName) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
		String line = new String();
		int entered = -1;
		int count =0;
               while((line = bufferedReader.readLine()) != null) {
			if(count%2==0&&line.contains("Enter")) {
				String lineSplit[] = line.split(" ");
				int processId = Integer.parseInt(lineSplit[4]);
				entered = processId;
				count++;
			}
			else if(count%2==1&&line.contains("Exit")){
				String lineSplit[] = line.split(" ");
				int processId = Integer.parseInt(lineSplit[4]);
				if(processId != entered) {
					return false;
				}
				count++;
			}
			else{
				return false;
			}	

		}
		System.out.println("Total number of lines "+count);
		return true;
	}
	public static void main(String args[]) throws IOException {
		String lamportLogFile = "/home/010/a/ax/axk153230/criticFile_lamport";
		String ricartaAgarwalaLogFile = "/home/010/a/ax/axk153230/criticFile_ricart";
		boolean status =false;
		System.out.println("The Lamport File Verification started");
		status = verify(lamportLogFile);
		System.out.println("The Lamport File Verification status "+ status);
		System.out.println("-------------");
		System.out.println("The Ricart Agarwala File Verification started");
		status = verify(ricartaAgarwalaLogFile);
		System.out.println("The Ricart Agarwala File Verification status "+ status);
	}
}
