package es.uvigo.esei.dai.hybridserver.http;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HTTPResponse {
	
	public HTTPResponse() {
	}

	public HTTPResponseStatus getStatus() {
		return this.getStatus();
	}

	public void setStatus(HTTPResponseStatus status) {
	}

	public String getVersion() {
		return null;
	}

	public void setVersion(String version) {
	}

	public String getContent() {
		return null;
	}

	public void setContent(String content) {
	}

	public Map<String, String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public String putParameter(String name, String value) {
		// TODO Auto-generated method stub
		return this.getParameters().put(name, value);
	}

	public boolean containsParameter(String name) {
		// TODO Auto-generated method stub
		return this.getParameters().containsKey(name);
	}

	public String removeParameter(String name) {
		// TODO Auto-generated method stub
		return this.getParameters().remove(name);
	}

	public void clearParameters() {
	}

	public List<String> listParameters() {
		List<String> parameters = new ArrayList<>();
		Collection<String> param = this.getParameters().values();
		Iterator<String> it = param.iterator();
		while(it.hasNext()) {
			parameters.add(it.next().toString());
		}
		return parameters;
	}

	public void print(Writer writer) throws IOException {
	}

	@Override
	public String toString() {
		final StringWriter writer = new StringWriter();

		try {
			this.print(writer);
		} catch (IOException e) {
		}

		return writer.toString();
	}
}
