package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HTTPResponse {

	private String HTTPVersion;
	private HTTPResponseStatus responseStatus;
	private Map<String, String> headerParameters;
	private String content;

	public HTTPResponse() {
		this.HTTPVersion = null;
		this.responseStatus = null;
		this.headerParameters = new LinkedHashMap<>();
		this.content = null;
	}

	public HTTPResponseStatus getStatus() {
		return this.responseStatus;
	}

	public void setStatus(HTTPResponseStatus status) {
		this.responseStatus = status;
	}

	public String getVersion() {
		return this.HTTPVersion;
	}

	public void setVersion(String version) {
		this.HTTPVersion = version;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getParameters() {
		return this.headerParameters;
	}

	public String putParameter(String name, String value) {
		return this.headerParameters.put(name, value);
	}

	public boolean containsParameter(String name) {
		return this.headerParameters.containsKey(name);
	}

	public String removeParameter(String name) {
		return this.headerParameters.remove(name);
	}

	public void clearParameters() {
		this.headerParameters.clear();
	}

	public List<String> listParameters() {
		List<String> parameters = new ArrayList<>();
		Collection<String> param = this.getParameters().values();
		Iterator<String> it = param.iterator();
		while (it.hasNext()) {
			parameters.add(it.next().toString());
		}
		return parameters;
	}

	public void print(Writer writer) throws IOException {
		writer.append(this.getVersion()).append(' ')
			.append(String.valueOf(this.getStatus().getCode())).append(' ')
			.append(String.valueOf(this.getStatus().getStatus()))
			.append("\r\n");
		
		if(this.getParameters().isEmpty()) {
			if(this.content == null) {
				writer.append("\r\n");
			} else {
				writer.append("Content-Length: ")
					.append(String.valueOf(this.getContent().length()))
					.append("\r\n\r\n")
					.append(this.getContent());
			}
		} else {
			if(this.content == null) {
				for(Entry<String,String> param : this.getParameters().entrySet()) {
					writer.append(param.getKey()).append(": ").append(param.getValue())
						.append("\r\n");
				}
				writer.append("\r\n");
			} else {
				for(Entry<String,String> param : this.getParameters().entrySet()) {
					writer.append(param.getKey()).append(": ").append(param.getValue())
						.append("\r\n");
				}
				writer.append("Content-Length: ").append(String.valueOf(this.getContent().length()))
					.append("\r\n\r\n");
				writer.append(this.getContent());
			}	
		}
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		System.out.println(writer.toString());
		return writer.toString();
	}
}
