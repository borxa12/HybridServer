package es.uvigo.esei.dai.hybridserver;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ServerPages {
	
	private Map<String,String> pages;
	
	public ServerPages(Map<String,String> pages) {
		this.pages = pages;
		pages.put("1", "Hybrid Server");
		pages.put("2","<html><body><h1>Hola Mundo!!</h1></body></html>");
	}
	
	public void add(String uuid, String content) {
		pages.put(uuid,content);
	}
	
	public void remove(String uuid){
		pages.remove(uuid);
	}
	
	public String getUUID() {
		Set<String> uuid = pages.keySet();
		Iterator<String> it = uuid.iterator();
		StringBuilder toret = new StringBuilder();
		while(it.hasNext()) {
			toret.append("<a href=\"html?uuid=");
			String aux = it.next();
			toret.append(aux);
			toret.append("\">");
			toret.append(aux);
			toret.append("</a>\r\n");
		}
		return toret.toString();
	}
	
	public String getUUID(String uuid) {
		StringBuilder toret = new StringBuilder();
		toret.append("<a href=\"html?uuid=");
		toret.append(uuid);
		toret.append("\">");
		toret.append(uuid);
		toret.append("</a>\r\n");
		return toret.toString();
	}
	
	public boolean exists(String uuid) {
		return this.pages.containsKey(uuid);
	}
	
	public String getContent (String uuid) {
		return this.pages.get(uuid);
	}

	public Map<String, String> getPages() {
		return pages;
	}
	
}
