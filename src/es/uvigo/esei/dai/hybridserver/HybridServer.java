package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HybridServer {

	private static final int SERVICE_PORT = 8888;
	private Thread serverThread;
	private boolean stop;
	private ServerPages pages;

	//private static final String WEB_PAGE = "Hybrid Server"; // "<html><body><h1>Hola Mundo!!</h1></body></html>";

	public HybridServer() {
		this.stop = false;
	}

	public HybridServer(Map<String, String> pages) {
		this.pages = new ServerPages(pages);
	}

	public HybridServer(Properties properties) {
		// Constructor necesario para los tests de la tercera semana
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(HybridServer.SERVICE_PORT)) {
					ExecutorService pool = Executors.newFixedThreadPool(50);
					while (true) {
						Socket socket = serverSocket.accept();
						if(stop) break;
						pool.execute(new Worker(socket,pages));
					}
				} catch (IOException e) {
					System.err.println("Error al abrir el socket: " + e.getMessage());
				}
			}
		};

		this.stop = false;
		this.serverThread.start();
	}

	public void stop() {
		this.stop = true;

		try (Socket socket = new Socket("localhost", SERVICE_PORT)) {
			// Esta conexión se hace, simplemente, para "despertar" el hilo servidor
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try {
			this.serverThread.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		this.serverThread = null;
	}

}
