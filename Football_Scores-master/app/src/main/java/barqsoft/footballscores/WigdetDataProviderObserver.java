package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;

import barqsoft.footballscores.R;

/**
 * Created by kostas on 29/02/2016.
 */
public class WigdetDataProviderObserver extends ContentObserver {

    private AppWidgetManager mAppWidgetManager;
    private ComponentName mComponentName;

    WigdetDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
        super(h);
        mAppWidgetManager = mgr;
        mComponentName = cn;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onChange(boolean selfChange) {
        mAppWidgetManager.notifyAppWidgetViewDataChanged(
                mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.listViewWidget);
    }

}
