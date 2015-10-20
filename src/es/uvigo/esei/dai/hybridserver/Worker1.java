package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
//import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

//import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
//import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Worker1 implements Runnable{
	
	private String WEB;
	private Socket socket;
	
	public Worker1(Socket socket) {
		this.socket = socket;
		this.WEB = "Hybrid Server";
	}
	
	public void run() {
		try (Socket socket = this.socket) {
//		HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
		
		// Responder al cliente
		HTTPResponse response = new HTTPResponse();
		response.setVersion("HTTP/1.1");
		response.setStatus(HTTPResponseStatus.S200);
		response.putParameter("Content-Type", "text/html");
		response.setContent(WEB);
		
		OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
		response.print(out);
		out.flush();
		
		} catch (IOException e) {
			System.err.println(e.getMessage());
//		} catch (HTTPParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}


}
