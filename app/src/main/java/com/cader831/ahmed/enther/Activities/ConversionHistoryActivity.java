package com.cader831.ahmed.enther.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.cader831.ahmed.enther.Adapters.ConversionHistoryAdapter;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.Exchange;
import com.cader831.ahmed.enther.R;

public class ConversionHistoryActivity extends AppCompatActivity {

    CoinController coinController;
    Coin primaryCoin,secondaryCoin;
    Exchange exchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        coinController = (CoinController) getIntent().getExtras().getSerializable("CoinController");
        primaryCoin = coinController.getCoinFromShortName((String) getIntent().getExtras().getSerializable("PrimaryCoinSName"));
        secondaryCoin = coinController.getCoinFromShortName((String) getIntent().getExtras().getSerializable("SecondaryCoinSName"));
        exchange = (Exchange) getIntent().getExtras().getSerializable("Exchange");
        String coinExchangePair = coinController.generateCoinPair(primaryCoin,secondaryCoin);

        ListView lstConversionListView = (ListView) findViewById(R.id.lstvConversionHistory);
        TextView tvHeader = (TextView) findViewById(R.id.tvHeader);

        getSupportActionBar().setTitle("Conversion History");
        tvHeader.setText(String.format("%s to %s", primaryCoin.getShortName(), secondaryCoin.getShortName()));
        ConversionHistoryAdapter conversionHistoryAdapter = new ConversionHistoryAdapter(this, coinController, coinExchangePair);
        lstConversionListView.setAdapter(conversionHistoryAdapter);
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
