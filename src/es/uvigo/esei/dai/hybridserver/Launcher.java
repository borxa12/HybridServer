package es.uvigo.esei.dai.hybridserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class Launcher {
	public static void main(String[] args) {
		//args[0] = new String("config.props");

		try {	FileReader reader = new FileReader(args[0]);
		
		
		if (!args[0].isEmpty()){
			Properties properties = new Properties();
			properties.load(reader);
			System.out.println(properties.toString());
			HybridServer hybridServer = new HybridServer(properties);

		}else{
			HybridServer hybridServer = new HybridServer();
		}
		
		ServerPages pages = new ServerPages(new HashMap<String, String>());
		HybridServer hybridServer = new HybridServer(pages.getPages());
		hybridServer.start();
		Socket socket = new Socket("localhost", hybridServer.getPort());
			/*
			 * Solo é para visualizar por consola o que se lee porque para que
			 * pase o test non se pode poñer o HTML
			 */
			InputStream in = socket.getInputStream();
			int line;
			while ((line = in.read()) != -1) {
				System.out.print((char) line);
			}
		} catch (UnknownHostException e) {
			System.err.println("Servidor no encontrado: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Problemas en el cliente: " + e.getMessage());
		}
	}
}
