package com.freecourier.mv;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

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
import java.util.List;


public class Send_Fragment extends Fragment {
    private static final String TAG = "SEND FRAGMENT";
    public static String dt = null;
    public static String tm = null;
    public String source1, destination, jdate;
    public static final int DATEPICKER_FRAGMENT = 1; // class variable


    View rootview;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.send_layout, container, false);
        RetrieveFeedTask obj = new RetrieveFeedTask();
        obj.execute();


        Button btnSearch = (Button) rootview.findViewById(R.id.button);
        btnSearch.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                Spinner s_1 = (Spinner) rootview.findViewById(R.id.spinner);
                Spinner s_2 = (Spinner) rootview.findViewById(R.id.spinner2);

                String[] args = new String[3];
                args[0] = s_1.getSelectedItem().toString();
                source1 = args[0];

                args[1] = s_2.getSelectedItem().toString();
                destination = args[1];

                ImageView btnNew = (ImageView) rootview.findViewById(R.id.newbutton);

                btnNew.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {


                        com.freecourier.mv.Declaration.DatePicker newFragment = new com.freecourier.mv.Declaration.DatePicker();

                        newFragment.show(getFragmentManager(), "DatePicker");


                    }


                });

          /*      DatePicker datePicker = (DatePicker) rootview.findViewById(R.id.datePicker);
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

               // SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

                //args[2] = sdf.format(new Date(calendar.getDate()));
                args[2] = year+"/"+month+"/"+day;*/

                jdate = args[2];
                // Toast.makeText(getActivity(), dt , Toast.LENGTH_LONG).show();


                Log.d(TAG, "Button inside kishore " + source1 + " " + destination + " " + jdate);

         /*       //Toast.makeText(getActivity(),"source"+source1+" destination"+destination+" jdate" +jdate,Toast.LENGTH_LONG).show();
                Fragment fragment = new Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("src",args[0]);
                bundle.putString("des",args[1]);
                bundle.putString("jdate", args[2]);
                fragment.setArguments(bundle);

*/

                new RetrieveFeedTask2().execute(args);

                Intent intent = new Intent(getActivity(), Traveller_activity.class);
                startActivity(intent);

                //Toast.makeText(getActivity(), "Successfully submitted ", Toast.LENGTH_LONG).show();

                // Log.d(TAG, "Button onclick end   ");
            }
        });

        return rootview;


    }


    class RetrieveFeedTask2 extends AsyncTask<String, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(String[] args) {
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/get_travel_users/";
            HttpPost request = new HttpPost(url);
            String responseStr = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("source", args[0]));
                nameValuePairs.add(new BasicNameValuePair("des", args[1]));
                nameValuePairs.add(new BasicNameValuePair("date", args[2]));
                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = client.execute(request);
                Log.d(TAG, "input = " + args[0] + " - " + args[1] + " - " + args[2]);
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

                JSONObject jsonobj = json.getJSONObject(0);
                String message = jsonobj.getString("message");
                Log.d("error out", "in onPostExecute message : " + message);
                if (message.equalsIgnoreCase("success")) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;
        private ArrayList<String> cities;

        public RetrieveFeedTask() {
            cities = new ArrayList<String>();
        }

        @Override
        protected String doInBackground(String[] args) {


            Log.d(TAG, "execute1");
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/get_cities/";
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
                cities.add("Select");
                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {
                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);
                    cities.add(val);
                    Log.d("error out - Element : ", "i + " + i + " val : " + val);

                }
                Log.d("error out", "in onPostExecute message : " + "list working");
                //getCities();

                Log.d(TAG, "execute size : " + cities.size());

                String[] arraySpinner = new String[cities.size()];
                arraySpinner = cities.toArray(arraySpinner);
                //obj.getCities().toArray(this.arraySpinner);

                Log.d(TAG, "execute");
                // Spinner s = (Spinner) rootview.findViewById(R.id.spinner);
                // Spinner s1 = (Spinner) rootview.findViewById(R.id.spinner2);
                Spinner s = (Spinner) rootview.findViewById(R.id.spinner);
                Spinner s1 = (Spinner) rootview.findViewById(R.id.spinner2);
                ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, arraySpinner);
                s.setAdapter(adapter);
                s1.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        public ArrayList<String> getCities() {
            return this.cities;
        }

    }


}
