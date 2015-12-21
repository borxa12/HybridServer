package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.ws.Endpoint;

public class HybridServer {

	private int servicePort;
	private Thread serverThread;
	private boolean stop;
	private ServerPages pages;
	private Properties properties;
	private int numClients;
	private int flag;
	private Configuration config;
	private Endpoint ep;

	public HybridServer() {
		this.stop = false;
		servicePort = 8888;
		this.numClients = 50;
		this.flag = 0;
	}

	public HybridServer(Map<String, String> pages) {
		this.pages = new ServerPages(pages);
		servicePort = 8888;
		this.numClients = 50;
		this.flag = 1;
	}

	public HybridServer(Properties properties) {
		this.properties = properties;
		servicePort = Integer.parseInt(properties.getProperty("port"));
		this.numClients = Integer.parseInt(properties.getProperty("numClients"));
		this.flag = 2;
	}
	
	public HybridServer(Configuration config){
		this.config = config;
		servicePort = config.getHttpPort();
		this.numClients = config.getNumClients();
		this.flag = 3;
		this.ep = null;
	}

	public int getPort() {
		return servicePort;
	}

	public void start() {
		if(this.flag == 3 && config.getWebServiceURL() != null)
			this.ep = Endpoint.publish(config.getWebServiceURL(),new HSImp(config));
		this.serverThread = new Thread() {
			@Override
			public void run() {
				try (ServerSocket serverSocket = new ServerSocket(servicePort)) {
					ExecutorService pool = Executors.newFixedThreadPool(numClients);
					while (true) {
						if (flag == 0) {
							Socket socket = serverSocket.accept();
							if (stop)
								break;
							pool.execute(new Worker(socket));
						}
						if (flag == 1) {
							Socket socket = serverSocket.accept();
							if (stop)
								break;
							pool.execute(new Worker(socket, pages));
						}
						if (flag == 2) {
							Socket socket = serverSocket.accept();
							if (stop)
								break;
							pool.execute(new Worker(socket, properties));
						}
						if (flag == 3) {
							Socket socket = serverSocket.accept();
							if (stop)
								break;
							pool.execute(new Worker(socket, config));
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

		try (Socket socket = new Socket("localhost", servicePort)) {
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
		
		if(this.flag == 3 && config.getWebServiceURL() != null)
			this.ep.stop();
	}

}
