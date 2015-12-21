package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HSService", serviceName = "HybridServerService")
public class HSImp implements HSService {
	
	private Configuration config;
	private Connection conect;
	
	// Conexion Service
	public HSImp(Configuration config) {
		this.config = config;
		this.conect = null;
	}
	
	@Override
	public List<String> getUUIDHTML() {
		ArrayList<String> uuid = new ArrayList<>();
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT uuid FROM HTML";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						uuid.add(results.getString("uuid"));
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public List<String> getUUIDXML() {
		ArrayList<String> uuid = new ArrayList<>();
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT uuid FROM XML";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						uuid.add(results.getString("uuid"));
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public List<String> getUUIDXSD() {
		ArrayList<String> uuid = new ArrayList<>();
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT uuid FROM XSD";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						uuid.add(results.getString("uuid"));
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public List<String> getUUIDXSLT() {
		ArrayList<String> uuid = new ArrayList<>();
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT uuid FROM XSLT";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				try (ResultSet results = statement.executeQuery()) {
					while (results.next()) {
						uuid.add(results.getString("uuid"));
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}
	
	@Override
	public String getUUID(String type) {
		StringBuilder links = new StringBuilder();
		List<String> uuids = new ArrayList<>();
		switch (type) {
			case "html": 
				uuids = this.getUUIDHTML();
				for(int i = 0; i < uuids.size(); i++) {
					links.append("<a href=\"html?uuid=" + uuids.get(i) + "\">" + uuids.get(i) + "</a><br/>");
				}
				break;
			case "xml":
				uuids = this.getUUIDXML();
				for(int i = 0; i < uuids.size(); i++) {
					links.append("<a href=\"html?uuid=" + uuids.get(i) + "\">" + uuids.get(i) + "</a><br/>");
				}
				break;
			case "xsd":
				uuids = this.getUUIDXSD();
				for(int i = 0; i < uuids.size(); i++) {
					links.append("<a href=\"html?uuid=" + uuids.get(i) + "\">" + uuids.get(i) + "</a><br/>");
				}
				break;
			case "xslt":
				uuids = this.getUUIDXSD();
				for(int i = 0; i < uuids.size(); i++) {
					links.append("<a href=\"html?uuid=" + uuids.get(i) + "\">" + uuids.get(i) + "</a><br/>");
				}
				break;
		}
		return links.toString();
	}

	@Override
	public String getContentHTML(String uuid) {
		String content = null;
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT content FROM HTML WHERE uuid=?";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet results = statement.executeQuery()) {
					if (results.next()) {
						content = results.getString("content");
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String getContentXML(String uuid) {
		String content = null;
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT content FROM XML WHERE uuid=?";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet results = statement.executeQuery()) {
					if (results.next()) {
						content = results.getString("content");
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String getContentXSD(String uuid) {
		String content = null;
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT content FROM XSD WHERE uuid=?";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet results = statement.executeQuery()) {
					if (results.next()) {
						content = results.getString("content");
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String getContentXSLT(String uuid) {
		String content = null;
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT content FROM XSLT WHERE uuid=?";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet results = statement.executeQuery()) {
					if (results.next()) {
						content = results.getString("content");
					}
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String uuidXSDofXSLT(String uuid) {
		String xsd = null;
		try {
			this.conect = DriverManager.getConnection(config.getDbURL(),
					config.getDbUser(), config.getDbPassword());
			String query = "SELECT xsd FROM XSLT WHERE uuid=?";
			try (PreparedStatement statement = this.conect.prepareStatement(query)) {
				statement.setString(1, uuid);
				try (ResultSet results = statement.executeQuery()) {
					if (results.next())
						xsd = results.getString("xsd");
				}
			}
			this.conect.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return xsd;
	}
	
	@Override
	public String getContent(String uuid, String type) {
		switch (type) {
			case "html": return this.getContentHTML(uuid);
			case "xml": return this.getContentXML(uuid);
			case "xsd": return this.getContentXSD(uuid);
			case "xslt": return this.getContentXSLT(uuid);
			default: return null;
		}
	}

}
