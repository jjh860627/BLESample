package com.jjh.blesample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.jjh.blesample.vo.BleCharacteristic;
import com.jjh.blesample.vo.BleService;

import java.util.List;

/**
 * Created by jjh860627 on 2017. 10. 27..
 */

public class BleServiceCharacterListAdapter extends BaseExpandableListAdapter {

    private List<BleService> mServiceList = null;
    private List<List<BleCharacteristic>> mCharacteristicList = null;
    private LayoutInflater mInflater = null;

    public BleServiceCharacterListAdapter(Context context, List<BleService> serviceList, List<List<BleCharacteristic>> characteristicList){
        this.mInflater = LayoutInflater.from(context);
        this.mServiceList = serviceList;
        this.mCharacteristicList = characteristicList;
    }

    @Override
    public int getGroupCount() {
        return mServiceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCharacteristicList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mServiceList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCharacteristicList.get(groupPosition).get(childPosition);
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
        return true;
    }

    public void clear(){
        mCharacteristicList.clear();
        mServiceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
            viewHolder.text1 = (TextView)convertView.findViewById(android.R.id.text1);
            viewHolder.text2 = (TextView)convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        BleService item = (BleService)getGroup(groupPosition);

        viewHolder.text1.setText(item.getName());
        viewHolder.text2.setText(item.getUuid().toString());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(android.R.layout.simple_expandable_list_item_2, null);
            convertView.setBackgroundColor(Color.parseColor("#FAF4C0"));
            viewHolder.text1 = (TextView)convertView.findViewById(android.R.id.text1);
            viewHolder.text2 = (TextView)convertView.findViewById(android.R.id.text2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        BleCharacteristic item = (BleCharacteristic)getChild(groupPosition, childPosition);

        viewHolder.text1.setText(item.getName());
        viewHolder.text2.setText(item.getUuid().toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder{
        public TextView text1;
        public TextView text2;
    }
}
