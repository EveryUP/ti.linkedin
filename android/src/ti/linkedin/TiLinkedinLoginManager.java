package ti.linkedin;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.linkedin.platform.AccessToken;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.ApiErrorResponse;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.listeners.ApiListener;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiActivityResultHandler;

import org.json.JSONObject;

import ti.linkedin.utils.Permissions;
import ti.linkedin.utils.Listener;

public class TiLinkedinLoginManager implements TiActivityResultHandler {
    private static final String LCAT = "TiLinkedinLoginManager";
    private static final String LIN_APP_PACKAGE = "com.linkedin.android";
    private static final String LIN_APP_CLASS = "com.linkedin.android.liauthlib.thirdparty.LiThirdPartyAuthorizeActivity";
    private static final String LIN_INTENT_SCOPE = "com.linkedin.thirdpartysdk.SCOPE_DATA";
    private static final String LIN_INTENT_ACTION = "com.linkedin.android.auth.AUTHORIZE_APP";
    private static final String LIN_INTENT_CATEGORY = "com.linkedin.android.auth.thirdparty.authorize";

    private TiLinkedinModule linkedinModule;
    private Listener loginListener;
    private AccessToken accessToken;
    private String[] defaultFields;

    public TiLinkedinLoginManager() {
        this.defaultFields = new String[] {
			"id", "first-name", "last-name", "email-address", "formatted-name",
			"headline", "location", "summary", "picture-url", "picture-urls::(original)"
		};;
    }

    public void authorize(TiLinkedinModule instance, Permissions permissions, Listener listener) {
        this.linkedinModule = instance;
        this.loginListener = listener;

        Intent intent = new Intent();

        intent.setClassName("com.linkedin.android", "com.linkedin.android.liauthlib.thirdparty.LiThirdPartyAuthorizeActivity");
        intent.putExtra("com.linkedin.thirdpartysdk.SCOPE_DATA", permissions.build());
        intent.setAction("com.linkedin.android.auth.AUTHORIZE_APP");
        intent.addCategory("com.linkedin.android.auth.thirdparty.authorize");
		intent.setPackage(TiApplication.getInstance().getPackageName());

		TiActivitySupport activitySupport = (TiActivitySupport)TiApplication.getInstance().getCurrentActivity();

		activitySupport.launchActivityForResult(intent, activitySupport.getUniqueResultCode(), this);
    }

    @Override
	public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
        KrollDict response = new KrollDict();

		if (resultCode == Activity.RESULT_OK) {
			this.accessToken = new AccessToken(data.getStringExtra("token"), data.getLongExtra("expiresOn", 0L));

			LISessionManager.getInstance(activity).init(this.accessToken);

			if (LISessionManager.getInstance(activity).getSession().isValid()) {
				String url = String.format("https://api.linkedin.com/v1/people/~:(%s)", TextUtils.join(",", this.defaultFields));

				APIHelper.getInstance(activity).getRequest(activity, url, new ApiListener() {
				    @Override
				    public void onApiSuccess(ApiResponse apiResponse) {
                        loginListener.onSuccess(prepareSuccessPayload(apiResponse.getResponseDataAsJson()));
				    }

				    @Override
				    public void onApiError(LIApiError apiError) {
						ApiErrorResponse error = apiError.getApiErrorResponse();

                        loginListener.onError(prepareErrorPayload(Integer.toString(error.getErrorCode()), error.getMessage()));
				    }
				});
			}
        } else if (resultCode == Activity.RESULT_CANCELED) {
            this.loginListener.onError(this.prepareErrorPayload("USER_CANCELED", "User canceled authorization process"));
        } else {
            String info = data.getStringExtra("com.linkedin.thirdparty.authorize.RESULT_ACTION_ERROR_INFO");
            String description = data.getStringExtra("com.linkedin.thirdparty.authorize.RESULT_ACTION_ERROR_DESCRIPTION");

			this.loginListener.onError(this.prepareErrorPayload(info, description));
        }
	}

	@Override
	public void onError(Activity activity, int requestCode, Exception e) {
        this.loginListener.onError(this.prepareErrorPayload(Integer.toString(requestCode), e.getMessage()));
	}

    /* Private methods */
    private KrollDict prepareSuccessPayload(JSONObject source) {
		Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		HashMap<String, Object> data = gson.fromJson(source.toString(), type);

		KrollDict payload = new KrollDict();

		payload.put("success", true);
        payload.put("canceled", false);
		payload.put("data", data);
		payload.put("uid", (String)data.get("id"));

		return payload;
	}

    private KrollDict prepareErrorPayload(String code, String message) {
        KrollDict payload = new KrollDict();

		payload.put("success", false);
        payload.put("canceled", code.equals("USER_CANCELED"));
		payload.put("code", code);
		payload.put("error", message);

		return payload;
	}
}
