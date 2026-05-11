import java.util.*; //Set, HashSet
import java.util.stream.*; //Collectors

enum Zanr{
	BELETRISTRIKA,
	PUTOPIS,
	TRILER,
	POEZIJA
}

class Knjiga{
	String naslov;
	String pisac;
	int godina;
	Zanr zanr;
	Knjiga(String naslov, String pisac, int god, Zanr zanr){
		this.naslov = naslov;
		this.pisac = pisac;
		this.godina = god;
		this.zanr = zanr;
	}
	public String toString(){
		return naslov + " " + pisac + " " + godina + " " + zanr.toString().toLowerCase();
	}
	public boolean equals(Object o){
		if(o == null || this.getClass() != o.getClass()) return false;
		
		//return this.naslov.equals(((Knjiga)o).naslov) && this.godina==((Knjiga) o).godina;  //citljivije ispod:
		Knjiga k = (Knjiga) o;
		return this.naslov.equals(k.naslov) && this.godina==k.godina;
	}
}

public class Knjige{
	static Set<Knjiga> knjige1 = new HashSet<>();
	static Set<Knjiga> knjige2 = new HashSet<>();
	
	static void popuni(){
		Zanr[] zanrovi = Zanr.values();
		Random rand = new Random();
		
		for(int i=0; i<20; i++){
			knjige1.add(new Knjiga("naslov"+i, "Pisac"+ (i+1), 1990 + rand.nextInt(30), zanrovi[rand.nextInt(zanrovi.length)] ));
			knjige2.add(new Knjiga("naslov"+i, "Pisac"+ (i+1), 1990 + rand.nextInt(30), zanrovi[rand.nextInt(zanrovi.length)] ));
		}
	}
	
	static void spoji(){
		//knjige1.addAll(knjige2); tekst zadataka kaze preko stream-ova
		knjige2.stream().forEach(k -> knjige1.add(k));
		knjige2.clear();
		long brPisaca = knjige1.stream().map(k -> k.pisac).distinct().count();
		System.out.println("Broj knjiga je " + knjige1.size() + " a broj razlicitih pisaca je " + brPisaca);
	}
	
	static void filtriraj(){
		//knjige1.stream().collect(Collectors.groupingBy(k -> k.zanr)).entrySet().forEach(System.out::println);
		/*
		knjige1.stream().collect(Collectors.groupingBy(k->k.zanr)) //Map<Zanr, List<Knjiga>>
						.entrySet()  //Set<Map.Entry<Zanr, List<Knjiga>>>
						.forEach(entry -> 
									System.out.println(entry.getKey() + ": " + entry.getValue()));
								*/	
		knjige1.stream().collect(Collectors.groupingBy(k -> k.zanr))
						.forEach((zanr, knjige) -> {
							System.out.println("Zanr je " + zanr + ". A knjiga ima : " + knjige.size());
							knjige.forEach(System.out::println);
								});
	}
	
	public static void main(String[] args){
		popuni();
		spoji();
		filtriraj();
		sortiraj();
	}
}