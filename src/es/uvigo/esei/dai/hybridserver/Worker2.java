package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Worker2 implements Runnable {

	private Socket socket;
	private ServerPages WEB_PAGES;

	public Worker2(Socket socket, ServerPages pages) {
		this.socket = socket;
		this.WEB_PAGES = pages;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {

			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			Map<String, String> resourceParameters = request.getResourceParameters();
			String page = resourceParameters.get("uuid");

			// Responder al cliente
			HTTPResponse response = new HTTPResponse();
			response.setVersion("HTTP/1.1");
			response.setStatus(HTTPResponseStatus.S200);
			response.putParameter("Content-Type", "text/html");

			// POST
			if (request.getMethod() == HTTPRequestMethod.POST) {
				String uuid = UUID.randomUUID().toString();

				if (request.getResourceParameters().get("html") == null)
					response.setStatus(HTTPResponseStatus.S400);
				this.WEB_PAGES.add(uuid, request.getResourceParameters().get("html"));
				System.out.println("ENLACE: " + WEB_PAGES.getUUID(uuid));
				response.setContent(WEB_PAGES.getUUID(uuid));

			}

			// DELETE
			if (request.getMethod() == HTTPRequestMethod.DELETE) {
				if (!WEB_PAGES.exists(page))
					response.setStatus(HTTPResponseStatus.S404);
				this.WEB_PAGES.remove(page);
			}
			
			// GET
			if (request.getMethod() == HTTPRequestMethod.GET) {
				// Si ResourceName no contiene HTML: error 400
				if (!request.getResourceName().contains("html"))
					response.setStatus(HTTPResponseStatus.S400);
				else { // Si no hay UUID
					if (page == null) {
						// response.setContent("Hybrid Server");
						response.setContent(this.WEB_PAGES.getUUID());
					} else { // Si hay UUID
						if (this.WEB_PAGES.exists(page)) {

							response.setContent(WEB_PAGES.getContent(page)); // UUIDexistente:visualiza página

						} else {
							if (!this.WEB_PAGES.exists(page))
								response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
							else
								response.setStatus(HTTPResponseStatus.S500);
						}
					}
				}

			}

			System.out.println("REQUEST: \r\n" + request + "\r\n");
			System.out.println("RESPONSE: \r\n" + response + "\r\n");

			OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
			response.print(out);
			out.flush();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (HTTPParseException e) {
			e.printStackTrace();
		}
	}

}
