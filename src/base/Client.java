package base;

public class Client {

	private int id;
	private String pseudo;
	private String ip;
	private boolean admin = false;
	private boolean muted = false;
	private boolean connected = true;
	private int[] couleur = {0,0,0};
	
	public Client(int id,String pseudo,String ip,int[] couleur){
		this.id = id;
		this.pseudo = pseudo;
		this.ip = ip;
		this.couleur = couleur;
	}
	
	public Client(int id,String pseudo,String ip,int[] couleur,boolean admin){
		this.id = id;
		this.pseudo = pseudo;
		this.ip = ip;
		this.admin = admin;
		this.couleur = couleur;
	}
	
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean isMuted() {
		return muted;
	}
	public void setMuted(boolean muted) {
		this.muted = muted;
	}
	public int getId() {
		return id;
	}
	public String getIp() {
		return ip;
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getAdminMark(){
		return ""+(admin?Chat.ADMIN:"");
	}

	public String getCouleurS() {
		return couleur[0]+","+couleur[1]+","+ couleur[2];
	}
	
	public int[] getCouleur() {
		return couleur;
	}

	public void setCouleur(int[] couleur) {
		this.couleur = couleur;
	}
	
}
