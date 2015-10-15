package es.uvigo.esei.dai.hybridserver;

import java.io.BufferedReader;
import java.io.IOException;
<<<<<<< HEAD
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
=======
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

>>>>>>> origin/master
import java.net.Socket;
import java.util.Map;

import es.uvigo.esei.dai.hybridserver.http.HTTPParseException;
import es.uvigo.esei.dai.hybridserver.http.HTTPRequest;
<<<<<<< HEAD
import es.uvigo.esei.dai.hybridserver.http.HTTPRequestMethod;
=======
>>>>>>> origin/master
import es.uvigo.esei.dai.hybridserver.http.HTTPResponse;
import es.uvigo.esei.dai.hybridserver.http.HTTPResponseStatus;

public class Worker implements Runnable {

	private Socket socket;
<<<<<<< HEAD
    private ServerPages WEB_PAGES;
	
	public Worker(Socket socket, ServerPages pages) {
=======
	private Map<String, String> WEB_PAGES;
	private HTTPRequest req;

	public Worker(Socket socket, Map<String, String> pages) {
>>>>>>> origin/master
		this.socket = socket;
		this.WEB_PAGES = pages;

	}

	@Override
	public void run() {
<<<<<<< HEAD
        try (Socket socket = this.socket) {
        	HTTPRequest request = new HTTPRequest(new InputStreamReader(socket.getInputStream()));
        	Map<String,String> resourceParameters = request.getResourceParameters();
        	String page = resourceParameters.get("uuid");
        	
//        	if(request.getMethod() == HTTPRequestMethod.POST) {
//        		this.WEB_PAGES.add(page,request.getContent());
//        	}
        	
            // Responder al cliente
            HTTPResponse response = new HTTPResponse();
            response.setVersion("HTTP/1.1");
            response.setStatus(HTTPResponseStatus.S200);
            response.putParameter("Content-Type", "text/html");
            
            
            // Si ResourceName no contiene HTML: eror 400
            if(!request.getResourceName().contains("html"))
            	response.setStatus(HTTPResponseStatus.S400);
            else { // Si no hay UUID
	            if(page == null) {
	            	//response.setContent("Hybrid Server");
	            	response.setContent(this.WEB_PAGES.getUUID());
	            } else { // Si hay UUID
	            	if(this.WEB_PAGES.exists(page)) {
	            		response.setContent(WEB_PAGES.getContent(page)); //UUID existente: visualiza página
	            	} else {
	            		if(!this.WEB_PAGES.exists(page))
	            			response.setStatus(HTTPResponseStatus.S404); // UUID inexistente: error 404
	            		else
	            			response.setStatus(HTTPResponseStatus.S500);
	            	}
	            }
            }
            //Post no va misteriosamente
            if(request.getMethod() == HTTPRequestMethod.POST) {
            	if(page == null) response.setStatus(HTTPResponseStatus.S400);
            	System.out.println(request.getResourceParameters().get("uuid"));
        		this.WEB_PAGES.add(request.getResourceParameters().get("uuid"),request.getContent());
        	}
            
            //delete perfecto
            if(request.getMethod() == HTTPRequestMethod.DELETE){
            	this.WEB_PAGES.remove(page);
            }
            
            
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
			response.print(out);
			out.flush();
			
        } catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (HTTPParseException e) {
			e.printStackTrace();
=======
		while (true) {
			try (Socket socket = this.socket) {

				InputStream in = socket.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				this.req = new HTTPRequest(br);

				OutputStream out = socket.getOutputStream();
				String uuidvalue = req.getResourceParameters().get("uuid");
				final int tam = WEB_PAGES.get(uuidvalue).length();

				out.write("HTTP/1.1 200 OK\r\n".getBytes());
				out.write("Content-Type: text/html\r\n".getBytes());
				out.write(("Content-Length: " + tam + "\r\n").getBytes());
				out.write("\r\n".getBytes());
				out.write(WEB_PAGES.get(uuidvalue).getBytes());
				out.flush();

				// Responder al cliente
				HTTPResponse response = new HTTPResponse();
				response.setVersion("HTTP/1.1");
				response.putParameter("Content-Type", "text/html");
				response.setContent(WEB_PAGES.get("1"));
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} catch (HTTPParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
>>>>>>> origin/master
		}
	}

}
