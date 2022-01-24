package crashRead;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
	static int trace = 3;
	static String[] errorStrings = { "fail", "missing", "unable", "Fail", "Missing", "Unable", "Error", "error",
			"ERROR" };

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File folder = new File("C:\\Users\\marty\\OneDrive\\Dokumentai\\Stalinis kompiuteris\\crash\\crash");
		File[] listOfFiles = folder.listFiles();
		Map<String, ArrayList<FileError>> hm = new HashMap<String, ArrayList<FileError>>();
		for (File file : listOfFiles) {
			if (file.isFile()) {

				FileError errorFile = FindErrors(file);
				if (hm.containsKey(errorFile.hardware)&& errorFile.errors.size()!=0) {
					hm.get(errorFile.hardware).add(errorFile);
				} else if(errorFile.errors.size()!=0){
					ArrayList<FileError> errorFileL = new ArrayList<FileError>();
					errorFileL.add(errorFile);
					hm.put(errorFile.hardware, errorFileL);
				}

			}
		}
		// Map<String, Integer> types = new HashMap<String, Integer>();
		for (String name : hm.keySet()) {
			System.out.println(name);
			ArrayList<FileError> typeList = new ArrayList<FileError>();

			// Map<String, Integer> types = new HashMap<String, Integer>();
			ArrayList<FileError> errors = hm.get(name);
			// WriteFile(errors, name.toString());

			for (int i = 0; i < errors.size(); i++) {
				FileError key;
				// if (errors.get(i).lastIndexOf(']') + 2 > errors.get(i).length()) {
				key = errors.get(i);// .substring(errors.get(i).indexOf(']') + 1);
				// } else {
				// key = errors.get(i).substring(errors.get(i).lastIndexOf(']') + 1);
				// }
				boolean found = false;
				int count = 0;
				while (!found) {
					boolean match = true;
					if (typeList.size() != 0) {
						for (int j = 0; j < trace; j++) {
							if (!key.errors.get(j).equals(typeList.get(count).errors.get(j))) {
								match = false;
							}
						}
						if (match) {
							typeList.get(count).count++;
							break;
						}
					} /*else {
						key.count = 1;
						typeList.add(key);
					}*/
					count++;
					
					if (count >= typeList.size()) {
						typeList.add(new FileError(key.errors));
						break;
					}
				}
			}
			for (int z = 0; z < typeList.size(); z++) {
				System.out.println(typeList.get(z).count);
				for (int j = 0; j < trace; j++) {
					System.out.println(typeList.get(z).errors.get(j)+" ");
				}
			}
			// typeList.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);
		}
	}

	static FileError FindErrors(File file) throws FileNotFoundException, IOException {
		FileError errorFile = new FileError();
		ArrayList<String> errors = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			boolean cutHere = false;
			boolean pc = false;
			int count = 0;
			boolean hardwareName = false;
			while ((line = br.readLine()) != null) {
				if (line.contains("Hardware name:") && !hardwareName) {
					hardwareName = true;
					errorFile.hardware = line.substring(line.indexOf("Hardware name:") + 15);
				}
				if (!cutHere) {
					if (line.contains("cut here")) {
						cutHere = true;
					}
				} else {
					if (!pc) {
						if (line.contains("PC is at")) {
							errorFile.pc = line.substring(line.indexOf("PC is at ") + 9);
							//errors.add(errorFile.pc);
							pc = true;
						}
					} else {
						if (count == 0) {
							if (line.contains(errorFile.pc)) {
								errors.add(errorFile.pc);
								count = 1;
							}
						} else if (count != trace) {
							String first= line.substring(line.indexOf(']') + 1);
							
							errors.add(first.substring(first.indexOf(']') + 1));
							count++;
						}
					}
				}
			}
			if (count == 0) {
				System.out.println(file.getName());
			}
		}
		errorFile.errors = errors;
		return errorFile;
	}

	static void WriteFile(FileError errorFile) throws IOException {
		String fileName = (errorFile.hardware + ".txt").replaceAll("\\s+", "");
		// System.out.println(errorFile.hardware);
		File dir = new File("C:\\Users\\marty\\OneDrive\\Dokumentai\\Stalinis kompiuteris\\crash\\errors");
		File actualFile = new File(dir, fileName);

		FileWriter fw = new FileWriter(actualFile);
		fw.write(errorFile.hardware);
		for (int i = 0; i < errorFile.errors.size(); i++) {
			fw.write(errorFile.errors.get(i) + System.getProperty("line.separator"));
		}
		fw.close();
	}

	static void WriteFile(ArrayList<String> errors, String name) throws IOException {
		String fileName = (name + ".txt").replaceAll("\\s+", "").substring(13);
		// System.out.println(fileName);
		File dir = new File("C:\\Users\\marty\\OneDrive\\Dokumentai\\Stalinis kompiuteris\\crash");
		File actualFile = new File(dir, fileName);

		FileWriter fw = new FileWriter(actualFile);
		for (int i = 0; i < errors.size(); i++) {
			fw.write(errors.get(i) + System.getProperty("line.separator"));
		}
		fw.close();
	}
}
