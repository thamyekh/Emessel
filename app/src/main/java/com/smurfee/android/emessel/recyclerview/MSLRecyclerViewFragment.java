package com.smurfee.android.emessel.recyclerview;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smurfee.android.emessel.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MSLRecyclerViewFragment extends Fragment {

    private RecyclerView recyclerView;
    public MSLRecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_mslrecycler_view, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_msl);
        return layout;
    }


}
