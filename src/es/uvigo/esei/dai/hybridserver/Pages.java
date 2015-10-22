package es.uvigo.esei.dai.hybridserver;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public interface Pages {
	public void create(String uuid, HTTPRequest request);
	public void remove(HTTPRequest request);
	public String get(HTTPRequest request);
	public String list();
	public String link(String uuid);
	public boolean exists(HTTPRequest request);
}
