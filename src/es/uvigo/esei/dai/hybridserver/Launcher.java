package es.uvigo.esei.dai.hybridserver;

//import java.beans.Statement;
//import java.io.BufferedReader;
//import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStream;
//import java.net.Socket;
import java.net.UnknownHostException;
//import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Properties;

public class Launcher {
	
	public static void main(String[] args) {
		
		

		// args[0] = new String("config.props");
		boolean param = false;

		try {
			FileReader reader = new FileReader(args[0]);

			if (!args[0].isEmpty()) {
//				File dataBase = new File("database.sql");
				/* Leo el fichero y lo almaceno en un String para poder ejecutarlo */
//				BufferedReader br = new BufferedReader(new FileReader("database.sql"));
//				String linea;
//				StringBuilder fichero = new StringBuilder();
//				while ((linea = br.readLine()) != null) {
//					fichero.append(linea);
//				}
//				
				Properties properties = new Properties();
				properties.load(reader);
//				System.out.println(properties.toString()); // SYSO
				HybridServer hybridServer = new HybridServer(properties);
				hybridServer.start();
			} else {
				if(!param) {
					HybridServer hybridServer = new HybridServer();
					hybridServer.start();
				} else {
					ServerPages pages = new ServerPages(new HashMap<String, String>());
					HybridServer hybridServer = new HybridServer(pages.getPages());
					hybridServer.start();
				}
			}

			
//			Socket socket = new Socket("localhost", hybridServer.getPort());
//			/*
//			 * Solo é para visualizar por consola o que se lee porque para que
//			 * pase o test non se pode poñer o HTML
//			 */
//			InputStream in = socket.getInputStream();
//			int line;
//			while ((line = in.read()) != -1) {
//				System.out.print((char) line);
//			}
		} catch (UnknownHostException e) {
			System.err.println("Servidor no encontrado: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Problemas en el cliente: " + e.getMessage());
		}
	}
}
