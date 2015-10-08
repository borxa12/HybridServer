package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class HybridServer {

	public HybridServer() {
		// Constructor necesario para los tests de la primera semana
	}

	public HybridServer(Map<String, String> pages) {
		// Constructor necesario para los tests de la segunda semana
	}

	public HybridServer(Properties properties) {
		// Constructor necesario para los tests de la tercera semana
	}

	public int getPort() {
		return -1;
	}

	public void start() {
		
	}

	public void stop() {
		
	}
}
