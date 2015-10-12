package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridServer {
	private static final int SERVICE_PORT = 8888;
    private Thread serverThread;
    private boolean stop;

    private static final String WEB_PAGE = "<html><body><h1>Hola Mundo!!</h1></body></html>";

    
    public HybridServer() {
		// Constructor necesario para los tests de la primera semana
    	this.start();
	}

	public HybridServer(Map<String, String> pages) {
		// Constructor necesario para los tests de la segunda semana
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
                try (final ServerSocket serverSocket = new ServerSocket(SERVICE_PORT)) {
                    while (true) {
                        try (Socket socket = serverSocket.accept()) {
                            if (stop) {
                                break;
                            }
                            // Responder al cliente
                           /* OutputStream out = socket.getOutputStream();
                            final int tam = WEB_PAGE.length();
                            out.write("HTTP/1.1 200 OK\r\n".getBytes());
                            out.write("Content-Type: text/html\r\n".getBytes());
                            out.write(("Content-Length: " + tam + "\r\n").getBytes());
                            out.write("\r\n".getBytes());
                            out.write(WEB_PAGE.getBytes());
                            out.flush();*/
                            
                            HTTPResponse response = new HTTPResponse();
                            response.setVersion("HTTP/1.1");
                            response.putParameter("Content-Type", "text/html");
                            response.setContent(WEB_PAGE);
                            
                            
                            
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
