package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Launcher {
	public static void main(String[] args) {
		ServerPages pages = new ServerPages(new HashMap<String,String>());
		HybridServer hybridServer = new HybridServer(pages.getPages());
		hybridServer.start();
		try(Socket socket = new Socket("localhost",hybridServer.getPort())) {
			/* Solo é para visualizar por consola o que se lee
			 * porque para que pase o test non se pode poñer o HTML
			 */
			InputStream in = socket.getInputStream();
			int line;
			while((line = in.read()) != -1) {
				System.out.print((char)line);
			}
		} catch (UnknownHostException e) {
			System.err.println("Servidor no encontrado: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Problemas en el cliente: " + e.getMessage());
		}
	}
}
