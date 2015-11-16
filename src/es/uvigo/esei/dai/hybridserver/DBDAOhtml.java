package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class DBDAOhtml implements Pages {

	private Connection connection;
//conexion html
	public DBDAOhtml(Connection connection) {
		this.connection = connection;
	}

	public void create(String uuid, HTTPRequest request) {
		String query = "INSERT INTO HTML (uuid,content) VALUES(?,?)";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			statement.setString(2, request.getResourceParameters().get("html"));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(HTTPRequest request) {
		String query = "DELETE FROM HTML WHERE uuid=?";
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
		String query = "SELECT content FROM HTML WHERE uuid=?";
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
		String query = "SELECT uuid FROM HTML";
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
		toret.append("<a href=\"html?uuid=").append(uuid).append("\">").append(uuid).append("</a><br/>");
		return toret.toString();
	}

	@Override
	public boolean exists(HTTPRequest request) {
		String query = "SELECT * FROM HTML WHERE uuid=?";
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

}
