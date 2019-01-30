package org.ljsn.clavardage.presence;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ljsn.clavardage.core.User;
import org.ljsn.clavardage.core.UserList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class PresenceServer implements HttpHandler {
	
	public static final int DEFAULT_SERVER_PORT = 8000;

	private static Logger logger = Logger.getLogger(PresenceServer.class.getName());
	
	
	private HttpServer server;
	private UserList users;
	
	public PresenceServer(int port) throws IOException {
		this.server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
		this.server.createContext("/", this);
		this.server.setExecutor(null);
		
		this.users = new UserList();
		
		logger.log(Level.INFO, "Started presence server on localhost:" + port);
		
		this.server.start();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		InetAddress address = exchange.getRemoteAddress().getAddress();
		logger.log(Level.INFO, "Received request from " + address);
		
		Request request = Request.readFromStream(exchange.getRequestBody());
		logger.log(Level.INFO, "Request type : " + request.getClass().getSimpleName());
		
		if (request instanceof RequestConnect) {
			RequestConnect requestConnect = (RequestConnect) request;
			User ipUser = this.users.getByIpAddress(address.getHostAddress());
			
			if (ipUser != null) {
				requestConnect.setResult(RequestConnect.Result.ALREADY_CONNECTED);
			}
			else {
				User pseudoUser = this.users.getByPseudo(requestConnect.getPseudo());
				
				if (pseudoUser != null) {
					requestConnect.setResult(RequestConnect.Result.PSEUDO_ALREADY_USED);
				}
				else {
					User newUser = new User(requestConnect.getPseudo(), requestConnect.getTcpPort(), address.getHostAddress());
					this.users.addUser(newUser);

					requestConnect.setResult(RequestConnect.Result.SUCCESS);
				}
			}
		}
		else if (request instanceof RequestChangePseudo) {
			RequestChangePseudo requestChangePseudo = (RequestChangePseudo) request;
			User ipUser = this.users.getByIpAddress(address.getHostAddress());
			
			if (ipUser != null) {
				User pseudoUser = this.users.getByPseudo(requestChangePseudo.getPseudo());
				
				if (pseudoUser != null) {
					requestChangePseudo.setResult(RequestChangePseudo.Result.ALREADY_USED_PSEUDO);
				}
				else {
					ipUser.setPseudo(requestChangePseudo.getPseudo());
					requestChangePseudo.setResult(RequestChangePseudo.Result.SUCCESS);
				}
			}
			else {
				requestChangePseudo.setResult(RequestChangePseudo.Result.USER_NOT_FOUND);
			}
		}
		else if (request instanceof RequestDisconnect) {
			RequestDisconnect requestDisconnect = (RequestDisconnect) request;
			User ipUser = this.users.getByIpAddress(address.getHostAddress());
			
			if (ipUser != null) {
				this.users.removeUser(ipUser);
				requestDisconnect.setResult(RequestDisconnect.Result.SUCCESS);
			}
			else {
				requestDisconnect.setResult(RequestDisconnect.Result.USER_NOT_FOUND);
			}
		}
		else if (request instanceof RequestGetUserList) {
			UserList ul = new UserList(this.users);
			((RequestGetUserList) request).setUserList(ul);
		}
		
		long length = request.length();
		exchange.sendResponseHeaders(200, length);
		
		request.writeToStream(exchange.getResponseBody());
	}
	
	@Override
	public void finalize() {
		logger.log(Level.INFO, "Server destroyed, application stopping...");
	}
	
	
	
	public static void main(String[] args) {
		int port = DEFAULT_SERVER_PORT;
		
		if (args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			}
			catch (NumberFormatException e) {
				System.err.println("Parameters : [port]");
				System.exit(-1);
			}
		}
		
		try {
			new PresenceServer(port);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
