package com.intimation.demoquiz.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intimation.demoquiz.R;
import com.intimation.demoquiz.utils.ImageLoaderUtil;
import com.intimation.demoquiz.utils.Utils;

import java.util.List;

/**
 * Created by gorillalogic on 7/4/15.
 */
public class SelectionAdapter extends BaseAdapter {

    private List<String> mItems;
    private Context mContext;
    private int mSelection=-1;

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
            holder.item_image = (ImageView) view.findViewById(R.id.item_image);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String option = mItems.get(i);
        if (option.contains(Utils.PREFIX_IMAGE)) {
            ImageLoaderUtil.displayImage(holder.item_image, option);
            holder.item.setVisibility(View.GONE);
            holder.item_image.setVisibility(View.VISIBLE);
        } else {
            holder.item.setText(option);
            holder.item.setVisibility(View.VISIBLE);
            holder.item_image.setVisibility(View.GONE);
        }
        holder.radioButton.setImageResource(mSelection == i ? R.drawable.radio_button_enabled : R.drawable.radio_button_disabled);

        return view;
    }

    private class ViewHolder {
        TextView item;
        ImageView radioButton, item_image;
    }
}
