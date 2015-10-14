package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.Socket;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;

public class Worker implements Runnable {

	private Socket socket;
	private Map<String, String> WEB_PAGES;
	private HTTPRequest req;

	public Worker(Socket socket, Map<String, String> pages) {
		this.socket = socket;
		this.WEB_PAGES = pages;

	}

	@Override
	public void run() {
		while (true) {
			try (Socket socket = this.socket) {

				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				this.req = new HTTPRequest(br);

				OutputStream out = socket.getOutputStream();
				String uuidvalue = req.getResourceParameters().get("uuid");
				final int tam = WEB_PAGES.get(uuidvalue).length();

				out.write("HTTP/1.1 200 OK\r\n".getBytes());
				out.write("Content-Type: text/html\r\n".getBytes());
				out.write(("Content-Length: " + tam + "\r\n").getBytes());
				out.write("\r\n".getBytes());
				out.write(WEB_PAGES.get(uuidvalue).getBytes());
				out.flush();

				// Responder al cliente
				HTTPResponse response = new HTTPResponse();
				response.setVersion("HTTP/1.1");
				response.putParameter("Content-Type", "text/html");
				response.setContent(WEB_PAGES.get("1"));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} catch (HTTPParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
