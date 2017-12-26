package com.renny.simplebrowser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by yh on 2016/6/27.
 */
public class WebViewFragment extends Fragment {

    private Map<String, String> title_map = new HashMap<>();//存放标题 键是url 值是标题
    View rootView;
    WebView mWebView;
    private String mUrl;
    private OnReceivedListener onReceivedTitleListener;
    private boolean isOnReceivedTitle = false;//是否触发

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mUrl = getArguments().getString("url");
            Log.d("hello-xxx", mUrl);
        }
        if (!TextUtils.isEmpty(mUrl) && rootView == null) {
            rootView = inflater.inflate(R.layout.webview_item, container, false);
            afterViewBind(savedInstanceState);
        }
        return rootView;
    }


    public WebView getWebView() {
        return mWebView;
    }

    public void afterViewBind(Bundle savedInstanceState) {
        mWebView = rootView.findViewById(R.id.webview);
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setAllowFileAccess(true);
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                if (onReceivedTitleListener != null) {
                    String url = webView.getUrl();
                    if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(title) && !TextUtils.equals(title, url)) {
                        onReceivedTitleListener.onReceivedTitle(title);
                        title_map.put(url, title);//存放标题
                        isOnReceivedTitle = true;
                    }
                }
            }
        };
        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                if (onReceivedTitleListener != null) {
                    onReceivedTitleListener.onPageFinish(webView,url);
                }
                if (!isOnReceivedTitle && title_map.containsKey(url)) {
                    String title = title_map.get(url);
                    if (onReceivedTitleListener != null) {
                        onReceivedTitleListener.onReceivedTitle(title);
                    }
                    isOnReceivedTitle = true;
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    webView.loadUrl(url);
                    return true;
                } else {
                    return jumpScheme(url);
                }
            }
        };
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        mWebView.loadUrl(mUrl);
    }

    private boolean jumpScheme(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("referer", url);
            getActivity().startActivity(intent);
        } catch (Exception e) {
            Log.e("xxx","您所打开的第三方App未安装！");
            return false;
        }

        return true;
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnReceivedListener) {
            onReceivedTitleListener = (OnReceivedListener) context;
        }
        super.onAttach(context);
    }

    public interface OnReceivedListener {
        void onReceivedTitle(String title);

        void onPageFinish(WebView webView,String url);
    }
}