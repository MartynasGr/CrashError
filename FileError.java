package crashRead;

import java.util.ArrayList;

public class FileError {
	public String hardware;
	public String pc;
	public int count;
	public ArrayList<String> errors;
	FileError(ArrayList<String> errors){
		count=1;
		this.errors=errors;
	}
	FileError(){
		
		count=1;
	}
}
