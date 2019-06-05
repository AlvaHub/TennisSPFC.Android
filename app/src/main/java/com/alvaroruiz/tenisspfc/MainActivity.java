package com.alvaroruiz.tenisspfc;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MYLOG";
    private static final int MY_PERMISSION_CAMERA = 1;
    public WebView myWebView;
    private final static int FILECHOOSER_RESULTCODE = 1;
    protected MyApp myApp;
    public Boolean webViewLoaded = false;
    public Boolean tokenSaved = false;
    public String token = null;
    public String UploadedPhotoPath = null;
    ValueCallback<Uri[]> UploadMessage = null;
    static final String Url = "https://tennisapi.websiteseguro.com/tennisWeb/";
    static final String UrlApi = "https://tennisapi.websiteseguro.com/";
    Uri CameraImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myApp = (MyApp) this.getApplicationContext();
        myApp.setActivity(this);


        setContentView(R.layout.activity_main);

        Log.i(TAG, Build.VERSION.RELEASE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        Log.i(TAG, "OnCreated");

        myWebView = findViewById(R.id.myWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //WEBCLIENT
        myWebView.setWebViewClient(new myWebClient());
        //JAVASCRIPT INTERFACE
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
        webSettings.setAllowFileAccess(true);
        enableHTML5(webSettings);

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("MyApplication", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

                UploadMessage = filePathCallback;
                final MainActivity act = (MainActivity) mWebView.getContext();

                if (ContextCompat.checkSelfPermission(act, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Ask Camera Permission
                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
                } else {
                    //Already has permission
                    return onFileChooser();
                }
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                final MainActivity act = (MainActivity) view.getContext();
                AlertDialog dialog = new AlertDialog.Builder(act).
                        setTitle("Tênis SPFC").
                        setMessage(message).
                        setIcon(ContextCompat.getDrawable(act, R.mipmap.ic_launcher)).
                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();

                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                result.cancel();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                final MainActivity act = (MainActivity) view.getContext();

                AlertDialog dialog = new AlertDialog.Builder(act).
                        setTitle("Tênis SPFC").
                        setMessage(message).
                        setIcon(ContextCompat.getDrawable(act, R.mipmap.ic_launcher)).
                        setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        }).
                        setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();

                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                result.cancel();
                            }
                        })
                        .create();
                dialog.show();
                return true;
            }
        });

        String url = MainActivity.Url;


//        Uri uri = Uri.parse("http://forrasports.servicos.ws/api/bet_image_2.jpg");
//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SEND);
//        sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
//        sendIntent.setType("image/jpeg");
//        startActivity(Intent.createChooser(sendIntent, "Share Baby"));

        Intent sharedIntent = getIntent();
        if (sharedIntent.getAction() != null && sharedIntent.getAction().equals(Intent.ACTION_SEND) && sharedIntent.getType() != null && sharedIntent.getType().startsWith("image/")) {
            Uri sharedImage = sharedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (sharedImage != null) {
                Long timestamp = System.currentTimeMillis() / 1000;
                final String imageName = timestamp.toString();
                url = MainActivity.Url + "court-status/" + imageName;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                try {
                    Bitmap bitmap = reziseBitmap(this, sharedImage, 400, 400);
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sharedImage);

                    String path = getPath(this, sharedImage);
                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(path);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);
                        bitmap = rotateBitmap(bitmap, orientation);

                    } catch (IOException e) {

                    }


                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    final String byteString = Base64.encodeToString(bytes, Base64.DEFAULT);

                    StringRequest request = new StringRequest(Request.Method.POST, MainActivity.UrlApi + "courtStatus/upload-image",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    getIntent().setAction(Intent.ACTION_MAIN);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showToast("Erro ao compartilhar imagem!");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("image", byteString);
                            params.put("name", imageName);
                            return params;

                        }
                    };
                    MySingleton.getInstance(this).addToRequestQueue(request);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        if (sharedIntent.getExtras() != null && sharedIntent.getExtras().get("PAGE") != null) {
            //showToast(sharedIntent.getExtras().get("PAGE").toString());
            myWebView.loadUrl(url + sharedIntent.getExtras().get("PAGE"));
        } else
            myWebView.loadUrl(url);
        Log.i(TAG, "LOADED");


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //showToast("NEW");
        //showToast(intent.getExtras().get("PAGE") == null ? "NO PAGE" : intent.getExtras().get("PAGE").toString());
    }

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }
    public void parseVolleyError(VolleyError verror) {
        try {
            VolleyError error = new VolleyError(new String(verror.networkResponse.data, "utf-8"));
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            JSONArray errors = data.getJSONArray("errors");
            JSONObject jsonMessage = errors.getJSONObject(0);
            String message = jsonMessage.getString("message");
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }catch(UnsupportedEncodingException x){

        }
        catch (JSONException e) {
        }
    }
    public void showToast(String message) {
        DialogBox dbx = new DialogBox();
        dbx.dialogBox(message, "OK", "", this);
    }

    public static Bitmap reziseBitmap(Context c, Uri uri, int width, int height)
            throws FileNotFoundException {
        BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
        scaleOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, scaleOptions);

        int scale = 1;
        while (scaleOptions.outWidth / scale / 2 >= width
                && scaleOptions.outHeight / scale / 2 >= height) {
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    boolean onFileChooser() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            try {
                File f = new File(getExternalFilesDir(null), "temp.jpg");
                CameraImageUri = Uri.fromFile(f);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Camera Intent
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, CameraImageUri);
            //Gallery Intent
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            //Chooser Intent
            Intent chooserItent = Intent.createChooser(intent, "Selecione");
            chooserItent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{takePictureIntent});
            startActivityForResult(chooserItent, 1);

        } else { //Only Gallery if any problem with camera
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getFilesDir();
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        UploadedPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onFileChooser();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (UploadMessage == null) return;
        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();

        if (result == null && CameraImageUri != null && intent == null && resultCode == RESULT_OK)
            result = CameraImageUri;

        if (result == null) {
            UploadMessage.onReceiveValue(new Uri[]{});
            UploadMessage = null;
            return;
        }

        Uri[] uris = new Uri[1];
        uris[0] = result;
        UploadMessage.onReceiveValue(uris);
        UploadMessage = null;
        CameraImageUri = null;

    }

    void enableHTML5(WebSettings ws) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            try {
                Log.d(TAG, "Enabling HTML5-Features");
                Method m1 = WebSettings.class.getMethod("setDomStorageEnabled", new Class[]{Boolean.TYPE});
                m1.invoke(ws, Boolean.TRUE);

                Method m2 = WebSettings.class.getMethod("setDatabaseEnabled", new Class[]{Boolean.TYPE});
                m2.invoke(ws, Boolean.TRUE);

                Method m3 = WebSettings.class.getMethod("setDatabasePath", new Class[]{String.class});
                m3.invoke(ws, "/data/data/" + getPackageName() + "/databases/");

                Method m4 = WebSettings.class.getMethod("setAppCacheMaxSize", new Class[]{Long.TYPE});
                m4.invoke(ws, 1024 * 1024 * 8);

                Method m5 = WebSettings.class.getMethod("setAppCachePath", new Class[]{String.class});
                m5.invoke(ws, "/data/data/" + getPackageName() + "/cache/");

                Method m6 = WebSettings.class.getMethod("setAppCacheEnabled", new Class[]{Boolean.TYPE});
                m6.invoke(ws, Boolean.TRUE);

                Log.d(TAG, "Enabled HTML5-Features");
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "Reflection fail", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "Reflection fail", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Reflection fail", e);
            }
        }
    }

    public void saveToken(String token) {
        Log.i("HELLOTOKEN", token);
        Log.i("WEBVIEWLOADED", webViewLoaded ? "SIM" : "NAO");

        this.token = token;
        if (webViewLoaded && !tokenSaved) {
            sendTokenToWebView();
        }
//        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("token",token);
//        editor.commit();
    }

    public void sendTokenToWebView() {
        tokenSaved = true;
        final String token = this.token;
        myWebView.post(new Runnable() {
            @Override
            public void run() {
                myWebView.loadUrl("javascript:saveToken('" + token + "')");
            }
        });

    }

    String getToken() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString("token", "Nothing");

    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}

class myWebClient extends WebViewClient {
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // TODO Auto-generated method stub
        Log.i("WEBVIEW-STARTED", url);

        super.onPageStarted(view, url, favicon);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        Log.i("WEBVIEW-OVERRIDEN", url);

        view.loadUrl(url);
        return true;

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // TODO Auto-generated method stub
        Log.i("WEBVIEW-FINISHED", url);
        super.onPageFinished(view, url);
        view.loadUrl("javascript:typeof checkLoaded !== 'undefined' ? checkLoaded() : null");
        //progressBar.setVisibility(View.GONE);
    }


}
