package com.traceandgigit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class amount_pay extends AppCompatActivity {

    private EditText amount_to_be_payed;
    Button pay_button ;
    Button cancle ;
    public String object_id;
    static String amount_to_pay;
    Button ecit_card ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount_pay);

        amount_to_be_payed = findViewById(R.id.amount_to_be_payed);
        pay_button = findViewById(R.id.pay_button);
        cancle = findViewById(R.id.cancle_pay_button);
        ecit_card = findViewById(R.id.ecit_card);

        object_id = SignInActivity.object_id;
        if (object_id == null){
            object_id = SignUpActivity.object_id;
        }


        ecit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseQuery<ParseObject> query = ParseQuery.getQuery("card_details");
                query.whereEqualTo("user_object_name", object_id);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(amount_pay.this,Payment.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(amount_pay.this, "User doesnt have any saved card details", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(amount_pay.this,Payment.class);
                            startActivity(intent);

                        }
                    }
                });

            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(amount_pay.this,MainActivity.class);
                startActivity(intent);

            }
        });


        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                amount_to_pay = amount_to_be_payed.getText().toString();



                ParseQuery<ParseObject> query = ParseQuery.getQuery("card_details");
                query.whereEqualTo("user_object_name", object_id);
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            Toast.makeText(amount_pay.this, "Payment succefull money " + amount_to_pay + "had been sent to saloon owner", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(amount_pay.this, "User doesnt have any saved card details", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(amount_pay.this,Payment.class);
                            startActivity(intent);

                        }
                    }
                });





            }
        });


    }


}
