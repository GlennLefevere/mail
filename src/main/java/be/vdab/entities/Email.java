package be.vdab.entities;

public class Email {
	private String zender;
	private String ontvanger;
	private String onderwerp;
	private String inhoud;
	
	public String getZender() {
		return zender;
	}
	public void setZender(String zender) {
		this.zender = zender;
	}
	public String getOntvanger() {
		return ontvanger;
	}
	public void setOntvanger(String ontvanger) {
		this.ontvanger = ontvanger;
	}
	public String getOnderwerp() {
		return onderwerp;
	}
	public void setOnderwerp(String onderwerp) {
		this.onderwerp = onderwerp;
	}
	public String getInhoud() {
		return inhoud;
	}
	public void setInhoud(String inhoud) {
		this.inhoud = inhoud;
	}
	
	@Override
	public String toString(){
		return "van: " + zender + " \nnaar: " + ontvanger + "\nonderwerp: " + onderwerp + " \ninhoud: " + inhoud;
	}
}
