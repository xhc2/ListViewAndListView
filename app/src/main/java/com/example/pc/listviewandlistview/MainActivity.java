package com.example.pc.listviewandlistview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ExpandableListView expandableListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_and_expand_listview_layout);
        findView();
    }

    private void findView(){
        LinearLayout llText = (LinearLayout) findViewById(R.id.ll_notedit);
        LinearLayout llEdit = (LinearLayout) findViewById(R.id.lledit);
        listView = (ListView)findViewById(R.id.deviceall_fullyListview);
        expandableListView = (ExpandableListView) findViewById(R.id.deviceall_expandableListView);
        String[] item = new String[]{"123","45341","123","45341","123","45341","123","45341","123","45341","123","45341","123","45341","123","45341"};
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1 , item));
        expandableListView.setAdapter(new ExpandAdapter(this));

        llEdit.setVisibility(View.GONE);
        expandableListView.setVisibility(View.GONE);
//        llText.setVisibility(View.GONE);
//        listView.setVisibility(View.GONE);


    }
}
