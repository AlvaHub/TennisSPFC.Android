package com.alvaroruiz.tenisspfc;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by alvaroruiz on 28/05/19.
 */

public class MySingleton {
    private static Context MyContext;
    private RequestQueue MyRequestQueue;
    private static MySingleton MyInstance;

    private MySingleton(Context context) {
        this.MyContext = context;
        this.MyRequestQueue = this.MyRequestQueue == null ? Volley.newRequestQueue(this.MyContext) : this.MyRequestQueue;
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (MyInstance == null)
            MyInstance = new MySingleton(context);
        return MyInstance;
    }

    public void addToRequestQueue(StringRequest request) {

        MyRequestQueue.add(request);

    }
}
