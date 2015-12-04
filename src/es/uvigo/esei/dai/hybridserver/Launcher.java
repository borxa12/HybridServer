package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

public class Launcher {

	public static void main(String[] args) {

		boolean param = false;

		try {
			if (args.length == 1) {
				
				
				if (args[0].toLowerCase().endsWith(".xml")) {
					File reader = new File(args[0]);
					Configuration config = new Configuration();
					try {
						config =  XMLConfigurationLoader.load(reader);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else {
					FileReader reader = new FileReader(args[0]);
					Properties properties = new Properties();
					properties.load(reader);
					HybridServer hybridServer = new HybridServer(properties);
					hybridServer.start();
				}
			} else {
				if (args.length == 0) {
					if (!param) {
						HybridServer hybridServer = new HybridServer();
						hybridServer.start();
					} else {
						ServerPages pages = new ServerPages(new HashMap<String, String>());
						HybridServer hybridServer = new HybridServer(pages.getPages());
						hybridServer.start();
					}
				} else {
					System.err.println("Invocación inválida");
				}
			}
		} catch (UnknownHostException e) {
			System.err.println("Servidor no encontrado: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Problemas en el cliente: " + e.getMessage());
		}
	}
}
