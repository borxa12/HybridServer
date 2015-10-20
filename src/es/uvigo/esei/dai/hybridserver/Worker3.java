package es.uvigo.esei.dai.hybridserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.PreparedStatement;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Worker3 implements Runnable {
	
	private Socket socket;
	private Properties properties;

	public Worker3(Socket socket, Properties properties2) {
		this.socket = socket;
		this.properties = properties2;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
			HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
			
			// Cargar los Drivers de la BD (MySQL)
			Class.forName("com.mysql.jdbc.Driver");
			// Conectarse a la BD (MySQL)
//			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/example", "hsdb", "hsdbpass");
			
			// Responder al cliente
			HTTPResponse response = new HTTPResponse();
			response.setVersion("HTTP/1.1");
			response.setStatus(HTTPResponseStatus.S200);
			response.putParameter("Content-Type", "text/html");
//			response.setContent("Hybrid Server");
			System.out.println(properties.toString());
			Connection connection = DriverManager.getConnection(properties.getProperty("db.url"),
					properties.getProperty("db.user"), properties.getProperty("db.password"));
			String query = "SELECT content FROM HTML WHERE uuid=?";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
//				statement.setString(1,"1");
				statement.setString(1, request.getResourceParameters().get("uuid"));
				try (ResultSet results = statement.executeQuery()) {
					if (results.next()) {
						response.setContent(results.getString("content"));
						System.out.println(results.getString("content"));
					}
				}
			}

			OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
			response.print(out);
			out.flush();
			
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (HTTPParseException e) {
			System.err.println(e.getMessage());
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}

}
