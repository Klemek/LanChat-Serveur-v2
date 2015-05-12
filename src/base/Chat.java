package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

public class Chat {

	protected static char ADMIN = '#';
	protected static String BEG = "";
	protected static String END = " : ";
	protected static String[] IMG = {"png","jpg","jpeg","gif"};
	private static String BEGP = "<HTML><HEAD><style>body{font-family:Arial;font-size:10px;text-align:justified;}</style></HEAD><BODY>";
	private static String ENDP = "</BODY></HTML>";
	
	private static int MAXCOUL = 530;
	
	private ArrayList<String> log = new ArrayList<String>();
	private ArrayList<String> log2 = new ArrayList<String>();
	private int time = 0;
	
	private Hashtable<Integer,Client> clients = new Hashtable<Integer,Client>();
	private int ind = 0;
	
	private ArrayList<String> ipban = new ArrayList<String>();
	
	private JEditorPane chat = new JTextPane();
	private JTextPane liste = new JTextPane();
	
	private String ips = "";
	
	private File file = new File("tmpLanChatServ.html");
	
	public Chat(JEditorPane chat2,JTextPane liste){
		this.chat = chat2;
		this.liste = liste;
		try {
			file.createNewFile();
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int connect(String pseudo,String ip, boolean adm){
		int id = getNewID();
		clients.put(id,new Client(id,pseudo,ip,getNewCouleur(),adm));
		refresh2();
		System.out.println("Client "+id+" : "+pseudo+" ("+ip+")");
		return id;
	}
	
	public void disconnect(int id){
		clients.remove(id);
		refresh2();
	}
	
	private int getNewID(){
		ind++;
		return ind-1;
	}
	
	private int[] getNewCouleur(){
		int[] coul;
		do{
			coul = getCouleur();
		}while(isCoulFree(coul)!=0);
		return coul;
	}
	
	
	
	public void say(String msg){
		log.add(msg);
		log2.add(msg);
		time++;
		refresh();
	}
	
	public void say2(String msg){
		log2.add(msg);
		refresh();
	}
	
	@SuppressWarnings("deprecation")
	public void refresh(){
		chat.setText("");
		if(log2 != null){
			String text = BEGP;
			for(String s:log2){
				text = text.equals(BEGP)?s:text+"<br />"+s;
			}
			text+=ENDP;
			FileWriter fw = null;  
	        try {
	        	fw = new FileWriter(file);
	        	fw.write(text);
	        	fw.close();
	        } catch (FileNotFoundException e1) {
	        	e1.printStackTrace();
	        } catch (IOException e1) {
	        	e1.printStackTrace();
	        }
	        try {
	        	chat.setEditorKit(new HTMLEditorKit());               
	        	chat.setPage(file.toURL());
	        } catch (IOException e1) {
	        	e1.printStackTrace();
	        }
		}
	}
	
	public void refresh2(){
		liste.setText("Clients connectés :");
		if(clients != null){
			String text = "Clients connectés :";
			for(Client c:clients.values()){
				text = text+"\n"+c.getAdminMark()+c.getPseudo()+" ("+c.getIp()+")";
			}
			liste.setText(text);
		}
	}
	
	public ArrayList<String> getLog() {
		return log;
	}
	
	public void clearLog() {
		log = null;
		time = 0;
	}
	
	public int isPseudFree(String pseudo){
		if(getClientByPseudo(pseudo)!=null)return 1;
		if(pseudo.contains(""+ADMIN) || pseudo.contains(" "))return 2;
		return 0;
	}
	
	public String getPseudo(int id){
		return getClientByID(id).getPseudo();
	}
	
	public void setPseudo(int id,String ps){
		Client cl = getClientByID(id);
		String old = cl.getPseudo();
		getClientByID(id).setPseudo(ps);
		refresh2();
		say(Html.couleur(old,cl.getCouleur())+" est maintenant "+Html.couleur(ps,cl.getCouleur())+".");
	}
	
	public String getIP(int id){
		return getClientByID(id).getIp();
	}
	
	public String getIP(String pseudo){
		return getClientByPseudo(pseudo).getIp();
	}
	
	public int getID(String pseudo){
		return getClientByPseudo(pseudo).getId();
	}

	public int getTime() {
		return time;
	}

	public boolean isAdmin(int id) {
		return getClientByID(id).isAdmin();
	}
	
	public Client getClientByID(int id){
		return clients.get(id);
	}
	
	public Client getClientByPseudo(String pseudo){
		if(clients == null)return null;
		for(Client c:clients.values()){
			if(c.getPseudo().equals(pseudo)){
				return c;
			}
		}
		System.err.println("Pas de client avec le pseudo "+pseudo);
		return null;
	}

	public Hashtable<Integer, Client> getClients() {
		return clients;
	}
	
	public String banIP(String ip){
		if(ipban.contains(ip)){
			return "l'ip "+ip+" a déjà été bannie.";
		}else{
			ipban.add(ip);
			return "l'ip "+ip+" a été bannie.";
		}	
	}
	
	public String debanIP(String ip){
		if(ipban.contains(ip)){
			ipban.remove(ip);
			return "l'ip "+ip+" a été débannie.";
		}else{
			return "l'ip "+ip+" n'est pas bannie.";
		}
	}
	
	public String mute(String pseudo){
		Client c = getClientByPseudo(pseudo);
		if(c.isMuted()){
			c.setMuted(false);
			return getPseudo(c)+" peut parler à nouveau.";
		}else{
			c.setMuted(true);
			return getPseudo(c)+" ne peut plus parler.";
		}
	}
	
	public boolean isBanned(String ip){
		return ipban.contains(ip);
	}
	
	public void setAdmin(String pseudo){
		Client c = getClientByPseudo(pseudo);
		if(c.isAdmin()){
			c.setAdmin(false);
			refresh2();
			say(getPseudo(c)+"n'est plus admin.");
		}else{
			c.setAdmin(true);
			refresh2();
			say(getPseudo(c)+"est maintenant admin.");
		}
	}

	public String getIps() {
		return ips;
	}

	public void setIps(String ips) {
		this.ips = ips;
		System.out.println("IP serveur : "+ips);
	}

	public int isCoulFree(int[] coul){
		if(coul[0]>255||coul[1]>255||coul[2]>255)return 2;
		if(coul[0]+coul[1]+coul[2] > MAXCOUL)return 3;
		if(clients == null)return 0;
		for(Client c:clients.values()){
			int[] temp = c.getCouleur();
			if(temp[0]==coul[0] && temp[1]==coul[1] && temp[2]==coul[2])return 1;
		}
		return 0;
	}
	
	public static int[] getCouleur(){
		int[] out;
		int s;
		do{
			out = new int[]{(int) Util.random(0,255),(int) Util.random(0,255),(int) Util.random(0,255)};
			s = 0;
			for(int k = 0; k < 3; k++){
				s+=out[k];
			}
		}while(s > MAXCOUL);
		return out;
	}
	
	public static String getPseudo(Client cl){
		return Html.couleur(cl.getAdminMark()+cl.getPseudo(), cl.getCouleur());
	}
	
	public static String getPseudoChat(Client cl){
		return Html.gras(Html.couleur(BEG+cl.getAdminMark()+cl.getPseudo()+END, cl.getCouleur()));		
	}
	
	public void deleteTemp(){
		file.delete();
	}
	
	
}
