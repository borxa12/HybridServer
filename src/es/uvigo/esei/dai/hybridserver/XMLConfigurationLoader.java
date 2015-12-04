/**
 *  HybridServer
 *  Copyright (C) 2014 Miguel Reboiro-Jato
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.esei.dai.hybridserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfigurationLoader {
	
	private final static String XSD_PATH = "configuration.xsd";
	
	public static Document loadAndValidateWithExternalXSD(String documentPath, String schemaPath) 
	throws ParserConfigurationException, SAXException, IOException {
		// ConstrucciÃ³n del schema
		SchemaFactory schemaFactory = 
			SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(new File(schemaPath));
		
		// ConstrucciÃ³n del parser del documento. Se establece el esquema y se activa
		// la validaciÃ³n y comprobaciÃ³n de namespaces
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		factory.setSchema(schema);
		
		// Se aÃ±ade el manejador de errores
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		return builder.parse(new File(documentPath));
	}
	
	public static Configuration load(File xmlFile)
	throws  Exception {
		try {
			Configuration config = new Configuration();
			String xmlPath = xmlFile.getName();
			Document document = loadAndValidateWithExternalXSD(xmlPath, XSD_PATH);
			
			int httpPort = Integer.parseInt(document.getElementsByTagName("http").item(0).getTextContent().trim());
			int numClients =  Integer.parseInt(document.getElementsByTagName("numClients").item(0).getTextContent().trim());
			
			String webServiceURL = document.getElementsByTagName("webservice").item(0).getTextContent().trim();
			String dbUser = document.getElementsByTagName("user").item(0).getTextContent().trim();
			String dbPassword = document.getElementsByTagName("password").item(0).getTextContent().trim();
			String dbURL = document.getElementsByTagName("url").item(0).getTextContent().trim();
			
			List<ServerConfiguration> servers = new ArrayList<>();

			NodeList serverslist = document.getElementsByTagName("server");
		
			for(int i=0; i< serverslist.getLength(); i++){
				
				ServerConfiguration server = new ServerConfiguration();
				Element node = (Element) serverslist.item(i);
				
				server.setHttpAddress(node.getAttribute("httpAddress"));
				server.setName(node.getAttribute("name"));
				server.setNamespace(node.getAttribute("namespace"));
				server.setService(node.getAttribute("service"));
				server.setWsdl(node.getAttribute("wsdl"));
				
				servers.add(server);
			}
			
			config.setHttpPort(httpPort);
			config.setNumClients(numClients);
			config.setWebServiceURL(webServiceURL);
			config.setDbUser(dbUser);
			config.setDbPassword(dbPassword);
			config.setDbURL(dbURL);
			config.setServers(servers);
			return config;		
			
		} catch (Exception e) {

			throw new Exception();
		}
	}
}
