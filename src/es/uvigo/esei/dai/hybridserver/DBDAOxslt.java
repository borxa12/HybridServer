package es.uvigo.esei.dai.hybridserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;

public class DBDAOxslt implements Pages {

	private Connection connection;
//conexion xslt
	public DBDAOxslt(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void create(String uuid, HTTPRequest request) {
		String query = "INSERT INTO XSLT (uuid,content,xsd) VALUES(?,?,?)";
		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
			statement.setString(1, uuid);
			statement.setString(2, request.getResourceParameters().get("xslt"));
			statement.setString(3, request.getResourceParameters().get("xsd"));
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void remove(HTTPRequest request) {
		String query = "DELETE FROM XSLT WHERE uuid=?";
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
		String query = "SELECT content FROM XSLT WHERE uuid=?";
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
		String query = "SELECT uuid FROM XSLT";
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
		toret.append("<a href=\"xslt?uuid=").append(uuid).append("\">").append(uuid).append("</a><br/>");
		return toret.toString();
	}

	@Override
	public boolean exists(HTTPRequest request) {
		if(request.getResourceParameters().get("uuid") != null) {
			String query = "SELECT * FROM XSLT WHERE uuid=? ";
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
		} else if (request.getResourceParameters().get("xsd") != null) {
			String query = "SELECT * FROM XSLT WHERE xsd=? ";
			try (PreparedStatement statement = this.connection.prepareStatement(query)) {
				statement.setString(1, request.getResourceParameters().get("xsd"));
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
		return false;
	}
	
//	public boolean existsXSD(HTTPRequest request) {
//		String query = "SELECT * FROM XSLT WHERE xsd=?";
//		try (PreparedStatement statement = this.connection.prepareStatement(query)) {
//			statement.setString(1, request.getResourceParameters().get("xsd"));
//			try (ResultSet results = statement.executeQuery()) {
//				if (results.next())
//					return true;
//				else
//					return false;
//			}
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//	}

}

