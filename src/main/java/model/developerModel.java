package model;

public class developerModel 
{
    private String Clientid,ClientSecret,Appname,Redirecturi;

	public String getClientid() {
		return Clientid;
	}

	public void setClientid(String clientid) {
		Clientid = clientid;
	}

	public String getClientSecret() {
		return ClientSecret;
	}

	public void setClientSecret(String clientSecret) {
		ClientSecret = clientSecret;
	}

	public String getAppname() {
		return Appname;
	}

	public void setAppname(String appname) {
		Appname = appname;
	}

	public String getRedirecturi() {
		return Redirecturi;
	}

	public void setRedirecturi(String redirecturi) {
		Redirecturi = redirecturi;
	}
}
