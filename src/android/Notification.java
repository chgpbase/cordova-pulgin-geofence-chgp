package com.cowbell.cordova.geofence;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.graphics.BitmapFactory;
import android.util.Log;
//import android.util.Log;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;

import com.google.gson.annotations.Expose;

public class Notification {
    private Context context;
    private AssetUtil assets;

    @Expose public int id;
    @Expose public String title;
    @Expose public String text;
    @Expose public long[] vibrate = new long[] { 1000 };
    @Expose public String icon = "";
    @Expose public String image = "";
    @Expose public String smallIcon = "";
    @Expose public Object data;
    @Expose public boolean openAppOnClick;
    @Expose public String dateStart;
    @Expose public String dateEnd;
    @Expose public String timeStart;
    @Expose public String timeEnd;
    @Expose public int scenarioDayType;
    @Expose public String deeplink;
    @Expose public boolean happensOnce;
    @Expose public boolean notificationShowed = false;

    public void setContext(Context context) {
        this.context = context;
        this.assets = AssetUtil.getInstance(context);
    }

    public String getText() {
        return this.text;
    }

    public String getTitle() {
        return this.title;
    }

    public int getSmallIcon() {
        int resId = assets.getResIdForDrawable(this.smallIcon);

        if (resId == 0) {
            resId = android.R.drawable.ic_menu_mylocation;
        }

        return resId;
    }

    public Bitmap getLargeIcon() {
        Bitmap bmp;

        try{
            Uri uri = assets.parse(this.icon);
            bmp = assets.getIconFromUri(uri);
        } catch (Exception e){
            bmp = assets.getIconFromDrawable(this.icon);
        }

        return bmp;
    }

    public Bitmap getImage() {
        Bitmap bmp;
        Uri uri;
        String scheme;
        Logger logger = Logger.getLogger();

        try{
                uri = Uri.parse(this.image);
                scheme = uri.getScheme();
            } catch (Exception e){
                scheme = "";
            }
               logger.log(Log.DEBUG, "Geofence scheme "+scheme);

             switch (scheme) {
                 case "https":
                 case "http":
                     try {
                         logger.log(Log.DEBUG, "Geofence image "+this.image);

                         URL url = new URL(this.image);
                         logger.log(Log.DEBUG, "Geofence  HttpURLConnection connection");
                         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                         logger.log(Log.DEBUG, "Geofence  connection.setDoInput(true);");
                         connection.setDoInput(true);
                         logger.log(Log.DEBUG, "Geofence  connection.connect();");
                         connection.connect();
                         logger.log(Log.DEBUG, "Geofence connection.getInputStream();");
                         InputStream input = connection.getInputStream();
                         logger.log(Log.DEBUG, "Geofence BitmapFactory.decodeStream(input);");
                         bmp = BitmapFactory.decodeStream(input);
                         logger.log(Log.DEBUG, "Geofence bmp ok");

                     } catch (Exception e) {
                         logger.log(Log.DEBUG, "Geofence exception: " + e.getMessage());
                         logger.log(Log.DEBUG, "Geofence exception: " + e.toString());
                         bmp = null;
                     }
                     break;
                 default:
                     try {
                         uri = assets.parse(this.image);
                         bmp = assets.getIconFromUri(uri);
                     } catch (Exception e){
                        bmp = assets.getIconFromDrawable(this.image);
                     }
             }


        return bmp;
    }

    public String getDataJson() {
        if (this.data == null) {
            return "";
        }

        return Gson.get().toJson(this.data);
    }

    public long[] getVibrate() {
        return concat(new long[] {0}, vibrate);
    }

    public String toString() {
        return "Notification title: " + getTitle() + " text: " + getText() + "image: "+this.image;
    }

    private long[] concat(long[] a, long[] b) {
        long[] c = new long[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
