package kr.appfactory.earlyvote;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView mWebView;
    private Context mContext=MainActivity.this;
    final AppCompatActivity activity = this;
    private static final int REQUEST = 112;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 0;
    private static  int networkYn = 0;
    private ProgressDialog pd;
    private CustomAnimationDialog customAnimationDialog;
    private BackPressCloseHandler backPressCloseHandler;

    private String myUrl = "https://appfactory.kr/map"; // 접속 URL (내장HTML의 경우 왼쪽과 같이 쓰고 아니면 걍 URL)



    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                //do here
            }
        } else {
            //do here
        }


        this.backPressCloseHandler = new BackPressCloseHandler(this);
        customAnimationDialog = new CustomAnimationDialog(MainActivity.this);
        updateIconBadge(activity,  0);
        setContentView(R.layout.activity_main);

        // 웹뷰 셋팅팅
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        Online();
        if(networkYn==2){

            NotOnline();
        }else {

            mWebView.setWebViewClient(new WebViewClientClass(){
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (url != null && ( url.startsWith("https://") ) ) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            mWebView.setWebChromeClient(new WebChromeClient(){

                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    super.onGeolocationPermissionsShowPrompt(origin, callback);
                    callback.invoke(origin, true, false); }

            });

            mWebView.loadUrl(myUrl); // 접속 URL
        }

        // 키보드 내리기
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        Log.d(TAG, "페이지이동 ");

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {

        if (this.mWebView.canGoBack()) {
            this.mWebView.goBack();
        } else {

            this.backPressCloseHandler.onBackPressed();
        }

    }
    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Online();
            if(networkYn==2){

                NotOnline();
                return true;

            }else {

                Log.d("check URL", url);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);


            }
        }
    }
    public static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    public static void updateIconBadge(Context context, int notiCnt) {
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", notiCnt);
        badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
        badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        context.sendBroadcast(badgeIntent);
    }
    public void NotOnline() {
        final String networkmsg = getString(R.string.networkmsg);

        //mWebView.loadUrl("javascript:alert('"+networkmsg+"')");



        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setIcon(R.drawable.vote_icon)
                .setTitle(R.string.app_name)
                .setMessage(""+networkmsg+"")
                .setNegativeButton(R.string.exitmsgN, null)
                .setPositiveButton(R.string.exitmsgY,new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,int whichButton)
                    {
                        finish();
                        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    }
                }).show();

    }
    public void Online() {
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
        if (wifi.isConnected() || mobile.isConnected()) {

            Log.d("연결됨" , "연결이 되었습니다.");
            networkYn =1;


        } else {
            Log.d("연결 안 됨" , "연결이 다시 한번 확인해주세요");
            networkYn =2;

        }
    }



}
