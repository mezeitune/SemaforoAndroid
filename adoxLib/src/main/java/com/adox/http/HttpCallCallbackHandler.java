package com.adox.http;

import java.util.HashMap;

public interface HttpCallCallbackHandler {
	public void onResponse(String action, HashMap<String, String> response, HashMap<String, String> map);
}
