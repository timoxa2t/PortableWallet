package com.example.alex.myapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class CustomDialogFragment extends DialogFragment implements
        DialogInterface.OnClickListener {
    public double DialogBonus = 0;
    double total = 0;
    private View form=null;
    EditText editBonus;
    boolean b = false;

    String mType;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        form= getActivity().getLayoutInflater()
                .inflate(R.layout.bonus_activity, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return(builder.setTitle("Поповнення балансу").setView(form)
                .setPositiveButton(android.R.string.ok, this)
                .setNegativeButton(android.R.string.cancel, null).create());

    }
    @Override
    public void onClick(DialogInterface dialog, int which) {

       editBonus = (EditText)form.findViewById(R.id.editBonus);

        if(!TextUtils.isEmpty(editBonus.getText().toString()))
            DialogBonus = Double.parseDouble(editBonus.getText().toString());

              TextView tv = (TextView)getActivity().findViewById(R.id.total);
        total += DialogBonus;
            DialogBonus = 0;
            tv.setText(NumberFormat.getCurrencyInstance().format(total));

    }
    public void setTotal(double total){
        this.total = total;

    }
    public double getTotal(){
        return total;
    }



    @Override
    public void onDismiss(DialogInterface unused) {
        super.onDismiss(unused);

    }

    @Override
    public void onCancel(DialogInterface unused) {
        super.onCancel(unused);
    }
}