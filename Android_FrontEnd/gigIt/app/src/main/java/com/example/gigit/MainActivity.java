package com.example.gigit;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView textViewResult;
    private String myResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OkHttpClient client = new OkHttpClient();
        String url = "https://reqres.in/api/users?page=2";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    textViewResult =findViewById(R.id.mTextViewResult);
                 myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //myResponse is the json data from the given url
                            textViewResult.setText(myResponse);
                                    try {
                                        //parsing data to get required value,here getting emails from the json data
                                        JSONObject jObject = new JSONObject(myResponse);
                                        JSONArray dataArray = jObject.getJSONArray("data");
                                        String email ="";
                                        for(int i=0;i<5;i++) {
                                            JSONObject firstinput = dataArray.getJSONObject(i);

                                                  email=  email+"\n"+firstinput.getString("email");
                                        }
                                        textViewResult.setText(email);
                                    } catch (JSONException e) {
                                        textViewResult.setText("error");
                                    }
                        }
                    });
                }
            }
        });


    }
}

