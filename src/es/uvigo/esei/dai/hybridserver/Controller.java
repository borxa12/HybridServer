package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.util.UUID;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Controller {

	private HTTPRequest request;
	private Pages WEB_PAGES;
	private Connection connection;
	private HTTPResponse response;
	
	public Controller(HTTPRequest request, Connection connection, Pages pages) {
		this.request = request;
		this.WEB_PAGES = pages;
		this.connection = connection;
		this.response = new HTTPResponse();
	}
	
	public HTTPResponse createResponse(){
		//Crea el resonnse
		this.response.setVersion("HTTP/1.1");
		this.response.setStatus(HTTPResponseStatus.S200);
		String type = request.getResourcePath()[0];
		System.err.println(type);//ahodfgohsdkgvjoajdsfjpasdjfkgjasdkfj
		switch (type) {

		case "html":
			this.WEB_PAGES = new DBDAOhtml(connection);
			this.response.putParameter("Content-Type", "text/html");
			if (this.request.getMethod() == HTTPRequestMethod.POST) {
				post(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.DELETE) {
				delete(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.GET) {
				get(type);
			}
			break;
		case "xml":
			this.WEB_PAGES = new DBDAOxml(connection);
			this.response.putParameter("Content-Type", "application/xml");
			if (this.request.getMethod() == HTTPRequestMethod.POST) {
				post(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.DELETE) {
				delete(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.GET) {
				get(type);
			}
			break;
		case "xsd":
			this.WEB_PAGES = new DBDAOxsd(connection);
			response.putParameter("Content-Type", "application/xml");
			if (this.request.getMethod() == HTTPRequestMethod.POST) {
				post(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.DELETE) {
				delete(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.GET) {
				get(type);
			}
			break;
		case "xslt":
			this.WEB_PAGES = new DBDAOxslt(connection);	
			response.putParameter("Content-Type", "application/xml");
			if (this.request.getMethod() == HTTPRequestMethod.POST) {
				post(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.DELETE) {
				delete(type);
			}
			if (this.request.getMethod() == HTTPRequestMethod.GET) {
				get(type);
			}
			break;
			default:
				this.response.setStatus(HTTPResponseStatus.S400);
				break;

		}
	
		return this.response;
		
				
	}

	public void post(String type){

			String uuid = UUID.randomUUID().toString();
			switch (type) {
			case "xslt":
				if(this.request.getContent() == null || this.request.getResourceParameters().get("xsd") == null) {
					this.response.setStatus(HTTPResponseStatus.S400);
				} else {
					if(this.request.getResourceParameters().get("xsd") != null &&
							!this.WEB_PAGES.exists(this.request)) {
						this.response.setStatus(HTTPResponseStatus.S404);
					} else {
						this.WEB_PAGES.create(uuid, this.request);
						this.response.setContent(this.WEB_PAGES.link(uuid));
					}
				}
				break;

			default:
				if (this.request.getResourceParameters().get(type) == null )
					this.response.setStatus(HTTPResponseStatus.S400);
				else
					this.WEB_PAGES.create(uuid, this.request);

				this.response.setContent(this.WEB_PAGES.link(uuid));
				break;
			}
			
		}
		
	public void delete(String type){

		if (!this.WEB_PAGES.exists(this.request))
			this.response.setStatus(HTTPResponseStatus.S404);
		this.WEB_PAGES.remove(this.request);
	}

	public void get(String type){
		// Si ResourceName no contiene HTML: error 400
		if (!this.request.getResourceName().contains(type))
			this.response.setStatus(HTTPResponseStatus.S400);
		else { // Si no hay UUID
			if (this.request.getResourceParameters().get("uuid") == null) {
				this.response.setContent(this.WEB_PAGES.list());
			} else { // Si hay UUID

				if (this.WEB_PAGES.exists(this.request)) {

					this.response.setContent(this.WEB_PAGES.get(this.request)); // UUIDexistente:visualiza página

				} else {
					if (!this.WEB_PAGES.exists(this.request))
						this.response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
					else
						this.response.setStatus(HTTPResponseStatus.S500);
				}
			}
		}
	}
}
