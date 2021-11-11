package com.rfid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.rfid.R;
import com.rfid.adapter.item.RfidListItem;

public class RfidRvAdapter extends RecyclerView.Adapter<RfidRvAdapter.RvViewHolder>
{
    private ArrayList<RfidListItem> mRfidItems;

    public class RvViewHolder extends RecyclerView.ViewHolder
    {
        TextView name ;
        TextView count ;

        RvViewHolder(View itemView)
        {
            super(itemView) ;

            name = itemView.findViewById(R.id.text_item_name) ;
            count = itemView.findViewById(R.id.text_item_count) ;
        }
    }

    public RfidRvAdapter(ArrayList<RfidListItem> list)
    {
        mRfidItems = list;
    }

    public void setRfidItems(ArrayList<RfidListItem> items)
    {
        mRfidItems = items;
    }

    @NonNull
    @Override
    public RvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.rfid_list_item, parent, false) ;
        RvViewHolder vh = new RvViewHolder(view) ;

        return vh ;
    }

    @Override
    public void onBindViewHolder(@NonNull RvViewHolder holder, int position)
    {
        RfidListItem item = mRfidItems.get(position);
        holder.name.setText(item.getName());
        holder.count.setText(""+item.getCount());
    }

    @Override
    public int getItemCount()
    {
        return mRfidItems.size();
    }
}
