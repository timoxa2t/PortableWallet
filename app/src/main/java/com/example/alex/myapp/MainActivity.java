package com.example.alex.myapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    TextView textTotal;
    TextView textDaily;
    TextView textBonus;
    Button calendar , saveButton;
    MenuItem bonus;
    CustomDialogFragment cdf;

    RelativeLayout relativeLayout;

    SharedPreferences sharedPreferences;

    public double total = 0;
    public double daily = 0;
    double spended = 0;



    final String TOTAL = "Total";
    final String DAILY = "Daily";


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout)findViewById(R.id.relativeLayout);

        editText = (EditText)findViewById(R.id.spended);
        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);
        calendar = (Button)findViewById(R.id.button);
        calendar.setOnClickListener(this);
        bonus = (MenuItem)findViewById(R.id.bonus) ;


        textDaily = (TextView)findViewById(R.id.textDaily);
        loadData();

        textTotal = (TextView)findViewById(R.id.total);
        setTextFields();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(TextUtils.isEmpty(editText.getText().toString()))
                    return false;
                spended = Double.parseDouble(editText.getText().toString());
                daily -= spended;
                total -= spended;
               setTextFields();

                    return false;
                }
        });
        textTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                total = cdf.getTotal();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:

                Intent intent = new Intent(this,CalendarActivity.class );
                intent.putExtra(TOTAL, total);
                intent.putExtra(DAILY, daily);
                startActivityForResult(intent, 1);
                break;
            case R.id.saveButton:
                saveData();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        daily = Double.parseDouble(data.getStringExtra(DAILY));
        textDaily.setText(""+ NumberFormat.getCurrencyInstance().format(daily));

    }

    public void loadData() {
       sharedPreferences = getPreferences(MODE_PRIVATE);
        daily = sharedPreferences.getFloat(DAILY, 0.0f);
        total = sharedPreferences.getFloat(TOTAL, 0.0f);


    }

    public void saveData(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(TOTAL, (float)total);
        editor.putFloat(DAILY, (float)daily);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         cdf = new CustomDialogFragment();


                if(item.getItemId() == R.id.bonus){
                    cdf.setTotal(total);
                    cdf.show(getSupportFragmentManager(), "editBonus");
                    Toast.makeText(MainActivity.this, "" + cdf.DialogBonus ,Toast.LENGTH_LONG).show();
                    setTextFields();
                }
                else if(item.getItemId() == R.id.reset) {
                    daily = 0.0;
                    total = 0.0;
                    setTextFields();


                }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStop()  {

        super.onStop();
        saveData();
    }
    public void setTextFields(){
        try {
            textDaily.setText("" + NumberFormat.getCurrencyInstance().format(daily));
            textTotal.setText("" + NumberFormat.getCurrencyInstance().format(total));
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

}
