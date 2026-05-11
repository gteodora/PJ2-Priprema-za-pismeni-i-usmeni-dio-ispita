import java.util.*; //List
import java.io.*;
import java.nio.file.*; //Paths, Files

public class Main{
	
	static void prebroj(String putanja){
		HashMap<String, Integer> map = new HashMap<>();
		try
		{
			List<String> lines = Files.readAllLines(Paths.get(putanja)); //LOGIKA:  radi optimizacije, radicu i ja sa linijama, a ne sa recenicama
			for(var line:lines)
			{
				var tmp = line.split("\\W+"); // ZAPAMTI: "\\W+"
				for(var word : tmp)
				{
					if(map.containsKey(word))
					{
						map.put(word, map.get(word)+1);
					}else
					{
						map.put(word, 1);
					}
				}
			}
			
		}catch(Exception e)
		{
			System.out.println("Greska prilikom citanja fajla: " + putanja);
		}
		
		System.out.println("rijeci u fajlu i br ponavljanja:");
		map.entrySet().stream().forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
	}
	
	static void zamijeni(String path, HashMap<String,String> mapa){
		try{
		List<String> lines = Files.readAllLines(Paths.get(path)); 
		 PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(path)));
		for(String line : lines){
			String[] words = line.split("\\W+");
				String newLine = new String(line);
				System.out.println(newLine);
			for(String word : words)
			{
				System.out.println(word);
				if(mapa.containsKey(word)){
					System.out.println("replace" + word + "sa" + mapa.get(word));
					newLine = newLine.replaceAll("\\b" + word + "\\b", mapa.get(word)); //DODIJELI!
				}
			}
			pw.println(newLine);
		}
		pw.flush();
		pw.close();
		}catch(Exception e){}
	}
	
	static void pronadji(Collection<String> rijeciKol, String... putanje){	//rijeci, putanje
		for(String putanja : putanje){
			System.out.println(putanja);
			try{
			String fileName = Paths.get(putanja).getFileName() + ".search.txt";
			System.out.println("Paths.get(putanja) " + Paths.get(putanja));
			System.out.println("filename " + fileName);
			System.out.println("Paths.get(putanja) PARENT " + Paths.get(putanja).getParent());
			System.out.println("Paths.get(putanja).getParent().toString() + File.separator + fileName  " + Paths.get(putanja).getParent().toString() + File.separator + fileName);
			
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter( fileName)));
			System.out.println("Paths.get(putanja).getParent().toString() + File.separator + fileName  " + Paths.get(putanja).getParent().toString() + File.separator + fileName);
			List<String> lines = Files.readAllLines(Paths.get(putanja).getFileName());
			for(var line : lines)
			{
				System.out.println("LINE" + line);
				String[] rijeci = line.split("\\W+");
				for(var rijec : rijeci)
				{
					boolean isPronadjeno = false;
					System.out.println(rijec);
					for(var rijecTrazi:rijeciKol)
					{
						if(isPronadjeno)break;
						System.out.println(rijec);
						if(rijec.equalsIgnoreCase(rijecTrazi))	//velicina slova nije bitna, pise tako
						{
							isPronadjeno=true;
							pw.println(line);	////// tekst3 je ispravan tekst
							break;
						}
					}
				}
			}
			pw.flush();
			pw.close();
		}catch(Exception e){ e.printStackTrace(); //System.out.println("EXCEPTION");
		}
		}
	}
	
	public static void main(String[] args){
		prebroj("prebroj.txt");
		////////
		try{
			Files.writeString(Paths.get("zamijeni.txt"), "java je programski jezik. Teodora programira");
		}catch(Exception e){}
		
		HashMap<String, String> zamijenaMap = new HashMap<>(); //key-stara, value-nova
		zamijenaMap.put("java", "Pajton");
		zamijenaMap.put("Teodora", "Bla");
		
		zamijeni("zamijeni.txt", zamijenaMap);
		////////
		pronadji(Arrays.asList("java", "Teodora", "kod"), "2zad\\tekst1.txt", "2zad\\tekst2.txt", "2zad\\tekst3.txt");
		
	}
}