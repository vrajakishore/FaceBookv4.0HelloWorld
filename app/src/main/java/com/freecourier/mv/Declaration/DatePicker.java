package com.freecourier.mv.Declaration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.freecourier.mv.R;
import com.freecourier.mv.Send_Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);


        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void populateSetDate(int year, int month, int day) {

        TextView tv = (TextView)getFragmentManager().findFragmentById(R.id.container).getView().findViewById(R.id.get_date);
        tv.setText(year+"-"+month+"-"+day);
        //Toast.makeText(getActivity(), year+"/"+month+"/"+day, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        populateSetDate(year, monthOfYear + 1, dayOfMonth);

    }



}
