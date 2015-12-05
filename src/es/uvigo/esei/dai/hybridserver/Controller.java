package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

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

	public void get(String type) {
		// Si ResourceName no contiene HTML: error 400
		if (!this.request.getResourceName().startsWith(type))
			this.response.setStatus(HTTPResponseStatus.S400);
		else { // Si no hay UUID
			if (this.request.getResourceParameters().get("uuid") == null) {
				this.response.setContent(this.WEB_PAGES.list());
			} else { // Si hay UUID

				if (this.WEB_PAGES.exists(this.request)) {
					if(type == "xml"){
						if (this.request.getResourceParameters().get("xslt") != null) { // XML con uuid && parámetro xslt
							String xmlid = this.request.getResourceParameters().get("uuid");
							String xsltid = this.request.getResourceParameters().get("xslt");
							DBDAOxslt dbdao = new DBDAOxslt(connection);
							String xsd = dbdao.recuperarXSD(xsltid);
							if(xsd != null){
								try{
									if(XMLConfigurationLoader.loadAndValidateWithExternalXSD(xmlid, xsd) != null){
										try{
											this.response.setContent(XMLConfigurationLoader.transformWithXSLT(new File(xmlid), new File(xsltid)));
										} catch (TransformerException e){
											System.err.println("ERROR en la transformación (XML conXSLT): "+e.getMessage());
										}
										response.removeParameter("Content-Type");
										response.putParameter("Content-Type", "text/html");
										this.response.setContent(this.WEB_PAGES.get(request));
									} else {
										this.response.setStatus(HTTPResponseStatus.S400);
									}
								} catch(ParserConfigurationException | SAXException | IOException e){
									e.printStackTrace();
								}
							}else{
								this.response.setStatus(HTTPResponseStatus.S400);
							}
						} else {
							
							this.response.setStatus(HTTPResponseStatus.S404);
						}
					} else {
						
						this.response.setContent(this.WEB_PAGES.get(request));// UUIDexistente:visualiza página
					}
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
