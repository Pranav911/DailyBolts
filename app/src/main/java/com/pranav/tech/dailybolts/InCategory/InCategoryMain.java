package com.pranav.tech.dailybolts.InCategory;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranav.tech.dailybolts.Adapters.ContentAdapter;
import com.pranav.tech.dailybolts.HomeActivity;
import com.pranav.tech.dailybolts.Models.ContentModel;
import com.pranav.tech.dailybolts.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InCategoryMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InCategoryMain extends Fragment {
    private static final String ARG_PARAM = "choice";

    private String choice;

    private TextView xod_tv;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public InCategoryMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param choice Parameter 1.
     * @return A new instance of fragment InCategoryMain.
     */
    public static InCategoryMain newInstance(String choice) {
        InCategoryMain fragment = new InCategoryMain();
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
        View view = inflater.inflate(R.layout.fragment_in_category_main, container, false);
        xod_tv = view.findViewById(R.id.in_cat_xod);

        DatabaseReference myRef = database.getReference("XOD");

        if (!choice.equals("")) {
            if (choice.equalsIgnoreCase("joke")) {
                xod_tv.setGravity(Gravity.LEFT);
                xod_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f);
            }
            myRef.child(choice).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    xod_tv.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getContext(), "Failed to read quote of the day", Toast.LENGTH_SHORT).show();
                }
            });

            xod_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(choice.toUpperCase(), xod_tv.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            history_list_handler(view, choice);

        } else {
            Toast.makeText(getContext(), "Invalid category selected", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getActivity()).finish();
            startActivity(new Intent(getContext(), HomeActivity.class));
        }

        return view;
    }


    private void history_list_handler(View view, String choice) {
        RecyclerView recyclerView = view.findViewById(R.id.simpleListView);
        recyclerView.setHasFixedSize(false);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);

        final List<ContentModel> cm = new ArrayList<>();
        final ContentAdapter adapt = new ContentAdapter(getContext(), cm);
        recyclerView.setAdapter(adapt);

        DatabaseReference prevRef = database.getReference(choice + "_history");
        prevRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                ContentModel model = new ContentModel();
                model.setContent(value);
                Collections.reverse(cm);
                cm.add(model);
                Collections.reverse(cm);
                adapt.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }
}
