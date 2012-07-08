package cloudtestingdi.configuration;


/**
 * Set the path of the framework.
 * 
 * @author Gustavo Sávio - saviojp@gmail.com
 * @since 05/10/2011
 * @version 0.1
 * 
 */
public enum Path {
	
	JUNIT("/home/ec2-user/lib/junit-4.9.jar"),
	USER("/home/ec2-user/"),
	PERMISSION(""),
	UPLOAD("");
;
	private String uri;

	Path(String uri) {
		this.uri = uri;
	}

	public String getPath() {
		return this.uri;
	}
	
	public void setPath(String uri) {
		this.uri = uri;
	}
}