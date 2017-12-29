package com.smurfee.android.emessel.pricefinder;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.smurfee.android.emessel.MainActivity;
import com.smurfee.android.emessel.R;
import com.smurfee.android.emessel.recyclerview.MSLViewFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tham on 26/12/2017.
 */

public class PriceFinder extends AsyncTask<String, Void, String[]> {

    private final WeakReference<Context> mRefContext;
    private final WeakReference<TextView> mSetPrice;
    private AlertDialog mPopup;
    private int mPosition = -1;

    public PriceFinder(WeakReference<Context> context, WeakReference<TextView> setPrice) {
        this.mRefContext = context;
        this.mSetPrice = setPrice;
    }

    public PriceFinder(WeakReference<Context> context, WeakReference<TextView> setPrice, int position) {
        this.mRefContext = context;
        this.mSetPrice = setPrice;
        this.mPosition = position;
    }

    @Override
    protected String[] doInBackground(String... keyword) {
        try {
            Document doc = Jsoup.connect("https://shop.countdown.co.nz/Shop/SearchProducts?search=" + keyword[0]).get();
            Elements product = doc.select("#product-list .gridProductStamp.gridStamp");

//            print("\nItem Name: %s, (%d)", keyword[0], product.size()); // XXX: Debugging
            List<String> printout = new ArrayList<>();
            for (Element cls : product) {  //TODO: Include club price, non-club price and volume
                String productName = trim(cls.select(".gridProductStamp-name").text(), 35);
                String productPrice = cls.select(".din-medium").first().text().split(" ", 2)[0];
                printout.add(String.format(" * %s. \n %s", productName, productPrice));
            }

            return printout.toArray(new String[printout.size()]);
        } catch (IOException e) {
        }
        return null;
    }

    protected void onPostExecute(final String[] result) {
        if (result == null) return;

        ListView popupDialog = new ListView(mRefContext.get());
        ArrayAdapter<String> popupAdapter = new ArrayAdapter<>(mRefContext.get(), R.layout.list_price, R.id.result_item, result);
        popupDialog.setAdapter(popupAdapter);
        popupDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String resultRow = ((TextView) view.findViewById(R.id.result_item)).getText().toString();
                String[] tokens = TextUtils.split(resultRow, "\\$");
                if (tokens.length == 0) return; // return if there is no price
                mSetPrice.get().setText(tokens[tokens.length - 1]);
                mPopup.cancel();
                if (mPosition >= 0) { // If Price Finder was invoked via swipe
                    MSLViewFragment f = (MSLViewFragment) ((MainActivity) mRefContext.get()).
                            getSupportFragmentManager().findFragmentById(R.id.fragment_recycler_msl);
                    View row = f.getRecyclerView().getLayoutManager().findViewByPosition(mPosition);
                    TextView label = row.findViewById(R.id.label);
                    ((EditText) row.findViewById(R.id.edit_label)).setText(label.getText());
                    ((EditText) row.findViewById(R.id.edit_price)).setText(tokens[tokens.length - 1]);
                    Button btnDone = row.findViewById(R.id.done);
                    btnDone.callOnClick();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mRefContext.get());
        builder.setCancelable(true);
        if (popupDialog.getParent() != null)
            ((ViewGroup) popupDialog.getParent()).removeView(popupDialog);
        builder.setView(popupDialog);
        builder.setTitle("Prices from Countdown");
        mPopup = builder.create();
        mPopup.show();
    }

    private String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s + ".";
    }
//    private void print(String msg, Object... args) {
//        Log.d("JSoup", String.format(msg, args));
//    }
}