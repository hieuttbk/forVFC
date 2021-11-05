package http;

import org.json.JSONObject;

public class NotifyMessage {
	String type;
	int ty ;
	String pi;
	JSONObject command;
	
	
	
	public NotifyMessage(String type, int ty, String pi, JSONObject command) {
		super();
		this.type = type;
		this.ty = ty;
		this.pi = pi;
		this.command = command;
	}
	public NotifyMessage() {
		// TODO Auto-generated constructor stub
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getTy() {
		return ty;
	}
	public void setTy(int ty) {
		this.ty = ty;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	public JSONObject getCommand() {
		return command;
	}
	public void setCommand(JSONObject command) {
		this.command = command;
	}
	

}
