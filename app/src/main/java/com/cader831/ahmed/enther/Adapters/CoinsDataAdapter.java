package com.cader831.ahmed.enther.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cader831.ahmed.enther.Activities.CoinSelectionActivity;
import com.cader831.ahmed.enther.Activities.ConversionHistoryActivity;
import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.CoinData;
import com.cader831.ahmed.enther.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class CoinsDataAdapter extends BaseAdapter {
    static class ViewHolder {
        TextView tvPrimaryC;
        TextView tvSecondaryC;
        TextView tvExchange;
        TextView tvUpdateDate;
        TextView tvSecondaryCTo;
    }

    private ArrayList<ArrayList<CoinData>> coinDataList;
    private Activity context;
    private CoinController coinController;

    public CoinsDataAdapter(Activity context, CoinController coinController) {
        this.context = context;
        updateListView(coinController);
    }

    @Override
    public int getCount() {
        return coinDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return coinDataList.get(position).get(0);
    }

    public void updateListView(CoinController coinController) {
        this.coinController = coinController;
        for (ArrayList<CoinData> c :coinController.getCoinDataMap().values()) {
            Collections.sort(c);
        }
        coinDataList = new ArrayList<>(coinController.getCoinDataMap().values());
        Collections.sort(coinDataList, (o2, o1) -> o1.get(0).getLastUpdate().compareTo(o2.get(0).getLastUpdate()));
        CoinsDataAdapter.this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CoinData coinData = (CoinData) getItem(position);
        LayoutInflater inflater = context.getLayoutInflater();

        viewHolder = new ViewHolder();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_coins_data_adaptor, null);
            viewHolder.tvPrimaryC = (TextView) convertView.findViewById(R.id.tvPrimaryC);
            viewHolder.tvSecondaryC = (TextView) convertView.findViewById(R.id.tvSecondaryC);
            viewHolder.tvExchange = (TextView) convertView.findViewById(R.id.tvExchange);
            viewHolder.tvUpdateDate = (TextView) convertView.findViewById(R.id.tvUpdateDate);
            viewHolder.tvSecondaryCTo = (TextView) convertView.findViewById(R.id.tvSecondaryCTo);
            ImageButton btnShowHistory = (ImageButton) convertView.findViewById(R.id.btnShowHistory);
            btnShowHistory.setOnClickListener(v -> {
                StartingConversionIntent(coinData);
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (coinData != null) {
            viewHolder.tvPrimaryC.setText(coinData.getPrimaryCoin().getShortName());
            viewHolder.tvSecondaryC.setText(coinData.getSecondaryCoin().getShortName());
            viewHolder.tvExchange.setText(coinData.getExchange().getName());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy h:mm a", Locale.US);
            viewHolder.tvUpdateDate.setText(simpleDateFormat.format(coinData.getLastUpdate()));
            viewHolder.tvSecondaryCTo.setText(String.format("%.8f", coinData.getDownloadPrice()));
            ImageButton btnShowHistory = (ImageButton) convertView.findViewById(R.id.btnShowHistory);
            btnShowHistory.setOnClickListener(v -> {
                StartingConversionIntent(coinData);
            });
        }
        return convertView;
    }

    private void StartingConversionIntent(CoinData coinData) {
        Intent showConversionHistoryActivity = new Intent(context, ConversionHistoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CoinController", coinController);
        bundle.putSerializable("PrimaryCoinSName", coinData.getPrimaryCoin().getShortName());
        bundle.putSerializable("SecondaryCoinSName", coinData.getSecondaryCoin().getShortName());
        bundle.putSerializable("Exchange", coinData.getExchange());
        showConversionHistoryActivity.putExtras(bundle);
        context.startActivity(showConversionHistoryActivity);
    }
}
