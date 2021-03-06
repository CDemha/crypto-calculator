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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cader831.ahmed.enther.APIManager;
import com.cader831.ahmed.enther.Adapters.BriefConversionHistoryAdapter;
import com.cader831.ahmed.enther.Adapters.ExchangeAdapter;
import com.cader831.ahmed.enther.AsyncTaskResultEvent;
import com.cader831.ahmed.enther.AsyncTasks.DownloadApiData;
import com.cader831.ahmed.enther.EventBus;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.CoinData;
import com.cader831.ahmed.enther.JObjects.Exchange;
import com.cader831.ahmed.enther.JObjects.ExchangeController;
import com.cader831.ahmed.enther.R;
import com.cader831.ahmed.enther.Serializer;
import com.cader831.ahmed.enther.Utility;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
            if (selectedExchange.getName() == "Exchange Average") {
                Toast.makeText(getApplicationContext(), String.format("Downloading %s.", coinPair), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), String.format("Downloading %s from %s.", coinPair, selectedExchange), Toast.LENGTH_SHORT).show();
            }
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
//        String coinExchangePair = coinController.generateCoinPair(selectedPrimaryCoin, selectedSecondaryCoin);
        String coinPair = coinController.generateCoinPair(selectedPrimaryCoin, selectedSecondaryCoin);
        coinData = coinController.getCoinData(coinPair);
        performCalculation(editPrimaryAmount.getText().toString());
        Toast.makeText(getApplicationContext(), String.format("%s downloaded successfully.", coinPair), Toast.LENGTH_SHORT).show();
        updateConversionListview(coinController);

    }

    private void updateConversionListview(CoinController coinController) {
        ((BriefConversionHistoryAdapter) lstvCoinData.getAdapter()).updateListView(coinController);
    }

    private void setCoinData(String downloadedData) {
        try {
            JSONObject coinJSONData = new JSONObject(downloadedData);
            Double d = Double.parseDouble(coinJSONData.get(selectedSecondaryCoin.getShortName()).toString());
            BigDecimal price = new BigDecimal(d, MathContext.DECIMAL64);
            Exchange selectedExchange = (Exchange) spExchanges.getSelectedItem();
            CoinData coinData = new CoinData(selectedPrimaryCoin, selectedSecondaryCoin, selectedExchange, price, getUserInputAmount(editPrimaryAmount.getText().toString()), new Date());
            addToHistory(coinData);
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

    private BigDecimal getUserInputAmount(String amountInString) {
        if (TextUtils.isEmpty((amountInString))) {
            return new BigDecimal(1, MathContext.DECIMAL64);
        } else {
            try {
                BigDecimal amountInBD = new BigDecimal(Double.parseDouble(amountInString));
                return amountInBD;
            } catch (NumberFormatException e) {
                return new BigDecimal(1, MathContext.DECIMAL64);
            }
        }
    }

    private void performCalculation(String amountInString) {
        String coinPair = coinController.generateCoinPair(selectedPrimaryCoin, selectedSecondaryCoin);
        coinData = coinController.getCoinData(coinPair);

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
        editPrimaryAmount.setOnEditorActionListener(editPrimaryAmountActionListener);

        spExchanges = (Spinner) findViewById(R.id.spExchanges);

        btnCopyToClipboard.setOnLongClickListener(btnCopyToClipboardHold);
        btnCopyToClipboard.setOnClickListener(btnCopyToClipboardClick);
        btnDownloadPair.setOnClickListener(btnDownloadPairClick);
        tvPrimaryCoinSelector.setOnClickListener(tvPrimaryCoinSelectorClick);
        tvPrimaryCoinSelector.setOnLongClickListener(tvPrimaryCoinSelectorLongClick);
        tvSecondaryCoinSelector.setOnClickListener(tvSecondaryCoinSelectorClick);
        btnSwapCoins.setOnClickListener(btnSwapCoinsClick);

        lstvCoinData.setLongClickable(true);
        lstvCoinData.setOnItemClickListener(lstvCoinDataHistoryClick);
        lstvCoinData.setOnItemLongClickListener(lstvLongClick);
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

        BriefConversionHistoryAdapter briefConversionHistoryAdapter = new BriefConversionHistoryAdapter(this, coinController);
        lstvCoinData.setAdapter(briefConversionHistoryAdapter);
        lstvCoinData.setEmptyView(findViewById(R.id.emptyElement));
    }

    private void addToHistory(CoinData coinData) {
        coinController.setCoinData(coinData, localCoinsFile);
        updateConversionListview(coinController);
    }


    private EditText.OnEditorActionListener editPrimaryAmountActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (coinData != null) {
                    CoinData newCoinData = new CoinData(coinData.getPrimaryCoin(), coinData.getSecondaryCoin(), coinData.getExchange(), coinData.getDownloadPrice(), getUserInputAmount(editPrimaryAmount.getText().toString()),new Date());
                    addToHistory(newCoinData);
                    return true;
                }

            }
            return false;
        }
    };

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

    private void StartingConversionIntent(CoinData coinData) {
        Intent showConversionHistoryActivity = new Intent(this, ConversionHistoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CoinController", coinController);
        bundle.putSerializable("PrimaryCoinSName", coinData.getPrimaryCoin().getShortName());
        bundle.putSerializable("SecondaryCoinSName", coinData.getSecondaryCoin().getShortName());
        bundle.putSerializable("Exchange", coinData.getExchange());
        showConversionHistoryActivity.putExtras(bundle);
        this.startActivityForResult(showConversionHistoryActivity, Utility.RESULT_CLEAR_HISTORY);
    }

    private AdapterView.OnItemLongClickListener lstvLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            CoinData coinData = (CoinData)lstvCoinData.getAdapter().getItem(position);
            StartingConversionIntent(coinData);
            return false;
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
        startActivityForResult(selectCoinsActivity, Utility.RESULT_PRIMARY_COIN_REQUEST);
    };

    private View.OnLongClickListener tvPrimaryCoinSelectorLongClick = v -> {

        return true;
    };

    private View.OnClickListener tvSecondaryCoinSelectorClick = v -> {
        Intent selectCoinsActivity = new Intent(getApplicationContext(), CoinSelectionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CoinController", coinController);
        selectCoinsActivity.putExtras(bundle);
        startActivityForResult(selectCoinsActivity, Utility.RESULT_SECONDARY_COIN_REQUEST);
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
        performCalculation(editPrimaryAmount.getText().toString());
    };

    private View.OnClickListener btnDownloadPairClick = (View v) -> downloadCoinPair();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            coinController = (CoinController) data.getSerializableExtra("CoinController");
            Coin selectedCoin = (Coin) data.getSerializableExtra("SelectedCoin");

            if (resultCode == RESULT_OK) {
                if (requestCode == Utility.RESULT_PRIMARY_COIN_REQUEST) {
                    if (selectedCoin != null) {
                        setPrimaryCoin(coinController.getCoin(selectedCoin.getLongName()).getShortName());
                    }
                }
                if (requestCode == Utility.RESULT_SECONDARY_COIN_REQUEST) {
                    if (selectedCoin != null) {
                        setSecondaryCoin(coinController.getCoin(selectedCoin.getLongName()).getShortName());
                    }
                }
            }
            Serializer.Serialize(localCoinsFile, coinController);
        }
        if (requestCode == Utility.RESULT_CLEAR_HISTORY) {
            if (data != null) {
                coinController = (CoinController) data.getExtras().getSerializable("CoinController");
            }
        }
    }

    private AdapterView.OnItemSelectedListener exchangeSpinnerClick = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedExchange = (Exchange) parent.getItemAtPosition(position);
            String coinPair = coinController.generateCoinPair(selectedPrimaryCoin, selectedSecondaryCoin);
            coinData = coinController.getCoinData(coinPair);
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
            case R.id.mSettings:
                Intent settingsAcitivty = new Intent(this, SettingsActivity.class);
                startActivity(settingsAcitivty);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}