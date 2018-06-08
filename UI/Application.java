package UI;

import java.util.ArrayList;
import java.util.List;

public class Application {

	private String app;
	//private String page;

	private List<Page> pageList = new ArrayList<>();

	public List<Page> getPage() {
		return pageList;
	}

	public void setPage(String page,String locator, String object,String objectProperty) {
		//this.page = page;
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
