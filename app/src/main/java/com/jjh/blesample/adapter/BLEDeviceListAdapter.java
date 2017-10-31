package com.jjh.blesample.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jjh.blesample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjh860627 on 2017. 10. 24..
 */

public class BLEDeviceListAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<BluetoothDevice> mBLEDeviceList = new ArrayList<>();

    public BLEDeviceListAdapter(Context context){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public void addItem(BluetoothDevice device){
        if(!mBLEDeviceList.contains(device)){
            mBLEDeviceList.add(device);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        mBLEDeviceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mBLEDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBLEDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BleDeviceViewHolder viewHolder = null;

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.layout_ble_device_item_view, null);
            viewHolder = new BleDeviceViewHolder();
            viewHolder.tvDeviceName = (TextView)convertView.findViewById(R.id.tvDeviceName);
            viewHolder.tvDeviceAddress = (TextView)convertView.findViewById(R.id.tvDeviceAddress);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (BleDeviceViewHolder)convertView.getTag();
        }

        BluetoothDevice item = (BluetoothDevice)getItem(position);

        viewHolder.tvDeviceName.setText((item.getName() == null ? "Unknown" : item.getName()));
        viewHolder.tvDeviceAddress.setText(String.format("(%s)", item.getAddress()));

        return convertView;
    }

    class BleDeviceViewHolder{
        TextView tvDeviceName;
        TextView tvDeviceAddress;
    }
}
