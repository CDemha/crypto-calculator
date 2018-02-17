package com.cader831.ahmed.enther.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.cader831.ahmed.enther.JObjects.Coin;
import com.cader831.ahmed.enther.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 12/01/2018.
 */

public class CoinsAdapter extends BaseAdapter implements Filterable {


    static class ViewHolder {
        TextView lName;
        TextView sName;
        ImageButton btnFav;
    }

    private Activity context;
    private CustomFilter filter;
    private List<Coin> filterList;
    private List<Coin> coinsList;
    private int selectedSpinnerItem;

    public CoinsAdapter(Activity context, List<Coin> data, int pos) {
        this.context = context;
        coinsList = data;
        filterList = data;
        selectedSpinnerItem = pos;
    }

    @Override
    public int getCount() {
        return coinsList.size();
    }

    @Override
    public Coin getItem(int position) {
        return coinsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    ViewHolder viewHolder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Coin coinItem = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.layout_coins_adapter, null);
            viewHolder = new ViewHolder();
            viewHolder.lName = (TextView) convertView.findViewById(R.id.tvMainHeading);
            viewHolder.sName = (TextView) convertView.findViewById(R.id.tvSubHeading);
            viewHolder.btnFav = (ImageButton) convertView.findViewById(R.id.btnFavourite);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (coinItem != null) {
            viewHolder.lName.setText(coinItem.getLongName().trim());
            viewHolder.sName.setText(coinItem.getShortName().trim());

            if (coinItem.isFavourite()) {
                viewHolder.btnFav.setImageResource(R.drawable.ic_action_selected);
                viewHolder.btnFav.setColorFilter(Color.rgb(255, 215, 0), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                viewHolder.btnFav.setImageResource(R.drawable.ic_action_unselected);
                viewHolder.btnFav.setColorFilter(null);
            }
        }

        viewHolder.btnFav.setOnClickListener(v -> {
            if (coinItem.isFavourite()) {
                if (selectedSpinnerItem == 2) {
                   coinsList.remove(coinItem);
                    coinItem.setFavourite(false);
                } else {
                    coinItem.setFavourite(false);
                    ((ImageButton) v).setImageResource(R.drawable.ic_action_unselected);
                    ((ImageButton) v).setColorFilter(null);
                }
            } else {
                coinItem.setFavourite(true);
                ((ImageButton) v).setImageResource(R.drawable.ic_action_selected);
                ((ImageButton) v).setColorFilter(Color.rgb(255, 215, 0), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            CoinsAdapter.this.notifyDataSetChanged();
        });
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence searchSequence) {
            FilterResults results = new FilterResults();
            if (searchSequence != null && searchSequence.length() > 0) {
                searchSequence = searchSequence.toString().toUpperCase();
                List<Coin> filters = new ArrayList<>();
                for (Coin coin : filterList) {
                    if (coin.getLongName().toUpperCase().contains(searchSequence) || coin.getShortName().toUpperCase().contains(searchSequence)) {
                        filters.add(coin);
                    }
                }
                results.count = filters.size();
                results.values = filters;
            } else {
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            coinsList = (List<Coin>) results.values;
            notifyDataSetChanged();
        }
    }
}
