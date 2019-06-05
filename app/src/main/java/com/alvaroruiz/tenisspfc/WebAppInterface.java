package com.alvaroruiz.tenisspfc;
import android.content.Context;
import android.webkit.JavascriptInterface;
/**
 * Created by alvaroruiz on 14/03/18.
 */

public class WebAppInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebAppInterface(Context c) {
        mContext = c;
    }

    /** Show a Message box from the web page */
    @JavascriptInterface
    public void showToast(String message) {
        DialogBox dbx = new DialogBox();
        dbx.dialogBox(message, "I get it", "",mContext);
    }
    @JavascriptInterface
    public void loaded(String message) {

       MainActivity act =  ((MainActivity)mContext);
        act.webViewLoaded = true;
        if(!act.tokenSaved && act.token != null){
            act.sendTokenToWebView();
        }
    }
}
