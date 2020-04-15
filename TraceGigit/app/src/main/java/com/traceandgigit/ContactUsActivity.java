package com.traceandgigit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Hashtable;


public class ContactUsActivity extends AppCompatActivity {

    public EditText from , message;
    public String to = "traceandgigit@gmail.com";
    public Button send;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        from = findViewById(R.id.user_email);
        message = findViewById(R.id.message_body);
        send = findViewById(R.id.sen_mail);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OnClick event

                if (!from.getText().toString().isEmpty() && !message.getText().toString().isEmpty()){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            sendMailUsingSendGrid(from.getText().toString(), to,
                                    "Question from user",
                                    message.getText().toString());

                            Toast.makeText(getApplicationContext(),"Sending mail...", Toast.LENGTH_SHORT).show();


                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"unable to sent mail please check the feilds", Toast.LENGTH_SHORT).show();
                }


            }
        });





    }


    private void sendMailUsingSendGrid(String from, String to, String subject, String mailBody){
        Hashtable<String, String> params = new Hashtable<>();
        params.put("to", to);
        params.put("from", from);
        params.put("subject", subject);
        params.put("text", mailBody);

        SendGridAsyncTask email = new SendGridAsyncTask();
        try{
            email.execute(params);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
