package ti.linkedin;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiLifecycle.OnActivityResultEvent;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.internals.AppStore;
import com.linkedin.platform.internals.LIAppVersion;

import android.app.Activity;

import ti.linkedin.utils.Permissions;
import ti.linkedin.utils.Listener;

@Kroll.module(name="TiLinkedin", id="ti.linkedin")
public class TiLinkedinModule extends KrollModule {
	@Kroll.constant public static final String PERMISSION_BASIC_PROFILE = "r_basicprofile";
	@Kroll.constant public static final String PERMISSION_COMPLETE_PROFILE = "r_fullprofile";
	@Kroll.constant public static final String PERMISSION_COMPANY_PERMISSION = "rw_company_admin";
	@Kroll.constant public static final String PERMISSION_EMAIL_ADDRESSES = "r_emailaddress";
	@Kroll.constant public static final String PERMISSION_CONTACT_INFO = "r_contactinfo";
	@Kroll.constant public static final String PERMISSION_SHARE = "w_share";

	// Standard Debugging variables
	private static final String LCAT = "TiLinkedinModule";
	private static final boolean DBG = TiConfig.LOGD;

	private Permissions permissions;

	public TiLinkedinModule() {
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app) {

	}

	// Exposed Methods
	@Kroll.method
	public void initialize() {
		if (this.permissions != null) {
			return;
		}

		this.permissions = new Permissions();
	}

	@Kroll.method
	public void authorize() {
		TiLinkedinLoginManager loginManager = new TiLinkedinLoginManager();

		Activity activity = TiApplication.getInstance().getCurrentActivity();

		if (!LIAppVersion.isLIAppCurrent(activity)) {
            AppStore.goAppStore(activity, true);

            return;
        }

		loginManager.authorize(this, this.permissions, new Listener() {
			@Override
			public void onSuccess(KrollDict response) {
				fireEvent("login", response);
			}

			@Override
			public void onError(KrollDict error) {
				fireEvent("login", error);
			}
		});
	}

	@Kroll.method
	public void logout() {
		Activity activity = TiApplication.getInstance().getCurrentActivity();

		LISessionManager.getInstance(activity).clearSession();

		if (!this.loggedIn()) {
			this.fireEvent("logout", null);
		}
	}

	// Properties
	@Kroll.getProperty
	@Kroll.method
	public boolean loggedIn() {
		Activity activity = TiApplication.getInstance().getCurrentActivity();

		return LISessionManager.getInstance(activity).getSession().isValid();
	}

	@Kroll.getProperty
	public String accessToken() {
		Activity activity = TiApplication.getInstance().getCurrentActivity();

		if (LISessionManager.getInstance(activity).getSession().isValid())
			return LISessionManager.getInstance(activity).getSession().getAccessToken().getValue();

		return null;
	}

	@Kroll.setProperty(name="permissions")
	public void setPermissions(String[] permissions) {
		this.permissions = new Permissions(permissions);
	}

	@Kroll.getProperty
	public String[] getPermissions() {
		return this.permissions.get();
	}
}
