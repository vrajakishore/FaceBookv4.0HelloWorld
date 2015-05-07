package com.freecourier.mv;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Carry_Fragment extends Fragment {
    View rootview;
    private static final String TAG = "your activity name";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.carrier_layout, container, false);

        RetrieveFeedTask obj = new RetrieveFeedTask();
        obj.execute();


        Button btnSearch = (Button) rootview.findViewById(R.id.button);
        btnSearch.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                Log.d(TAG, "Button inside ");
                Fragment home = new Travellers();  //this is your new fragment.
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, home)
                        .commit();



                Toast.makeText(getActivity(), "Successfully submitted ", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Button onclick end   ");
            }
        });

        return rootview;
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
