package ru.komissarovea.pubtram.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.komissarovea.pubtram.R;
import ru.komissarovea.pubtram.data.Stop;

public class StopAdapter extends BaseAdapter {

    private Activity _context;
    private ArrayList<Stop> _items;

    public StopAdapter(Activity context, ArrayList<Stop> items) {
        _context = context;
        _items = items;
    }

    @Override
    public int getCount() {
        return _items.size();
    }

    @Override
    public Object getItem(int position) {
        return _items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams") @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        Stop item = _items.get(position);
        if (convertView == null) {
            vh = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.stop_layout, null, true);
            vh.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        String s = String.format("%s (%.0f м.)", item.getName(), item.getCurrentDistance());
        vh.textView.setText(s);
        return convertView;
    }

    private class ViewHolder {

        TextView textView;

        // public ImageView imageView;

        // public Button btnOpen;
    }

}