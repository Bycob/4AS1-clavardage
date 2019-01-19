package org.ljsn.clavardage.presence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.google.gson.Gson;

public abstract class Request {

	public enum RequestType {
		CONNECT(RequestConnect.class),
		DISCONNECT(RequestDisconnect.class),
		GET_USER_LIST(RequestGetUserList.class),
		CHANGE_PSEUDO(RequestChangePseudo.class);
		
		public final Class<? extends Request> myClass;
		
		RequestType(Class<? extends Request> myClass) {
			this.myClass = myClass;
		}
	}
	
	public static Request readFromStream(InputStream stream) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
			StringBuilder input = new StringBuilder();
			
			String line = reader.readLine();
			if (line != null) {
				RequestType type = RequestType.valueOf(line);
				
				while ((line = reader.readLine()) != null) {
					input.append(line);
				}
				
				Gson gson = new Gson();
				return gson.fromJson(input.toString(), type.myClass);
			}
			else {
				throw new IOException("Could not read the type of the request");
			}
		}
	}
	
	
	
	private final RequestType type;
	
	protected Request(RequestType type) {
		this.type = type;
	}
	
	private StringBuilder buildRequestString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.type.toString());
		builder.append("\n");
		Gson gson = new Gson();
		builder.append(gson.toJson(this));
		return builder;
	}
	
	public long length() {
		return buildRequestString().toString().getBytes().length;
	}
	
	public void writeToStream(OutputStream stream) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream))) {
			writer.write(buildRequestString().toString());
			writer.flush();
		}
	}
}
