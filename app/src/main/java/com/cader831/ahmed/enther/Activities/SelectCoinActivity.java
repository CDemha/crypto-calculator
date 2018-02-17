package com.cader831.ahmed.enther.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.cader831.ahmed.enther.Adapters.CoinsAdapter;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.R;

public class SelectCoinActivity extends FragmentActivity {

    private CoinsAdapter coinsExpandableAdapter;
    private Coin selectedCoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coin);

        ListView lstCoinsList = (ListView) findViewById(R.id.lstvCoinsList);
        lstCoinsList.setOnItemClickListener(coinsListviewSelect);

        lstCoinsList.setFastScrollEnabled(true);

        Intent thisIntent = getIntent();
        CoinController coinController = (CoinController) thisIntent.getSerializableExtra("CoinController");
        Coin primaryCoin = (Coin) thisIntent.getSerializableExtra("PrimaryCoin");
        Coin secondaryCoin = (Coin) thisIntent.getSerializableExtra("SecondaryCoin");

//        List<Coin> coinsToList = coinController.getCryptosToList();
//        coinsToList.removeIf(p -> p.getShortName().equals(primaryCoin.getShortName()));
//        coinsToList.removeIf(p -> p.getShortName().equals(secondaryCoin.getShortName()));
//
//        Collections.sort(coinsToList);

//        coinsExpandableAdapter = new CoinsAdapter(SelectCoinActivity.this, coinsToList, false);
        lstCoinsList.setAdapter(coinsExpandableAdapter);

    }

    private AdapterView.OnItemClickListener coinsListviewSelect = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedCoin = (Coin) parent.getItemAtPosition(position);
            onBackPressed();
        }
    };

    private SearchView.OnQueryTextListener searchCoins = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            coinsExpandableAdapter.getFilter().filter(newText);
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (selectedCoin == null) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();

        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("SelectedCoin", selectedCoin);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();

        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setQueryHint("Search Coin...");
        searchViewAndroidActionBar.setOnQueryTextListener(searchCoins);
        searchViewAndroidActionBar.setMaxWidth(Integer.MAX_VALUE);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
