package ti.linkedin.utils;

import android.text.TextUtils;

import com.linkedin.platform.utils.Scope;
import com.linkedin.platform.utils.Scope.LIPermission;

import ti.linkedin.TiLinkedinModule;

public class Permissions {
    private String[] permissions;

    public Permissions() {
        this.permissions = new String[]{
           TiLinkedinModule.PERMISSION_BASIC_PROFILE,
           TiLinkedinModule.PERMISSION_EMAIL_ADDRESSES
       };
    }

    public Permissions(String[] scopes) {
        this.permissions = scopes;
    }

    public void set(String[] newPermissions) {
        this.permissions = newPermissions;
    }

    public String[] get() {
        return this.permissions;
    }

    public String build() {
        return TextUtils.join(" ", this.permissions);
    }
}
