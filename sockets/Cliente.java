package sockets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Cliente {

	public static void main(String[] args) {

		MarcoCliente mimarco=new MarcoCliente();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
		}	
	
}

class LaminaMarcoCliente extends JPanel implements Runnable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField campo1,nick,ip;
	private JTextArea campoChat;
	private JButton miboton;
	private ServerSocket serverSocket;
	private ObjectInputStream paquete_recibido;
	
	
	@Override
	public void run() {
		System.out.println("Escuchando por : 9999");
			try {
				serverSocket = new ServerSocket(9999);			
				while(true) {
					Socket miSocket=serverSocket.accept();
					paquete_recibido = new ObjectInputStream(miSocket.getInputStream());
					PaqueteEnviado paquete_recibido_datos=(PaqueteEnviado) paquete_recibido.readObject();
					campoChat.append("\n"+paquete_recibido_datos.getNick()+" : "+paquete_recibido_datos.getMsg());			
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
		nick=new JTextField(6);
		ip=new JTextField(10);
		add(nick);
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
	
	
	private class EnviaTexto implements ActionListener{
		
		private Socket misocket;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			
			try {
				misocket = new Socket("10.246.64.205", 27000);
				PaqueteEnviado datos = new PaqueteEnviado(nick.getText(), ip.getText(), campo1.getText());
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


class PaqueteEnviado implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 249125452092785838L;
	private String nick,ip,msg;
	
	public PaqueteEnviado(String nick, String ip, String msg) {
		this.nick = nick;
		this.ip = ip;
		this.msg = msg;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}