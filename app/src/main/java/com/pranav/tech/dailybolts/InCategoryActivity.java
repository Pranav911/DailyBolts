package com.pranav.tech.dailybolts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class InCategoryActivity extends AppCompatActivity {

    TextView xod_tv;
    ListView mListView;
    ArrayAdapter adapt;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_category);

        String choice = getIntent().getExtras().getString("choice");
        xod_tv = findViewById(R.id.in_cat_xod);


        DatabaseReference myRef = database.getReference("XOD");

        if(choice!=null) {
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

            previous_list_handler(choice);

        } else{
            Toast.makeText(this, "Invalid category selected", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(InCategoryActivity.this, HomeActivity.class));
        }
    }

    private void previous_list_handler(String choice) {
        final ArrayList<String> mData = new ArrayList<>();
        adapt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mData);
        mListView = findViewById(R.id.simpleListView);

        DatabaseReference prevRef = database.getReference(choice+"_history");
        prevRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);

                Collections.reverse(mData);
                mData.add(value);
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
    }
}
