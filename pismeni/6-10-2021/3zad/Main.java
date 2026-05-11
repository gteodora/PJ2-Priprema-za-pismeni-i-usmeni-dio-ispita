import java.util.*; //ArrayList, Date, Random
import java.time.*; //LocalDate
import java.util.stream.*;

public class Main{
	static ArrayList<Oglas> oglasi = new ArrayList<>();
	
	static void generisiOglase(){
		String[] gradovi = new String[]{"BL", "Gr", "Blj", "Tr", "Pd"};
		for(int i=0; i<Kategorija.values().length; i++){
			Kategorija kat = Kategorija.values()[i];
			for(int j=0; j<12; j++){
				Random rand = new Random();
				Oglas o = new Oglas(
				"Naziv"+j, 
				"Opis"+j, 
				LocalDate.of(2026,rand.nextInt(12)+1, rand.nextInt(27)+1),
				rand.nextInt(15),
				rand.nextInt(1000)+1000,
				rand.nextInt(15),
				gradovi[rand.nextInt(gradovi.length)],
				kat
				);
				oglasi.add(o);
			}
		}
		/*
		oglasi.addAll(generisiPoKat(Kategorija.IT));
		oglasi.addAll(generisiPoKat(Kategorija.EKONOMIJA));
		oglasi.addAll(generisiPoKat(Kategorija.MEDICINA));
		oglasi.addAll(generisiPoKat(Kategorija.NOVINARSTVO));
		oglasi.addAll(generisiPoKat(Kategorija.PRAVO));*/
	}
	
	/*ArrayList<Oglas> generisiPoKat(Kategorija kat){
		ArrayList oglasi = new ArrayList<>();
		for(int i=0; i<12; i++){
			Oglas o = new Oglas("Naziv"+i, "Opis"+i, new Date(2026, )
		}
	}*/
	
	public static void main(String[] args){
		generisiOglase();
		//System.out.println(oglasi); ruzan zapis
		oglasi.stream().forEach(System.out::println);
		System.out.println("Ukupan broj objavljenih oglasa u jednom danu (za svaki datum pojedinačno),");
		oglasi.stream() //Stream<Oglasi>
		.collect(Collectors.groupingBy(o -> o.datum)) //Map<LocalDate, List<Oglas>>
		.entrySet() //Set<Map.Entry<LocalDate, List<Oglas>>>    ne moze map u stream
		.stream() 	
		.sorted(Comparator.comparing(Map.Entry::getKey))
		.forEach( e -> System.out.println(e.getKey() + " " + e.getValue().size()));
		
		System.out.println("Prosječnu ponuđenu platu u kategoriji IT,");
		oglasi.stream()
		.filter(o->o.kat==Kategorija.IT)
		.mapToInt(o->o.plata)
		.average()
		.ifPresent(System.out::println);
		
		System.out.println("Najčešći grad u kom se nudi posao,");
		oglasi.stream()
		.collect(Collectors.groupingBy( o-> o.grad)) //Map<String, List<Oglas>>
		.entrySet()
		.stream()
		.sorted( (e1, e2) -> e2.getValue().size() - e1.getValue().size())
		.limit(1)
		.forEach(e -> System.out.println( e.getKey() + " " + e.getValue().size() ));
	}
}

class Oglas{
	String naziv;
	String opis;
	LocalDate datum;
	int trajanje;
	int plata;
	int godIskustva;
	String grad;
	Kategorija kat;
	
	Oglas(String n, String o, LocalDate d, int t, int p, int g, String gr, Kategorija k){
		this.naziv = n;
		opis=o;
		datum=d;
		trajanje=t;
		plata=p;
		godIskustva=g;
		grad=gr;
		kat=k;
	}
	
	public String toString(){
		return naziv + " " + opis + " " + datum + " " + trajanje + " " + plata + " " + godIskustva + " " + grad + " " + kat;
	}
}

enum Kategorija{
	IT, 
	EKONOMIJA,
	MEDICINA,
	NOVINARSTVO,
	PRAVO
}