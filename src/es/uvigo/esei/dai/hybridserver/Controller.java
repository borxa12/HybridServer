package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
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
	private Configuration config;
	
	public Controller(HTTPRequest request, Connection connection, Pages pages, Configuration config) {
		this.request = request;
		this.WEB_PAGES = pages;
		this.connection = connection;
		this.response = new HTTPResponse();
		this.config = config;
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

	public void get(String type){
		// Si ResourceName no contiene HTML: error 400
		if (!this.request.getResourceName().contains(type))
			this.response.setStatus(HTTPResponseStatus.S400);
		else { // Si no hay UUID
			if (this.request.getResourceParameters().get("uuid") == null) {
				StringBuilder uuids = new StringBuilder();
				uuids.append("<h1>Local Server</h1>");
				uuids.append(this.WEB_PAGES.list());
				List<HSService> remoteServices = connectService(config);
				if(!remoteServices.isEmpty()) {
					for (HSService hsService : remoteServices) {
						String contenido = hsService.getUUID(type);
						if(!contenido.isEmpty()) {
							uuids.append("<h1>" /* + Nombre Server */ + "</h1>");
							uuids.append(contenido);
						}
					}
					this.response.setContent(uuids.toString());
				} else {
					this.response.setContent(this.WEB_PAGES.list());
				}
			} else { // Si hay UUID
				if (this.WEB_PAGES.exists(this.request)) {
					if (type.equals("xml") && this.request.getResourceParameters().get("xslt") != null){
						transform(request);
					} else{
					this.response.setContent(this.WEB_PAGES.get(this.request)); // UUIDexistente:visualiza página
					}
				} else {
					if (!this.WEB_PAGES.exists(this.request)) {
						List<HSService> remoteServices = connectService(config);
						if(!remoteServices.isEmpty()) {
							String contenido = null;
							for (HSService hsService : remoteServices) {
								contenido = hsService.getContent(this.request.getResourceParameters().get("uuid"),type);
								if(contenido != null) break;
							}
							if(contenido != null) {
								this.response.setContent(contenido);
							} else {
								this.response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
							}
						} else {
							this.response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
						}
					} else this.response.setStatus(HTTPResponseStatus.S500);
				}
			}
		}
	}
	
	public void transform(HTTPRequest request){
		String xmlid = this.request.getResourceParameters().get("uuid");
		String xsltid = this.request.getResourceParameters().get("xslt");
		DBDAOxslt dbdao = new DBDAOxslt(connection);
		String xsdid = dbdao.recuperarXSD(xsltid);
		
		if(!dbdao.existsUUID(xsltid)){
			this.response.setStatus(HTTPResponseStatus.S404);
		} else {
				try{
					if(loadAndValidateWithExternalXSD(xmlid, xsdid) == null) {
						this.response.setStatus(HTTPResponseStatus.S400);
					} else {
						try {
							response.removeParameter("Content-Type");
							response.putParameter("Content-Type", "text/html");
							this.response.setContent(transformWithXSLT(xmlid, xsltid));
						} catch (TransformerException e) {
							this.response.setStatus(HTTPResponseStatus.S400);
						}
					}
				} catch(ParserConfigurationException | SAXException | IOException e) {
					this.response.setStatus(HTTPResponseStatus.S400);
				}
		}
	}
	
	public Document loadAndValidateWithExternalXSD(String xml, String xsd)
			throws ParserConfigurationException, SAXException, IOException {
		// Construccion del schema
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		DBDAOxml dbdaoxml = new DBDAOxml(connection);
		DBDAOxsd dbdaoxsd = new DBDAOxsd(connection);
		
		BufferedReader xsdReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(dbdaoxsd.getContent(xsd).getBytes())));
		BufferedReader xmlReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(dbdaoxml.getContent(xml).getBytes())));
		
		Source xsdSource = new StreamSource(xsdReader);
		InputSource xmlSource = new InputSource(xmlReader);
		Schema schema = schemaFactory.newSchema(xsdSource);
		
		// Construccion del parser del documento. Se establece el esquema y se activa la validacion y comprobacion de namespaces
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		
		// Se añade el manejador de errores
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new SimpleErrorHandler());
		
		return builder.parse(xmlSource);
	}
	
	public String transformWithXSLT(String xml, String xslt) throws TransformerException {
		
		DBDAOxml dbdaoxml = new DBDAOxml(connection);
		DBDAOxslt dbdaoxslt = new DBDAOxslt(connection);

		BufferedReader xsltReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(dbdaoxslt.getContent(xslt).getBytes())));
		BufferedReader xmlReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(dbdaoxml.getContent(xml).getBytes())));
		
		Transformer transformer = null;
		TransformerFactory tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(new StreamSource(xsltReader));
		
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(xmlReader), new StreamResult(writer));
		return writer.toString();
	}
	
	public List<HSService> connectService(Configuration config) {
		List<HSService> remoteServices = new ArrayList<>();
		if(config != null) {
			List<ServerConfiguration> servers = config.getServers();
			for(int i = 0; i < servers.size(); i++) {
				//Service service = null;
				try {
					Service service = Service.create(new URL(servers.get(i).getWsdl()),
							new QName(servers.get(i).getNamespace(),servers.get(i).getService()));
					remoteServices.add(service.getPort(HSService.class));
				} catch (MalformedURLException malURLe) {
					System.err.println("URL mal formada: " + malURLe.getMessage());
				} catch (WebServiceException webSe) {
					System.err.println("Error WebService: " + webSe.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return remoteServices;
	}
	
}
