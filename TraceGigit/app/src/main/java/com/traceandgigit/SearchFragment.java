package com.traceandgigit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public ImageButton search_action ;
    public EditText search_text ;
    List<ParseUser> matched_users = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_search_filter, container, false);

        search_action = rootView.findViewById(R.id.search_action);
        search_text = rootView.findViewById(R.id.search_text);

        search_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                matched_users= new ArrayList<>();
                final String search_string = search_text.getText().toString();
                ParseQuery<ParseUser> saloonQuery = ParseUser.getQuery();
                try{
                    saloonQuery.whereEqualTo(Constants.USER_TYPE,true);
                    saloonQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {

                            for (int i=0; i< objects.size(); i++){
                                ParseUser user = objects.get(i);
                                String shop_name = user.getString("shop_name");
                                if (shop_name.contains(search_string)){
                                    matched_users.add(user);
                                }

                            }

                            //findTheNearestSaloons(objects, latitude, longitude);

                        }
                    });

                    send_data_to_main();

                }catch (Exception e){
                    e.printStackTrace();
                }




            }
        });

        return rootView;
    }

    private void send_data_to_main(){
        if (isAdded() && getActivity() != null) {
            ((MainActivity)getActivity()).addDataToRecyclerView(matched_users);
        }
    }
}
