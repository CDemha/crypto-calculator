package com.cader831.ahmed.enther.Adapters;

import android.app.Activity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cader831.ahmed.enther.JObjects.CoinController;
import com.cader831.ahmed.enther.JObjects.CoinData;
import com.cader831.ahmed.enther.R;

import java.util.ArrayList;
import java.util.Collections;

public class CoinsDataAdapter extends BaseAdapter {
    static class ViewHolder {
        TextView tvPrimaryC;
        TextView tvSecondaryC;
        TextView tvExchange;
        TextView tvUpdateDate;
    }

    private ArrayList<CoinData> coinDataList;
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
        return coinDataList.get(position);
    }

    public void updateListView(CoinController coinController) {
        this.coinController = coinController;
        coinDataList = new ArrayList<>(coinController.getCoinDataMap().values());
        Collections.sort(coinDataList, (o2, o1) -> o1.getLastUpdate().compareTo(o2.getLastUpdate()));
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
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (coinData != null) {
            viewHolder.tvPrimaryC.setText(coinData.getPrimaryCoin().getShortName());
            viewHolder.tvSecondaryC.setText(coinData.getSecondaryCoin().getShortName());
            viewHolder.tvExchange.setText(coinData.getExchange().getName());
            CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(coinData.getLastUpdate().getTime(), System.currentTimeMillis(), DateUtils.FORMAT_ABBREV_TIME);
            viewHolder.tvUpdateDate.setText(relativeTimeSpanString);
            Log.i("MyApp", String.valueOf(coinData));
        }

        return convertView;
    }
}
