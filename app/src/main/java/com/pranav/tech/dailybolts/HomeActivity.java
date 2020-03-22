package com.pranav.tech.dailybolts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pranav.tech.dailybolts.User.LoginActivity;
import com.pranav.tech.dailybolts.Utility.ConnectionManagement;

public class HomeActivity extends AppCompatActivity {

    private boolean pressTwice;
    private Menu menu;
    private InterstitialAd mInterstitialAd;
    private boolean quote_check, fact_check, joke_check, shayari_check;

    private void setSubNotifValues() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.pranav.tech.dailybolts.Notifications", Context.MODE_PRIVATE);
        quote_check = sharedPreferences.getBoolean("quote", true);
        fact_check = sharedPreferences.getBoolean("fact", true);
        joke_check = sharedPreferences.getBoolean("joke", true);
        shayari_check = sharedPreferences.getBoolean("shayari", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6104835133508466/1405541974");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdLeftApplication() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        findViewById(R.id.quotes_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, InCategoryActivity.class);
                i.putExtra("choice", "Quote");
                startActivity(i);
            }
        });
        findViewById(R.id.facts_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, InCategoryActivity.class);
                i.putExtra("choice", "Fact");
                startActivity(i);
            }
        });
        findViewById(R.id.jokes_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, InCategoryActivity.class);
                i.putExtra("choice", "Joke");
                startActivity(i);
            }
        });
        findViewById(R.id.shayari_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, InCategoryActivity.class);
                i.putExtra("choice", "Shayari");
                startActivity(i);
            }
        });

        checkInternetSnack();

        // Subscribe for very first time app is run
        SharedPreferences prefs = getSharedPreferences("com.pranav.tech.dailybolts.FirstRunCheck", Context.MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            setSubNotifValues();
            if (quote_check)
                subscribeNotification("quote", null);
            if (fact_check)
                subscribeNotification("fact", null);
            if (joke_check)
                subscribeNotification("joke", null);
            if (shayari_check)
                subscribeNotification("shayari", null);
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    private void checkInternetSnack() {
        if (!ConnectionManagement.isConnected(this)) {
            final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet connection!!", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Dismiss", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(Color.CYAN);
            snackbar.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (pressTwice) {
            exitApp();
        }
        pressTwice = true;
        Toast.makeText(HomeActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressTwice = false;
            }
        }, 2000);
    }

    private void exitApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if the user is not logged in
        //opening the login activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.action_profile);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        ImageView at = rootView.findViewById(R.id.avatar);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().equals("")) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(at);
        }
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.ads:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Toast.makeText(this, "The interstitial wasn't loaded yet", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_profile:
                profileDialog();
                return true;

            case R.id.rateus:
//                Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
//                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                }
//                try {
//                    startActivity(goToMarket);
//                } catch (ActivityNotFoundException e) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
//                }
                Toast.makeText(this, "Thanks for your support :)", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.share:
//                try {
//                    Intent ishare = new Intent(Intent.ACTION_SEND);
//                    ishare.setType("text/plain");
//                    ishare.putExtra(Intent.EXTRA_SUBJECT, "DailyBolts");
//                    String sAux = "Let me recommend you this app!!\n\n";
//                    sAux = sAux + "http://play.google.com/store/apps/details?id=" + this.getPackageName() + "\n\n";
//                    ishare.putExtra(Intent.EXTRA_TEXT, sAux);
//                    startActivity(Intent.createChooser(ishare, "Share via"));
//                } catch (Exception e) {
//                    Toast.makeText(this, "Please share via Playstore", Toast.LENGTH_SHORT).show();
//                }
                Toast.makeText(this, "Thanks for your support :)", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_exit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want to exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitApp();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void profileDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.profile_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        handleProfile(dialog);
    }

    public void handleProfile(Dialog dialog) {
        handleProfileUserInfo(dialog);
        handleProfileNotificationInfo(dialog);
        handleProfileLogout(dialog);
    }

    private void handleProfileLogout(Dialog dialog) {
        dialog.findViewById(R.id.logout_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedLoginCheck = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedLoginCheck.edit();
                editor.putInt("check", 0);
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void handleProfileNotificationInfo(Dialog dialog) {
        final Switch quote_switch = dialog.findViewById(R.id.quote_notif_switch);
        final Switch joke_switch = dialog.findViewById(R.id.joke_notif_switch);
        final Switch sher_switch = dialog.findViewById(R.id.shayari_notif_switch);
        final Switch fact_switch = dialog.findViewById(R.id.fact_notif_switch);

        // Populating with old values
        setSubNotifValues();
        quote_switch.setChecked(quote_check);
        fact_switch.setChecked(fact_check);
        joke_switch.setChecked(joke_check);
        sher_switch.setChecked(shayari_check);

        // Checking the change in state of switch
        checkSwitch(quote_switch, "quote");
        checkSwitch(fact_switch, "fact");
        checkSwitch(joke_switch, "joke");
        checkSwitch(sher_switch, "shayari");
    }

    private void handleProfileUserInfo(Dialog dialog) {
        final ImageView imageView = dialog.findViewById(R.id.avatar);
        final TextView textName = dialog.findViewById(R.id.textViewName);
        final TextView textEmail = dialog.findViewById(R.id.textViewEmail);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            if (name != null && !name.equals("")) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .into(imageView);
                textName.setText(user.getDisplayName());
            }
            textEmail.setText(user.getEmail());
        }
    }

    private void checkSwitch(Switch mySwitch, final String topicName) {
        final SharedPreferences.Editor editor = getSharedPreferences("com.pranav.tech.dailybolts.Notifications", Context.MODE_PRIVATE).edit();
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    subscribeNotification(topicName, editor);
                else
                    unsubscribeNotification(topicName, editor);
            }
        });
    }

    private void subscribeNotification(final String topicName, SharedPreferences.Editor editor) {
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Subscription Failed.. Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, topicName + " subscribed successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        if (editor != null)
            editor.putBoolean(topicName, true).apply();
    }

    private void unsubscribeNotification(final String topicName, SharedPreferences.Editor editor) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Failed to unsubscribe.. Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, topicName + " unsubscribed successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if (editor != null)
            editor.putBoolean(topicName, false).apply();
    }

}
