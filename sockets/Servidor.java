package sockets;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Servidor  {

	public static void main(String[] args) {
		
		MarcoServidor mimarco=new MarcoServidor();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}

class MarcoServidor extends JFrame implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4074461314007951347L;

	private JTextField ip;
	private	JTextArea areatexto;
	private ServerSocket servidor;
	private ArrayList<ClienteRed> clientes;
	
	public MarcoServidor(){
		ip=new JTextField(10);
		add(ip);
		setBounds(1200,300,280,350);				
		JPanel milamina= new JPanel();
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		milamina.add(areatexto,BorderLayout.CENTER);
		add(milamina);
		setVisible(true);
		
		Thread mihilo= new Thread(this);
		mihilo.start();
		
		ClearUsers tareaLimpiarUsr = new ClearUsers();
		Thread mihiloClear= new Thread(tareaLimpiarUsr);
		mihiloClear.start();
		
		
		}
	
	
 class ClearUsers implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(25000);
				System.out.println("-------------Clear disconnected users--------");
				if (clientes==null) {
					System.out.println(clientes.toString());
					clientes.clear();					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	@Override
	public void run() {
		System.out.println("Escuchando por : 27000");
		while(true) {
			try {
				servidor = new ServerSocket(27000);
				PaqueteEnviado paquete_recibido;
				while(true) {					
					Socket miSocket= servidor.accept();
					
					//-------------Detecta Online------------
					InetAddress localizacion = miSocket.getInetAddress();
					String ipRemota = localizacion.getHostAddress();
					areatexto.append("\nOnline :" + ipRemota);
					
					//-----------------fin Detecta Online------------
					ObjectInputStream paquete_datos= new ObjectInputStream(miSocket.getInputStream());
					paquete_recibido=(PaqueteEnviado) paquete_datos.readObject();
					if (paquete_recibido.getMsg()==null) {
						areatexto.append("\n"+paquete_recibido.getNick()+": mensaje Nulo,No se envia. para "+paquete_recibido.getIp());
					}else if(paquete_recibido.getMsg().equals("KeepAliveClientOnline")){
					//------------array clientes-------------
						
						areatexto.append("\n"+paquete_recibido.getNick()+": Online Con Ip:"+ipRemota);
						ClienteRed cliente =new ClienteRed(paquete_recibido.getIp(), paquete_recibido.getNick());
						
						boolean repetido=false;
						if (clientes==null) {
							repetido=false;
						} else {
							Iterator<ClienteRed> it=clientes.iterator();
							while (it.hasNext()) {
								ClienteRed element = it.next();
								if (element.getIp().equals(cliente.getIp())) {
									repetido = true;
								} 
							}	
						}
						
						if (repetido) {							
							clientes.add(cliente);
						}
						
						Socket enviaDestino = new Socket(ipRemota, 9999);
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestino.getOutputStream());
						paqueteReenvio.writeObject(clientes);
						enviaDestino.close();
						
					//-------------fin array clientes-------------------
					}else {
					areatexto.append("\n"+paquete_recibido.getNick()+": "+paquete_recibido.getMsg()+" para "+paquete_recibido.getIp());
					//reenvio
					Socket enviaDestino = new Socket(paquete_recibido.getIp(), 9999);
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestino.getOutputStream());
					paqueteReenvio.writeObject(paquete_recibido);
					enviaDestino.close();
					//fin reenvio
					}
					miSocket.close();
				}
			} catch (IOException | ClassNotFoundException  e) {
				try {
					servidor.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}
}




