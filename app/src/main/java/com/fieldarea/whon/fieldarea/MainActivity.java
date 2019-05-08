package com.fieldarea.whon.fieldarea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private Context                     context = this;

    private Button openFieldButton;
    private Button newFieldButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Translucent.setStatusTranslucent(getWindow());
        SetStatusBar.setStatusBarLightMode(getWindow(),false);
        setContentView(R.layout.activity_main);
        init();
        if(Build.VERSION.SDK_INT>=22){
            if(hasPermission(Constants.permissions)){
                initListener();
            }else {
                requestPermisson(Constants.REQUEST_PERMISSION_SUCCESS,Constants.permissions);
            }
        }else {
            initListener();
        }

    }

    private void init(){
        openFieldButton = (Button)findViewById(R.id.openFieldButton);
        newFieldButton = (Button)findViewById(R.id.newFieldButton);
    }

    private void initListener(){
        openFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        newFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,NewFieldActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean hasPermission(String... permissions)
    {
        for(String permission:permissions)
        {
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return  true;
    }
    private void requestPermisson(int code,String...permissions)
    {
        ActivityCompat.requestPermissions(this,permissions,code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case Constants.REQUEST_PERMISSION_SUCCESS:
                initListener();
                break;
        }
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
