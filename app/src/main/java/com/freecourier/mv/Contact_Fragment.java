package com.freecourier.mv;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

;import com.freecourier.mv.Declaration.ConnectionDetector;


public class Contact_Fragment extends Fragment {
    View rootview;
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.contact_layout, container, false);

        cd = new ConnectionDetector(rootview.getContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            // showAlertDialog(getActivity(), "Internet Connection",
            //    "You have internet connection", true);
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
            showAlertDialog(getActivity(), "No Internet Connection",
                    "You don't have internet connection.", false);
        }
        
        return rootview;
    }
    /**
     * Function to display simple Alert Dialog
     * @param context - application context
     * @param title - alert dialog title
     * @param message - alert message
     * @param status - success/failure (used to set icon)
     * */
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.mipmap.success : R.mipmap.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
