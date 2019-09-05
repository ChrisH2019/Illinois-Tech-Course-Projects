package com.weiconghong.converter_weiconghong;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText text1;
    private TextView text2;
    private TextView history;
    private RadioButton fc;
    private RadioButton cf;

    //private SharedPreferences myPrefs;
    //private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text1 = findViewById(R.id.editText);
        text2 = findViewById(R.id.resultTextview);
        history = findViewById(R.id.historyTextView);
        fc = findViewById(R.id.F2CRadioButton);
        cf = findViewById(R.id.C2FRadioButton);

        history.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder sb = new StringBuilder();
        history.setText(sb.toString());

        //myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        //editor = myPrefs.edit();

        //if (myPrefs.contains("HISTORY")){
        //String myHistory = myPrefs.getString("HISTORY", "-----");
        //((TextView)findViewById(R.id.history)).setText(myHistory);
        //}
    }

    public void fClicked(View v){
        String selected = ((RadioButton) v).getText().toString();
        Toast.makeText(this, "You selected " + selected, Toast.LENGTH_SHORT).show();
    }

    public void cClicked(View v){
        String selected = ((RadioButton) v).getText().toString();
        Toast.makeText(this, "You selected " + selected, Toast.LENGTH_SHORT).show();
    }

    public void convertClicked(View v){
        if (text1.getText().length() != 0){
            double src = Double.parseDouble(text1.getText().toString());
            double des;
            if (fc.isChecked()) {
                des = (src - 32.0) / 1.8;
                text2.setText(String.format("%,.1f", des));
                String sText = text1.getText().toString();
                String dText = text2.getText().toString();
                String historyText = history.getText().toString();
                history.setText("F to C: " + sText + " -> "+ dText + "\n" + historyText);

                //editor.putString("HISTORY", historyText);
                //editor.apply();
            }
            else if (cf.isChecked()){
                des = (src * 1.8) + 32;
                text2.setText(String.format("%,.1f", des));
                String sText = text1.getText().toString();
                String dText = text2.getText().toString();
                String historyText = history.getText().toString();
                history.setText("C to F: " + sText + " -> " + dText + "\n" + historyText);

                //editor.putString("HISTORY", historyText);
                //editor.apply();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putString("HISTORY", history.getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);

        history.setText(savedInstanceState.getString("HISTORY"));

    }
}