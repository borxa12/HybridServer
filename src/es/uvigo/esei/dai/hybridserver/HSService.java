package es.uvigo.esei.dai.hybridserver;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface HSService {
	@WebMethod public List<String> getUUIDHTML();
	@WebMethod public List<String> getUUIDXML();
	@WebMethod public List<String> getUUIDXSD();
	@WebMethod public List<String> getUUIDXSLT();
	@WebMethod public String getUUID(String type);
	@WebMethod public String getContentHTML(String uuid);
	@WebMethod public String getContentXML(String uuid);
	@WebMethod public String getContentXSD(String uuid);
	@WebMethod public String getContentXSLT(String uuid);
	@WebMethod public String uuidXSDofXSLT(String uuid);
	@WebMethod public String getContent(String uuid, String type);
}
