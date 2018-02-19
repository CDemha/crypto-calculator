package com.cader831.ahmed.enther.Activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.cader831.ahmed.enther.*;
import com.cader831.ahmed.enther.Adapters.CoinsDataAdapter;
import com.cader831.ahmed.enther.Adapters.ExchangeAdapter;
import com.cader831.ahmed.enther.AsyncTasks.DownloadApiData;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.CoinData;
import com.cader831.ahmed.enther.JObjects.Exchange;
import com.cader831.ahmed.enther.JObjects.ExchangeController;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyApp";
    private TextView tvPrimaryCoinSelector;
    private TextView tvSecondaryCoinSelector;
    private TextView tvConversionResult;
    private ListView lstvCoinData;

    private EditText editPrimaryAmount;
    private Spinner spExchanges;

    private Coin selectedPrimaryCoin;
    private Coin selectedSecondaryCoin;
    private Exchange selectedExchange;
    private int coinChangeResult;
    private File localCoinsFile;

    private CoinController coinController;
    private ExchangeController exchangeController;
    private APIManager apiManager;

    private CoinData coinData;

    private void downloadCoinPair() {

        if (Utility.networkAvailable(this)) {
            String coinPair = coinController.generateCoinPair(selectedPrimaryCoin, selectedSecondaryCoin);
            String downloadPrice;
            if (selectedExchange.equals(new Exchange("Exchange Average"))) {
                downloadPrice = apiManager.generatePriceLink(selectedPrimaryCoin, selectedSecondaryCoin);
            } else {
                downloadPrice = apiManager.generatePriceLink(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange);
            }
            Toast.makeText(getApplicationContext(), String.format("Downloading %s from %s.", coinPair, selectedExchange), Toast.LENGTH_SHORT).show();
            DownloadApiData downloadApiData = new DownloadApiData();
            downloadApiData.execute(downloadPrice);
        } else {
            Toast.makeText(this, "A network connection is required.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onAsyncTaskResult(AsyncTaskResultEvent event) {
        setCoinData(event.getResult());
        String coinExchangePair = coinController.generateCoinExchangePair(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange);
        String coinPair = coinController.generateCoinPair(selectedPrimaryCoin, selectedSecondaryCoin);
        coinData = coinController.getCoinData(coinExchangePair);
        performCalculation(editPrimaryAmount.getText().toString());
        Toast.makeText(getApplicationContext(), String.format("%s downloaded successfully.", coinPair), Toast.LENGTH_SHORT).show();
        updateConversionListview(coinController);
    }

    private void updateConversionListview(CoinController coinController) {
        ((CoinsDataAdapter) lstvCoinData.getAdapter()).updateListView(coinController);
    }

    private void setCoinData(String downloadedData) {
        try {
            JSONObject coinJSONData = new JSONObject(downloadedData);
            Double d = Double.parseDouble(coinJSONData.get(selectedSecondaryCoin.getShortName()).toString());
            BigDecimal price = new BigDecimal(d, MathContext.DECIMAL64);
            Exchange selectedExchange = (Exchange) spExchanges.getSelectedItem();
            CoinData coinData = new CoinData(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange, price, new Date());
            coinController.setCoinData(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange, coinData, localCoinsFile);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayResult(double amount) {
        BigDecimal resultOfPriceMulti = coinData.getDownloadPrice().multiply(new BigDecimal(amount, MathContext.DECIMAL64));
        resultOfPriceMulti.setScale(8, RoundingMode.HALF_UP);
        DecimalFormat decimalFormat = new DecimalFormat("0.00000000");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        String result = decimalFormat.format(resultOfPriceMulti);
        tvConversionResult.setText(result);
    }

    private void performCalculation(String amountInString) {
        String coinExchangePair = coinController.generateCoinExchangePair(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange);
        coinData = coinController.getCoinData(coinExchangePair);

        if (coinData == null) {
            downloadCoinPair();
        } else {
            if (TextUtils.isEmpty((amountInString))) {
                displayResult(0);
            } else {
                try {
                    displayResult(Double.parseDouble(amountInString.toString()));
                } catch (NumberFormatException e) {
                    displayResult(0);
                }
            }
        }
    }

    private void setCoinPair(String primaryCoinShortName, String secondaryCoinShortName) {
        setPrimaryCoin(primaryCoinShortName);
        setSecondaryCoin(secondaryCoinShortName);
    }

    private void setPrimaryCoin(String primaryCoinShortName) {
        selectedPrimaryCoin = coinController.getCoinFromShortName(primaryCoinShortName);
        editPrimaryAmount.setHint(String.format("Enter %s amount.", selectedPrimaryCoin.getShortName()));
        tvPrimaryCoinSelector.setText(selectedPrimaryCoin.getShortName());
        coinChangeResult += 1;
        TriggerCoinSelection();
    }

    private void setSecondaryCoin(String secondaryCoinShortName) {
        selectedSecondaryCoin = coinController.getCoinFromShortName(secondaryCoinShortName);
        tvSecondaryCoinSelector.setText(selectedSecondaryCoin.getShortName());
        coinChangeResult += 1;
        TriggerCoinSelection();
    }

    ArrayList<Exchange> exchangeList;

    private void TriggerCoinSelection() {
        if (coinChangeResult == 2) {
            if (selectedPrimaryCoin != null && selectedSecondaryCoin != null) {
                coinChangeResult = 1;
                exchangeList = new ArrayList<>();
                for (Exchange exchange : exchangeController.getExchangesToList()) {
                    List<Coin> supportedCoinsA = exchange.getSupportedCoins(selectedPrimaryCoin.getLongName());
                    if ((supportedCoinsA != null && supportedCoinsA.size() > 0)) {
                        if (supportedCoinsA.contains(selectedSecondaryCoin)) {
                            exchangeList.add(exchange);
                        }
                    }
                }
                SetExchangeAdapter(exchangeList);
            }
        }
    }

    private void SetExchangeAdapter(ArrayList<Exchange> exchangeList) {
        if (exchangeList != null && exchangeList.size() > 0) {
            Collections.sort(exchangeList);
            spExchanges.setEnabled(true);
        } else {
            spExchanges.setEnabled(false);
        }

        if (exchangeList != null) {
            exchangeList.add(0, new Exchange("Exchange Average"));
        }

        ExchangeAdapter spExchangeAdapter = new ExchangeAdapter(this, android.R.layout.simple_spinner_dropdown_item, exchangeList);
        spExchanges.setAdapter(spExchangeAdapter);
        selectedExchange = (Exchange) spExchanges.getSelectedItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPrimaryCoinSelector = (TextView) findViewById(R.id.tvPrimaryCoinSelector);
        tvSecondaryCoinSelector = (TextView) findViewById(R.id.tvSecondaryCoinSelector);
        tvConversionResult = (TextView) findViewById(R.id.tvConversionResult);
        lstvCoinData = (ListView) findViewById(R.id.lstvCoinData);

        ImageButton btnDownloadPair = (ImageButton) findViewById(R.id.btnDownloadPair);
        ImageButton btnCopyToClipboard = (ImageButton) findViewById(R.id.btnCopyToClipboard);
        ImageButton btnSwapCoins = (ImageButton) findViewById(R.id.btnSwapCoins);

        editPrimaryAmount = (EditText) findViewById(R.id.editPrimaryAmount);
        editPrimaryAmount.addTextChangedListener(editPrimaryAmountTextWatcher);

        spExchanges = (Spinner) findViewById(R.id.spExchanges);

        btnCopyToClipboard.setOnLongClickListener(btnCopyToClipboardHold);
        btnCopyToClipboard.setOnClickListener(btnCopyToClipboardClick);
        btnDownloadPair.setOnClickListener(btnDownloadPairClick);
        tvPrimaryCoinSelector.setOnClickListener(tvPrimaryCoinSelectorClick);
        tvPrimaryCoinSelector.setOnLongClickListener(tvPrimaryCoinSelectorLongClick);
        tvSecondaryCoinSelector.setOnClickListener(tvSecondaryCoinSelectorClick);
        btnSwapCoins.setOnClickListener(btnSwapCoinsClick);

        lstvCoinData.setOnItemClickListener(lstvCoinDataHistoryClick);

        spExchanges.setOnItemSelectedListener(exchangeSpinnerClick);

        localCoinsFile = new File(getFilesDir(), Utility.FILE_COINSCONTROLLER);
        File localExchangesFile = new File(getFilesDir(), Utility.FILE_EXCHANGECONTROLLER);

        apiManager = new APIManager();
        if (localCoinsFile.exists() && localExchangesFile.exists()) {
            coinController = (CoinController) Serializer.Deserialize(localCoinsFile);
            exchangeController = (ExchangeController) Serializer.Deserialize(localExchangesFile);
        }
        setCoinPair("BTC", "ETH");
        EventBus.getInstance().register(this);

        CoinsDataAdapter coinsDataAdapter = new CoinsDataAdapter(this, coinController);
        lstvCoinData.setAdapter(coinsDataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateConversionListview(coinController);
    }

    private TextWatcher editPrimaryAmountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            performCalculation(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private AdapterView.OnItemClickListener lstvCoinDataHistoryClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            CoinData coinData = ((CoinData) lstvCoinData.getAdapter().getItem(i));

            Coin coinDataPrimaryCoin = coinData.getPrimaryCoin();
            Coin coinDataSecondaryCoin = coinData.getSecondaryCoin();
            Exchange coinDataExchange = coinData.getExchange();

            setCoinPair(coinDataPrimaryCoin.getShortName(), coinDataSecondaryCoin.getShortName());
            spExchanges.setSelection(exchangeList.indexOf(coinDataExchange));
        }
    };


    private View.OnClickListener tvPrimaryCoinSelectorClick = v -> {
        Intent selectCoinsActivity = new Intent(getApplicationContext(), CoinSelectionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CoinController", coinController);
        selectCoinsActivity.putExtras(bundle);
        startActivityForResult(selectCoinsActivity, Utility.ACTIVITYRESULT_PRIMARY_COIN_REQUEST);
    };

    private View.OnLongClickListener tvPrimaryCoinSelectorLongClick = v -> {
        Toast.makeText(getApplicationContext(), "LONG", Toast.LENGTH_SHORT);
        return true;
    };

    private View.OnClickListener tvSecondaryCoinSelectorClick = v -> {
        Intent selectCoinsActivity = new Intent(getApplicationContext(), CoinSelectionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CoinController", coinController);
        selectCoinsActivity.putExtras(bundle);
        startActivityForResult(selectCoinsActivity, Utility.ACTIVITYRESULT_SECONDARY_COIN_REQUEST);
    };

    private View.OnClickListener btnCopyToClipboardClick = v -> {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Calculated Amount", tvConversionResult.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Copied to clipboard.", Toast.LENGTH_SHORT).show();
    };

    private View.OnLongClickListener btnCopyToClipboardHold = v -> {
        String calculatedAmount = tvConversionResult.getText().toString();
        editPrimaryAmount.setText(calculatedAmount);
        return true;
    };

    private View.OnClickListener btnSwapCoinsClick = v -> {
        String primary = tvSecondaryCoinSelector.getText().toString();
        String secondary = tvPrimaryCoinSelector.getText().toString();
        setCoinPair(primary, secondary);
    };

    private View.OnClickListener btnDownloadPairClick = (View v) -> downloadCoinPair();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            coinController = (CoinController) data.getSerializableExtra("CoinController");
            Coin selectedCoin = (Coin) data.getSerializableExtra("SelectedCoin");

            if (resultCode == RESULT_OK) {
                if (requestCode == Utility.ACTIVITYRESULT_PRIMARY_COIN_REQUEST) {
                    if (selectedCoin != null) {
                        setPrimaryCoin(coinController.getCoin(selectedCoin.getLongName()).getShortName());
                    }
                }
                if (requestCode == Utility.ACTIVITYRESULT_SECONDARY_COIN_REQUEST) {
                    if (selectedCoin != null) {
                        setSecondaryCoin(coinController.getCoin(selectedCoin.getLongName()).getShortName());
                    }
                }
            }
            Serializer.Serialize(localCoinsFile, coinController);
            performCalculation(editPrimaryAmount.getText().toString());
        }
    }

    private AdapterView.OnItemSelectedListener exchangeSpinnerClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedExchange = (Exchange) parent.getItemAtPosition(position);
            String coinExchangePair = coinController.generateCoinExchangePair(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange);
            coinData = coinController.getCoinData(coinExchangePair);
            if (coinData == null) {
                downloadCoinPair();
            } else {
                performCalculation(editPrimaryAmount.getText().toString());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mSyncItems:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
