package com.freecourier.mv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentSimpleLoginButton extends Fragment {

    private static final String TAG = "Fragment Simple Login Page";
    final String requestId = "367001493485680";
    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;



    public FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.d("VIVZ", "onSuccess");
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            mTextDetails.setText(constructWelcomeMessage(profile));




            String[] arg = new String[7];
            arg[0] = profile.getId();
            arg[1] = profile.getName();
            new RetrieveFeedTask().execute(arg);


        }


        @Override
        public void onCancel() {
            Log.d("VIVZ", "onCancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.d("VIVZ", "onError " + e);
        }
    };


    public FragmentSimpleLoginButton() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCallbackManager = CallbackManager.Factory.create();
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simple_login_button, container, false);




    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupTextDetails(view);
        setupLoginButton(view);


    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        mTextDetails.setText(constructWelcomeMessage(profile));
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.text_details);
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("VIVZ", "" + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("VIVZ", "" + currentProfile);
                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    private void setupLoginButton(View view) {
        LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
        mButtonLogin.setFragment(this);
        mButtonLogin.setReadPermissions("user_friends");

        mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
    }

    public String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if (profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
            Intent intent = new Intent(getActivity(), Main_Navigation.class);
            // Intent intent = new Intent(MainActivity.this, Signup_Page.class);
            Toast.makeText(getActivity(), "Welcome "+profile.getName(), Toast.LENGTH_LONG).show();
            startActivity(intent);

        }
        return stringBuffer.toString();
    }



    class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        private Exception exception;



        @Override
        protected String doInBackground(String[] args) {

            DefaultHttpClient client = new DefaultHttpClient();
            String url = "http://172.16.32.54:8888/rest/user/registration/";
            HttpPost request = new HttpPost(url);
            String responseStr = "";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
                nameValuePairs.add(new BasicNameValuePair("email", args[0]));
                nameValuePairs.add(new BasicNameValuePair("fullname", args[1]));
                nameValuePairs.add(new BasicNameValuePair("pwd", args[2]));
                nameValuePairs.add(new BasicNameValuePair("phone", args[3]));
                nameValuePairs.add(new BasicNameValuePair("gender", args[4]));
                nameValuePairs.add(new BasicNameValuePair("dob", args[5]));
                nameValuePairs.add(new BasicNameValuePair("city", args[6]));


                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = client.execute(request);
                //Log.d(TAG, "input = " + args[0]+" - "+args[1]+" - "+args[2]+" - "+args[3]+" - "+args[4]+" - "+args[5]+" - "+args[6]);
                responseStr = EntityUtils.toString(response.getEntity());
               // Log.d(TAG, "outcome = " + responseStr);
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
                JSONArray json = new JSONArray( result);

                JSONObject jsonobj = json.getJSONObject(0);
                Log.d("error out", "in onPostExecute results123 : " + result);
                String message = jsonobj.getString("message");
                // Log.d("error out", "in onPostExecute message : " + message);
                if(message.equalsIgnoreCase("success")){
                    //Log.d("1111error out", "in onPostExecute message : " + message);
                    //Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_LONG).show();


                }else if(message.equalsIgnoreCase("already registered")){
                    //Log.d("error out", "in onPostExecute message11111 : " + message);
                    //Intent intent = new Intent(getActivity(), Registration.class);
                    // Toast.makeText(getActivity(), "Already registered", Toast.LENGTH_LONG).show();
                    //startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



}


