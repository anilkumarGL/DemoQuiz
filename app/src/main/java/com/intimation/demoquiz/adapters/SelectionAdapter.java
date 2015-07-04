package com.intimation.demoquiz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intimation.demoquiz.R;

import java.util.List;

/**
 * Created by gorillalogic on 7/4/15.
 */
public class SelectionAdapter extends BaseAdapter {

    private List<String> mItems;
    private Context mContext;
    private int mSelection;

    public SelectionAdapter(Context context, List<String> items) {
        super();
        mItems = items;
        mContext = context;
    }

    public void setSelectedItemPosition(int pos) {
        mSelection = pos;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_selector, null);
            holder = new ViewHolder();
            holder.item = (TextView) view.findViewById(R.id.item);
            holder.radioButton = (ImageView) view.findViewById(R.id.radioButton);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.item.setText(mItems.get(i));
        holder.radioButton.setImageResource(mSelection == i ? R.drawable.radio_button_enabled : R.drawable.radio_button_disabled);

        return view;
    }

    private class ViewHolder {
        TextView item;
        ImageView radioButton;
    }
}
