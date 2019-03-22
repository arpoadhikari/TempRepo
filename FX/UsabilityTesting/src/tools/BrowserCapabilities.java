package tools;

import java.util.HashMap;

public class BrowserCapabilities {

	@SuppressWarnings("serial")
	public static HashMap<String, String> firefox = new HashMap<String, String>()
	{{
		put("marionette", "false");
		put("elementScrollBehavior", "0");
	}};
	@SuppressWarnings("serial")
	public static HashMap<String, String> chrome = new HashMap<String, String>()
	{{
		put("elementScrollBehavior", "0");
	}};
	@SuppressWarnings("serial")
	public static HashMap<String, String> ie = new HashMap<String, String>()
	{{
		put("acceptSslCerts", "true");
		put("ignoreProtectedModeSettings", "true");
		put("ignoreZoomSetting", "true");
		put("unexpectedAlertBehaviour", "accept");
		put("enablePersistentHover", "true");
		put("elementScrollBehavior", "0");
	}};
	@SuppressWarnings("serial")
	public static HashMap<String, String> edge = new HashMap<String, String>()
	{{
		put("elementScrollBehavior", "0");
		put("pageLoadStrategy", "eager");
	}};

}
