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
	
	public HTTPResponse createResponse() {
		this.response.setVersion("HTTP/1.1");
		this.response.setStatus(HTTPResponseStatus.S200);
		String type;
		if(request.getResourcePath().length == 0) {
			type = "html";
		} else {
			type = request.getResourcePath()[0];
		}
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
		if (!this.request.getResourceName().contains(type)){
			this.response.setStatus(HTTPResponseStatus.S200);
			StringBuilder uuids = new StringBuilder();
			uuids.append("<h1>Hybrid Server</h1>");
			uuids.append(this.WEB_PAGES.list());
			List<HSService> remoteServices = connectService(config);
			if(!remoteServices.isEmpty()) {
<<<<<<< HEAD
				for (HSService hsService : remoteServices) {
					String contenido = hsService.getUUID(type);
					if(!contenido.isEmpty()) {
						uuids.append(contenido);
=======
				System.err.println("aquiQ1");
				for (HSService hsService : remoteServices) {
					String contenido = hsService.getUUID(type);
					if(!contenido.isEmpty()) {
						//uuids.append("<h1>" /* + Nombre Server */ + "</h1>");
						uuids.append(contenido);
						System.err.println("aquiQ2");
>>>>>>> origin/master
					}
				}
				this.response.setContent(uuids.toString());
			} else {
				this.response.setContent(this.WEB_PAGES.list());
<<<<<<< HEAD
			}
		} else { // Si no hay UUID
=======
				System.err.println("aquiQ3");
			}
			System.err.println("aqui1");
		} else { // Si no hay UUID
			System.err.println("aqui2");
>>>>>>> origin/master
			if (this.request.getResourceParameters().get("uuid") == null) {
				System.err.println("aqui3");
				StringBuilder uuids = new StringBuilder();
<<<<<<< HEAD
=======
				//uuids.append("<h1>Local Server</h1>");
>>>>>>> origin/master
				uuids.append(this.WEB_PAGES.list());
				List<HSService> remoteServices = connectService(config);
				if(!remoteServices.isEmpty()) {
					System.err.println("aqui4");
					for (HSService hsService : remoteServices) {
						String contenido = hsService.getUUID(type);
						if(!contenido.isEmpty()) {
<<<<<<< HEAD
=======
							//uuids.append("<h1>" /* + Nombre Server */ + "</h1>");
>>>>>>> origin/master
							uuids.append(contenido);
							System.err.println("aqui5");
						}
					}
					this.response.setContent(uuids.toString());
				} else {
					this.response.setContent(this.WEB_PAGES.list());
					System.err.println("aqui6");
				}
			} else { // Si hay UUID
				System.err.println("aqui7");
				if (this.WEB_PAGES.exists(this.request)) {
					System.err.println("aqui8");
					if (type.equals("xml") && this.request.getResourceParameters().get("xslt") != null){
						System.err.println("aqui9");
						transform(request);
					} else{
<<<<<<< HEAD
=======
						System.err.println("aqui10");
>>>>>>> origin/master
						this.response.setContent(this.WEB_PAGES.get(this.request)); // UUIDexistente:visualiza página
					}
				} else {
					System.err.println("aqui11");
					if (!this.WEB_PAGES.exists(this.request)) {
						System.err.println("aqui12");
						List<HSService> remoteServices = connectService(config);
						if(!remoteServices.isEmpty()) {
							System.err.println("aqui13");
							String contenido = null;
							for (HSService hsService : remoteServices) {
								System.err.println("aqui14");
								contenido = hsService.getContent(this.request.getResourceParameters().get("uuid"),type);
								if(contenido != null) break;
							}
							if(contenido != null) {
<<<<<<< HEAD
								if (type.equals("xml") && this.request.getResourceParameters().get("xslt") != null){
									transform(request);
								} else {
									this.response.setContent(contenido);
								}
=======
								System.err.println("aqui15");
								if (type.equals("xml") && this.request.getResourceParameters().get("xslt") != null){
									System.err.println("aqui15.1");
									transform(request);
								} else{
									System.err.println("aqui15.2");
									this.response.setContent(contenido);
									//this.response.setContent(this.WEB_PAGES.get(this.request)); // UUIDexistente:visualiza página
								}
								//
>>>>>>> origin/master
							} else {
								System.err.println("aqui16");
								this.response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
							}
						} else {
							System.err.println("aqui17");
							this.response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
						}
					} else {
<<<<<<< HEAD
=======
						System.err.println("aqui18");
>>>>>>> origin/master
						this.response.setStatus(HTTPResponseStatus.S500);
					}
				}
			}
		}
	}
	
	public void transform(HTTPRequest request){
		String xmlid = this.request.getResourceParameters().get("uuid");
		String xsltid = this.request.getResourceParameters().get("xslt");
		DBDAOxslt dbdao = new DBDAOxslt(connection);
		String xsdid = dbdao.recuperarXSD(xsltid);

		List<HSService> remote = this.connectService(this.config);
		if (xsdid == null){
<<<<<<< HEAD
			for (HSService hsService : remote) {
				String xsduuid = hsService.uuidXSDofXSLT(xsltid);
				if(xsduuid != null){
=======
			System.err.println("aquiT1");
			for (HSService hsService : remote) {
				System.err.println("aquiT2");
				String xsduuid = hsService.uuidXSDofXSLT(xsltid);
				if(xsduuid != null){
					System.err.println("aquiT3");
>>>>>>> origin/master
					xsdid = xsduuid;
					break;
				}	
			}
		}
		boolean exists = false;
		if(!dbdao.existsUUID(xsltid)){	
<<<<<<< HEAD
			for (HSService hsService : remote) {
				if(hsService.getContent(xsdid, "xsd") != null) {
=======
			System.err.println("aquiT4");
			for (HSService hsService : remote) {
				System.err.println("aquiT5");
				if(hsService.getContent(xsdid, "xsd") != null){
					System.err.println("aquiT6");
>>>>>>> origin/master
					exists = true;
					break;
				}
			}
		} else{
<<<<<<< HEAD
			exists = true;
		}
		if (!exists){
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
=======
			System.err.println("aquiT7");
			exists = true;
		}
		if (!exists){
			System.err.println("aquiT8");
			this.response.setStatus(HTTPResponseStatus.S404);
		} else {
			try{
				System.err.println("aquiT9");
				if(loadAndValidateWithExternalXSD(xmlid, xsdid) == null) {
					this.response.setStatus(HTTPResponseStatus.S400);
					System.err.println("aquiT10");
				} else {
					try {
						System.err.println("aquiT11");
						response.removeParameter("Content-Type");
						response.putParameter("Content-Type", "text/html");
						this.response.setContent(transformWithXSLT(xmlid, xsltid));
						System.err.println("aquiA1");
						System.err.println(this.response.getContent());
>>>>>>> origin/master
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
		String xmlContent = dbdaoxml.getContent(xml);
		String xsdContent = dbdaoxsd.getContent(xsd);
		List<HSService> remote = this.connectService(this.config);
		if(xmlContent == null){
			for (HSService hsService : remote) {
				String xmlC = hsService.getContent(xml, "xml");
				if(xmlC != null){
					xmlContent = xmlC;
					break;
				}
			}
		}
		if(xsdContent == null){
			for (HSService hsService : remote) {
				String xsdC = hsService.getContent(xsd, "xsd");
				if(xsdC != null){
					xsdContent = xsdC;
					break;
				}
			}
		}
		BufferedReader xsdReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xsdContent.getBytes())));
		BufferedReader xmlReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xmlContent.getBytes())));
		
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

		String xmlContent = dbdaoxml.getContent(xml);;
		String xsltContent = dbdaoxslt.getContent(xslt);;
		List<HSService> remote = this.connectService(this.config);
		if(xmlContent == null){
<<<<<<< HEAD
			for (HSService hsService : remote) {
				String xmlC = hsService.getContent(xml, "xml");
				if(xmlC != null){
=======
			System.err.println("aquiX1");
			for (HSService hsService : remote) {
				String xmlC = hsService.getContent(xml, "xml");
				System.err.println("aquiX2");
				if(xmlC != null){
					System.err.println("aquiX3");
>>>>>>> origin/master
					xmlContent = xmlC;
					break;
				}
			}
		}
		if(xsltContent == null){
<<<<<<< HEAD
			for (HSService hsService : remote) {
				String xsltC = hsService.getContent(xslt, "xslt");
				if(xsltC != null){
=======
			System.err.println("aquiX4");
			for (HSService hsService : remote) {
				System.err.println("aquiX5");
				String xsltC = hsService.getContent(xslt, "xslt");
				if(xsltC != null){
					System.err.println("aquiX6");
>>>>>>> origin/master
					xsltContent = xsltC;
					break;
				}
			}
		}
		BufferedReader xsltReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xsltContent.getBytes())));
		BufferedReader xmlReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(xmlContent.getBytes())));
		
		Transformer transformer = null;
		TransformerFactory tFactory = TransformerFactory.newInstance();
		transformer = tFactory.newTransformer(new StreamSource(xsltReader));
		
		StringWriter writer = new StringWriter();
		transformer.transform(new StreamSource(xmlReader), new StreamResult(writer));
		System.err.println(writer.toString());
		System.err.println("aquiX7");
		return writer.toString();
	}
	
	public List<HSService> connectService(Configuration config) {
		List<HSService> remoteServices = new ArrayList<>();
		if(config != null) {
			List<ServerConfiguration> servers = config.getServers();
			for(int i = 0; i < servers.size(); i++) {
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
