package com.freecourier.mv;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.freecourier.mv.Declaration.ConnectionDetector;


public class Share_Fragment extends Fragment {
    View rootview;
    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.share_layout, container, false);
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
        ImageView btnNew = (ImageView) rootview.findViewById(R.id.newbutton);
        ImageView btnNew1 = (ImageView) rootview.findViewById(R.id.newbutton1);

        btnNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i;
                PackageManager manager = getActivity().getPackageManager();
                try {
                    i = manager.getLaunchIntentForPackage("com.lenovo.anyshare.gps");
                    if (i == null)
                        throw new PackageManager.NameNotFoundException();
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {

//if not found in device then will come here
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.lenovo.anyshare.gps"));
                    startActivity(intent);
                }

            }

        });

        btnNew1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i;
                PackageManager manager = getActivity().getPackageManager();
                try {
                    i = manager.getLaunchIntentForPackage("com.whatsapp");
                    if (i == null)
                        throw new PackageManager.NameNotFoundException();
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {

//if not found in device then will come here
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.whatsapp"));
                    startActivity(intent);
                }

            }

        });

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
