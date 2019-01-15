package org.ljsn.clavardage.presence;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.ljsn.clavardage.core.User;
import org.ljsn.clavardage.core.UserList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class PresenceServer implements HttpHandler {
	
	public static final int DEFAULT_SERVER_PORT = 8000;
	
	
	private HttpServer server;
	private UserList users;
	
	public PresenceServer(int port) throws IOException {
		this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
		this.server.createContext("/", this);
		this.server.setExecutor(null);
		
		this.users = new UserList();
		
		this.server.start();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		InetAddress address = exchange.getRemoteAddress().getAddress();
		Request request = Request.readFromStream(exchange.getRequestBody());
		
		// TODO send headers
		int length = 2;
		exchange.sendResponseHeaders(200, length);
		
		if (request instanceof RequestConnect) {
			RequestConnect requestConnect = (RequestConnect) request;
			User ipUser = this.users.getByIpAddress(address.getHostAddress());
			
			if (ipUser != null) {
				// TODO return "already connected"
			}
			else {
				User pseudoUser = this.users.getByPseudo(requestConnect.getPseudo());
				
				if (pseudoUser != null) {
					// TODO return "already used pseudo"
				}
				else {
					User newUser = new User(requestConnect.getPseudo(), requestConnect.getTcpPort(), address.getHostAddress());
					this.users.addUser(newUser);
					// TODO return "you are now connected"
				}
			}
		}
		else if (request instanceof RequestChangePseudo) {
			RequestChangePseudo requestChangePseudo = (RequestChangePseudo) request;
			User ipUser = this.users.getByIpAddress(address.getHostAddress());
			
			if (ipUser != null) {
				User pseudoUser = this.users.getByPseudo(requestChangePseudo.getPseudo());
				
				if (pseudoUser != null) {
					// TODO return "pseudo already taken"
				}
				else {
					ipUser.setPseudo(requestChangePseudo.getPseudo());
					// TODO return "pseudo changed"
				}
			}
			else {
				// TODO return "user not found"
			}
		}
		else if (request instanceof RequestDisconnect) {
			User ipUser = this.users.getByIpAddress(address.getHostAddress());
			
			if (ipUser != null) {
				this.users.removeUser(ipUser);
				// TODO return "you are removed"
			}
			else {
				// TODO return "no user with this host address"
			}
		}
		else if (request instanceof RequestGetUserList) {
			// TODO return "users"
		}
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
