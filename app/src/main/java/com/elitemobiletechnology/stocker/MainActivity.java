package com.elitemobiletechnology.stocker;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "onCreate";
    ActionBar actionBar;
    ImageView buttonAdd;
    GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate");
        setActionBar();
        buttonAdd = (ImageView) findViewById(R.id.ivAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogBox();
            }
        });
        gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<Stock> stocks = new ArrayList<Stock>();
        Stock google = new Stock();
        google.setName("GOOGLE");
        google.setPrice("$100");
        stocks.add(google);
        Stock apple = new Stock();
        apple.setName("APPLE");
        apple.setPrice("$550");
        stocks.add(apple);
        gridview.setEmptyView(findViewById(R.id.empty_grid_view));
        gridview.setAdapter(new StockGridAdapter(this, stocks));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridview.setNumColumns(2);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridview.setNumColumns(1);
        }
    }

    private void openDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_stock_dialog, null);
        ImageButton cancel_dialog = (ImageButton) dialogView.findViewById(R.id.cancel_button);
        ImageButton upArrow = (ImageButton) dialogView.findViewById(R.id.ibUp);
        ImageButton downArrow = (ImageButton) dialogView.findViewById(R.id.ibDown);
        final EditText etPercentChange = (EditText) dialogView.findViewById(R.id.etNotify);

        upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int change = 0;
                try {
                    change = Integer.parseInt(etPercentChange.getText().toString()) + 1;
                    etPercentChange.setText(String.valueOf(change));
                } catch (NumberFormatException e) {
                    change = 0;
                    etPercentChange.setText(String.valueOf(change));
                }
            }
        });

        downArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int change = 0;
                try {
                    change = Integer.parseInt(etPercentChange.getText().toString()) - 1;
                    etPercentChange.setText(String.valueOf(change));
                } catch (NumberFormatException e) {
                    change = 0;
                    etPercentChange.setText(String.valueOf(change));
                }
            }
        });

        builder.setView(dialogView);
        final AlertDialog myDialog = builder.create();
        cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
    }

    private void setActionBar() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF6CC5BA));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        View viewActionBar = getLayoutInflater().inflate(R.layout.action_bar_layout, null);
        actionBar.setCustomView(viewActionBar);
    }
}
