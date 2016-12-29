package ru.komissarovea.pubtram.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.komissarovea.pubtram.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NextStopsFragment extends Fragment {


    public NextStopsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_stops, container, false);
    }

}
