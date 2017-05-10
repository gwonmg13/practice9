package kr.soen.practice9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    EditText et;
    WebView webView;
    ProgressDialog dialog;
    Animation animTop;
    ListView listview;
    LinearLayout linear,linear2;
    ArrayList<String> urldata  = new ArrayList<String>();
    ArrayAdapter<String> adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listview = (ListView)findViewById(R.id.listview);

        linear = (LinearLayout)findViewById(R.id.linear);
        linear2 = (LinearLayout)findViewById(R.id.linear2);

        et = (EditText)findViewById(R.id.et);
        webView = (WebView)findViewById(R.id.webview);
        webView.setVisibility(View.VISIBLE);
        linear.setVisibility(View.VISIBLE);



        //웹에서 버튼누르면 바뀌게 하는거
        webView.addJavascriptInterface(new JavaScriptMethods(),"MyApp");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }
        });
        webView.loadUrl("http://www.naver.com");
        WebSettings webSettings = webView.getSettings();

        //자바스크립트 사용하기
        webSettings.setJavaScriptEnabled(true);

        //WebView 내장 Zoom 사용
        webSettings.setBuiltInZoomControls(true);

        webSettings.setSupportZoom(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                et.setText(url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });

        dialog = new ProgressDialog(this);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dialog.setMessage("Loading...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress >=100)dialog.dismiss();
            }
        });
        animTop = AnimationUtils.loadAnimation(this, R.anim.translate_top);
        animTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linear.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listview.setVisibility(View.INVISIBLE);

                linear2.setVisibility(View.VISIBLE);
                int pointer = urldata.get(position).indexOf(" ");
                String url = urldata.get(position).substring(pointer + 1);

                webView.loadUrl("http://"+url);
                webView.setVisibility(View.VISIBLE);
                if (url.contains("http")) {
                    webView.loadUrl(url);
                } else {
                    webView.loadUrl("http://" + url);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,1,0,"즐겨찾기추가");
        menu.add(0,2,0,"즐겨찾기목록");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == 1){
            linear2.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);
            linear.setVisibility(View.VISIBLE);
            linear.setAnimation(animTop);
            webView.loadUrl("File:///android_asset/www/urladd.html");
            animTop.start();



        }else if(item.getItemId()==2){

            listview.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.GONE);
            webView.setVisibility(View.INVISIBLE);
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_single_choice,urldata);
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();



        }
        return super.onOptionsItemSelected(item);
    }


    Handler myhandler = new Handler();

    class JavaScriptMethods{
        //어노테이션 있는것만 웹페이지에서 앱에서 불러서 쓸 수 있다.
        @JavascriptInterface
        public void displayToast(){

            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Main2Activity.this);
                    dlg.setTitle("그림변경")
                    .setMessage("그림을 변경하시겠습니까?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            webView.loadUrl("javascript:changeImage()");
                        }
                    }).setNegativeButton("Cancel",null)
                            .show();
                }
            });
        }
        @JavascriptInterface
        public void displayurl(){

            myhandler.post(new Runnable() {
                @Override
                public void run() {
                    linear.setVisibility(View.VISIBLE);
                }
            });
        }
        @JavascriptInterface
        public void adddata(final String sitename, final String url){
            myhandler.post(new Runnable() {
                @Override
                public void run() {

                    int count = urldata.size();

                    if(count == 0 ){
                        urldata.add("<" + sitename + ">" + " " +url);

                    }else{

                        for (int i = 0 ; i < count ; i++){
                            if(urldata.get(i).contains(url)){
                                webView.loadUrl("javascript:displayMsg()"); webView.loadUrl("javascript:displayMsg()");


                            }else{
                                urldata.add("<"+sitename+">"+url);
                                adapter.notifyDataSetChanged();
                                String msg = sitename + " 이(가) 등록되었습니다.";
                                webView.loadUrl("javascript:setMsg('" + msg + "')");
                            }

                        }

                    }

                    Toast.makeText(getApplicationContext(),"확인하겠습니다 : "+sitename+":"+url,Toast.LENGTH_SHORT).show();


                }
            });

        }




    }
}
