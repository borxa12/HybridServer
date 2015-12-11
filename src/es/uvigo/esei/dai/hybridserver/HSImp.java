package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.jws.WebService;

@WebService(endpointInterface = "es.uvigo.esei.dai.hybridserver.HSService")
public class HSImp implements HSService {
	
	private Connection connection;
	// Conexion Service
	public HSImp(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public ArrayList<String> getUUIDHTML() {
		ArrayList<String> uuid = new ArrayList<>();
		String query = "SELECT uuid FROM HTML";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					uuid.add(results.getString("uuid"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public ArrayList<String> getUUIDXML() {
		ArrayList<String> uuid = new ArrayList<>();
		String query = "SELECT uuid FROM XML";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					uuid.add(results.getString("uuid"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public ArrayList<String> getUUIDXSD() {
		ArrayList<String> uuid = new ArrayList<>();
		String query = "SELECT uuid FROM XSD";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					uuid.add(results.getString("uuid"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public ArrayList<String> getUUIDXSLT() {
		ArrayList<String> uuid = new ArrayList<>();
		String query = "SELECT uuid FROM XSLT";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					uuid.add(results.getString("uuid"));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return uuid;
	}

	@Override
	public String getContentHTML(String uuid) {
		String content = null;
		String query = "SELECT content FROM HTML WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					content = results.getString("content");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String getContentXML(String uuid) {
		String content = null;
		String query = "SELECT content FROM XML WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					content = results.getString("content");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String getContentXSD(String uuid) {
		String content = null;
		String query = "SELECT content FROM XSD WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					content = results.getString("content");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String getContentXSLT(String uuid) {
		String content = null;
		String query = "SELECT content FROM XSLT WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next()) {
					content = results.getString("content");
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content;
	}

	@Override
	public String uuidXSDofXSLT(String uuid) {
		String query = "SELECT xsd FROM XSLT WHERE uuid=?";
		String xsd = null;
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			try (ResultSet results = statement.executeQuery()) {
				if (results.next())
					xsd = results.getString("xsd");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return xsd;
	}

}
