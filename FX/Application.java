package application.support;

import java.util.ArrayList;
import java.util.List;

/**
 * It used to store all the "Application" node details including the "Page" details of that application to read/write from/to the XML
 *
 */
public class Application {

	private String app;

	private List<Page> pageList = new ArrayList<>();

	public List<Page> getPage() {
		return pageList;
	}

	public void setPage(String page,String locator, String object,String objectProperty) {

		boolean pageFlag = false;

		for(Page p:pageList) {
			pageFlag = false;
			if(p.getPage().equals(page)) {
				pageFlag = true;
				p.setObjectProperty(locator, object,objectProperty);
				break;
			}
		}

		if(!pageFlag) {
			Page p = new Page();
			p.setPage(page);
			p.setObjectProperty(locator, object,objectProperty);
			pageList.add(p);
		}
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

}
