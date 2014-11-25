package com.example.nsalerni.smslistener;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

/**
 * MainActivity is the main class in this project and executes the normal functions of the application.
 * The app has two number fields and a button solve. When two numbers are added as input and the
 * solve button is pressed, the text below the button will update with the result (n1 + n2).
 */
public class MainActivity extends Activity
{
    Button solveButton;             // Solve button - will compute the sum of first and second.
    double first, second, sum;      // Holds the numeric values of the first and second value and the sum.
    EditText firstNum, secondNum;   // Holds the string representations of the values entered in both fields.
    TextView resultView;            // Holds the text for the result (the sum of both numbers).
    AlarmManager alarmMgr;          // Allows the application to be scheduled to run in the future.
    PendingIntent pendingIntent;    // A reference to a token maintained by the system describing the original data used to retrieve it.

    /**
     * This method is called when the activity is starting.
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);       // Installs the UI defined in res/layout/main.xml

        /*
         * Create the alarm manager and set its Intent to "com.nsalerni.alarm.ACTION" (this is done
         * in order to be able to check for the Intent in class SMSListener.
         */
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(MainActivity.this, SMSListener.class);
        alarmIntent.setAction("com.nsalerni.alarm.ACTION");
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

        // Set the alarm to start at 12:00 a.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        solveButton = (Button)findViewById(R.id.solveButton);  // Maps solveButton to the UI Button.
        firstNum = (EditText)findViewById(R.id.firstValue);    // Maps firstNum to the value in number field firstValue.
        secondNum = (EditText)findViewById(R.id.secondValue);  // Maps secondNum to the value in number field secondValue.
        resultView = (TextView)findViewById(R.id.resultView);  // Maps resultView to the UI Text View resultView.

        /*
         * Set the onClickListener for solveButton.
         */
        solveButton.setOnClickListener(new View.OnClickListener()
        {
            /*
             * If solveButton is clicked, find the double value of the first and second number
             * (which are in the number field, but they are stored as strings). Compute the result,
             * and update the resultView to show the sum.
             */
            public void onClick(View v)
            {
                // If both values in the number field are not null, set the result to the sum of both values.
                if (!firstNum.getText().toString().matches("") && !secondNum.getText().toString().matches(""))
                {
                    first = Double.parseDouble(firstNum.getText().toString());
                    second = Double.parseDouble(secondNum.getText().toString());
                    sum = first + second;

                    resultView.setText("Result: " + Double.toString(sum));
                }
                else
                {
                    // If either field does not have a number, show the result as 0.
                    resultView.setText("Result: 0");
                }
            }
        });
    }
}
