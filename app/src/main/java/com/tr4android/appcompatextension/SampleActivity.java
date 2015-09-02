package com.tr4android.appcompatextension;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tr4android.support.extension.internal.Account;
import com.tr4android.support.extension.typeface.TypefaceCompatFactory;
import com.tr4android.support.extension.widget.AccountHeaderView;
import com.tr4android.support.extension.widget.FloatingActionMenu;


public class SampleActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // install typeface factory before(!) onCreate()
        TypefaceCompatFactory.installViewFactory(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(getString(R.string.sample_title));

        // Setup DrawerLayout so we can close it later
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Setup NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                return true;
            }
        });
        // Setup AccountHeaderView
        AccountHeaderView accountHeaderView = (AccountHeaderView) findViewById(R.id.account_header);
        accountHeaderView.addAccounts(new Account().setName("TR4Android").setEmail("tr4android@example.com").setIconResource(R.drawable.account_drawer_profile_image_tr4android),
                new Account().setName("Fountain Geyser").setEmail("fountaingeyser@example.com").setIconResource(R.drawable.account_drawer_profile_image_fountaingeyser),
                new Account().setName("Alpha Account").setEmail("alpha.account@example.de"),
                new Account().setName("Beta Account").setEmail("beta.account@example.de").setPlaceholderIconEnabled(true).setPlaceholderCircleColor(Color.parseColor("#2196f3")));
        accountHeaderView.setAccountSelectedListener(new AccountHeaderView.OnAccountSelectedListener() {
            @Override
            public void onAccountSelected(Account account) {
                Snackbar.make(findViewById(R.id.main_layout), account.getEmail(), Snackbar.LENGTH_LONG).show();
                drawerLayout.closeDrawers();
            }

            @Override
            public void onAccountAddSelected() {
                Snackbar.make(findViewById(R.id.main_layout), "Aha! So you want to add an account!", Snackbar.LENGTH_LONG).show();
                drawerLayout.closeDrawers();
            }

            @Override
            public void onAccountManageSelected() {
                Snackbar.make(findViewById(R.id.main_layout), "Yes! Management is everything!", Snackbar.LENGTH_LONG).show();
                drawerLayout.closeDrawers();
            }
        });

        // Setup the dimming of FloatingActionMenu
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        floatingActionMenu.setupWithDimmingView(findViewById(R.id.dimming_view), Color.parseColor("#99000000"));

        // Setup the sample adapter
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FileAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(R.string.dialog_message)
                        .setNeutralButton(R.string.dialog_neutral_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "https://github.com/TR4Android/Swipeable-RecyclerView";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(R.string.dialog_negative_button, null)
                        .show();
                return true;
            case R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
