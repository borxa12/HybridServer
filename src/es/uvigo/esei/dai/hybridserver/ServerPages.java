package es.uvigo.esei.dai.hybridserver;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class ServerPages implements Pages {

	private Map<String, String> pages;

	public ServerPages(Map<String, String> pages) {
		this.pages = pages;
		pages.put("1", "Hybrid Server");
		pages.put("2", "<html><body><h1>Hola Mundo!!</h1></body></html>");
	}

	@Override
	public void create(String uuid, HTTPRequest request) {
		pages.put(uuid, request.getResourceParameters().get("html"));
	}

	@Override
	public void remove(HTTPRequest request) {
		pages.remove(request.getResourceParameters().get("uuid"));
	}

	@Override
	public String get(HTTPRequest request) {
		return this.pages.get(request.getResourceParameters().get("uuid"));
	}

	@Override
	public String list() {
		Set<String> uuid = pages.keySet();
		Iterator<String> it = uuid.iterator();
		StringBuilder toret = new StringBuilder();
		while (it.hasNext()) {
			toret.append(this.link(it.next()));
		}
		return toret.toString();
	}

	@Override
	public String link(String uuid) {
		StringBuilder toret = new StringBuilder();
		toret.append("<a href=\"html?uuid=");
		toret.append(uuid);
		toret.append("\">");
		toret.append(uuid);
		toret.append("</a>\r\n");
		return toret.toString();
	}

	@Override
	public boolean exists(HTTPRequest request) {
		return this.pages.containsKey(request.getResourceParameters().get("uuid"));
	}

	public Map<String, String> getPages() {
		return pages;
	}

}
