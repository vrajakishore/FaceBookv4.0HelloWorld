package com.freecourier.mv;

import static com.freecourier.mv.Declaration.Constant.*;
import com.freecourier.mv.Declaration.ListViewAdapter;
import com.freecourier.mv.Send_Fragment;

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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        rootview = inflater.inflate(R.layout.travellers_fragment, container, false);

        //Bundle bundle = this.getArguments();
        String []argu = new String[3];
        //argu[0] = bundle.getString("src");
        //argu[1] = bundle.getString("des");
        //argu[2] = bundle.getString("jdate");
        argu[0]="Hyderabad";
        argu[1]="Kakinada";
        argu[2]="2015-04-09";
        Log.d(TAG, argu[0] + " " + " " + argu[1] + " "+argu[2]);



        new RetrieveFeedTask().execute(argu);




        return rootview;
    }


    class RetrieveFeedTask extends AsyncTask<String, Void, String> {



        private Exception exception;
        //private ArrayList<HashMap<String, String>> list;

        public RetrieveFeedTask() {
            list=new ArrayList<HashMap<String,String>>();
        }

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
                Log.d(TAG, "input = " + args[0]+" - "+args[1]+" - "+args[2]) ;
                responseStr = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "outcome = " + responseStr);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "["+responseStr+"]";
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

                    JSONArray json1 = new JSONArray("["+val+"]");

                    JSONObject jsonobj1 = json1.getJSONObject(0);
                    val = jsonobj1.getString("email");
                    temp1.put(FIRST_COLUMN, val);
                    val = jsonobj1.getString("name");
                    temp1.put(SECOND_COLUMN, val);
                    val = jsonobj1.getString("journey_date");
                    temp1.put(THIRD_COLUMN, val);
                    val = jsonobj1.getString("journey_time");
                    temp1.put(FOURTH_COLUMN, val);

                    list.add(temp1);

                    Log.d("error out", "in onPostExecute message : " + "list working");

                    // Log.d("error out - Element : ", "i + " + i + " val : " + key);

                }


                Log.d("error out", "in onPostExecute message : " + "list working and size :" +list.size()+ " - "+list.toString());
                //getCities();

                ListView listView=(ListView)rootview.findViewById(R.id.listView1);
                ListViewAdapter adapter=new ListViewAdapter(getActivity(), list);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                        int pos = position;
                        if(pos>0) {
                            HashMap<String,String> selectedItem = list.get(pos);
                           // Toast.makeText(getActivity(), selectedItem.toString() + " Clicked", Toast.LENGTH_SHORT).show();
                            String email = selectedItem.get(FIRST_COLUMN);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Do you want to select this?");
                            builder.setTitle("Confirm the traveller !!! ");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do something after confirm
                                    Toast.makeText(getActivity(), "Selected", Toast.LENGTH_SHORT).show();
                                }
                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            builder.create().show();
                        }
                    }

                });



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }

}
