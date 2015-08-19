package com.tr4android.appcompatextension;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.tr4android.support.extension.internal.Account;
import com.tr4android.support.extension.widget.AccountHeaderView;


public class SampleActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

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
                new Account().setName("Account Name").setEmail("accountemail@example.de"));
        accountHeaderView.setAccountSelectedListener(new AccountHeaderView.OnAccountSelectedListener() {
            @Override
            public boolean onAccountSelected(Account account) {
                Snackbar.make(findViewById(R.id.main_layout), account.getEmail(), Snackbar.LENGTH_LONG).show();
                drawerLayout.closeDrawers();
                return true;
            }

            @Override
            public boolean onAccountAddSelected() {
                Snackbar.make(findViewById(R.id.main_layout), "Aha! So you want to add an account!", Snackbar.LENGTH_LONG).show();
                drawerLayout.closeDrawers();
                return false;
            }

            @Override
            public boolean onAccountManageSelected() {
                Snackbar.make(findViewById(R.id.main_layout), "Yes! Management is everything!", Snackbar.LENGTH_LONG).show();
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sample, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
