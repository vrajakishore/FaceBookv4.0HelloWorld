package com.freecourier.mv;

import static com.freecourier.mv.Declaration.Constant.*;
import com.freecourier.mv.Declaration.ListViewAdapter;
import com.freecourier.mv.Send_Fragment;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.HashMap;

/**
 * Created by m.v on 04-05-2015.
 */
public class Travellers extends Fragment {

    private EditText date;
    View rootview;
    private static final String TAG = "TRAVELLER PAGE";
    private ArrayList<HashMap<String, String>> list;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.travellers_fragment, container, false);

        Bundle bundle = this.getArguments();
        String []argu = new String[3];
       //argu[0] = bundle.getString("src");
       // argu[1] = bundle.getString("des");
       // argu[2] = bundle.getString("jdate");
        argu[0]="Hyderabad";
        argu[1]="Kakinada";
        argu[2]="2015-04-09";
        Log.d(TAG, argu[0] + " " + " " + argu[1] + " "+argu[2]);



        new RetrieveFeedTask().execute(argu);
  /*
        list=new ArrayList<HashMap<String,String>>();

        HashMap<String,String> temp4 = new HashMap<String, String>();
        temp4.put(FIRST_COLUMN, "NAME");
        temp4.put(SECOND_COLUMN, "EMAIL");
        temp4.put(THIRD_COLUMN, "DATE");
        temp4.put(FOURTH_COLUMN, "TIME");
        list.add(temp4);

        HashMap<String,String> temp = new HashMap<String, String>();
        temp.put(FIRST_COLUMN, "Ankit Karia");
        temp.put(SECOND_COLUMN, "Male");
        temp.put(THIRD_COLUMN, "22");
        temp.put(FOURTH_COLUMN, "Unmarried");
        list.add(temp);

        HashMap<String,String> temp2=new HashMap<String, String>();
        temp2.put(FIRST_COLUMN, "Rajat Ghai");
        temp2.put(SECOND_COLUMN, "Male");
        temp2.put(THIRD_COLUMN, "25");
        temp2.put(FOURTH_COLUMN, "Unmarried");
        list.add(temp2);

        HashMap<String,String> temp3=new HashMap<String, String>();
        temp3.put(FIRST_COLUMN, "Karina Kaif");
        temp3.put(SECOND_COLUMN, "Female");
        temp3.put(THIRD_COLUMN, "31");
        temp3.put(FOURTH_COLUMN, "Unmarried");
        list.add(temp3);
*/


        return rootview;
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {



        private Exception exception;
        private ArrayList<HashMap<String, String>> list;

        public RetrieveFeedTask() {
            list=new ArrayList<HashMap<String,String>>();
        }

        @Override
        protected String doInBackground(String[] args) {

            Log.d(TAG, "execute1");
            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/get_travel_users/";
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
                temp.put(FIRST_COLUMN, "EMAIL");
                temp.put(SECOND_COLUMN, "NAME");
                temp.put(THIRD_COLUMN, "DATE");
                temp.put(FOURTH_COLUMN, "TIME");
                list.add(temp);



                JSONObject jsonobj = json.getJSONObject(0);
                for (int i = 0; i < jsonobj.names().length(); i++) {
                    HashMap<String,String> temp1 = new HashMap<String, String>();

                    String key = (String) jsonobj.names().get(i);
                    String val = jsonobj.getString(key);
                    temp1.put(FIRST_COLUMN, val);
                    temp1.put(SECOND_COLUMN, val);
                    temp1.put(THIRD_COLUMN, val);
                    temp1.put(FOURTH_COLUMN, val);

                    list.add(temp1);
                    Log.d("error out - Element : ", "i + " + i + " val : " + val);

                }
                Log.d("error out", "in onPostExecute message : " + "list working");
                //getCities();

                Log.d(TAG, "execute size : " + list.size());

                ListView listView=(ListView)rootview.findViewById(R.id.listView1);
                ListViewAdapter adapter=new ListViewAdapter(getActivity(), list);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                        int pos = position + 1;
                        Toast.makeText(getActivity(), Integer.toString(pos) + " Clicked", Toast.LENGTH_SHORT).show();
                    }

                });



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

}
