package be.vdab.enums;

public enum mailServers {
	GMAIL("imap.gmail.com"), HOTMAIL("pop-mail.outlook.com");
	private String value;
	private mailServers(String value){
		this.value = value;
	}
	
	public String toString(){
		return this.value;
	}
}
