package imposo.com.application.global;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.List;

import imposo.com.application.allfeeds.volley.LruBitmapCache;
import imposo.com.application.dto.PhoneContactsDTO;

/**
 * Created by adityaagrawal on 25/10/15.
 */
public class GlobalData extends Application {
    public static final String TAG = GlobalData.class.getSimpleName();
    private String otp;
    private List<PhoneContactsDTO> contactsDTOList;
    private String gcmID;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LruBitmapCache mLruBitmapCache;
    private static GlobalData mInstance;


    public List<PhoneContactsDTO> getContactsDTOList() {
        return contactsDTOList;
    }

    public void setContactsDTOList(List<PhoneContactsDTO> contactsDTOList) {
        this.contactsDTOList = contactsDTOList;
    }

    public String getGcmID() {
        return gcmID;
    }

    public void setGcmID(String gcmID) {
        this.gcmID = gcmID;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized GlobalData getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
