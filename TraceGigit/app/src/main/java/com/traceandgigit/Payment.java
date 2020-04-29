package com.traceandgigit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class Payment extends AppCompatActivity {


    private EditText card_number;
    private EditText name_on_card;
    private EditText expiry_date;
    private EditText cvv;
    private EditText postal_code;
    private EditText mobile_number;
    private Button pay_now;
    public String object_id;

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



        ParseQuery<ParseObject> query = ParseQuery.getQuery("card_details");
        query.whereEqualTo("user_object_name", object_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    card_number.setText(object.getString("card_number"));
                    name_on_card.setText(object.getString("name_on_card"));
                    expiry_date.setText(object.getString("expiry_date"));
                    cvv.setText(object.getString("cvv"));
                    postal_code.setText(object.getString("postal_code"));
                    mobile_number.setText(object.getString("mobile_number"));


                } else {



                }
            }
        });

        object_id = SignInActivity.object_id;
        if (object_id == null){
            object_id = SignUpActivity.object_id;
        }


        pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (card_number.getText() == null || card_number.getText().toString().isEmpty() ||
                name_on_card.getText().toString().isEmpty() || name_on_card.getText() == null ||
                expiry_date.getText().toString().isEmpty() || expiry_date.getText() == null ||
                cvv.getText().toString().isEmpty() || cvv.getText() == null ||
                        postal_code.getText().toString().isEmpty() || postal_code.getText() == null ||
                        mobile_number.getText().toString().isEmpty() || mobile_number.getText() == null){
                    Toast.makeText(Payment.this, "Please check the inputs which you have entered ..!!" , Toast.LENGTH_SHORT).show();



                }
                else{

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("card_details");
                    query.whereEqualTo("user_object_name", object_id);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {

                                object.put("card_number", card_number.getText().toString());
                                object.put("name_on_card",name_on_card.getText().toString());
                                object.put("expiry_date",expiry_date.getText().toString());
                                object.put("cvv",cvv.getText().toString());
                                object.put("postal_code",postal_code.getText().toString());
                                object.put("mobile_number",mobile_number.getText().toString());

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Intent ini = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(ini);
                                            Toast.makeText(Payment.this, "card details added successfully and money " + amount_pay.amount_to_pay + "had been sent to saloon owner", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(Payment.this, "unable to add card details " + e, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                            } else {

                                final ParseObject payment_details = new ParseObject("card_details");
                                payment_details.put("card_number", card_number.getText().toString());
                                payment_details.put("name_on_card",name_on_card.getText().toString());
                                payment_details.put("expiry_date",expiry_date.getText().toString());
                                payment_details.put("cvv",cvv.getText().toString());
                                payment_details.put("postal_code",postal_code.getText().toString());
                                payment_details.put("mobile_number",mobile_number.getText().toString());
                                payment_details.put("user_object_name", object_id);
                                payment_details.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Intent ini = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(ini);
                                            Toast.makeText(Payment.this, "card details added successfully and money " + amount_pay.amount_to_pay + "had been sent to saloon owner", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(Payment.this, "unable to add card details " + e, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        }
                    });








                }
            }
        });


    }







}