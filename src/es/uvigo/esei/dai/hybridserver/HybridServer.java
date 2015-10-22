package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HybridServer {

	private static int SERVICE_PORT;
	private Thread serverThread;
	private boolean stop;
	private ServerPages pages;
	private Properties properties;
	private int numClients;
	private int flag;

	//private static final String WEB_PAGE = "Hybrid Server"; // "<html><body><h1>Hola Mundo!!</h1></body></html>";

	public HybridServer() {
		this.stop = false;
		SERVICE_PORT = 8888;
		this.numClients = 50;
		this.flag = 0;
	}

	public HybridServer(Map<String, String> pages) {
		this.pages = new ServerPages(pages);
		SERVICE_PORT = 8888;
		this.numClients = 50;
		this.flag = 1;
	}

	public HybridServer(Properties properties) {
		this.properties = properties;
		SERVICE_PORT = Integer.parseInt(properties.getProperty("port"));
		this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		this.flag = 2;
	}

	public int getPort() {
		return SERVICE_PORT;
	}

	public void start() {
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(HybridServer.SERVICE_PORT)) {
					ExecutorService pool = Executors.newFixedThreadPool(numClients);
					while (true) {
						if(flag == 0){
							Socket socket = serverSocket.accept();
							if(stop) break;
							pool.execute(new Worker2(socket));
						}
						if (flag == 1){
							Socket socket = serverSocket.accept();
							if(stop) break;
							pool.execute(new Worker2(socket,pages));
						}
						if (flag == 2){
							Socket socket = serverSocket.accept();
							if(stop) break;
							pool.execute(new Worker2(socket,properties));
						}
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
