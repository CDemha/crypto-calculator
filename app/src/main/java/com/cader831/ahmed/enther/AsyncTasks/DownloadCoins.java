package com.cader831.ahmed.enther.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.Serializer;
import com.cader831.ahmed.enther.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class DownloadCoins extends AsyncTask<String, Integer, String> {

    ProgressDialog dlSynchronizeDialog;
    Map<String, Coin> coinsMap = new HashMap<>();
    Context context;
    File coinControllerFile;
    SharedPreferences preferences;
    CoinController coinController;

    public DownloadCoins(Context context, SharedPreferences preferences) {
        this.context = context;
        this.preferences = preferences;
        coinController = new CoinController();

        dlSynchronizeDialog = new ProgressDialog(this.context);
        dlSynchronizeDialog.setTitle("Step 1 of 2");
        dlSynchronizeDialog.setMessage("Synchronizing Coins...");
        dlSynchronizeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dlSynchronizeDialog.setIndeterminate(true);
        dlSynchronizeDialog.setCancelable(false);
        dlSynchronizeDialog.setCanceledOnTouchOutside(false);
    }

    protected void onPreExecute() {
        coinControllerFile = new File(context.getFilesDir(), Utility.FILE_COINSCONTROLLER);
        dlSynchronizeDialog.show();
    }

    protected String doInBackground(String... downloadURL) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(downloadURL[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream input = new BufferedInputStream(url.openStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            input.close();
            connection.disconnect();
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            super.onPostExecute(result);
            JSONObject listOfCoinsJson = new JSONObject(result.toString()).getJSONObject("Data");
            Iterator listOfCoinsIterator = listOfCoinsJson.keys();
            while (listOfCoinsIterator.hasNext()) {
                String coinName = (String) listOfCoinsIterator.next();
                String coinProperties = listOfCoinsJson.getString(coinName);
                JSONObject jsonCoinProperties = new JSONObject(coinProperties);
                Coin newCoin = new Coin(Integer.valueOf(jsonCoinProperties.getString("Id")), jsonCoinProperties.getString("Symbol").trim(), jsonCoinProperties.getString("CoinName").trim(), false, false);
                coinController.addCoin(newCoin);
            }

            Locale[] locs = Locale.getAvailableLocales();
            for (Locale loc : locs) {
                try {
                    Currency currency = Currency.getInstance(loc);
                    if (currency != null) {
                        Coin newCoin = new Coin(0, currency.getCurrencyCode().trim(), currency.getDisplayName().trim(), false, true);
                        coinController.addCoin(newCoin);
                    }
                } catch (Exception exc) {
                }
            }
            Serializer.Serialize(coinControllerFile, coinController);

            preferences.edit().putBoolean(Utility.PREFS_COINS_FILE_CREATED, true).apply();
            new DownloadExchanges(context, preferences, dlSynchronizeDialog, coinController).execute("https://min-api.cryptocompare.com/data/all/exchanges");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
