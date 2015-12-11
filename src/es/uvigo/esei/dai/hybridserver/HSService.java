package es.uvigo.esei.dai.hybridserver;

import java.util.ArrayList;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HSService {
	@WebMethod public ArrayList<String> getUUIDHTML();
	@WebMethod public ArrayList<String> getUUIDXML();
	@WebMethod public ArrayList<String> getUUIDXSD();
	@WebMethod public ArrayList<String> getUUIDXSLT();
	@WebMethod public String getContentHTML(String uuid);
	@WebMethod public String getContentXML(String uuid);
	@WebMethod public String getContentXSD(String uuid);
	@WebMethod public String getContentXSLT(String uuid);
	@WebMethod public String uuidXSDofXSLT(String uuid);
}
