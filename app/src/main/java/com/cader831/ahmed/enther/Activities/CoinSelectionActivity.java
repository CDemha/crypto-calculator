package com.cader831.ahmed.enther.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import com.cader831.ahmed.enther.Adapters.CoinsAdapter;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.R;

import java.util.Collections;
import java.util.List;

public class CoinSelectionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner filterSpinner;
    private ListView listViewCoinsList;
    private CoinsAdapter coinsAdapter;
    private SearchView searchView = null;
    private CoinController coinController;
    private Coin selectedCoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_selection);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Coin");

        listViewCoinsList = (ListView) findViewById(R.id.lstvCoinsList);
        listViewCoinsList.setOnItemClickListener(coinsListviewSelect);
        listViewCoinsList.setFastScrollEnabled(true);

        coinController = (CoinController) getIntent().getExtras().getSerializable("CoinController");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_bar, menu);
        setupSearchView(menu);
        setupFilterSpinner(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        MenuItem mSearchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (mSearchMenuItem != null) {
            searchView = (SearchView) mSearchMenuItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
        }
        searchView.setIconifiedByDefault(true);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        MenuItemCompat.expandActionView(mSearchMenuItem);
        searchView.setOnQueryTextListener(onQueryListener);
    }

    private void setupFilterSpinner(Menu menu) {
        MenuItem mFilterMenuItem = menu.findItem(R.id.filter);
        filterSpinner = (Spinner) MenuItemCompat.getActionView(mFilterMenuItem);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Cryptos", "Locals", "Favourites"});
        filterSpinner.setOnItemSelectedListener(onSpinnerItemSelected);
        filterSpinner.setAdapter(adapter);
    }

    private void setFilter(int pos) {
        switch (pos) {
            case 0:
                List<Coin> cryptosToList = coinController.getCryptosToList();
                Collections.sort(cryptosToList);
                coinsAdapter = new CoinsAdapter(this, cryptosToList, pos);
                break;
            case 1:
                List<Coin> localsToList = coinController.getLocalsToList();
                Collections.sort(localsToList);
                coinsAdapter = new CoinsAdapter(this, localsToList, pos);
                break;
            case 2:
                List<Coin> favouritesToList = coinController.getFavouritesToList();
                Collections.sort(favouritesToList);
                coinsAdapter = new CoinsAdapter(this, favouritesToList, pos);
                break;
        }
        coinsAdapter.getFilter().filter(searchView.getQuery());
        listViewCoinsList.setAdapter(coinsAdapter);

    }

    private AdapterView.OnItemClickListener coinsListviewSelect = (parent, view, position, id) -> {
        selectedCoin = (Coin) parent.getItemAtPosition(position);
        onBackPressed();
    };


    private AdapterView.OnItemSelectedListener onSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setFilter(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private SearchView.OnQueryTextListener onQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String filterQuery) {
            coinsAdapter.getFilter().filter(filterQuery);
            return false;
        }
    };

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

    public Bundle getData() {
        Bundle b = new Bundle();
        b.putSerializable("SelectedCoin", selectedCoin);
        b.putSerializable("CoinController", coinController);
        return b;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = getIntent();
        returnIntent.putExtras(getData());
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}