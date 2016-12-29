package ru.komissarovea.pubtram.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.komissarovea.pubtram.MainActivity;
import ru.komissarovea.pubtram.R;
import ru.komissarovea.pubtram.adapters.StopAdapter;
import ru.komissarovea.pubtram.data.Stop;
import ru.komissarovea.pubtram.data.StopsHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class NextStopsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private MainActivity activity;
    private ListView listView;
    private ArrayList<Stop> nextStops;

    public NextStopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_next_stops,
                container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        nextStops = new ArrayList<>();//StopsHelper.getNextStops(null, 500);
        ArrayList<Stop> actualStops = StopsHelper.getActualStops();

        for (int i = 0; i < 10; i++) {
            Stop stop = actualStops.get(i);
            nextStops.add(stop);
        }

        StopAdapter adapter = new StopAdapter(activity, nextStops);
        listView.setAdapter(adapter);
        listView.invalidate();

        return rootView;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
