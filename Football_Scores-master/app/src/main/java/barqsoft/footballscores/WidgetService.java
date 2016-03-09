package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViewsService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kostas on 24/02/2016.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    private Context mContext;
    private int mAppWidgetId;
    private Cursor mCursor;

    public WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);


    }


    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();

        if (mCursor != null) {
            mCursor.close();
        }
        Date fragmentdate = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        String[] temp = {mformat.format(fragmentdate)};

        mContext.grantUriPermission("barqsoft.footballscores.widget", DatabaseContract.scores_table.buildScoreWithDate(), Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        mCursor = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, temp, null);
        Log.i("onDataSetChanged","getCount: "+mCursor.getCount());

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_scores_list_item);
        String home = "";
        String away = "";
        String matchTime = "";
        String score = "";
        int iconHome = 0;
        int iconAway = 0;
        if (mCursor.moveToPosition(position)) {
            home = mCursor.getString(COL_HOME);
            away = mCursor.getString(COL_AWAY);
            matchTime = mCursor.getString(COL_MATCHTIME);
            score = Utilies.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS));
            Double matchId = mCursor.getDouble(COL_ID);
            iconHome = Utilies.getTeamCrestByTeamName(mCursor.getString(COL_HOME));
            iconAway = Utilies.getTeamCrestByTeamName(mCursor.getString(COL_AWAY));
        }

        Log.i("gva",home);
        Log.i("gva",away);
        Log.i("gva",matchTime);
        Log.i("gva",score);
        Log.i("gva",""+iconHome);
        Log.i("gva",""+iconAway);

        remoteView.setTextViewText(R.id.home_name, home);
        remoteView.setContentDescription(R.id.home_name, mContext.getString(R.string.a11y_home_name, home));
        remoteView.setTextViewText(R.id.away_name, away);
        remoteView.setContentDescription(R.id.away_name, mContext.getString(R.string.a11y_away_name, away));
        remoteView.setTextViewText(R.id.data_textview, matchTime);
        remoteView.setContentDescription(R.id.data_textview, mContext.getString(R.string.a11y_away_name, matchTime));
        remoteView.setTextViewText(R.id.score_textview, score);
        remoteView.setContentDescription(R.id.score_textview, score);
        remoteView.setImageViewResource(R.id.home_crest, iconHome);
        remoteView.setImageViewResource(R.id.away_crest, iconAway);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
