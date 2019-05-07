package com.fieldarea.whon.fieldarea;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Whon on 2018/6/28.
 */

public class FieldActivity extends Activity {

    private static final String TAG = FieldActivity.class.getSimpleName();

    private Context context = this;
    private ArrayAdapter<String > adapter;

    private int unit = 0;//0 面积,1 亩,2 人
    private float areaPrePeople;
    private float fieldHeight;
    private float fieldDownBottom;
    private float fieldUpBottom;
    private float fieldArea;

    private FieldView fieldView;
    private EditText inputFieldArea;
    private Spinner selectCalculateSpinner;
    private TextView restArea;
    private Button addDivideField;
    private Button deleteDivideField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Translucent.setStatusTranslucent(getWindow());
        SetStatusBar.setStatusBarLightMode(getWindow(),false);
        setContentView(R.layout.activity_field);
        init();
        Intent intent = getIntent();
        areaPrePeople = intent.getFloatExtra("areaPrePeople",1);
        fieldHeight = intent.getFloatExtra("fieldHeight",0);
        fieldDownBottom = intent.getFloatExtra("fieldDownBottom",0);
        fieldUpBottom = intent.getFloatExtra("fieldUpBottom",0);
        fieldArea = intent.getFloatExtra("fieldArea",0);
        fieldView.setParams(fieldHeight,fieldDownBottom,fieldUpBottom,fieldArea);
        setText();
        adapter = new ArrayAdapter<String >(context,R.layout.item_select_white,R.id.text,Constants.calculate);
        adapter.setDropDownViewResource(R.layout.item_select_dropdown);
        selectCalculateSpinner.setAdapter(adapter);
        Log.d(TAG, "onClick: "+fieldHeight+" "+fieldDownBottom+" "+fieldUpBottom+" "+fieldArea);
        initListener();
    }

    private void init(){
        fieldView = (FieldView)findViewById(R.id.fieldView);
        inputFieldArea = (EditText)findViewById(R.id.inputFieldArea);
        selectCalculateSpinner = (Spinner)findViewById(R.id.selectCalculateSpinner);
        restArea = (TextView)findViewById(R.id.restArea);
        addDivideField = (Button)findViewById(R.id.addDivideField);
        deleteDivideField = (Button)findViewById(R.id.deleteDivideField);
    }

    private void initListener(){
        selectCalculateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unit = position;
                inputFieldArea.setHint("单位 "+ Constants.calculate[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        inputFieldArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(inputFieldArea.getText().toString().length()>0){
                    float srcArea = Float.parseFloat(inputFieldArea.getText().toString());
                    if(srcArea > fieldArea){
                        checkArea("面积必须小于"+fieldArea+"平方米("+Constants.decimalFormat.format(fieldArea/1000*1.5)+"亩)！！！");
                        inputFieldArea.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        addDivideField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputFieldArea.getText().toString().length()>0&&Float.parseFloat(inputFieldArea.getText().toString()) > 0){
                    float srcArea;
                    if(fieldArea == 0){
                        checkArea("可惜，没田可分了,可以返回新建一块田。");
                        inputFieldArea.setText("");
                        return;
                    }
                    srcArea = getArea();
                    if(srcArea > fieldArea){
                        checkArea("面积必须小于"+fieldArea+"平方米！！！");
                        inputFieldArea.setText("");
                        return;
                    }
                    float height,downBottom,upBottom,area;
                    area = fieldArea - srcArea;
                    height =fieldHeight - getDivideLength(area,true);
                    downBottom = fieldDownBottom;
                    upBottom = getDivideLength(area,false);
                    fieldView.createShape(Color.RED,height,downBottom,upBottom,srcArea);
                    fieldView.postInvalidate();
                    fieldArea = fieldArea - srcArea;
                    fieldHeight = fieldHeight - height;
                    fieldDownBottom = upBottom;
                    setText();
                }else {
                    Toast.makeText(context,"请输入面积",Toast.LENGTH_SHORT).show();
                }
            }
        });
        deleteDivideField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Shape shape = fieldView.deleteShape();
                if(shape != null){
                    fieldArea = fieldArea + shape.fieldArea;
                    fieldHeight = fieldHeight + shape.height;
                    fieldDownBottom = shape.downBottom;
                    setText();
                    Log.d(TAG, "onClick: fieldDownBottom:"+fieldDownBottom);
                }else {
                    checkArea("好像没了。");
                }
            }
        });
    }
    private void setText(){
        restArea.setText("剩余面积"+fieldArea+"平方米("+Constants.decimalFormat.format(fieldArea/1000*1.5)+"亩)\n"
                +areaPrePeople+"亩/人");
    }
    private void checkArea(String con){
        new AlertDialog.Builder(context)
                .setTitle("重要提示")
                .setMessage(con)
                .setNegativeButton("继续", null)
                .show();
    }
    private float getArea(){
        float area = Float.parseFloat(inputFieldArea.getText().toString());
        switch (unit){
            case 0:
                break;
            case 1:
                area = area/1.5f*1000;
                break;
            case 2:
                area = area*areaPrePeople/1.5f*1000;
                break;
            default:
                break;
        }
        return area;
    }
    private float getDivideLength(float area,boolean hORw){
        if(fieldUpBottom == 0){
            if(hORw){
                return (float) (fieldHeight*Math.sqrt(2*area/fieldHeight/fieldDownBottom));
            }else {
                return (float) (fieldDownBottom*Math.sqrt(2*area/fieldHeight/fieldDownBottom));
            }
        }else {
            float a = (fieldDownBottom - fieldUpBottom)/fieldHeight;
            float lenght;
            if(a != 0)
                lenght= (float) ((Math.sqrt(fieldUpBottom*fieldUpBottom+2*a*area)-fieldUpBottom)/a);
            else
                lenght = area/fieldUpBottom;
            if(hORw){
                return lenght;
            }else {
                return fieldUpBottom + lenght*a;
            }
        }
    }
}
