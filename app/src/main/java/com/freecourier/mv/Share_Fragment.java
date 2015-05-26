package com.freecourier.mv;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class Share_Fragment extends Fragment {
    View rootview;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.share_layout, container, false);

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
}
