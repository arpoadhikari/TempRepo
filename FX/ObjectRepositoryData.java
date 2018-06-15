package application.support;

/**
 * It handles the ObjectRepository table data
 *
 */
public class ObjectRepositoryData {

	private String objectName;
	private String locatorType;
	private String objectProperties;

	public ObjectRepositoryData(String objectName, String locatorType, String objectProperties) {
		this.objectName = objectName;
		this.locatorType = locatorType;
		this.objectProperties = objectProperties;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getLocatorType() {
		return locatorType;
	}

	public void setLocatorType(String locatorType) {
		this.locatorType = locatorType;
	}

	public String getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(String objectProperties) {
		this.objectProperties = objectProperties;
	}

}
