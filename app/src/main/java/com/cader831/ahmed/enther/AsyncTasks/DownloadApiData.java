package com.cader831.ahmed.enther.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.cader831.ahmed.enther.AsyncTaskResultEvent;
import com.cader831.ahmed.enther.EventBus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadApiData extends AsyncTask<String, Void, String> {


    protected void onPreExecute() {

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
    protected void onPostExecute(String result) {
        EventBus.getInstance().post(new AsyncTaskResultEvent(result));
        this.cancel(true);
    }
}

