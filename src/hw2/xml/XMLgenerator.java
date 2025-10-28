package hw2.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class XMLgenerator {
	public static void makeXML(String filename,String XMLpacket) throws IOException {
		String root = System.getProperty("user.dir");

		Path path = Paths.get(root);
		
		path = path.resolve("XMLpacket").resolve(filename);
		
		File file = new File(path.toString());
		
		String contents =
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n"
			+ "<Packets>\r\n";
		
		contents += XMLpacket;
		
		contents += "</Packets>";
		
		if(file.exists()) file.delete();
		if(!file.exists()) file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter br = new BufferedWriter(fw);
		
		br.write(contents);
		br.close();
	}
}
