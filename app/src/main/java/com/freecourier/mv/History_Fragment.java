package com.freecourier.mv;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.freecourier.mv.Declaration.Constant;
import com.freecourier.mv.Declaration.ListViewAdapter;
import com.freecourier.mv.Declaration.ListViewAdapterHistory;
import com.freecourier.mv.Declaration.UserSessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.freecourier.mv.Declaration.Constant_History.EIGHTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.FIRST_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.FOURTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.SECOND_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.THIRD_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.FIFTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.SIXTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.SEVENTH_COLUMN;

public class History_Fragment extends Fragment {
    View rootview;

    private static final String TAG = "HISTORY PAGE";
    private ArrayList<HashMap<String, String>> list;

    UserSessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.history_layout, container, false);


        RetrieveFeedTask2 obj1 = new RetrieveFeedTask2();
        obj1.execute();


        return rootview;



    }

    class RetrieveFeedTask2 extends AsyncTask<String, Void, String> {

        private Exception exception;
        public RetrieveFeedTask2() {
            list=new ArrayList<HashMap<String,String>>();
        }

        @Override
        protected String doInBackground(String[] args) {


            session = new UserSessionManager(getActivity().getApplicationContext());

            if(session.checkLogin())
                getActivity().finish();

            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // get name
            String name = user.get(UserSessionManager.KEY_NAME);

            // get email
            String email = user.get(UserSessionManager.KEY_EMAIL);


            Log.d(TAG, "session = " +name+" "+email);


            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/get_booking_info/"+email;
            HttpGet request = new HttpGet(url);
            String responseStr = "";
            try {

                HttpResponse response = client.execute(request);
                responseStr = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "outcome = " + responseStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "[" + responseStr + "]";
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            try {
                JSONArray json = new JSONArray(result);
                Log.d("error out", "in onPostExecute results123 : " + result);

                HashMap<String,String> temp = new HashMap<String, String>();
                temp.put(FIRST_COLUMN, "BookingID");
                temp.put(SECOND_COLUMN, "Name");
                temp.put(THIRD_COLUMN, "Phone");
                temp.put(FOURTH_COLUMN, "Email");
                temp.put(FIFTH_COLUMN, "Source");
                temp.put(SIXTH_COLUMN, "Destination");
                temp.put(SEVENTH_COLUMN, "Date");
                temp.put(EIGHTH_COLUMN, "Status");
                list.add(temp);

                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {

                    HashMap<String,String> temp1 = new HashMap<String, String>();
                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);

                    JSONArray json1 = new JSONArray("["+val+"]");
                    JSONObject jsonobj1 = json1.getJSONObject(0);
                    val = jsonobj1.getString("id");
                    temp1.put(FIRST_COLUMN, val);
                    val = jsonobj1.getString("traveller_name");
                    temp1.put(SECOND_COLUMN, val);
                    val = jsonobj1.getString("traveller_phone");
                    temp1.put(THIRD_COLUMN, val);
                    val = jsonobj1.getString("traveller_mail");
                    temp1.put(FOURTH_COLUMN, val);
                    val = jsonobj1.getString("source");
                    temp1.put(FIFTH_COLUMN, val);
                    val = jsonobj1.getString("dest");
                    temp1.put(SIXTH_COLUMN, val);
                    val = jsonobj1.getString("booked_on");
                    temp1.put(SEVENTH_COLUMN, val);
                    val = jsonobj1.getString("status");
                    temp1.put(EIGHTH_COLUMN, val);

                    list.add(temp1);
                    Log.d("error out - Element : ", "i + " + i + " val : " + val);
                }

                Log.d("error out", "in onPostExecute message : " + "list working and size :" + list.size() + " - " + list.toString());
                ListView listView=(ListView)rootview.findViewById(R.id.hlistView1);
                ListViewAdapterHistory adapter1=new ListViewAdapterHistory(getActivity(), list);
                listView.setAdapter(adapter1);


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }


}
