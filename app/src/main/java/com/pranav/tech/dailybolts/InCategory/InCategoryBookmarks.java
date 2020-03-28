package com.pranav.tech.dailybolts.InCategory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pranav.tech.dailybolts.Adapters.ContentAdapter;
import com.pranav.tech.dailybolts.Models.ContentModel;
import com.pranav.tech.dailybolts.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InCategoryBookmarks#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InCategoryBookmarks extends Fragment {

    private static final String ARG_PARAM = "choice";

    private String choice = "quote";

    public InCategoryBookmarks() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param choice Parameter 1.
     * @return A new instance of fragment InCategoryBookmarks.
     */
    public static InCategoryBookmarks newInstance(String choice) {
        InCategoryBookmarks fragment = new InCategoryBookmarks();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, choice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            choice = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_category_bookmarks, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.simpleListView);
        recyclerView.setHasFixedSize(false);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<ContentModel> cm = new ArrayList<>();
        for(int i=1; i<21; i++){
            ContentModel model = new ContentModel();
            model.setContent(choice + " -> " + i);
            cm.add(model);
        }
        ContentAdapter adapt = new ContentAdapter(getContext(), cm);
        recyclerView.setAdapter(adapt);
        return view;
    }
}
