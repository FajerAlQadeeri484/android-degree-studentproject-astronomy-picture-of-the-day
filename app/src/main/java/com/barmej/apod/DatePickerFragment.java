package com.barmej.apod;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DatePickerDialog.OnDateSetListener){
            onDateSetListener = (DatePickerDialog.OnDateSetListener) context;
        }else {
            throw new RuntimeException("Activity must implement DatePickerDialog.OnDateSetListener");
        }
    }
}
