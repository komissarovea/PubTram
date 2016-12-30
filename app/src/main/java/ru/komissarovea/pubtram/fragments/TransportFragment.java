package ru.komissarovea.pubtram.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.komissarovea.pubtram.R;
import ru.komissarovea.pubtram.adapters.TransportAdapter;
import ru.komissarovea.pubtram.data.Transport;
import ru.komissarovea.pubtram.data.UrlTask;
import ru.komissarovea.pubtram.data.WebHelper;

public class TransportFragment extends Fragment implements UrlTask.OnRequestComplete{

    private ListView listView;
    private ArrayList<Transport> tlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String url = WebHelper.getUrl(14937);
        UrlTask task = new UrlTask(null, this);
        task.execute(url);

        View rootView = inflater.inflate(R.layout.fragment_transport,
                container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);


        return rootView;
    }

    @Override
    public void onRequestComplete(ArrayList<Transport> list) {
        tlist = list;

        TransportAdapter adapter = new TransportAdapter(getActivity(),
                tlist);
        listView.setAdapter(adapter);
        listView.invalidate();
    }
}
