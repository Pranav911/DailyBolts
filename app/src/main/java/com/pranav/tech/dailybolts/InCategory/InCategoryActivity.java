package com.pranav.tech.dailybolts.InCategory;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.pranav.tech.dailybolts.R;
import com.pranav.tech.dailybolts.Utility.ConnectionManagement;

public class InCategoryActivity extends AppCompatActivity {

    private FragmentManager fm = getSupportFragmentManager();
    private boolean inBookmarks = false;
    private String choice = "quote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_category);

        choice = getIntent().getExtras().getString("choice");

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle(choice + "s");
        choice = choice.toLowerCase();

        checkInternetSnack();

        fragmentHandler();
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
            snackbar.setActionTextColor(Color.GRAY);
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_incategory, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.action_bookmarks);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        final ImageView at = rootView.findViewById(R.id.avatar);
        at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inBookmarks){
                    at.setImageResource(R.drawable.avatar);
                    inBookmarks =false;
                    fragmentHandler();
                    Toast.makeText(InCategoryActivity.this, "In xod now", Toast.LENGTH_SHORT).show();
                } else{
                    at.setImageResource(R.drawable.dailybolts);
                    inBookmarks =true;
                    fragmentHandler();
                    Toast.makeText(InCategoryActivity.this, "In bookmarks now", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void fragmentHandler(){
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag;
        if(!inBookmarks){
            frag = InCategoryMain.newInstance(choice);
            ft.add(R.id.container_in_catagory, frag);
        } else {
            frag = InCategoryBookmarks.newInstance(choice);
            ft.add(R.id.container_in_catagory, frag);
        }
        fm.popBackStack();
        ft.commit();
    }
}
