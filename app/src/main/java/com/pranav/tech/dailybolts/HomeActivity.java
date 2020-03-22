package com.pranav.tech.dailybolts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pranav.tech.dailybolts.User.LoginActivity;
import com.pranav.tech.dailybolts.User.ProfileActivity;

public class HomeActivity extends AppCompatActivity {

    private boolean pressTwice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
    }

    @Override
    public void onBackPressed() {
        if (pressTwice) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_signout) {
            SharedPreferences sharedLoginCheck = getSharedPreferences("LoginCheck", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedLoginCheck.edit();
            editor.putInt("check", 0);
            editor.commit();
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        if (id == R.id.action_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
