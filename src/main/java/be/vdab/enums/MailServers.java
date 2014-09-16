package be.vdab.enums;

public enum MailServers {
	GMAIL("imap.gmail.com"), HOTMAIL("pop-mail.outlook.com");
	private final String value;
	private MailServers(String value){
		this.value = value;
	}
	
	public String toString(){
		return this.value;
	}
}
