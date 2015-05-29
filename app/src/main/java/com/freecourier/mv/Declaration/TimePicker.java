package com.freecourier.mv.Declaration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.freecourier.mv.R;

import java.util.Calendar;


public class TimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hh = calendar.get(Calendar.HOUR);
        int mm = calendar.get(Calendar.MINUTE);


        return new TimePickerDialog(getActivity(),this,mm,hh,false);
    }


    public void populateSetTime(int hour, int minute) {
        TextView tt = (TextView)getFragmentManager().findFragmentById(R.id.container).getView().findViewById(R.id.get_time);
        tt.setText(hour+":"+minute);
        //Toast.makeText(getActivity(), hour + " : " + minute + " " , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        populateSetTime(hourOfDay,minute);
    }
}
