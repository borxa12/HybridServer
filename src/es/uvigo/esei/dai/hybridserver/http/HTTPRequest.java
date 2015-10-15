package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class HTTPRequest {

	private BufferedReader bf;

	// Atributos
	private HTTPRequestMethod method;
	private String resourceChain;
	private String[] resourcePath;
	private String resourceName;
	private Map<String, String> resourceParameters;
	private String HTTPVersion;
	private Map<String, String> headerParameters;
	private String content;
	private int contentLength;

	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		bf = new BufferedReader(reader);

		/**
		 * Linea 1
		 */
		String linea = bf.readLine();
		String[] seccion = linea.split(" ");
		
		//method
		String metodo = seccion[0].trim();
		try {
			this.method = HTTPRequestMethod.valueOf(metodo);
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new HTTPParseException("Missing Method");
		}
		
		//resourceChain
		this.resourceChain = seccion[1];
		if(this.resourceChain.contains("HTTP/1.1")) throw new HTTPParseException("Missing resource");
		
		//HTTPVersion
		if(seccion.length < 3) throw new HTTPParseException("Missing version");
		this.HTTPVersion = seccion[2].trim();

		// ResourcePath
		String[] chain = this.resourceChain.split("\\?");
		String algo = "algo";
		chain[0] = algo.concat(chain[0]);
		String[] path = chain[0].split("\\/");
		String[] resourcePath = new String[path.length - 1];
		for (int i = 0; i < path.length - 1; i++) {
			resourcePath[i] = path[i + 1];
		}
		this.resourcePath = resourcePath;

		// ResourceName
		String[] rp = this.resourcePath;
		String resourceName = null;
		if (rp.length == 0) {
			resourceName = "";
		} else if (rp.length == 1) {
			resourceName = rp[0].toString();
		} else if (rp.length > 1) {
			resourceName = rp[0];
			for (int i = 1; i < rp.length; i++) {
				resourceName = resourceName.concat("/");
				resourceName = resourceName.concat(resourcePath[i]);
			}
		}
		this.resourceName = resourceName;
		
		// Variables de control
		String[] resourceChain = this.resourceChain.split("\\?");
		int flag;
		if (resourceChain.length == 1)
			flag = 0; // resourceParameters en el resto de lineas
		else
			flag = 1; // resourceParameters en primera linea

		boolean emptyLine = false; // Bandera de informacion sobre linea vacia
		
		// Mapas para resourceParameters y heaerParameters
		Map<String, String> resourceParameters = new LinkedHashMap<>();
		Map<String, String> headerParameters = new LinkedHashMap<>();
		
		//Busqueda de los resourceParameters en la primera linea
		if (flag == 1) {
			String[] parameters = resourceChain[1].split("&");
			for (int i = 0; i < parameters.length; i++) {
				String[] param = parameters[i].split("=");
				resourceParameters.put(param[0], param[1]);
			}
		}
		this.resourceParameters = resourceParameters;
		
		/**
		 * Resto de lineas
		 */
		//while ((linea = bf.readLine()) != null && !emptyLine) {
		while(!emptyLine) {
			
			linea = bf.readLine();
			
			String read = URLDecoder.decode(linea, "UTF-8"); // Conversión de la linea a UTF-8
			
			// Busqueda de headerParameters
			if (!read.isEmpty() && !emptyLine) {
				String[] aux = read.split(": ");
				if(aux.length == 1) throw new HTTPParseException("Invalid header");
				headerParameters.put(aux[0], aux[1]);
			}
			
//			// Busqueda de resourceParameters
//			if (flag == 0 && emptyLine) {
//				String[] param_1 = read.split("&");
//				for (int i = 0; i < param_1.length; i++) {
//					String[] param_2 = param_1[i].split("=");
//					resourceParameters.put(param_2[0], param_2[1]);
//					System.out.println(resourceParameters);
//				}
//			}
			
//			// Busqueda de content
//			if (emptyLine) {
//				this.content = read;
//			}
			
			// Busqueda de contentLength
			if (read.contains("Content-Length: ")) {
				String[] aux = read.split(": ");
				this.contentLength = Integer.parseInt(aux[1]);
			}
			
			// Comprobacion de lectura de linea vacio (variable de control)
			if (read.isEmpty())
				emptyLine = true;

		}
		
		// Asignacion de los mapas a sus atributos tras la lectura de todas las lineas
		this.headerParameters = headerParameters;
		this.resourceParameters = resourceParameters;
		
		// Busqueda de content
		if (emptyLine && headerParameters.containsKey("Content-Length")) {
			StringBuilder toret = new StringBuilder();
			for(int i = 0; i < Integer.parseInt(headerParameters.get("Content-Length")); i++) {
				toret.append((char)bf.read());
			}
			String contentReturns = URLDecoder.decode(toret.toString(), "UTF-8"); // Conversión de la linea a UTF-8
			this.content = contentReturns;
			if(flag == 0) {
				String[] param_1 = contentReturns.split("&");
				for (int i = 0; i < param_1.length; i++) {
					String[] param_2 = param_1[i].split("=");
					resourceParameters.put(param_2[0], param_2[1]);
				}
			}
		}
	}

	public HTTPRequestMethod getMethod() {
		//if(this.method == null) new HTTPParseException("Missing Method" + this.toString());
		return this.method;
	}

	// "/hello/world.html?country=Spain&province=Ourense&city=Ourense"
	public String getResourceChain() {
		return this.resourceChain;
	}

	// "hello" "world.html"
	public String[] getResourcePath() {
		return this.resourcePath;
	}

	// "hello/world.html"
	public String getResourceName() {
		return this.resourceName;
	}

	// Map<String,String> {"message", "Hello world!!"}
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
