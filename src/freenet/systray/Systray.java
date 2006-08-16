package freenet.systray;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import freenet.config.FilePersistentConfig;

public class Systray {
	private boolean isConnected;
	private static FilePersistentConfig cfg;
	private final TrayIcon trayIcon;
	
	MenuItem startItem;
	MenuItem stopItem;
	private final Image _imageConnected = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/freenet/systray/resources/logo_connected.jpg"));
	private final Image _imageDisconnected = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/freenet/systray/resources/logo_disconnected.jpg"));
	Image image = _imageDisconnected;
	
	private final int _port = 9184;
	private final String _host = "127.0.0.1";
	
	public static void main(String[] args) {
		
		if(!SystemTray.isSupported()){
			System.err.println("Systray isn't supported on your system.");
			System.exit(0);
		}
		
		// Load the config
		File configFilename = new File("systray.ini");
		try{
    		cfg = new FilePersistentConfig(configFilename);	
    	}catch(IOException e){
    		System.out.println("Error : "+e);
    		e.printStackTrace();
    		System.exit(-1);
    	}
    	cfg.finishedInit();
    	cfg.store();

    	//SubConfig loggingConfig = new SubConfig("node", cfg);
		Systray s = new Systray();
	}
	
	Systray(){
		//TODO: detect properly and REMOVE
		this.isConnected = isConnected();
		// 	trayIcon.displayMessage("Freenet 0.7", isNodeAlive ? "Connected to FRED !" : "Unable to connect to FRED on "+_host+":"+_port,
		// TrayIcon.MessageType.INFO);
	
		SystemTray tray = SystemTray.getSystemTray();
		PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(image, "Freenet 0.7", popup);

		MouseListener mouseListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				System.out.println("Tray Icon - Mouse clicked!");
				if(isConnected && (e.getButton() == 1))
					System.out.println("Starting the browser");
					BareBonesBrowserLaunch.launch("http://127.0.0.1:8888/");
			}

			public void mouseEntered(MouseEvent e) {}		
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
		};

		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exiting...");
				cfg.store();
				System.exit(0);
			}
		};
		
		ActionListener startListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		};
		
		ActionListener stopListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		};

		ActionListener openFproxyListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch.launch("http://127.0.0.1:8888/");
			}
		};

		ActionListener openWebsiteListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch.launch("http://freenetproject.org/");
			}
		};

		ActionListener openConfigListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BareBonesBrowserLaunch.launch("http://127.0.0.1:8888/config/");
			}
		};
		
		startItem = new MenuItem("Start the node up!");
		stopItem = new MenuItem("Shut the node down!");
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem openFproxyItem = new MenuItem("Browse Freenet");
		MenuItem openWebsiteItem = new MenuItem("Browse the Freenet's project website");
		MenuItem openConfigItem = new MenuItem("Configure the node");
		exitItem.addActionListener(exitListener);
		startItem.addActionListener(startListener);
		stopItem.addActionListener(stopListener);
		openFproxyItem.addActionListener(openFproxyListener);
		openWebsiteItem.addActionListener(openWebsiteListener);
		openConfigItem.addActionListener(openConfigListener);
		openConfigItem.setEnabled(false);
		popup.add(startItem);
		popup.add(stopItem);
		popup.add(exitItem);
		popup.addSeparator();
		popup.add(openFproxyItem);
		popup.add(openWebsiteItem);
		popup.addSeparator();
		popup.add(openConfigItem);
		popup.setLabel("Freenet 0.7");
		
		refresh();
		
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				trayIcon.displayMessage("Action Event", 
						"An Action Event Has Been Peformed!",
						TrayIcon.MessageType.INFO);
			}
		};

		trayIcon.addActionListener(actionListener);
		trayIcon.addMouseListener(mouseListener);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println("TrayIcon could not be added.");
		}
	}
	
	private boolean isConnected(){
		Socket pingSocket;
		try{
			pingSocket = new Socket(_host, _port);
			pingSocket.setSoTimeout(500);
			// FIXME: verify we are talking to fred
			pingSocket.close();
		} catch(SocketTimeoutException ste){
			return true;
		} catch(SocketException e){
			return false;
		} catch (IOException ie){
			return false;
		} 
		return true;
	}
	
	private void disconnect(){
		isConnected = true;
		System.out.println("Stop...");
		trayIcon.displayMessage("Freenet 0.7", "Unable to connect to FRED on "+_host+":"+_port,TrayIcon.MessageType.WARNING);
		refresh();
	}
	
	private void connect(){
		isConnected = false;
		System.out.println("Start...");
		trayIcon.displayMessage("Freenet 0.7", "Connected to FRED on "+_host+":"+_port,TrayIcon.MessageType.INFO);
		refresh();
	}
	
	private void refresh(){
		startItem.setEnabled(isConnected);
		stopItem.setEnabled(!isConnected);
		image = !isConnected ? _imageConnected : _imageDisconnected;
		trayIcon.setImage(image);
		trayIcon.setImageAutoSize(true);
	}
}
