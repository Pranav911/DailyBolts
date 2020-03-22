package com.pranav.tech.dailybolts;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class InCategoryActivity extends AppCompatActivity {

    TextView xod_tv;
    ListView mListView;
    SimpleAdapter adapt;
    String finalChoice;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_category);

        String choice = getIntent().getExtras().getString("choice");
        xod_tv = findViewById(R.id.in_cat_xod);

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle(choice+"s");
        finalChoice = choice.toUpperCase();
        choice = choice.toLowerCase();
        DatabaseReference myRef = database.getReference("XOD");

        if(!choice.equals("")) {
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
                    Toast.makeText(InCategoryActivity.this, "Failed to read quote of the day", Toast.LENGTH_SHORT).show();
                }
            });

            xod_tv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(finalChoice, xod_tv.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(InCategoryActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            history_list_handler(choice);

        } else{
            Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(InCategoryActivity.this, HomeActivity.class));
        }
    }

    private void history_list_handler(String choice) {
        final ArrayList<HashMap<String, String>> mData = new ArrayList<>();
        String[] from = new String[]{"content"};
        int[] to = new int[]{R.id.list_text};
        adapt = new SimpleAdapter(this, mData, R.layout.tiles, from, to);
        mListView = findViewById(R.id.simpleListView);

        DatabaseReference prevRef = database.getReference(choice+"_history");
        prevRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                HashMap<String, String> extract = new HashMap<String, String>();
                extract.put("content", value);
                Collections.reverse(mData);
                mData.add(extract);
                Collections.reverse(mData);
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
        Collections.reverse(mData);
        mListView.setAdapter(adapt);
        mListView.setNestedScrollingEnabled(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shareBody = mData.get(position).get("content");
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, finalChoice);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent,"Share using... "));
            }
        });
    }
}
