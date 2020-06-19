package com.mposyandu.mposyandu;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mposyandu.mposyandu.tools.ConnectivityReceiver;

/**
 * Created by adi37 on 03/23/2018.
 */

public class Controller extends Application {
  public static final String TAG = Controller.class.getSimpleName();
  private RequestQueue requestQueue;
  private static Controller mInstance;
  private ImageLoader mImageLoader;

  @Override
  public void onCreate() {
    super.onCreate();
    mInstance = this;
  }

  public static synchronized Controller getmInstance() {
    return mInstance;
  }

  public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
    ConnectivityReceiver.connectivityReceiverListener = listener;
  }

  public RequestQueue getRequestQueue() {
    if(requestQueue == null) {
      requestQueue = Volley.newRequestQueue(getApplicationContext());
    }
    return requestQueue;
  }
  public <T> void addToRequestQueue(Request<T> req, String tag) {
    req.setTag(TextUtils.isEmpty(tag)? TAG : tag);
    req.setRetryPolicy((new DefaultRetryPolicy(
            5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    getRequestQueue().add(req);
  }
  public <T> void addToRequestQueue(Request<T> req) {
    req.setTag(TAG);
    req.setRetryPolicy((new DefaultRetryPolicy(
            5000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    getRequestQueue().add(req);
  }
  public void cancelPendingRequest(Object tag) {
    if(requestQueue != null) {
      requestQueue.cancelAll(tag);
    }
  }
  public ImageLoader getImageLoader() {
    getRequestQueue();
    if (mImageLoader == null) {
      mImageLoader = new ImageLoader(this.requestQueue,
              new BitmapCache());
    }
    return this.mImageLoader;
  }

}
