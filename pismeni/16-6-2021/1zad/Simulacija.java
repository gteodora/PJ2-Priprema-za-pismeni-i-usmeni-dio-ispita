import java.util.*; //Random, 

public class Simulacija{
	public static int REDOVI = 3;
	public static int KOLONE;
	public static Object[][] mapa; 
	public static ArrayList<Plovilo> plovila;
	public static boolean isPauza;
		
		private void kreiraj(){
			plovila = new ArrayList<Plovilo>();
			
			Razarac razarac = new Razarac();
				plovila.add(razarac);
			Nosac nosac = new Nosac();
				plovila.add(nosac);
			Podmornica p1 = new Podmornica();
			Podmornica p2 = new Podmornica();
				plovila.add(p1);
				plovila.add(p2);
		}
		
	private void start(){
		for(var p:plovila){
			p.start();
			System.out.println(p);
		}
	}
	
	//MAIN:
	public static void main(String[] args){
		if(args.length != 1){
			System.out.println("java Simulacija <n>");
			System.exit(1);
		}
		
		
		int n = Integer.parseInt(args[0]);
		if(n>=40 || n<=20){
			System.out.println("arg nije dobar");
			System.exit(1);
		}
		
		Simulacija.KOLONE=n;
		Simulacija.mapa = new Object[REDOVI][KOLONE];
		
		Simulacija simulacija = new Simulacija(); //Da nisam kreirala ovaj objekat, ne bih mogla da pozovem NESTATICKE metode kreiraj i start
		//postavi();
		simulacija.kreiraj();
		simulacija.start();
		
		Scanner s = new Scanner(System.in);
		String line = "";
		System.out.println("unesi nesto: ");
		while(!"END".equals(line)){
			try{
			if(line.startsWith("INFO")){
				String[] tmp = line.split(" ");
				try{
					int id = Integer.parseInt(tmp[1]);
					for(var pl:plovila){
						 if(id == pl.id) {
							 System.out.println("INFO : " + pl);
						 }
					}
				}catch(NumberFormatException e){
					System.out.println("ID nije validan");
				}
				
			} else if(line.startsWith("TIME")){
				String[] tmp = line.split(" ");
				int id = Integer.parseInt(tmp[1]);
				
				try{
					for(var pl : plovila){
						if(id == pl.id){
							//long vrijeme =  new Date().getTime() - pl.startTime;
							System.out.println("Vrijeme izvrsavanja je: " + (new Date().getTime() - pl.startTime)/1000 + "s");
						}
					}
				}catch(NumberFormatException e){
					System.out.println("ID nije dobar");
				}
			} else if("WAIT".equals(line)){
				Simulacija.pauziraj();
			} else if("NOTIFY".equals(line)){
				if(!Simulacija.isPauza){
					throw new CommandNotValidException("1. wait 2. notify");
				}
				Simulacija.pokreni();
			}
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			
			line = s.nextLine();
		}
		s.close();
	}
	
	private static void pauziraj(){
		Simulacija.isPauza = true;
	}
	
	private static void pokreni(){
		synchronized(Simulacija.class){
			Simulacija.isPauza = false;
			Simulacija.class.notifyAll();
		}
	}
}




abstract class Plovilo extends Thread{
	Random r = new Random();
	
	public static int ID;
	public int id;
	
	public int red;
	public int kol;
	
	public long startTime;
	public long endTime;
	
	public boolean smjer;
	
	public Naoruzanje[] naoruzanje;
	public boolean potopljen;
	
	public Plovilo(){
		this.id = Plovilo.ID++;
		if((this instanceof Radar)){
			int i = r.nextInt(Simulacija.REDOVI);
			while(Simulacija.mapa[i][0] != null){
				i=r.nextInt(Simulacija.REDOVI);
			}
			this.red = i;
			this.kol = 0;
			System.out.println("BROD " + this);
			Simulacija.mapa[i][0] = this;
		}else{
			int i = r.nextInt(Simulacija.REDOVI);
			while(Simulacija.mapa[i][Simulacija.KOLONE-1] != null){
				i = r.nextInt(Simulacija.REDOVI);
			}
			this.red=i;
			this.kol = Simulacija.KOLONE-1;
			System.out.println("PODMORNICA " + this);
			Simulacija.mapa[i][Simulacija.KOLONE-1] = this;
		}
	}
	
	public String toString(){
		return "Plovilo id = " + this.id + " se nalazi na poziciji [" + this.red + "][" + this.kol + "]\n";
	}
	
	public void run(){
		this.startTime = new Date().getTime();
		
		while(true){
			
			synchronized(Simulacija.class){					
				if(Simulacija.isPauza){
					try{
						 Simulacija.class.wait();
					}catch(Exception e){
					}
				}
			}
			
			//podmornica naisla na brod:
			synchronized(Simulacija.class){
				if(this instanceof Sonar 
				&& (this.kol-3) >= 0 
				&& Simulacija.mapa[this.red][this.kol-3] instanceof Radar){
					Object meta = Simulacija.mapa[this.red][this.kol-3];
					System.out.println("Podmornica ispaljuje tornado. Podmornica je:" + this + " A brod je: " + meta);
					if(Simulacija.mapa[this.red][this.kol-3] instanceof StitTorpeda){
						System.out.println("***Napad odbijen od strane: " + ((Plovilo)meta).toString());
					}else{
						System.out.println("***POTPOPLJEN BROD;" + ((Plovilo)meta));
						((Plovilo)meta).potopljen = true; //treba da naznacim da ta nit zna da kada doije svoje vrijeme, da sebe ubije
						Simulacija.mapa[this.red][this.kol-3] = null;		
					}
				}
			}
			
			if(this.potopljen){
				System.out.println("UKLOLJEN BROD sa mape!" + this.toString());
				break;
			}
			
			//KRETANJE podmornice:
			synchronized(Simulacija.class){
				if((this instanceof Sonar) && this.kol > 0){
					this.kol--;
					Simulacija.mapa[this.red][this.kol] = this;
					Simulacija.mapa[this.red][this.kol +1] = null;
				}else if((this instanceof Sonar) && this.kol == 0){
					Simulacija.mapa[red][kol] = null;
					this.endTime = new Date().getTime();
					long vrijeme = this.endTime - this.startTime;
					System.out.println("Podmornica dosla do kraja. " + this + "  /n Vrijeme izvrsavanja je "+ vrijeme);
					break;
				}
			}
			
			//KRETANJE broda:
			synchronized(Simulacija.class){
			if((this instanceof Radar) && this.kol < Simulacija.KOLONE-1 ){ //20<[21]
				this.kol++;
				Simulacija.mapa[this.red][this.kol] = this;
				Simulacija.mapa[this.red][this.kol-1] = null;
			}else if((this instanceof Radar) && this.kol == Simulacija.KOLONE-1){
				Simulacija.mapa[red][kol] = null;
				this.endTime = new Date().getTime();
				long vrijeme = this.endTime - this.startTime;
				System.out.println("Brod dosao do kraja. " + this + "Vrijeme izvrsavanja je: "+ vrijeme);
				break;
			}
			}
			//ISPIS:
			System.out.println(this);
			try{
				Thread.sleep(1000);
			}catch(Exception e){ //brzina, InterruptedException
				System.err.println(e);
			}
		}
	}
}

 interface Sonar{}
 interface Radar{}
 interface RaketniStit{}
 interface StitTorpeda{}


//PLOVILA:
 class Podmornica extends Plovilo implements Sonar, StitTorpeda{
	Podmornica(){
		super();
		this.smjer = false;
		this.naoruzanje = new Naoruzanje[1];
		this.naoruzanje[0] = new Torpedo(false);
	}
	
	public String toString(){
		return("Podmornica: " + super.toString());
	}
}

 class Razarac extends Plovilo implements Radar, RaketniStit{
	Razarac(){
		super();
		this.smjer = true;
		this.naoruzanje = new Naoruzanje[2];
		this.naoruzanje[0] = new Torpedo(false);
		this.naoruzanje[1] = new Raketa(1,1);
	}
	
	public String toString(){
		return("Razarac: " + super.toString());
	}
}

 class Nosac extends Plovilo implements Radar, RaketniStit, StitTorpeda{
	Nosac(){
		super();
		this.smjer = true;
		this.naoruzanje = new Naoruzanje[2];
		this.naoruzanje[0] = new Torpedo(false);
		this.naoruzanje[1] = new Raketa(1,1);
	}
	
	public String toString(){
		return "Nosac " + super.toString();
	}
}



//NAORUZANJE:
	 abstract class Naoruzanje {
		public int jacina;
		
		public Naoruzanje(){
			Random r = new Random();
			this.jacina = r.nextInt();
		}
	}

	 class Torpedo extends Naoruzanje{
		public boolean smjer;
		public Torpedo(boolean smjer){
			super();
			this.smjer = smjer;
		}
	}

	 class Raketa extends Naoruzanje{
		public int redCilja;
		public int kolCilja;
		public Raketa(int red, int kol){
			super();
			redCilja = red;
			kolCilja = kol;
		}
	}

class CommandNotValidException extends Exception {
	//public CommandNotValidException(){ 
	//super(); 
	//}
	public CommandNotValidException(String message){
		super(message);
	}
}
