package ru.komissarovea.pubtram.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import ru.komissarovea.pubtram.R;
import ru.komissarovea.pubtram.adapters.TransportAdapter;
import ru.komissarovea.pubtram.data.Transport;
import ru.komissarovea.pubtram.data.UrlTask;
import ru.komissarovea.pubtram.data.WebHelper;

public class TransportFragment extends Fragment implements UrlTask.OnRequestComplete{

    private ListView listView;
    private int stopId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transport,
                container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        if (stopId > 0) {
            Activity activity = getActivity();
            ProgressBar mProgressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

            String url = WebHelper.getUrl(stopId);
            UrlTask task = new UrlTask(mProgressBar, this);
            task.execute(url);
        }
        return rootView;
    }

    @Override
    public void onRequestComplete(ArrayList<Transport> list) {
        TransportAdapter adapter = new TransportAdapter(getActivity(),
                list);
        listView.setAdapter(adapter);
        listView.invalidate();
    }

    public void setStopId(int value) {
        stopId = value;
    }
}
