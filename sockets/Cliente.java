package sockets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Cliente {
	
	private static String nick;
	private static String server;
	private static int port=27000;

	public static void main(String[] args) {
		
		setServer(JOptionPane.showInputDialog("Introduce ip del servidor: "));
		setNick(JOptionPane.showInputDialog("Introduce tu nick: "));
		MarcoCliente mimarco=new MarcoCliente();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static String getNick() {
		return nick;
	}

	public static void setNick(String nick) {
		Cliente.nick = nick;
	}

	public static String getServer() {
		return server;
	}

	public static void setServer(String server) {
		Cliente.server = server;
	}

	public static int getPort() {
		return port;
	}


}


class MarcoCliente extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MarcoCliente(){
		setBounds(600,300,280,350);	
		LaminaMarcoCliente milamina=new LaminaMarcoCliente();
		add(milamina);
		setVisible(true);
		addWindowListener(new windowsController());
		}	
	
}


class OnlineController implements Runnable {
	
	private String ip;
	private int port;
	private String nick;

	public OnlineController(String ip, int port, String nick) {
		this.ip = ip;
		this.port = port;
		this.nick = nick;
	}

	public void ConectDisconect(String staus) {
		try {
			while (true) {
			Socket setOnlineUsr = new Socket(ip, port);
			Thread.sleep(10000);
			PaqueteEnviado datos = new PaqueteEnviado(nick, ip, staus);
			ObjectOutputStream paquete_datos= new ObjectOutputStream(setOnlineUsr.getOutputStream());
			paquete_datos.writeObject(datos);
			setOnlineUsr.close();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ConectDisconect("KeepAliveClientOnline");	
	}
	
}

class windowsController extends WindowAdapter{
	@Override
	public void windowClosing(WindowEvent e) {
		try {
			
			//-------------------------------------error
			System.out.println("Cerrando...");
			Socket setDisconectUsr = new Socket(Cliente.getServer(), Cliente.getPort());
			PaqueteEnviado dataGram = new PaqueteEnviado(Cliente.getNick(), Cliente.getServer(), "Disconect");
			ObjectOutputStream paquete_datos= new ObjectOutputStream(setDisconectUsr.getOutputStream());
			paquete_datos.writeObject(dataGram);
			setDisconectUsr.close();
		} catch (UnknownHostException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
	
	
	
}

class LaminaMarcoCliente extends JPanel implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField campo1;
	private JComboBox<String> ip;
	private JTextArea campoChat;
	private JButton miboton;
	private ServerSocket serverSocket;
	private ObjectInputStream paquete_recibido;
	private ArrayList<ClienteRed> clientes;
	
	@Override
	public void run() {
		System.out.println("Escuchando por : 9999");
			try {
				serverSocket = new ServerSocket(9999);			
				while(true) {
					Socket miSocket=serverSocket.accept();
					paquete_recibido = new ObjectInputStream(miSocket.getInputStream());

					if (paquete_recibido.readObject() instanceof PaqueteEnviado) {
						PaqueteEnviado paquete_recibido_datos = (PaqueteEnviado) paquete_recibido.readObject();
						campoChat.append("\n"+paquete_recibido_datos.getNick()+" : "+paquete_recibido_datos.getMsg());			
					} else if (paquete_recibido.readObject() instanceof ArrayList) {
					 @SuppressWarnings("unchecked")
					 ArrayList<ClienteRed> readObject = (ArrayList<ClienteRed>) paquete_recibido.readObject();
					 clientes =readObject;
					System.out.println("Actualizando clientes "+ clientes.toString());
					}else{
						System.out.println("Que coño se envio");
					}
					miSocket.close();
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}finally {
				try {
					serverSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	public LaminaMarcoCliente(){
		
		JLabel titulo=new JLabel("-CHAT-");
		JLabel n_nick=new JLabel("Nick: "+Cliente.getNick());
		
		OnlineController onlineHilo = new OnlineController(Cliente.getServer(), Cliente.getPort(), Cliente.getNick());
		Thread online = new Thread(onlineHilo);
		online.start();
		ip=new JComboBox<String>();
		
		actualizarClientesVista actualizaClientes = new actualizarClientesVista();
		Thread hiloClientes = new Thread(actualizaClientes);
		hiloClientes.start();
		
		add(n_nick);
		add(titulo);
		add(ip);
		campoChat= new JTextArea(12,20);
		add(campoChat);
		campo1=new JTextField(20);
		add(campo1);		
		miboton=new JButton("Enviar");
		EnviaTexto mievento= new EnviaTexto();
		miboton.addActionListener(mievento);
		add(miboton);
		Thread r = new Thread(this);
		r.start();
		
	}
	
	class actualizarClientesVista implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(5000);
				ip=new JComboBox<String>();
				if (clientes == null) {					
					for (ClienteRed clienteRed : clientes) {
						ip.addItem(clienteRed.getIp());
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

	
	private class EnviaTexto implements ActionListener{
		private Socket misocket;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			try {
				misocket = new Socket(Cliente.getServer(), 27000);
				PaqueteEnviado datos = new PaqueteEnviado(Cliente.getServer(), ip.getSelectedItem().toString(), campo1.getText());
				ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
				paquete_datos.writeObject(datos);
				paquete_datos.close();
				campoChat.append("\nYo: "+datos.getMsg());
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					misocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}		

}
