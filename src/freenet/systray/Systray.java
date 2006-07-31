package freenet.systray;

import java.awt.AWTException;
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

public class Systray {
	private boolean isNodeAlive;
	
	public static void main(String[] args) {
		Systray s = new Systray();
	}
	
	Systray(){
		this.isNodeAlive = true;
		
		if (SystemTray.isSupported()) {
			final TrayIcon trayIcon;
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("logo.jpg");

			MouseListener mouseListener = new MouseListener() {

				public void mouseClicked(MouseEvent e) {
					System.out.println("Tray Icon - Mouse clicked!");
					if(isNodeAlive && (e.getButton() == MouseEvent.BUTTON1))
						BareBonesBrowserLaunch.launch("http://127.0.0.1:8888/");
				}

				public void mouseEntered(MouseEvent e) {
					System.out.println("Tray Icon - Mouse entered!");                 
				}

				public void mouseExited(MouseEvent e) {
					System.out.println("Tray Icon - Mouse exited!");                 
				}

				public void mousePressed(MouseEvent e) {
					System.out.println("Tray Icon - Mouse pressed!");                 
				}

				public void mouseReleased(MouseEvent e) {
					System.out.println("Tray Icon - Mouse released!");                 
				}
			};

			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exiting...");
					System.exit(0);
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

			PopupMenu popup = new PopupMenu();
			MenuItem exitItem = new MenuItem("Exit");
			MenuItem openFproxyItem = new MenuItem("Browse Freenet");
			MenuItem openWebsiteItem = new MenuItem("Browse the Freenet's project website");
			MenuItem openConfigItem = new MenuItem("Configure the node");
			exitItem.addActionListener(exitListener);
			openFproxyItem.addActionListener(openFproxyListener);
			openWebsiteItem.addActionListener(openWebsiteListener);
			openConfigItem.addActionListener(openConfigListener);
			openConfigItem.setEnabled(false);
			popup.add(exitItem);
			popup.add(openFproxyItem);
			popup.add(openWebsiteItem);
			popup.add(openConfigItem);

			trayIcon = new TrayIcon(image, "Tray Demo", popup);

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					trayIcon.displayMessage("Action Event", 
							"An Action Event Has Been Peformed!",
							TrayIcon.MessageType.INFO);
				}
			};
			
			trayIcon.setImage(image);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);
			trayIcon.addMouseListener(mouseListener);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("TrayIcon could not be added.");
			}


		} else {
			System.err.println("Systray isn't supported on your system.");
		}
	}
}
