package com.pranav.tech.dailybolts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class ProfileActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textName, textEmail;
    FirebaseAuth mAuth;
    private Switch quote_switch,joke_switch,sher_switch,fact_switch;
    private Button btnNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        SharedPreferences sharedPreferences = getSharedPreferences("com.pranav.tech.dailybolts.Notifications", Context.MODE_PRIVATE);
        boolean quote_check = sharedPreferences.getBoolean("quote", true);
        boolean fact_check = sharedPreferences.getBoolean("fact", true);
        boolean joke_check = sharedPreferences.getBoolean("joke", true);
        boolean shayari_check = sharedPreferences.getBoolean("shayari", true);

        quote_switch.setChecked(quote_check);
        fact_switch.setChecked(fact_check);
        joke_switch.setChecked(joke_check);
        sher_switch.setChecked(shayari_check);

        FirebaseUser user = mAuth.getCurrentUser();

        Glide.with(this)
                .load(user != null ? user.getPhotoUrl() : null)
                .into(imageView);

        textName.setText(user.getDisplayName());
        textEmail.setText(user.getEmail());

        btnNotif.setOnClickListener(new View.OnClickListener() {

            SharedPreferences sharedPreferences = getSharedPreferences("com.pranav.tech.dailybolts.Notifications", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            @Override
            public void onClick(View v) {
                if (quote_switch.isChecked())
                    subscribeNotification("quote", editor);
                else
                    unsubscribeNotification("quote", editor);
                if (fact_switch.isChecked())
                    subscribeNotification("fact", editor);
                else
                    unsubscribeNotification("fact", editor);
                if (joke_switch.isChecked())
                    subscribeNotification("joke", editor);
                else
                    unsubscribeNotification("joke", editor);
                if (sher_switch.isChecked())
                    subscribeNotification("shayari", editor);
                else
                    unsubscribeNotification("shayari", editor);

                editor.commit();
            }
        });
    }

    private void subscribeNotification(final String topicName, SharedPreferences.Editor editor) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = topicName + " - Subscribed successfully";
                        if (!task.isSuccessful()) {
                            msg = topicName + " - Subscription Failed";
                        }
                        Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        editor.putBoolean(topicName, true);
    }

    private void unsubscribeNotification(String topicName, SharedPreferences.Editor editor) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName);

        editor.putBoolean(topicName, false);
    }

    private void initializeUI(){
        imageView = findViewById(R.id.imageView);
        textName = findViewById(R.id.textViewName);
        textEmail = findViewById(R.id.textViewEmail);

        quote_switch = findViewById(R.id.quote_notif_switch);
        joke_switch = findViewById(R.id.joke_notif_switch);
        sher_switch = findViewById(R.id.shayari_notif_switch);
        fact_switch = findViewById(R.id.fact_notif_switch);

        btnNotif = findViewById(R.id.notif_save);
    }
}