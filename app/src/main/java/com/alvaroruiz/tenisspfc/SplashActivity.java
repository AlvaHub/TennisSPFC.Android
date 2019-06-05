package com.alvaroruiz.tenisspfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redirectToMain();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        redirectToMain();
    }

    void redirectToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(getIntent().getAction());
        intent.setType(getIntent().getType());
        intent.putExtra(Intent.EXTRA_STREAM, getIntent().getParcelableExtra(Intent.EXTRA_STREAM));
        startActivity(intent);
    }
}
