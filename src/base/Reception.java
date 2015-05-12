package base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Reception implements Runnable{

	private BufferedReader in;
    private BufferedWriter out;
    private Client cl;
    private Chat c;
	private boolean stop = false;
	
	public Reception(Chat c, BufferedReader in,BufferedWriter out, int id) {
		this.c = c;
		this.in = in;
		this.out = out;
		this.cl = c.getClientByID(id);

	}

	@Override
	public void run() {
		while(!stop){
			if(!cl.isConnected())deco();
			try {
				String msg = in.readLine();
				if(msg != null && msg != ""){
					if(msg.toCharArray()[0]!='/'){
						if(cl.isMuted()){
							sayP(Html.err("Vous ne pouvez plus parler."));
						}else{
							msg = msg.replace("<","&lt;").replace(">","&gt;");
							if(msg.contains("&lt;")){
								if(msg.split("&lt;").length >=2 && msg.split("&lt;")[1].contains("&gt;")){
									msg = msg.replace("&lt;","").replace("&gt;","");
								}
							}
							String msg2 = msg;
							while(msg.contains("http://")||msg.contains("https://")){
								if(msg.contains("http://")){
									String lnk = msg.split("http://")[1].split(" ")[0];
									String[] dec = lnk.split("\\.");
									if(Util.find(Chat.IMG,dec[dec.length-1])!=null){
										msg2 = msg2.replace("http://"+lnk,Html.lien(lnk,"image"));
									}else{
										msg2 = msg2.replace("http://"+lnk,Html.lien(lnk,"lien"));
									}
									msg = msg.replace("http://"+lnk,"");
								}else{
									String lnk = msg.split("https://")[1].split(" ")[0];
									String[] dec = lnk.split("\\.");
									if(Util.find(Chat.IMG,dec[dec.length-1])!=null){
										msg2 = msg2.replace("https://"+lnk,Html.lien(lnk,"image"));
									}else{
										msg2 = msg2.replace("https://"+lnk,Html.lien(lnk,"lien"));
									}
									msg = msg.replace("https://"+lnk,"");
								}
								
								
							}
							c.say(Chat.getPseudoChat(cl)+Html.couleur(msg2,cl.getCouleur()));
						}
						
					}else{
						c.say2(Html.italique(Chat.getPseudoChat(cl)+Html.info2(msg)));
						String[] par = msg.split(" ");
						switch(par[0]){
							case "/adm":
							case "/admin":
								admin(par);
								break;
							case "/ban":
								ban(par);
								break;
							case "/col":
							case "/couleur":
								couleur(par);
								break;
							case "/deban":
								deban(par);
								break;
							case "/dis":
							case "/disconnect":
								deco();
								break;
							case "/help":
								help(par);
								break;
							case "/ip":
								ip(par);
								break;
							case "/kick":
								kick(par);
								break;
							case "/list":
								list();
								break;
							case "/me":
								me(par);
								break;
							case "/mute":
								mute(par);
								break;
							case "/nick":
							case "/pseudo":
								pseudo(par);
								break;
							case "/s":
							case "/souligne":
								souligne(par,msg);
								break;
							case "/b":
							case "/barre":
								barre(par,msg);
								break;
							case "/g":
							case "/gras":
								gras(par,msg);
								break;
							case "/i":
							case "/italique":
								italique(par,msg);
								break;
							default:
								sayP(Html.err("Commande inconnue"));
								break;
						}
					}
				}
			} catch (IOException e) {
				c.say2(Html.info2(Chat.getPseudo(cl)+" "+e.getLocalizedMessage()));
				deco();
			}
		}
	}
		
	
	private void pseudo(String[] par) {
		if(par.length < 2){
			sayP(Html.info1("/pseudo NouveauPseudo"));
		}else{
			if(par.length == 3 && cl.isAdmin()){
				Client cl1 = c.getClientByPseudo(par[1]);
				if(cl1==null){
					sayP(Html.err(par[1]+" n'existe pas."));
				}else if(cl1.isAdmin()){
					sayP(Html.err(par[1]+" est admin, vous ne pouvez pas changer son pseudo."));
				}else{
					switch(c.isPseudFree(par[2])){
						case 1:
							sayP(Html.err("Ce pseudo est déjà pris !"));
							break;
						case 2:
							sayP(Html.err("Pseudo invalide"));
							break;
						default:
							c.setPseudo(cl1.getId(),par[2]);
							break;
					}
				}
			}else{
				switch(c.isPseudFree(par[1])){
					case 1:
						sayP(Html.err("Ce pseudo est déjà pris !"));
						break;
					case 2:
						sayP(Html.err("Pseudo invalide"));
						break;
					default:
						c.setPseudo(cl.getId(),par[1]);
						break;
				}
			}
		}
		
	}

	private void mute(String[] par) {
		if(cl.isAdmin()){
			if(par.length < 2){
				sayP(Html.info1("/mute pseudo"));
			}else{
				Client cl1 = c.getClientByPseudo(par[1]);
				if(cl1==null){
					sayP(Html.err(par[1]+" n'existe pas."));
				}else if(cl1.isAdmin()){
					sayP(Html.err(par[1]+" est admin, vous ne pouvez pas le mute."));
				}else{
					sayP(Html.info1(c.mute(par[1])));
				}
			}
		}else{
			sayP(Html.err("Vous n'avez pas l'accès à cette commande"));
		}
	}

	private void me(String[] par) {
		if(par.length < 2){
			sayP(Html.info1("/me action"));
		}else{
			String action = par[1];
			for(int k = 2; k < par.length; k++){
				action = action+" "+par[k];
			}
			c.say(Html.couleur("*"+cl.getPseudo()+" "+action+"*",cl.getCouleur()));
		}
	}

	private void list() {
		sayP(Html.info1("Liste de connectés :"));
		for(int k = 0; k < c.getClients().length; k++){
			sayP(Html.info1(c.getClients()[k].getAdminMark()+c.getClients()[k].getPseudo()));
		}
	}

	private void kick(String[] par) {
		if(cl.isAdmin()){
			if(par.length < 2){
				sayP(Html.info1("/kick pseudo"));
			}else{
				Client cl1 = c.getClientByPseudo(par[1]);
				if(cl1==null){
					sayP(Html.err(par[1]+" n'existe pas."));
				}else if(cl1.isAdmin()){
					sayP(Html.err(par[1]+" est admin, vous ne pouvez pas le kick."));
				}else{
					cl1.setConnected(false);
				}
			}
		}else{
			sayP(Html.err("Vous n'avez pas l'accès à cette commande"));
		}
	}

	private void ip(String[] par) {
		if(par.length < 2){
			sayP(Html.info1("/ip pseudo"));
		}else{
			if(c.getClientByPseudo(par[1])==null){
				sayP(Html.err(par[1]+" n'existe pas."));
			}else{
				sayP(Html.info1("L'ip de "+par[1]+" est "+c.getIP(par[1])));
			}
		}
	}

	private void help(String[] par) {
		if(par.length==1){
			String[] comm = {"/disconnect,/dis)","/help","/ip",
					"/list","/me","/pseudo,nick","/couleur,/col)",
					"/gras,/g)","/barre,/b)","/italique,/i)",
					"/souligne,/s)"};
			String[] commAdm = {"/kick,/eject","/ban","/deban",
					"/mute","/admin,/adm"};
			if(cl.isAdmin()){
				comm = Util.sortA(Util.extend(comm,commAdm));
			}
			String[] text = Util.extend(new String[]{"Liste des commandes :"},comm);
			for(int k = 0; k < text.length; k++){
				sayP(Html.info1(text[k]));
			}
		}else{
			switch(par[1]){
				case "disconnect":
					sayP(Html.info1(Html.gras("/disconnect")+" permet de quitter le chat."));
					break;
				case "couleur":
					sayP(Html.info1(Html.gras("/couleur")+" permet de changer sa propre couleur:"));
					sayP(Html.info1(Html.gras("/couleur")+" vous donne une couleur aléatoire."));
					sayP(Html.info1(Html.gras("/couleur nom")+" vous donne une couleur précise avec des noms comme \"red\" ou \"rouge-foncé\"."));
					sayP(Html.info1(Html.gras("/couleur R G B")+" vous donne une couleur précise avec avec des valeurs comprises entre 0 et 255."));
					break;
				case "help":
					sayP(Html.info1(Html.gras("/help")+" vous donne la liste des commandes et "+Html.gras("/help commande")+" vous donne l'aide sur une commande précise."));
					break;
				case "ip":
					sayP(Html.info1(Html.gras("/ip pseudo")+" vous donne l'ip de la personne choisie."));
					break;
				case "list":
					sayP(Html.info1(Html.gras("/list")+" vous donne la liste des personnes connectées."));
					break;
				case "pseudo":
					sayP(Html.info1(Html.gras("/pseudo nouveauPseudo")+" permet de changer votre pseudo actuel en \"nouveauPseudo\"."));
					break;
				case "me":
					sayP(Html.info1(Html.gras("/me action")+" permet d'afficher un message du type : *"+cl.getPseudo()+" action*."));
					break;
				case "gras":
					sayP(Html.info1(Html.gras("/gras msg")+Html.gras(" permet d'afficher un message gras.")));
					break;
				case "barre":
					sayP(Html.info1(Html.gras("/barre msg")+Html.barre(" permet d'afficher un message barré.")));
					break;
				case "italique":
					sayP(Html.info1(Html.gras("/italique msg")+Html.italique(" permet d'afficher un message italique.")));
					break;
				case "souligne":
					sayP(Html.info1(Html.gras("/souligne msg")+Html.souligne(" permet d'afficher un message souligné.")));
					break;
				case "":
					sayP(Html.info1(Html.gras("/")+""));
					break;
				default:
					sayP(Html.err("Cette fonction n'existe pas."));
					break;
			}
		}
		
	}
	
	private void admin(String[] par){
		if(cl.isAdmin()){
			if(par.length < 2){
				sayP(Html.info1("/admin pseudo"));
			}else{
				if(c.getClientByPseudo(par[1])==null){
					sayP(Html.err(par[1]+" n'existe pas."));
				}else{
					c.setAdmin(par[1]);
				}
			}
		}else{
			sayP(Html.err("Vous n'avez pas l'accès à cette commande"));
		}
	}
	
	private void ban(String[] par){
		if(cl.isAdmin()){
			if(par.length < 2){
				sayP(Html.info1("/ban pseudo"));
			}else{
				Client cl1 = c.getClientByPseudo(par[1]);
				if(cl1==null){
					sayP(Html.err(par[1]+" n'existe pas."));
				}else if(cl1.isAdmin()){
					sayP(Html.err(par[1]+" est admin, vous ne pouvez pas le bannir."));
				}else if(cl1.getIp().equals(c.getIps())){
					sayP(Html.err("Vous ne pouvez pas bannir l'ip du serveur"));
				}else{
					sayP(Html.info1(c.banIP(cl1.getIp())));
					cl1.setConnected(false);
				}
			}
		}else{
			sayP(Html.err("Vous n'avez pas l'accès à cette commande"));
		}
	}
	
	private void deban(String[] par){
		if(cl.isAdmin()){
			if(par.length < 2){
				sayP(Html.info1("/deban ip"));
			}else{
				sayP(Html.info1(c.debanIP(par[1])));
			}
		}else{
			sayP(Html.err("Vous n'avez pas l'accès à cette commande"));
		}
	}
	
	private void couleur(String[] par){
		boolean error = false;
		if(par.length >= 4){
			try{
				int[] coul = {Integer.parseInt(par[1]),
								Integer.parseInt(par[2]),
								Integer.parseInt(par[3])};
				switch(c.isCoulFree(coul)){
					default:
						cl.setCouleur(coul);
						sayP(Html.couleur("Voici votre nouvelle couleur.",coul));
						return;
					case 1:
						sayP(Html.couleur("Cette couleur est déjà utilisée.",coul));
						return;
					case 2:
						error = true;
					case 3:
						sayP(Html.err("Cette couleur est trop claire."));
						return;
				}
			}catch(NumberFormatException e){
				error = true;

			}
		}else if(par.length >= 2){
			int[] coul = Html.getCoulByName(par[1]);
			if(coul == null){
				sayP(Html.err("Cette couleur n'existe pas."));
				return;
			}
			switch(c.isCoulFree(coul)){
				default:
					cl.setCouleur(coul);
					sayP(Html.couleur("Voici votre nouvelle couleur.",coul));
					return;
				case 1:
					sayP(Html.couleur("Cette couleur est déjà utilisée.",coul));
					return;
				case 3:
					sayP(Html.err("Cette couleur est trop claire."));
					return;
			}
		}
		if(error){
			sayP(Html.err("Couleur invalide, tapez "+Html.gras("/help couleur")+" pour l'aide de cette commande."));
			return;
		}
		cl.setCouleur(Chat.getCouleur());
		sayP(Html.couleur("Voici votre nouvelle couleur.",cl.getCouleur()));
	}
	
	private void gras(String[] par,String msg){
		if(par.length < 2){
			sayP(Html.info1("/gras msg"));
		}else{
			if(cl.isMuted()){
				sayP(Html.err("Vous ne pouvez plus parler."));
			}else{
				c.say(Chat.getPseudoChat(cl)+Html.gras(Html.couleur(msg.replace(par[0]+" ",""),cl.getCouleur())));									
			}
		}
	}
	
	private void italique(String[] par,String msg){
		if(par.length < 2){
			sayP(Html.info1("/italique msg"));
		}else{
			if(cl.isMuted()){
				sayP(Html.err("Vous ne pouvez plus parler."));
			}else{
				c.say(Chat.getPseudoChat(cl)+Html.italique(Html.couleur(msg.replace(par[0]+" ",""),cl.getCouleur())));									
			}
		}
	}
	
	private void souligne(String[] par,String msg){
		if(par.length < 2){
			sayP(Html.info1("/souligne msg"));
		}else{
			if(cl.isMuted()){
				sayP(Html.err("Vous ne pouvez plus parler."));
			}else{
				c.say(Chat.getPseudoChat(cl)+Html.souligne(Html.couleur(msg.replace(par[0]+" ",""),cl.getCouleur())));									
			}
		}
	}
	
	private void barre(String[] par,String msg){
		if(par.length < 2){
			sayP(Html.info1("/barre msg"));
		}else{
			if(cl.isMuted()){
				sayP(Html.err("Vous ne pouvez plus parler."));
			}else{
				c.say(Chat.getPseudoChat(cl)+Html.barre(Html.couleur(msg.replace(par[0]+" ",""),cl.getCouleur())));									
			}
		}
	}
	
	private void sayP(String msg){
		c.say2(Html.info2(Html.italique(Chat.getPseudo(cl)+" &lt; "+msg)));
		try {
			out.write(Html.italique(msg));
			out.newLine();
			out.flush();
		} catch (IOException e) {
			c.say2(e.getLocalizedMessage());
			deco();
		}
	}
	
	private void deco(){
		c.say(Chat.getPseudo(cl)+" s'est déconnecté.");
		c.disconnect(cl.getId());
		stop = true;
		try {
			out.close();
			in.close();
		} catch (IOException e1) {}
	}
	
	

}
