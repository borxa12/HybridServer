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

public class Worker2 implements Runnable {

	private Socket socket;
	private Pages WEB_PAGES;
	private String WEB;
	private boolean flag;

	public Worker2(Socket socket) {
		this.socket = socket;
		this.WEB = "Hybrid Server";
		flag = false;
	}

	public Worker2(Socket socket, ServerPages pages) {
		this.socket = socket;
		this.WEB_PAGES = pages;
		flag = true;
	}

	public Worker2(Socket socket, Properties properties) {
		this.socket = socket;
		Connection connection;
		try {
			connection = DriverManager.getConnection(properties.getProperty("db.url"),
					properties.getProperty("db.user"), properties.getProperty("db.password"));
			this.WEB_PAGES = new PagesDBDAO(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		flag = true;
		// PagesDBDAO dataBase = new PagesDBDAO(connection);
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {

			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			// Map<String, String> resourceParameters =
			// request.getResourceParameters();
			// String page = resourceParameters.get("uuid");

			// Responder al cliente
			HTTPResponse response = new HTTPResponse();
			response.setVersion("HTTP/1.1");
			response.setStatus(HTTPResponseStatus.S200);
			response.putParameter("Content-Type", "text/html");

			if (flag) {
				// POST
				if (request.getMethod() == HTTPRequestMethod.POST) {
					String uuid = UUID.randomUUID().toString();

					if (request.getResourceParameters().get("html") == null)
						response.setStatus(HTTPResponseStatus.S400);
					this.WEB_PAGES.create(uuid,request);
					System.out.println("ENLACE: " + WEB_PAGES.link(uuid));
					response.setContent(WEB_PAGES.link(uuid));
					
					System.err.println(this.WEB_PAGES.exists(request) + WEB_PAGES.link(uuid));
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
					if (!request.getResourceName().contains("html"))
						response.setStatus(HTTPResponseStatus.S400);
					else { // Si no hay UUID
						if (request.getResourceParameters().get("uuid") == null) {
							// response.setContent("Hybrid Server");
							response.setContent(this.WEB_PAGES.list());
						} else { // Si hay UUID
							System.err.println(this.WEB_PAGES.exists(request));
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
			} else
				response.setContent(WEB);

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
