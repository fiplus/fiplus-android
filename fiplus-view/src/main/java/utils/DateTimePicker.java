package utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.Fiplus.R;
import com.wordnik.client.model.Time;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimePicker {

    private static final String DATEFORMAT = "E MMM dd, hh:mm a";
    private final static int TIME_PICKER_INTERVAL = 5;
    private final Integer MAX = 3;

    private Dialog mDateTimeDialog;
    private Button mSetTimeButton;
    private Button mCancelTimeButton;
    private CheckBox mSetEndTime;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private Time time = null;

    private Context callingContext;
    private Class<?> callingClass;

    //for create event
    protected List<Time> mDateTimeListItemsUTC;
    protected List<String> mDateTimeListItems;
    protected ArrayAdapter<String> dateTimeAdapter;

    //for create event
    public DateTimePicker (Context c)
    {
        callingContext = c;
        time = new Time();
        callingClass = c.getClass();
    }

    public void showDateTimePickerDialog() {

        if(callingContext == null)
            System.out.print("LAME");
        mDateTimeDialog = new Dialog(callingContext);
        mDateTimeDialog.setContentView(R.layout.date_time_layout);

        mDateTimeDialog.setTitle(R.string.create_event_start_time);

        mDateTimeDialog.show();

        mSetEndTime = (CheckBox) mDateTimeDialog.findViewById(R.id.no_end_time_checkbox);
        mDatePicker = (DatePicker) mDateTimeDialog.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) mDateTimeDialog.findViewById(R.id.timePicker);

        //update 30 mins
        Integer mins = mTimePicker.getCurrentMinute() + 30;
        Integer hour = mTimePicker.getCurrentHour();
        mTimePicker.setCurrentMinute(mins);
        if(mins>59)
        {
            mTimePicker.setCurrentHour(hour + 1);
        }

        //for cancel button
        mCancelTimeButton = (Button) mDateTimeDialog.findViewById(R.id.cancelPicker);
        mCancelTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimeDialog.dismiss();
            }
        });

        //for set button
        mSetTimeButton = (Button) mDateTimeDialog.findViewById(R.id.setPicker);
        mSetTimeButton.setText("Set Start Time");
        mSetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int month = mDatePicker.getMonth();
                int year = mDatePicker.getYear();
                int day = mDatePicker.getDayOfMonth();
                int hour = mTimePicker.getCurrentHour();
                int minutes = mTimePicker.getCurrentMinute();

                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day, hour, minutes);

                Long lTime = cal.getTime().getTime();

                time.setStart(lTime.doubleValue());
                mDateTimeDialog.dismiss();

                String timeCheck = checkTime(time, cal);
                if(timeCheck != null)
                {
                    //AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateEventActivity.this);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(callingContext);
                    alertDialog.setTitle("Error").setMessage(timeCheck).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                else if(!mSetEndTime.isChecked())
                {
                    //set end date if start time is set properly
                    getEndTime(year, month, day, hour, minutes);
                }
                else
                {
                    addToList();
                }
            }
        });
    }

    private void getEndTime(int year, int month, int day, int hour, int minutes)
    {
        mDateTimeDialog = new Dialog(callingContext);
        mDateTimeDialog.setContentView(R.layout.date_time_layout);

        mDateTimeDialog.setTitle(R.string.create_event_end_time);

        mDateTimeDialog.show();

        mSetEndTime = (CheckBox) mDateTimeDialog.findViewById(R.id.no_end_time_checkbox);
        mSetEndTime.setVisibility(View.GONE);
        mDatePicker = (DatePicker) mDateTimeDialog.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) mDateTimeDialog.findViewById(R.id.timePicker);

        //set the end date and time picker to start as to what the start date & time got set
        mDatePicker.updateDate(year, month, day);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minutes);

        //for cancel button
        mCancelTimeButton = (Button) mDateTimeDialog.findViewById(R.id.cancelPicker);
        mCancelTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //TODO: (Jobelle) Implement end time as optional
                    //this should still create the start time
                    mDateTimeDialog.dismiss();
            }
        });

        //for set button
        mSetTimeButton = (Button) mDateTimeDialog.findViewById(R.id.setPicker);
        mSetTimeButton.setText("Set End Time");
        mSetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int month = mDatePicker.getMonth();
                int year = mDatePicker.getYear();
                int day = mDatePicker.getDayOfMonth();
                int hour = mTimePicker.getCurrentHour();
                int minutes = mTimePicker.getCurrentMinute();

                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day, hour, minutes);

                Long lTime = cal.getTime().getTime();

                time.setEnd(lTime.doubleValue());
                mDateTimeDialog.dismiss();

                String timeCheck = checkTime(time, cal);
                if (timeCheck != null) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(callingContext);
                    alertDialog.setTitle("Error").setMessage(timeCheck).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                else
                {
                    addToList();
                }
            }
        });
    }

    private void addToList()
    {
        try {
            //instantiate an object of the class above
            //Class helloClass = Class.forName(callingClass.getName());//callingClass.newInstance();
            Object o = callingContext;

            //Prepare array of the arguments that your function accepts
            Class[] paramTypes = new Class[2];
            paramTypes[0]=Time.class;
            paramTypes[1]=String.class;

            Method j[] = callingClass.getDeclaredMethods();

            //Instantiate an object of type method that returns you method name
            Method m = callingClass.getDeclaredMethod("addDateTime", paramTypes);

            //invoke method with actual params
            m.invoke(o, time, convertTimeToString(time));
        }
        catch (Throwable e) {
            System.err.println(e);
        }
    }


    private String checkTime(Time time, Calendar cal)
    {
        String isError = null;

        //get current date and date
        Calendar c = Calendar.getInstance();

        int result = (int)((cal.getTimeInMillis() - c.getTimeInMillis()) / (1000 * 60 * 60 * 24));

        //check if entered time is more than a year from now
        if((result) > 365)
        {
            isError = callingContext.getString(R.string.time_more_than_a_year);
        }
        // check if start time is less than the current time
        else if(time.getEnd() == null && (time.getStart() < c.getTime().getTime()))
        {
            isError = callingContext.getString(R.string.start_time_error);
        }
        //check if end time is less than start time
        else if(time.getEnd() != null && time.getEnd() < time.getStart())
        {

            isError = callingContext.getString(R.string.end_time_error);
        }

        return isError;
    }

    public String convertTimeToString(Time time)
    {
        SimpleDateFormat sdf1 = new SimpleDateFormat(DATEFORMAT);

        long startDate = time.getStart().longValue();
        Date d1 = new Date(startDate);

        if(time.getEnd() != null)
        {
            long endDate= time.getEnd().longValue();
            Date d2 = new Date(endDate);

            String format2 = DATEFORMAT;
            String middle = " to ";
            boolean curYear = d1.getYear() == new Date().getYear();
            boolean sameMonth = d1.getMonth() == d2.getMonth();
            boolean sameDay = d1.getDate() == d2.getDate();

            if(sameDay && sameMonth)
            {
                format2 = "hh:mm a";
                middle = " to ";
            }
            if(!curYear)
            {
                format2 += ", yyyy";
            }

            SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
            return sdf1.format(d1) + middle + sdf2.format(d2);
        }
        else
        {
            return sdf1.format(d1);
        }
    }

}
