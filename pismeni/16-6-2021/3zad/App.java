import java.util.Scanner;
import java.nio.file.*; //Path, Paths, Files
import java.io.*; //File
import java.util.*;

public class App{
	String ekstenzija;
	ArrayList<String> putanje = new ArrayList<>();  // ili ArrayzList<File> putanje, eventualno Path
	
	void obidji(File file){
		if(file.isFile() && file.getPath().endsWith(ekstenzija)){
			System.out.println(file);
			putanje.add(file.getPath()); 
		}else if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){			//niz ima for koji se ovako pise
				obidji(f);
			}
		}
	}
	
	public static void main(String[] args){
		if(args.length < 3){  		//.length bez ()
			System.exit(1);
		}
		
		App m = new App();
		m.ekstenzija = args[2];
		File rootDir = new File(args[0]); // Path pocetniPath = Paths.get(args[0]);   P.S. args[0] je tipa String
		System.out.println(rootDir);
		if(!rootDir.exists() || !rootDir.isDirectory())  		// !Files.isDirectory(pocetniPath)
			System.exit(1);
		
		m.obidji(rootDir);
		m.putanje.forEach(System.out::println); // ArrayList ima forEach i unutra lambda ili ovako napisana metoda!!!
		
		Path destDir = Paths.get(args[1]);  // LOGIKA: u Path, jer koristi Files.copy() metodu koja prima Path!!!
		destDir.toFile().mkdirs();
			//File destDir = new File(args[1]);
			//if(!destDir.exists()){ destDir.mkdirs(); }
		
		m.putanje.forEach( putanja -> {    //for(var putanja : m.putanje){ try ...
			try{
				Path source = Paths.get(putanja);
				Path destination = Path.of(destDir.toString(), source.getFileName().toString());
				//kopiranje svakog source u destination dirketorijum
				Files.copy(source, destination);
			}catch(Exception e){
				e.printStackTrace();
			}
		});
		
	}
}

//For example, if the name separator is "/" and getPath("/foo","bar","gus") is invoked, 
//then the path string "/foo/bar/gus" is converted to a Path.