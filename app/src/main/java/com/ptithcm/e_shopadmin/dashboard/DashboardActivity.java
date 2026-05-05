package com.ptithcm.e_shopadmin.dashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.model.DashboardSummary;
import com.ptithcm.e_shopadmin.model.RevenueTrendItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;

public class DashboardActivity extends AdminBaseActivity {
    private TextView tvDashboardState;
    private TextView tvRevenueValue;
    private TextView tvOrdersValue;
    private TextView tvPaymentsValue;
    private TextView tvCustomersValue;
    private TextView tvAverageOrderValue;
    private TextView tvConversionRate;
    private TextView tvRevenueEmpty;
    private Button btnDashboardRefresh;
    private ListView lvRevenueTrend;
    private ArrayList<RevenueTrendItem> revenueTrendItems;
    private RevenueTrendAdapter revenueTrendAdapter;
    private NumberFormat currencyFormat;
    private NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_dashboard);

        initViews();
        initFormatters();
        setupRevenueList();
        initListeners();
        loadDashboard();
    }

    private void initViews() {
        tvDashboardState = findViewById(R.id.tvDashboardState);
        tvRevenueValue = findViewById(R.id.tvRevenueValue);
        tvOrdersValue = findViewById(R.id.tvOrdersValue);
        tvPaymentsValue = findViewById(R.id.tvPaymentsValue);
        tvCustomersValue = findViewById(R.id.tvCustomersValue);
        tvAverageOrderValue = findViewById(R.id.tvAverageOrderValue);
        tvConversionRate = findViewById(R.id.tvConversionRate);
        tvRevenueEmpty = findViewById(R.id.tvRevenueEmpty);
        btnDashboardRefresh = findViewById(R.id.btnDashboardRefresh);
        lvRevenueTrend = findViewById(R.id.lvRevenueTrend);
    }

    private void initFormatters() {
        currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
    }

    private void setupRevenueList() {
        revenueTrendItems = new ArrayList<>();
        revenueTrendAdapter = new RevenueTrendAdapter(this, revenueTrendItems);
        lvRevenueTrend.setAdapter(revenueTrendAdapter);
    }

    private void initListeners() {
        btnDashboardRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDashboard();
            }
        });
    }

    private void loadDashboard() {
        setLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = sessionManager.getAccessToken();
                    ApiResponse summaryResponse = ApiClient.get("/api/admin/analytics/summary?period=30d", token);
                    if (summaryResponse.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!summaryResponse.isSuccessful()) {
                        showError(summaryResponse.getErrorMessage());
                        return;
                    }

                    Instant end = Instant.now();
                    Instant start = end.minus(30, ChronoUnit.DAYS);
                    String revenuePath = "/api/admin/analytics/revenue?start="
                            + URLEncoder.encode(start.toString(), "UTF-8")
                            + "&end="
                            + URLEncoder.encode(end.toString(), "UTF-8")
                            + "&interval=daily";

                    ApiResponse revenueResponse = ApiClient.get(revenuePath, token);
                    if (revenueResponse.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!revenueResponse.isSuccessful()) {
                        showError(revenueResponse.getErrorMessage());
                        return;
                    }

                    final DashboardSummary summary = DashboardSummary.fromJson(new JSONObject(summaryResponse.getBody()));
                    final ArrayList<RevenueTrendItem> parsedRevenueItems = parseRevenueItems(revenueResponse.getBody());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDashboard(summary, parsedRevenueItems);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot load dashboard. Please try again.");
                }
            }
        });
        thread.start();
    }

    private ArrayList<RevenueTrendItem> parseRevenueItems(String body) throws Exception {
        ArrayList<RevenueTrendItem> items = new ArrayList<>();
        JSONArray array = new JSONArray(body);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            items.add(RevenueTrendItem.fromJson(object));
        }
        return items;
    }

    private void setLoading(final boolean loading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDashboardRefresh.setEnabled(!loading);
                if (loading) {
                    tvDashboardState.setText("Loading dashboard...");
                    tvDashboardState.setVisibility(View.VISIBLE);
                    tvRevenueEmpty.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showDashboard(DashboardSummary summary, ArrayList<RevenueTrendItem> parsedRevenueItems) {
        btnDashboardRefresh.setEnabled(true);
        tvDashboardState.setVisibility(View.GONE);

        tvRevenueValue.setText(currencyFormat.format(summary.getRevenue()));
        tvOrdersValue.setText(numberFormat.format(summary.getOrders()));
        tvPaymentsValue.setText(currencyFormat.format(summary.getCapturedPayments()));
        tvCustomersValue.setText(numberFormat.format(summary.getNewCustomers()));
        tvAverageOrderValue.setText(currencyFormat.format(summary.getAverageOrderValue()));
        tvConversionRate.setText(String.format(Locale.US, "%.2f%%", summary.getConversionRate()));

        revenueTrendItems.clear();
        revenueTrendItems.addAll(parsedRevenueItems);
        revenueTrendAdapter.notifyDataSetChanged();

        if (revenueTrendItems.isEmpty()) {
            tvRevenueEmpty.setText("No revenue trend data for the last 30 days.");
            tvRevenueEmpty.setVisibility(View.VISIBLE);
        } else {
            tvRevenueEmpty.setVisibility(View.GONE);
        }
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDashboardRefresh.setEnabled(true);
                tvDashboardState.setText(message);
                tvDashboardState.setVisibility(View.VISIBLE);
                tvRevenueEmpty.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DashboardActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                sessionManager.clearSession();
                openLogin();
            }
        });
    }
}
