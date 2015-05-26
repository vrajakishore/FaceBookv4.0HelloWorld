package com.freecourier.mv.Declaration;

/**
 * Created by m.v on 06-05-2015.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.freecourier.mv.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.freecourier.mv.Declaration.Constant_History.EIGHTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.FIRST_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.FOURTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.SECOND_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.THIRD_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.FIFTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.SIXTH_COLUMN;
import static com.freecourier.mv.Declaration.Constant_History.SEVENTH_COLUMN;

public class ListViewAdapterHistory extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    //Fragment frg;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    TextView txtFourth;
    TextView txtFifth;
    TextView txtSixth;
    TextView txtSeventh;
    TextView txtEighth;
    public ListViewAdapterHistory(Activity activity, ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.tablehistory, null);

            txtFirst=(TextView) convertView.findViewById(R.id.booking);
            txtSecond=(TextView) convertView.findViewById(R.id.hname);
            txtThird=(TextView) convertView.findViewById(R.id.hphone);
            txtFourth=(TextView) convertView.findViewById(R.id.hmail);
            txtFifth=(TextView) convertView.findViewById(R.id.hsource);
            txtSixth=(TextView) convertView.findViewById(R.id.hdestination);
            txtSeventh=(TextView) convertView.findViewById(R.id.hdate);
            txtEighth=(TextView) convertView.findViewById(R.id.hstatus);
        }

        HashMap<String, String> map=list.get(position);
        txtFirst.setText(map.get(FIRST_COLUMN));
        txtSecond.setText(map.get(SECOND_COLUMN));
        txtThird.setText(map.get(THIRD_COLUMN));
        txtFourth.setText(map.get(FOURTH_COLUMN));
        txtFifth.setText(map.get(FIFTH_COLUMN));
        txtSixth.setText(map.get(SIXTH_COLUMN));
        txtSeventh.setText(map.get(SEVENTH_COLUMN));
        txtEighth.setText(map.get(EIGHTH_COLUMN));

        return convertView;
    }

}
