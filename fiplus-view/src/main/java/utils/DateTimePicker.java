package utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private static final String DATEFORMAT = "MMM-dd-yyyy hh:mm a";
    private final Integer MAX = 3;

    private Dialog mDateTimeDialog;
    private Button mSetTimeButton;
    private Button mCancelTimeButton;
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

        mDatePicker = (DatePicker) mDateTimeDialog.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) mDateTimeDialog.findViewById(R.id.timePicker);

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
                if(checkTime(time))
                {
                    //AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateEventActivity.this);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(callingContext);
                    alertDialog.setTitle("Error").setMessage(R.string.start_time_error).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                else
                {
                    //set end date if start time is set properly
                    getEndTime();
                }
            }
        });
    }

    private void getEndTime()
    {
        mDateTimeDialog = new Dialog(callingContext);
        mDateTimeDialog.setContentView(R.layout.date_time_layout);

        mDateTimeDialog.setTitle(R.string.create_event_end_time);

        mDateTimeDialog.show();

        mDatePicker = (DatePicker) mDateTimeDialog.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) mDateTimeDialog.findViewById(R.id.timePicker);

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
                if (checkTime(time)) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(callingContext);
                    alertDialog.setTitle("Error").setMessage(R.string.end_time_error).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                else
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
            }
        });
    }


    private boolean checkTime(Time time)
    {
        boolean isError;

        //get current date and date
        Calendar c = Calendar.getInstance();

        if(time.getEnd() == null)
        {
            isError = time.getStart() < c.getTime().getTime();
        }
        else
        {
            isError = time.getEnd() < time.getStart();
        }

        return isError;
    }

    public String convertTimeToString(Time time)
    {
        long startDate = time.getStart().longValue();//Double.doubleToLongBits(time.getStart());
        long endDate = time.getEnd().longValue();//Double.doubleToLongBits(time.getEnd());

        Date d1 = new Date(startDate);
        Date d2 = new Date(endDate);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return "From: " + dateFormat.format(d1) + "\nUntil : " + dateFormat.format(d2);

//        PrettyTime t1 = new PrettyTime(new Date());
//        PrettyTime t2 = new PrettyTime(new Date());
//        return "Start: " + t1.format(d1) + "\nEnd  : " + t2.format(d2);
    }

}
