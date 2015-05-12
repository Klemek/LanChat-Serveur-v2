package base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Connexion implements Runnable{

	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
    private InetAddress ip;
    private boolean conn = false;
    private boolean admin = false;
    private Client cl;
    private Chat c;

    
    public Connexion(Socket s, Chat c){
    	socket = s;
        this.c = c;
        admin = s.getInetAddress().equals(s.getLocalAddress());
        if(admin){
        	c.setIps(s.getInetAddress().getHostAddress());
        }
    }

	@Override
	public void run() {
		try {
			c.say2(Html.info2("Connexion : "+socket.getInetAddress().getHostAddress()));
			ip = socket.getInetAddress();
			out = new BufferedWriter (new OutputStreamWriter (socket.getOutputStream()));
			in = new BufferedReader (new InputStreamReader (socket.getInputStream()));
			if(c.isBanned(ip.getHostAddress())){
				sayP(Html.err("Vous avez été banni de ce serveur"));
				c.say2(Html.info2("Déconnexion : "+socket.getInetAddress()+" (ip bannie)"));
			}else{
				sayP(Html.info1("Bienvenue sur le serveur,"+(!admin?"":" administrateur")));
				String pseudo = null;
				boolean cont = false;
				while(!cont){
					cont = true;
					sayP(Html.info1("Entrez votre pseudo :"));
					while(!in.ready());
					pseudo = in.readLine();
					switch(c.isPseudFree(pseudo)){
						case 1:
							sayP(Html.err("Ce pseudo est déjà pris !"));
							cont = false;
							break;
						case 2:
							sayP(Html.err("Pseudo invalide"));
							cont = false;
							break;
					}
				}
				cl = c.getClientByID(c.connect(pseudo, ip.getHostAddress(),admin));
				conn = true;
				c.say(Chat.getPseudo(cl)+" vient de se connecter.</span>");
				sayP(Html.info1("Connecté en tant que "+Chat.getPseudo(cl)+"</span>"));
				Thread tr = new Thread(new Reception(c,in,out,cl.getId()));
				Thread te = new Thread(new Emission(c,out));
				tr.start();
				te.start();
			}
			

		} catch (IOException e) {
			deco();
		}
	}
	
	private void sayP(String msg){
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			deco();
		}
		
	}
	
	private void deco(){
		if(conn){
			c.say(Html.info2(Chat.getPseudo(cl)+" s'est déconnecté."));
			c.disconnect(cl.getId());
		}else{
			c.say2(Html.info2("Tentative de connexion de "+socket.getInetAddress()+" échouée"));
		}
		
		try {
			out.close();
			in.close();
			socket.close();	
		} catch (IOException e1) {}
	}


}
