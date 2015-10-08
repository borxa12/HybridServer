package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private BufferedReader bf;
	private HTTPRequestMethod method;
	private String resourceChain;
	private String[] resourcePath;
	private String resourceName;
	private Map<String,String> resourceParameters;
	private String HTTPVersion;
	private Map<String,String> headerParameters;
	private String content;
	private int contentLength;
	//private String toString;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		bf = new BufferedReader(reader);
		//this.method = null;
		//this.resourceChain = null;
		//this.resourcePath = null;
		//this.resourceName = null;
		//this.resourceParameters = null;
		//this.HTTPVersion = null;
		//this.headerParameters = null;
		//this.content = null;
		//this.contentLength = 0;
		//this.toString = null;
		this.createMethod();
		this.createResourceChain();
		this.createResourcePath();
		this.createResourceName();
		this.createResourceParameters();
		this.createHttpVersion();
		this.createHeaderParameters();
		this.createContent();
		this.createContentLength();
	}
	
	/**
	 * Métodos que llaman a los método creadores y devuelven los atributos
	 */
	public HTTPRequestMethod getMethod() {
		return this.method;
	}
	
	public String getResourceChain() {
		return this.resourceChain;
	}
	
	public String[] getResourcePath() {
		return this.resourcePath;
	}
	
	public String getResourceName() {
		return this.resourceName;
	}
	
	public Map<String, String> getResourceParameters() {
		return this.resourceParameters;
	}
	
	public String getHttpVersion() {
		return this.HTTPVersion;
	}
	
	public Map<String, String> getHeaderParameters() {
		return this.headerParameters;
	}
	
	public String getContent() {
		return this.content;
	}
	
	public int getContentLength() {
		return this.contentLength;
	}
	
	/**
	 * Métodos creadores de los atributos
	 */
	public void createMethod() {
		HTTPRequestMethod method = null;
		try {
			String linea = bf.readLine();
			String[] seccion = linea.split(" ");
			String metodo = seccion[0].trim();
			method = HTTPRequestMethod.valueOf(metodo);
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		this.method = method;
	}

	// "/hello/world.html?country=Spain&province=Ourense&city=Ourense"
	public void createResourceChain() {
		String resourceChain = null;
		String linea;
		try {
			linea = bf.readLine();
			String[] seccion = linea.split(" ");
			resourceChain = seccion[1];
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		this.resourceChain = resourceChain;
	}

	// "hello" "world.html"
	public void createResourcePath() {
		String[] chain = this.getResourceChain().split("\\?");
		String algo = "algo";
		chain[0] = algo.concat(chain[0]);
		String[] path = chain[0].split("\\/");
		String[] resourcePath = new String[path.length - 1];
		for (int i = 0; i < path.length - 1; i++) {
			resourcePath[i] = path[i + 1];
		}
		this.resourcePath = resourcePath;
	}

	// "hello/world.html"
	public void createResourceName() {
		String[] resourcePath = this.getResourcePath();
		String resourceName = null;
		if (resourcePath.length == 0) {
			resourceName = "";
		} else if (resourcePath.length == 1) {
			resourceName = resourcePath[0].toString();
		} else if (resourcePath.length > 1) {
			resourceName = resourcePath[0];
			for (int i = 1; i < resourcePath.length; i++) {
				resourceName = resourceName.concat("/");
				resourceName = resourceName.concat(resourcePath[i]);
			}
		}
		this.resourceName = resourceName;
	}

	// Map<String,String> {"message", "Hello world!!"}
	public void createResourceParameters() {
		Map<String, String> resourceParameters = new LinkedHashMap<>();
		String[] resourceChain = this.getResourceChain().split("\\?");
		if (resourceChain.length == 1) {
			try {
				String linea;
				String reader = "";
				while ((linea = bf.readLine()) != null) {
					linea = linea.concat("\r\n");
					reader = reader.concat(linea);
				}
				reader = URLDecoder.decode(reader, "UTF-8");
				String[] chain = reader.split("\r\n");
				int pos = 0;
				for (int i = 0; i < chain.length; i++) {
					if (chain[i].isEmpty())
						pos = i;
				}
				if (pos == 0) {
					resourceParameters = new HashMap<>();
				} else {
					String[] resource_Chain = chain[pos + 1].split("&");
					resourceParameters = new HashMap<>();
					for (int i = 0; i < resource_Chain.length; i++) {
						String[] aux = resource_Chain[i].split("=");
						resourceParameters.put(aux[0], aux[1]);
					}
				}
			} catch (IOException ioe) {
				System.err.println(ioe.getMessage());
			}
		} else {
			String[] parameters = resourceChain[1].split("&");
			resourceParameters = new HashMap<>();
			for (int i = 0; i < parameters.length; i++) {
				String[] param = parameters[i].split("=");
				resourceParameters.put(param[0], param[1]);
			}
		}
		this.resourceParameters = resourceParameters;
	}

	public void createHttpVersion() {
		String[] aux = null;
		try {
			String head = this.bf.readLine();
			aux = head.split(" ");
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		this.HTTPVersion = aux[2];
	}

	public void createHeaderParameters() {
		Map<String, String> headerParameters = new HashMap<>();
		try {
			String linea;
			String reader = "";
			while ((linea = bf.readLine()) != null) {
				linea = linea.concat("\r\n");
				reader = reader.concat(linea);
			}
			reader = URLDecoder.decode(reader, "UTF-8");
			String[] chain = reader.split("\r\n");
			ArrayList<String> parameters = new ArrayList<>();
			for(int i = 1; i < chain.length; i++) {
				if(chain[i].isEmpty())
					break;
				parameters.add(chain[i]);
			}
			String[] aux = new String[parameters.size()];
			for(int i = 0; i < parameters.size(); i++) {
				aux[i] = parameters.get(i);
			}
			for(int i = 0; i < aux.length; i++) {
				String[] aux2 = aux[i].split(": ");
				headerParameters.put(aux2[0],aux2[1]);
			}
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		this.headerParameters = headerParameters;
	}

	public void createContent() {
		String content = "";
		try {
			String linea;
			String reader = "";
			while ((linea = bf.readLine()) != null) {
				linea = linea.concat("\r\n");
				reader = reader.concat(linea);
			}
			reader = URLDecoder.decode(reader, "UTF-8");
			String[] chain = reader.split("\r\n");
			int pos = 0;
			for (int i = 0; i < chain.length; i++) {
				if (chain[i].isEmpty())
					pos = i;
			}
			if (pos == 0) {
				content = null;
			} else {
				content = chain[pos+1];
			}
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		this.content = content;
	}

	public void createContentLength() {
		int contentLength = 0;
		try {
			String linea;
			String reader = "";
			while ((linea = bf.readLine()) != null) {
				linea = linea.concat("\r\n");
				reader = reader.concat(linea);
			}
			reader = URLDecoder.decode(reader, "UTF-8");
			String[] chain = reader.split("\r\n");
			int pos = 0;
			for (int i = 0; i < chain.length; i++) {
				if (chain[i].contains("Content-Length: "))
					pos = i;
			}
			if (pos == 0) {
				contentLength = 0;
			} else {
				String[] aux = chain[pos].split(": ");
				contentLength = Integer.parseInt(aux[1]);
			}
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		this.contentLength = contentLength;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(this.getMethod().name()).append(' ').append(this.getResourceChain())
				.append(' ').append(this.getHttpVersion()).append("\r\n");

		for (Map.Entry<String, String> param : this.getHeaderParameters().entrySet()) {
			sb.append(param.getKey()).append(": ").append(param.getValue()).append("\r\n");
		}

		if (this.getContentLength() > 0) {
			sb.append("\r\n").append(this.getContent());
		}

		return sb.toString();
	}
}
