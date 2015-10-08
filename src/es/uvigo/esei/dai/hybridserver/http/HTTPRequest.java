package es.uvigo.esei.dai.hybridserver.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class HTTPRequest {
	
	private BufferedReader bf;
	
	public HTTPRequest(Reader reader) throws IOException, HTTPParseException {
		bf = new BufferedReader(reader);
	}

	public HTTPRequestMethod getMethod() {
		HTTPRequestMethod method = null;
		try {
			String line = bf.readLine();
			int pos = line.lastIndexOf("\\s");
			String metodo = line.substring(0,pos-1);
			for(int i = 0; i < HTTPRequestMethod.values().length; i++) {
				if(HTTPRequestMethod.values()[i].equals(metodo))
					method = HTTPRequestMethod.values()[i];
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return method;
	}

	public String getResourceChain() {
		String chain = null;
		try {
			String line = bf.readLine();
			int pos = line.lastIndexOf("\\s");
			String substring = line.substring(pos+1,line.length());
			int pos2 = substring.lastIndexOf("\\s");
			String substring2 = substring.substring(0,pos2-1);
			int pos3 = substring2.indexOf('?');
			chain = substring2.substring(0,pos3-1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return chain;
	}

	public String[] getResourcePath() {
		String[] path = null;
		path = this.getResourceChain().split("/");
		return path;
	}

	public String getResourceName() {
		String[] path = this.getResourcePath();
		return path[path.length];
	}

	@SuppressWarnings("null")
	public Map<String, String> getResourceParameters() {
		Map<String,String> parameters = null;
		try {
			String line = bf.readLine();
			int pos = line.indexOf('?');
			String substring = line.substring(pos+1,line.length());
			int pos2 = substring.indexOf("\\s");
			String substring2 = substring.substring(0,pos2-1);
			String[] aux = substring2.split("&");
			for(int i = 0; i < aux.length; i++) {
				String[] auxP;
				auxP = aux[i].split("=");
				parameters.put(auxP[0],auxP[1]);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return parameters;
	}

	public String getHttpVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getHeaderParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getContentLength() {
		// TODO Auto-generated method stub
		return -1;
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
