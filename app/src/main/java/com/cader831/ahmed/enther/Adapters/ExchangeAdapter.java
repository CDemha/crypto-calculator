package com.cader831.ahmed.enther.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cader831.ahmed.enther.JObjects.Exchange;
import com.cader831.ahmed.enther.R;
import java.util.ArrayList;

public class ExchangeAdapter extends ArrayAdapter<Exchange> {
    private Activity context;
    private ArrayList<Exchange> exchangeList;

    public ExchangeAdapter(Activity context, int resource, ArrayList<Exchange> exchangeList) {
        super(context, resource, exchangeList);
        this.context = context;
        this.exchangeList = exchangeList;
    }

    @Override
    public int getPosition(Exchange item) {
        return exchangeList.indexOf(item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {   // This view starts when we click the spinner.
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.layout_spinner_exchange_adaptor, parent, false);
        }

        Exchange item = exchangeList.get(position);
        TextView myTextView = (TextView) row.findViewById(R.id.tv_exchange_name);

        if (item != null)
            myTextView.setText(item.getName());
        return row;
    }
}

