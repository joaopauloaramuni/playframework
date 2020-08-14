package models;

import play.db.ebean.*;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
public class Email extends Model{
	
	public String sender;
	public String recipient;
	public String subject;
	public String message;
	
	public Email(String sender, String recipient, String subject, String message)  {
		this.sender = sender;
		this.recipient = recipient;
		this.subject = subject;
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "Email [From=" + sender + ", to=" + recipient + ", subject="
				+ subject + ", message=" + message +"]";
	}
}

