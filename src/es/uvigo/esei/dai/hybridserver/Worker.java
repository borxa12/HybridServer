package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
//import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Worker implements Runnable {

	private Socket socket;
	private Pages WEB_PAGES;
	private String WEB;
	private boolean flag;
	private Connection connection;
	private Controller controller;

	public Worker(Socket socket) {
		this.socket = socket;
		this.WEB = "Hybrid Server";
		this.flag = false;
	}

	public Worker(Socket socket, ServerPages pages) {
		this.socket = socket;
		this.WEB_PAGES = pages;
		this.flag = true;
	}

	public Worker(Socket socket, Properties properties) {
		this.socket = socket;
		this.connection = null;
		this.WEB = "Hybrid Server";
		try {
			this.connection = DriverManager.getConnection(properties.getProperty("db.url"),
					properties.getProperty("db.user"), properties.getProperty("db.password"));
			//this.WEB_PAGES = new DBDAOhtml(connection);
			this.controller = null;
			this.flag = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {

			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			HTTPResponse response = new HTTPResponse();
			
			response.setVersion("HTTP/1.1");
			response.setStatus(HTTPResponseStatus.S200);

			if (this.flag) {
				if (this.connection == null) {
				//crea el response cuando no hay bd	
					response.setStatus(HTTPResponseStatus.S200);
					response.putParameter("Content-Type", "text/html");
					response.setContent(WEB);
					// POST
					if (request.getMethod() == HTTPRequestMethod.POST) {
						String uuid = UUID.randomUUID().toString();

						if (request.getResourceParameters().get("html") == null)
							response.setStatus(HTTPResponseStatus.S400);
						this.WEB_PAGES.create(uuid,request);
						
						response.setContent(WEB_PAGES.link(uuid));
					}

					// DELETE
					if (request.getMethod() == HTTPRequestMethod.DELETE) {
						if (!WEB_PAGES.exists(request))
							response.setStatus(HTTPResponseStatus.S404);
						this.WEB_PAGES.remove(request);
						
					}

					// GET
					if (request.getMethod() == HTTPRequestMethod.GET) {
						// Si ResourceName no contiene HTML: error 400
						if (!request.getResourceName().equals("/html"))
							response.setStatus(HTTPResponseStatus.S400);
						else { // Si no hay UUID
							if (request.getResourceParameters().get("uuid") == null) {
								// response.setContent("Hybrid Server");
								response.setContent(this.WEB_PAGES.list());
							} else { // Si hay UUID
							
								if (this.WEB_PAGES.exists(request)) {
									
									response.setContent(WEB_PAGES.get(request)); // UUIDexistente:visualiza página

								} else {
									if (!this.WEB_PAGES.exists(request))
										response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
									else
										response.setStatus(HTTPResponseStatus.S500);
								}
							}
						}

					}
				}else{		
				controller = new Controller(request, this.connection, this.WEB_PAGES);
				response = this.controller.createResponse();
				}
				
			} else {

				response.setStatus(HTTPResponseStatus.S200);
				response.setContent(WEB);
			}
			
			

			System.out.println("REQUEST: \r\n" + request + "\r\n");
			System.out.println("RESPONSE: \r\n" + response + "\r\n");

			OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
			response.print(out);
			out.flush();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (HTTPParseException e) {
			System.err.println(e.getMessage());
		}
	}

}
