package com.fieldarea.whon.fieldarea;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Whon on 2018/6/27.
 */

public class NewFieldActivity extends Activity{
    private static final String TAG = NewFieldActivity.class.getSimpleName();

    private Context                                         context = this;
    private List<HashMap<String,Object>>                    datalist;
    private ArrayAdapter<String >                           spinnerAdapter;

    private float                                          area = 0;
    private boolean                                        shape = true;//三角true，梯形false

    @BindView(R.id.selectShapSpinner)Spinner selectShapeSpinner;
    @BindView(R.id.inputAreaPrePeople)EditText inputAreaPrePeople;
    @BindView(R.id.inputHeight)EditText inputHeight;
    @BindView(R.id.inputDownBottom)EditText inputDownBottom;
    @BindView(R.id.inputUpBottom)EditText inputUpBottom;
    @BindView(R.id.allArea)TextView allArea;
    @BindView(R.id.layoutUpBottom)FrameLayout layoutUpBottom;
    @BindView(R.id.createFieldButton)Button createFieldButton;
    @BindView(R.id.newFieldBackButton)Button newFieldBackButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Translucent.setStatusTranslucent(getWindow());
        SetStatusBar.setStatusBarLightMode(getWindow(),false);
        setContentView(R.layout.activity_new_field);
        ButterKnife.bind(this);
        spinnerAdapter = new ArrayAdapter<String>(context,R.layout.item_select,R.id.text,Constants.shape);
        spinnerAdapter.setDropDownViewResource(R.layout.item_select_dropdown);
        selectShapeSpinner.setAdapter(spinnerAdapter);
        initListener();
    }

    private void initListener(){
        selectShapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    layoutUpBottom.setVisibility(View.VISIBLE);
                    shape = false;
                }else {
                    inputUpBottom.setText("");
                    textChanged();
                    layoutUpBottom.setVisibility(View.GONE);
                    shape = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected");
            }
        });
        inputHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputDownBottom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputUpBottom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        createFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,FieldActivity.class);
                if(inputAreaPrePeople.getText().toString().length()!=0){
                    intent.putExtra("areaPrePeople", Float.parseFloat(inputAreaPrePeople.getText().toString()));
                }
                if(inputHeight.getText().toString().length()!=0) {
                    intent.putExtra("fieldHeight", Float.parseFloat(inputHeight.getText().toString()));
                }else {
                    Toast.makeText(context,"输入高的长度",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(inputDownBottom.getText().toString().length()!=0) {
                    intent.putExtra("fieldDownBottom", Float.parseFloat(inputDownBottom.getText().toString()));
                }
                else{
                    Toast.makeText(context,"输入底的长度",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!shape){
                    if(inputUpBottom.getText().toString().length()!=0) {
                        intent.putExtra("fieldUpBottom", Float.parseFloat(inputUpBottom.getText().toString()));
                    }
                }
                intent.putExtra("fieldArea",area);
                startActivity(intent);
            }
        });
        newFieldBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void textChanged(){
        float height = 0,downBottom = 0,upBottom = 0;
        if(inputHeight.getText().toString().length() != 0){
            height = Float.parseFloat(inputHeight.getText().toString());
        }
        if(inputDownBottom.getText().toString().length() != 0){
            downBottom = Float.parseFloat(inputDownBottom.getText().toString());
        }
        if(inputUpBottom.getText().toString().length() != 0){
            upBottom = Float.parseFloat(inputUpBottom.getText().toString());
        }
        if(shape){
            if(height != 0&&downBottom != 0){
                area = height*downBottom/2;
                allArea.setText("总面积"+area+"平方米("+Constants.decimalFormat.format(area/1000*1.5)+"亩)");
                allArea.setVisibility(View.VISIBLE);
            }else {
                allArea.setVisibility(View.GONE);
            }
        }else {
            if(height != 0&&(downBottom != 0||upBottom != 0)){
                area = height*(downBottom + upBottom)/2;
                allArea.setText("总面积"+area+"平方米("+Constants.decimalFormat.format(area/1000*1.5)+"亩)");
                allArea.setVisibility(View.VISIBLE);
            }else {
                allArea.setVisibility(View.GONE);
            }
        }
        Log.d(TAG, "onTextChanged: height:"+height+" downBottom:"+downBottom+" upBottom:"+upBottom);
    }
}
