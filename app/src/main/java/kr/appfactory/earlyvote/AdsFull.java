package kr.appfactory.earlyvote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AdsFull {

    private static InterstitialAd adFull;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static AdsFull instance = null;
    private static Context context;
    private InterstitialAd mInterstitialAd;
    private Toast toast;

    public AdsFull(Context context) {
        this.context = context;
    }

    public static AdsFull getInstance(Context context) {


        if (instance == null) {
            instance = new AdsFull(context);
           // adFull = new InterstitialAd(context);
           // setAds2(this);
        }

        return instance;
    }


    public void  setAdsFull() {//

        Log.d("asd", "광고호출2");
        MobileAds.initialize(context, context.getResources().getString(R.string.ap_ad_id));
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded(){
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("asd", "The interstitial wasn't loaded yet.");
                }
            }
        });
    }



}
