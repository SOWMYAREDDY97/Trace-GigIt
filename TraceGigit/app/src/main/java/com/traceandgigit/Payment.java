package com.traceandgigit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class Payment extends AppCompatActivity {


    private EditText card_number;
    private EditText name_on_card;
    private EditText expiry_date;
    private EditText cvv;
    private EditText postal_code;
    private EditText mobile_number;
    private Button pay_now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        card_number = findViewById(R.id.card_number);
        name_on_card = findViewById(R.id.name_on_card);
        expiry_date = findViewById(R.id.expiry_date);
        cvv = findViewById(R.id.cvv);
        postal_code = findViewById(R.id.postal_code);
        mobile_number = findViewById(R.id.mobile_number);
        pay_now = findViewById(R.id.pay_now);


        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ParseObject payment_details = new ParseObject("payment_details");
                payment_details.put("card_number", card_number.getText().toString());
                payment_details.put("name_on_card",name_on_card.getText().toString());
                payment_details.put("expiry_date",expiry_date.getText().toString());
                payment_details.put("cvv",cvv.getText().toString());
                payment_details.put("postal_code",postal_code.getText().toString());
                payment_details.put("mobile_number",mobile_number.getText().toString());
                payment_details.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Intent ini = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(ini);
                            Toast.makeText(Payment.this, "card details added successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Payment.this, "unable to add card details " + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


    }







}