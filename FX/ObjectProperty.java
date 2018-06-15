package application.support;

/**
 * It used to store all the "Object" node details to read/write from/to the XML
 *
 */
public class ObjectProperty {

	private String object;
	private String locator;
	private String objectProperty;

	public String getObjectProperty() {
		return objectProperty;
	}

	public void setObjectProperty(String objectProperty) {
		this.objectProperty = objectProperty;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	public String getObjectProperties() {
		return locator + "~" + object;
	}
}
