package rodriguez.diego.com.week_11_email_sms;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver smsSentReceiver;
    BroadcastReceiver smsDeliveredReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        if (view.getId() == R.id.sendSms) {
            // start - for Android 6.0 (Marshmallow) API level 23 and up
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS}, 0);
            }
            // end - for Android 6.0 (Marshmallow) API level 23

            EditText editText = (EditText) findViewById(R.id.textToSend);
            String message = editText.getText().toString();
            String phoneNumber = "416";  // when running on a real device you can use a real phone number
            System.out.println("MSG = " + message);

            SmsManager sms = SmsManager.getDefault();
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent("SENT"), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent("DELIVERED"), 0);

            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }

        if (view.getId() == R.id.sendEmail) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            String[] to = new String[]{"digma89@hotmail.com","test@test.com"};
            String[] cc = new String[]{"digma89@hotmail.com"};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent.putExtra(Intent.EXTRA_CC, cc);
            String subject  = "Regarding Sending emails from Android";

            EditText editText = (EditText) findViewById(R.id.textToSend);
            String message = editText.getText().toString();

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            emailIntent.setType("text/plain");

            startActivity(Intent.createChooser(emailIntent, "Send email with"));
            //startActivity(emailIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("MSG SUCCESSFULLY SENT");
                Toast.makeText(context, "Successfully sent!", Toast.LENGTH_LONG).show();
            }

        };
        smsDeliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("MSG SUCCESSFULLY DELIVERED");
                Toast.makeText(context, "Successfully delivered!", Toast.LENGTH_LONG).show();
            }
        };

        registerReceiver(smsSentReceiver, new IntentFilter("SENT"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("DELIVERED"));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

}
