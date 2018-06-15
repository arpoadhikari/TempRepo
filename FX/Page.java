package application.support;

import java.util.ArrayList;
import java.util.List;

/**
 * It used to store all the "Page" node details including the "Object" details of that page to read/write from/to the XML
 *
 */
public class Page {

	private String page;

	private List<ObjectProperty> objectPropertyList = new ArrayList<>();

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public void setObjectProperty(String locator, String object,String objectProperty) {
		boolean objectFlag = false;
		for(ObjectProperty o:objectPropertyList) {
			objectFlag = false;
			if(object.equals(o.getObject())) {
				objectFlag = true;
				break;
			}
		}

		if(!objectFlag) {
			ObjectProperty o = new ObjectProperty();
			o.setLocator(locator);
			o.setObject(object);
			o.setObjectProperty(objectProperty);
			objectPropertyList.add(o);
		}

	}

	public List<ObjectProperty> getobjectPropertyList() {
		return objectPropertyList;
	}

}
