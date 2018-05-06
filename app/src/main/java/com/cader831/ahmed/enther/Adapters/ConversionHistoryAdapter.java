package com.cader831.ahmed.enther.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.CoinData;
import com.cader831.ahmed.enther.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ConversionHistoryAdapter extends BaseAdapter {
    static class ViewHolder {
        TextView tvPrimaryCoin;
        TextView tvSecondaryCoin;
        TextView tvGivenUnit;
        TextView tvAmount;
        TextView tvUpdateDate;
        TextView tvExchange;
    }

    private ArrayList<CoinData> coinConversionHistoryList;
    private Activity context;
    private CoinController coinController;
    private String coinExchangePair;

    public ConversionHistoryAdapter(Activity context, CoinController coinController, String coinExchangePair) {
        this.context = context;
        this.coinExchangePair = coinExchangePair;
        updateListView(coinController);
    }

    @Override
    public int getCount() {
        return coinConversionHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return coinConversionHistoryList.get(position);
    }

    public void updateListView(CoinController coinController) {
        this.coinController = coinController;
        coinConversionHistoryList = new ArrayList<>(coinController.getCoinDataMap().get(coinExchangePair));
        Collections.sort(coinConversionHistoryList, (o2, o1) -> o1.getLastUpdate().compareTo(o2.getLastUpdate()));
        ConversionHistoryAdapter.this.notifyDataSetChanged();
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
            convertView = inflater.inflate(R.layout.layout_conversion_history, null);
            viewHolder.tvPrimaryCoin = (TextView) convertView.findViewById(R.id.tvPrimaryCoin);
            viewHolder.tvSecondaryCoin = (TextView) convertView.findViewById(R.id.tvSecondaryCoin);
            viewHolder.tvGivenUnit = (TextView) convertView.findViewById(R.id.tvGivenUnit);
            viewHolder.tvAmount = (TextView) convertView.findViewById(R.id.tvAmount);
            viewHolder.tvUpdateDate = (TextView) convertView.findViewById(R.id.tvUpdateDate);
            viewHolder.tvExchange = (TextView) convertView.findViewById(R.id.tvExchange);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (coinData != null) {
            viewHolder.tvPrimaryCoin.setText(coinData.getPrimaryCoin().getShortName());
            viewHolder.tvSecondaryCoin.setText(coinData.getSecondaryCoin().getShortName());
            viewHolder.tvGivenUnit.setText(String.format("%.8f", coinData.getGivenUnit()));
            viewHolder.tvAmount.setText(String.format("%.8f", coinData.getCalculatedAmount()));
            viewHolder.tvExchange.setText(coinData.getExchange().getName());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy h:mm a", Locale.US);
            viewHolder.tvUpdateDate.setText(simpleDateFormat.format(coinData.getLastUpdate()));
        }
        return convertView;
    }
}
