package com.test.bleproject.UI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.test.bleproject.DataModel.DashboardResponseBody;
import com.test.bleproject.DataModel.PreviousFuelHistory;
import com.test.bleproject.R;

import java.util.List;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private DashboardResponseBody dashboardResponseBody;
    private String reportType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);



        //Dashboard customa ctionbar
        setContentView(R.layout.activity_dashboard);




        Intent intent = getIntent();
        dashboardResponseBody = (DashboardResponseBody) intent.getSerializableExtra("dashboard_data");
        reportType = intent.getStringExtra("reportType");

        // Dashboard custom action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.dashboard_actionbar); // Set the custom view first

            // Now, get references to the views inside the custom action bar
            View customActionBarView = actionBar.getCustomView();
            TextView action_bar_title1 = customActionBarView.findViewById(R.id.action_bar_title1);
            TextView action_bar_title2 = customActionBarView.findViewById(R.id.action_bar_title2);
            ImageView backbuttonfromDashboard = customActionBarView.findViewById(R.id.backbuttonfromDashboard);

            // Check reportType and set visibility accordingly
            if (Objects.equals(reportType, "sortedReport")) {
                action_bar_title1.setVisibility(View.GONE);
                action_bar_title2.setVisibility(View.VISIBLE);
            } else {
                action_bar_title1.setVisibility(View.VISIBLE);
                action_bar_title2.setVisibility(View.GONE);
            }

            // Set back button click listener
            backbuttonfromDashboard.setOnClickListener(v -> {
                Intent intent2 = new Intent(Dashboard.this, HomeActivity.class);
                startActivity(intent2);
                finish();
            });
        }


        LinearLayout cardViewFuelHistory = findViewById(R.id.cardViewFuelHistory);
        if (dashboardResponseBody != null) {

            if(Objects.equals(reportType, "sortedReport")){

                cardViewFuelHistory.setVisibility(View.GONE);
                populateTable(dashboardResponseBody.getPreviousFuelHistories());
            }
            else{
                cardViewFuelHistory.setVisibility(View.VISIBLE);
                populateTable(dashboardResponseBody.getPreviousFuelHistories());
                setFuelCostCardData(dashboardResponseBody);
            }
        }


            // Register a callback for the back button press
            getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // Create an intent to go to the HomeActivity
                    Intent intent = new Intent(Dashboard.this, HomeActivity.class);

                    // Set flags to clear the activity stack and start a new task
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Start the HomeActivity
                    startActivity(intent);

                    // Optionally finish the current activity
                    finish();
                }
            });

    }

    private void populateTable(List<PreviousFuelHistory> fuelHistories) {
        TableLayout tableLayout = findViewById(R.id.dashboard_table);

        // Create table headers
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        headerRow.setBackgroundColor(Color.LTGRAY);
        headerRow.addView(createTextView("Date", true, 2f));
        headerRow.addView(createTextView("Vehicle Id", true, 2f));
        headerRow.addView(createTextView("Driver Name", true,2f));
        headerRow.addView(createTextView("Fuel", true, 1f));
        headerRow.addView(createTextView("Fuel Unit", true, 1f));
        headerRow.addView(createTextView("Fuel Cost", true, 1f));
        headerRow.setPadding(0, 35, 0, 35);  // Add padding to space out header row
        tableLayout.addView(headerRow);

        // Populate the table with data
        for (PreviousFuelHistory history : fuelHistories) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            row.addView(createTextView(history.getDate(), false, 2f));
            row.addView(createTextView(history.getBrtaRegNum(), false, 2f));
            row.addView(createTextView(history.getDriverName(), false, 2f));

            TextView fuelTextView = createTextView(history.getFuel(), false, 1f);
            fuelTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,10);
            row.addView(fuelTextView);

            row.addView(createTextView(String.valueOf(history.getFuelUnit()), false, 1f));
            row.addView(createTextView(String.valueOf(history.getFuelCost()), false, 1f));

            row.setPadding(0, 30, 0, 30);


            // Add row to table
            tableLayout.addView(row);
        }
    }

    private TextView createTextView(String text, boolean isHeader, float weight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);  // Increase padding to 16dp for more space inside the cells
        textView.setGravity(Gravity.CENTER);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight);
        textView.setLayoutParams(params);
        textView.setTextColor(Color.BLACK);
        textView.setSingleLine(false);
        if (isHeader) {
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        }else{
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        }
        return textView;
    }



    private void setFuelCostCardData(DashboardResponseBody dashboardResponseBody){
        int weeklyFuelCost = dashboardResponseBody.getWeeklyFuelCost();
        int monthlyFuelCost = dashboardResponseBody.getMonthlyFuelCost();
        int totalFuelCost = dashboardResponseBody.getTotalFuelCost();

        TextView weeklyFuelCostView = findViewById(R.id.weeklyFuelCost);
        TextView monthlyFuelCostView = findViewById(R.id.monthlyFuelCost);
        TextView totalFuelCostView = findViewById(R.id.totalFuelCost);


        weeklyFuelCostView.setText("Weekly Fuel Cost: "+ weeklyFuelCost);
        monthlyFuelCostView.setText("Monthly Fuel Cost: "+ monthlyFuelCost);
        totalFuelCostView.setText("Total Fuel Cost: "+ totalFuelCost);
    }


}
