package base;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
//import javax.swing.Timer;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.DefaultCaret;

public class Serveur extends JFrame{

	private String VERSION = "2.0";
	
	private static final long serialVersionUID = 1L;
	private int port = 2000;
	private Chat c;
	private JEditorPane chat = new JEditorPane();
	private JScrollPane scrollC = new JScrollPane(chat);
	private JTextPane liste = new JTextPane();
	private JScrollPane scrollL = new JScrollPane(liste);
	//private Timer refresh;
	
    public static void main(String[] zero){
        new Serveur();
    }
    
    public Serveur(){
    	makeFenetre();
    	ServerSocket socket = null;
        try {
        	c = new Chat(chat,liste);
        	c.say2(Html.info1("LanChat serveur version "+VERSION));
        	c.say2(Html.info1("Démarrage du serveur sur le port : "+port));
        	socket = new ServerSocket(port);
	        Thread t = new Thread(new Accueil(socket,c));
	        t.start();
        } catch (IOException e) {
        	c.say2(Html.err("Port déjà utilisé : "+port));
        }
        
        /*refresh = new Timer(1000,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				c.refresh();
			}
        });
        refresh.start();*/
        
    }
    
    void makeFenetre(){
		this.setTitle("LanChat - Serveur (v"+VERSION+")");
		this.setSize(600,400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
            	close();
            }
        });
		chat.setEditable(false);
		chat.setText("");
		chat.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		        	if(Desktop.isDesktopSupported()) {
		        	    try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
		        	}
		        }
		    }
		});
		((DefaultCaret)chat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		liste.setEditable(false);
		liste.setText("Clients connectés :");
		
		liste.setPreferredSize(new Dimension(180,0));
		scrollL.setAutoscrolls(false);
		this.add(scrollC,BorderLayout.CENTER);
		this.add(scrollL,BorderLayout.EAST);
		
		URL iconurl = this.getClass().getResource("icon64p.ico");
		if(iconurl != null){
			ImageIcon icon = new ImageIcon(iconurl);
			this.setIconImage(icon.getImage());
		}
		this.setVisible(true);
	}
    
    void close(){
    	c.deleteTemp();
		this.dispose();
		System.exit(ABORT);
	}
}

