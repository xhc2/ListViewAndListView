package com.example.pc.listviewandlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 2016/5/26.
 */
public class ExpandAdapter extends BaseExpandableListAdapter {


    private List<String> listGroup = new ArrayList<>();
    private List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
    private Context context;
    private LayoutInflater inflater;
    public ExpandAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        for(int i = 0 ;i < 60 ; ++ i){
            listGroup.add("group "+i);
            ArrayList<String> aList = new ArrayList<String>();
            for(int j = 0 ; j < 3 ; ++ j){
                aList.add("child"+j);
            }
            list.add(aList);
        }
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return list.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return list.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.text_view_item,parent,false);
        TextView tv = (TextView)convertView.findViewById(R.id.tv);
        tv.setText(listGroup.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.text_view_item,parent,false);
        TextView tv = (TextView)convertView.findViewById(R.id.tv);
        tv.setText(list.get(groupPosition).get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
