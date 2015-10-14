package es.uvigo.esei.dai.hybridserver;

import java.util.HashMap;
import java.util.Map;

public class ServerPages {
	
	private Map<String,String> pages;
	
	public ServerPages() {
		this.pages = new HashMap<>();
		pages.put("1", "Hybrid Server");
		pages.put("2","<html><body><h1>Hola Mundo!!</h1></body></html>");
	}

	public Map<String, String> getPages() {
		return pages;
	}
	
}
