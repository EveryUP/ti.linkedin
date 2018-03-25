package ti.linkedin.utils;

import org.appcelerator.kroll.KrollDict;

public interface Listener {
    void onSuccess(KrollDict response);
    void onError(KrollDict error);
}
