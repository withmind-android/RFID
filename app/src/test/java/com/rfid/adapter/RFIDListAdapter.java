package com.rfid.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.rfid.R;


public class RFIDListAdapter extends BaseAdapter
{
    private ArrayList<BluetoothDevice> mItems;
    private Context mContext;

    class ViewHolder {
        TextView name;
        TextView address;
    }

    public RFIDListAdapter(Context context)
    {
        mContext = context;
    }

    @Override
    public int getCount() {
        if(mItems != null)
            return mItems.size();

        return 0;
    }

    public void setItems(ArrayList<BluetoothDevice> items)
    {
        mItems = items;
    }

    @Override
    public Object getItem(int position) {
        if(mItems != null)
            return mItems.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.bt_list_row, parent, false);
            holder = new ViewHolder();

            holder.name = (TextView) v.findViewById(R.id.text_name);
            holder.address = (TextView)v.findViewById(R.id.text_address);

            v.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) v.getTag();
        }

        if (mItems == null)
            return v;

        if (position < mItems.size())
        {
            BluetoothDevice item = mItems.get(position);
            holder.name.setText(item.getName());
            holder.address.setText(item.getAddress());
        }

        return v;
    }
}
