package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class DBDAOxsd implements Pages {

	private Connection connection;
	
	public DBDAOxsd(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void create(String uuid, HTTPRequest request) {
		String query = "INSERT INTO XSD (uuid,content) VALUES(?,?)";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			statement.setString(2, request.getResourceParameters().get("xsd"));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void remove(HTTPRequest request) {
		String query = "DELETE FROM XSD WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, request.getResourceParameters().get("uuid"));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String get(HTTPRequest request) {
		String content = null;
		String query = "SELECT content FROM XSD WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, request.getResourceParameters().get("uuid"));
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
	public String list() {
		StringBuilder content = new StringBuilder();
		String query = "SELECT uuid FROM XSD";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			try (ResultSet results = statement.executeQuery()) {
				while (results.next()) {
					content.append(this.link(results.getString("uuid")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return content.toString();
	}

	@Override
	public String link(String uuid) {
		StringBuilder toret = new StringBuilder();
		toret.append("<a href=\"xsd?uuid=").append(uuid).append("\">").append(uuid).append("</a><br/>");
		return toret.toString();
	}

	@Override
	public boolean exists(HTTPRequest request) {
		String query = "SELECT * FROM XSD WHERE uuid=?";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, request.getResourceParameters().get("uuid"));
			try (ResultSet results = statement.executeQuery()) {
				if (results.next())
					return true;
				else
					return false;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public String getContent(String uuid) {
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
}

