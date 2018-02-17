package com.cader831.ahmed.enther.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import com.cader831.ahmed.enther.*;
import com.cader831.ahmed.enther.Activities.FirstTimeActivity;
import com.cader831.ahmed.enther.Activities.MainActivity;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.Exchange;
import com.cader831.ahmed.enther.JObjects.ExchangeController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadExchanges extends AsyncTask<String, Integer, String> {

    Context context;
    ProgressDialog dlSynchronizeDialog;
    ArrayList<Exchange> exchangeList;
    File exchangesControllerFile;
    SharedPreferences preferences;
    CoinController coinController;
    ExchangeController exchangeController;

    public DownloadExchanges(Context context, SharedPreferences preferences, ProgressDialog dlSynchronizeDialog, CoinController coinController) {
        this.context = context;
        this.preferences = preferences;
        this.coinController = coinController;
        exchangeController = new ExchangeController();
        this.dlSynchronizeDialog = dlSynchronizeDialog;
        exchangeList = new ArrayList<>();
        dlSynchronizeDialog.setTitle("Step 2 of 2");
        dlSynchronizeDialog.setMessage("Synchronizing Exchanges...");
    }


    @Override
    protected void onPreExecute() {
        exchangesControllerFile = new File(context.getFilesDir(), Utility.FILE_EXCHANGECONTROLLER);
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

        } catch (IOException e1) {
            e1.printStackTrace();
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
            JSONObject listOfCoinsJson = new JSONObject(result.toString());
            Iterator listOfCoinsIterator = listOfCoinsJson.keys();
            while (listOfCoinsIterator.hasNext()) {
                String exchangeName = (String) listOfCoinsIterator.next();
                Exchange exchange = new Exchange(exchangeName);
                JSONObject listOfSupportedCurrencies = new JSONObject(listOfCoinsJson.getString(exchangeName));
                Iterator listOfCurrenciesIterator = listOfSupportedCurrencies.keys();
                while (listOfCurrenciesIterator.hasNext()) {
                    String currencyName = (String) listOfCurrenciesIterator.next();
                    Coin primaryCoin = coinController.getCoinFromShortName(currencyName);
                    if (primaryCoin != null) {
                        JSONArray jsonArrayOfSupportedCoins = listOfSupportedCurrencies.getJSONArray(currencyName);
                        if (jsonArrayOfSupportedCoins.length() != 0) {
                            List<Coin> arrayOfSupportedCoins = new ArrayList<>();
                            for (int i = 0; i < jsonArrayOfSupportedCoins.length(); i++) {
                                Coin secondaryCoin = coinController.getCoinFromShortName(jsonArrayOfSupportedCoins.get(i).toString());
                                if (secondaryCoin != null) {
                                    arrayOfSupportedCoins.add(secondaryCoin);
                                }
                            }
                            exchange.addSupportedCoins(primaryCoin, arrayOfSupportedCoins);
                        }
                    }
                }
                exchangeController.AddExchange(exchange.Name, exchange);
            }

            Serializer.Serialize(exchangesControllerFile, exchangeController);

            preferences.edit().putBoolean(Utility.PREFS_EXCHANGES_FILE_CREATED, true).apply();

            dlSynchronizeDialog.dismiss();
            Toast.makeText(context, "Data Successfully Synchronized.", Toast.LENGTH_LONG).show();

            Intent mainActivity = new Intent(context, MainActivity.class);
            context.startActivity(mainActivity);
            ((FirstTimeActivity) context).finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}