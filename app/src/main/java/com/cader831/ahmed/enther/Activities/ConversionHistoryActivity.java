package com.cader831.ahmed.enther.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.cader831.ahmed.enther.Adapters.DepthConversionHistoryAdapter;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.Exchange;
import com.cader831.ahmed.enther.R;
import com.cader831.ahmed.enther.Utility;

public class ConversionHistoryActivity extends AppCompatActivity {

    private static final String TAG = "MyApp";
    CoinController coinController;
    Coin primaryCoin, secondaryCoin;
    Exchange exchange;
    String coinPair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        coinController = (CoinController) getIntent().getExtras().getSerializable("CoinController");
        primaryCoin = coinController.getCoinFromShortName((String) getIntent().getExtras().getSerializable("PrimaryCoinSName"));
        secondaryCoin = coinController.getCoinFromShortName((String) getIntent().getExtras().getSerializable("SecondaryCoinSName"));
        exchange = (Exchange) getIntent().getExtras().getSerializable("Exchange");
        coinPair = coinController.generateCoinPair(primaryCoin, secondaryCoin);

        ListView lstConversionListView = (ListView) findViewById(R.id.lstvConversionHistory);
        TextView tvHeader = (TextView) findViewById(R.id.tvHeader);

        getSupportActionBar().setTitle("Conversion History");
        tvHeader.setText(String.format("%s to %s", primaryCoin.getShortName(), secondaryCoin.getShortName()));
        DepthConversionHistoryAdapter depthConversionHistoryAdapter = new DepthConversionHistoryAdapter(this, coinController, coinPair);
        lstConversionListView.setAdapter(depthConversionHistoryAdapter);
    }

    private void showClearHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Conversion History?");
        builder.setMessage(String.format("Data related to %s to %s will be deleted forever. Do you want to continue?", primaryCoin.getShortName(), secondaryCoin.getShortName())).setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener);
        builder.show();
    }

    DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                clearConversionHistory(coinPair);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    };

    private void clearConversionHistory(String coinPair) {
        coinController.getCoinDataMap().remove(coinPair);
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("CoinController", coinController);
        resultIntent.putExtras(bundle);
        setResult(Utility.RESULT_CLEAR_HISTORY, resultIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.conversion_history_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.mClear:
                showClearHistoryDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
