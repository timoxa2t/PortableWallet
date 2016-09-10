package com.example.alex.myapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity  implements CalendarView.OnDateChangeListener, View.OnClickListener{

    CalendarView calendarView;
    EditText editText;
    Calendar cal;
    Button submit, home;
    SharedPreferences shPref;
    DBHelper dbHelper;
    CheckBox chb;

    double total = 0;
    double daily = 0;
    int key = 0;
    int currentDay = 0;
    int maxDay = 0;
    public static int idCounter = 0;


    final String TOTAL = "Total";
    final String DAILY = "Daily";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("mLog", "In ");
        cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        idCounter = currentDay;
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
         maxDay = cal.get(Calendar.DAY_OF_MONTH);

        setContentView(R.layout.calendar_activity);
        dbHelper = new DBHelper(this);


        calendarView = (CalendarView)findViewById(R.id.calendarView);

        chb = (CheckBox)findViewById(R.id.checkBox);
        editText = (EditText)findViewById(R.id.editText);
        submit = (Button) findViewById(R.id.submit);
        home = (Button) findViewById(R.id.home);
        submit.setOnClickListener(this);
        home.setOnClickListener(this);
        chb.setOnClickListener(this);
        shPref = getPreferences(MODE_PRIVATE);



        Intent intent = getIntent();
         total = intent.getDoubleExtra(TOTAL, 0.0);
         daily = intent.getDoubleExtra(DAILY, 0.0);

        editText.setText(""+ NumberFormat.getCurrencyInstance().format(total));
        calendarView.setOnDateChangeListener(this);


    }


    @Override
    public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
        key = i2;
        if(i2 < currentDay)
            editText.setText("" + 0);
       else
        editText.setText("" + NumberFormat.getCurrencyInstance().format(readValue(i2)));

    }


    @Override
    public void onClick(View view) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();



        String number = editText.getText().toString();
        number = number.substring(0,editText.getText().length()-2);
        int breakPoint = number.indexOf(",");
        double value = 0;
        if(breakPoint != 0) {
            value = Double.parseDouble(number.substring(0, breakPoint) + "." + number.substring(breakPoint + 1, number.length()));
        }
        String id = key + "";

        switch(view.getId()) {
            case R.id.home:
                Intent intent = new Intent();
                intent.putExtra(DAILY, daily +"");
                setResult(RESULT_OK, intent);
                finish();
            case R.id.submit:
                if(key == currentDay)
                        daily = value;
                contentValues.put(DBHelper.KEY_VALUE, value);
                contentValues.put(DBHelper.KEY_CHECKED, true);
                int updCount = database.update(DBHelper.TABLE_CONTACTS, contentValues, DBHelper.KEY_ID + "= ?", new String[]{id});
                recount();
                break;
            case R.id.checkBox:
                if(!chb.isChecked()){
                    contentValues.put(DBHelper.KEY_CHECKED, false);
                    chb.setChecked(false);
                    database.update(DBHelper.TABLE_CONTACTS, contentValues, DBHelper.KEY_ID + "= ?", new String[]{id});
                    recount();
                }
                else if(chb.isChecked()){
                    contentValues.put(DBHelper.KEY_CHECKED, true);
                    chb.setChecked(true);
                    database.update(DBHelper.TABLE_CONTACTS, contentValues, DBHelper.KEY_ID + "= ?", new String[]{id});
                    recount();
                }
                break;
        }
    }

    public void recount(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int valueIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
            int checkerIndex = cursor.getColumnIndex(DBHelper.KEY_CHECKED);
            int checkedCount = 0;
            double localTotal = total;

            do {
                if (cursor.getInt(checkerIndex) == 0) {
                    checkedCount++;
                } else if (cursor.getInt(checkerIndex) == 1)
                    localTotal -= cursor.getDouble(valueIndex);
            } while (cursor.moveToNext());
            cursor.moveToFirst();
            double var = localTotal / checkedCount;
            String id = "" + currentDay;
            checkedCount = currentDay;
            do {
                if(cursor.getInt(checkerIndex) == 0){
                    contentValues.put(DBHelper.KEY_VALUE, var);
                    database.update(DBHelper.TABLE_CONTACTS, contentValues, DBHelper.KEY_ID + "= ?", new String[]{id});
                }
                checkedCount++;
                id = "" + checkedCount;
            } while (cursor.moveToNext());
        }
        cursor.close();

        dbHelper.close();


    }


    public double readValue(int day){

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        double value = 0;

        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int valueIndex = cursor.getColumnIndex(DBHelper.KEY_VALUE);
            int checkerIndex = cursor.getColumnIndex(DBHelper.KEY_CHECKED);

            do{

                if(key == cursor.getInt(idIndex)) {
                    value = cursor.getDouble(valueIndex);
                    if(cursor.getInt(checkerIndex) == 1)
                    chb.setChecked(true);
                    else
                        chb.setChecked(false);
                }
                if(key == currentDay){
                    contentValues.put(DBHelper.KEY_VALUE, daily);
                    contentValues.put(DBHelper.KEY_CHECKED, true);
                    database.update(DBHelper.TABLE_CONTACTS, contentValues, DBHelper.KEY_ID + "= ?", new String[]{key + ""});
                    value = daily;
                }

            }while(cursor.moveToNext());
        }else {
            addValue();
            Log.d("mLog", "0 rows");
        }

        cursor.close();

        dbHelper.close();
        return value;
    }

    public void addValue(){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        double value  = total / (maxDay - currentDay);

        contentValues.put(DBHelper.KEY_ID, idCounter);
        contentValues.put(DBHelper.KEY_VALUE, value);
        if(currentDay == idCounter)
            contentValues.put(DBHelper.KEY_CHECKED, true);
            else
        contentValues.put(DBHelper.KEY_CHECKED, false);

        database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
        if(idCounter <= maxDay) {
            idCounter++;
            addValue();
        }

        dbHelper.close();
    }

}

