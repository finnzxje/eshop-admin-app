package com.ptithcm.e_shopadmin.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.model.OrderPaymentItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersActivity extends AdminBaseActivity {
    private static final int PAGE_SIZE = 10;
    private Spinner spPaymentStatus;
    private EditText edtOrderNumberFilter;
    private Button btnOrdersBack;
    private Button btnOrderSearch;
    private Button btnOrderRefresh;
    private Button btnOrderPrevious;
    private Button btnOrderNext;
    private TextView tvOrdersState;
    private TextView tvOrdersPageInfo;
    private ListView lvOrderPayments;
    private ArrayList<OrderPaymentItem> orderPaymentItems;
    private OrderPaymentAdapter orderPaymentAdapter;
    private String[] statusValues;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_orders);

        initViews();
        setupStatusSpinner();
        setupList();
        initListeners();
        loadOrderPayments(0);
    }

    private void initViews() {
        spPaymentStatus = findViewById(R.id.spPaymentStatus);
        edtOrderNumberFilter = findViewById(R.id.edtOrderNumberFilter);
        btnOrdersBack = findViewById(R.id.btnOrdersBack);
        btnOrderSearch = findViewById(R.id.btnOrderSearch);
        btnOrderRefresh = findViewById(R.id.btnOrderRefresh);
        btnOrderPrevious = findViewById(R.id.btnOrderPrevious);
        btnOrderNext = findViewById(R.id.btnOrderNext);
        tvOrdersState = findViewById(R.id.tvOrdersState);
        tvOrdersPageInfo = findViewById(R.id.tvOrdersPageInfo);
        lvOrderPayments = findViewById(R.id.lvOrderPayments);
    }

    private void setupStatusSpinner() {
        statusValues = new String[]{"", "PENDING", "AUTHORIZED", "CAPTURED", "FAILED", "VOIDED"};
        String[] statusLabels = new String[]{"All statuses", "Pending", "Authorized", "Captured", "Failed", "Voided"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentStatus.setAdapter(adapter);
    }

    private void setupList() {
        orderPaymentItems = new ArrayList<>();
        orderPaymentAdapter = new OrderPaymentAdapter(this, orderPaymentItems);
        lvOrderPayments.setAdapter(orderPaymentAdapter);
    }

    private void initListeners() {
        btnOrdersBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnOrderSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOrderPayments(0);
            }
        });

        btnOrderRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOrderPayments(currentPage);
            }
        });

        btnOrderPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPrevious) {
                    loadOrderPayments(currentPage - 1);
                }
            }
        });

        btnOrderNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasNext) {
                    loadOrderPayments(currentPage + 1);
                }
            }
        });

        lvOrderPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                OrderPaymentItem item = orderPaymentItems.get(position);
                Intent intent = new Intent(OrdersActivity.this, OrderPaymentDetailActivity.class);
                intent.putExtra(OrderPaymentDetailActivity.EXTRA_TRANSACTION_ID, item.getId());
                startActivity(intent);
            }
        });
    }

    private void loadOrderPayments(final int page) {
        final String status = statusValues[spPaymentStatus.getSelectedItemPosition()];
        final String orderNumber = edtOrderNumberFilter.getText().toString().trim();

        setLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = sessionManager.getAccessToken();
                    ApiResponse response = ApiClient.getOrderPaymentList(token, status, orderNumber, page, PAGE_SIZE);
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        return;
                    }

                    JSONObject object = new JSONObject(response.getBody());
                    final ArrayList<OrderPaymentItem> parsedItems = parseItems(object.optJSONArray("content"));
                    final int parsedPage = object.optInt("page", page);
                    final int parsedTotalPages = object.optInt("totalPages", 0);
                    final boolean parsedHasNext = object.optBoolean("hasNext", false);
                    final boolean parsedHasPrevious = object.optBoolean("hasPrevious", false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showList(parsedItems, parsedPage, parsedTotalPages, parsedHasNext, parsedHasPrevious);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot load transactions. Please try again.");
                }
            }
        });
        thread.start();
    }

    private ArrayList<OrderPaymentItem> parseItems(JSONArray array) throws Exception {
        ArrayList<OrderPaymentItem> items = new ArrayList<>();
        if (array == null) {
            return items;
        }

        for (int i = 0; i < array.length(); i++) {
            items.add(OrderPaymentItem.fromJson(array.getJSONObject(i)));
        }
        return items;
    }

    private void setLoading(final boolean loading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnOrderSearch.setEnabled(!loading);
                btnOrderRefresh.setEnabled(!loading);
                btnOrderPrevious.setEnabled(!loading && hasPrevious);
                btnOrderNext.setEnabled(!loading && hasNext);
                if (loading) {
                    tvOrdersState.setText("Loading transactions...");
                    tvOrdersState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showList(ArrayList<OrderPaymentItem> parsedItems, int parsedPage, int parsedTotalPages, boolean parsedHasNext, boolean parsedHasPrevious) {
        currentPage = parsedPage;
        totalPages = parsedTotalPages;
        hasNext = parsedHasNext;
        hasPrevious = parsedHasPrevious;

        orderPaymentItems.clear();
        orderPaymentItems.addAll(parsedItems);
        orderPaymentAdapter.notifyDataSetChanged();

        btnOrderSearch.setEnabled(true);
        btnOrderRefresh.setEnabled(true);
        btnOrderPrevious.setEnabled(hasPrevious);
        btnOrderNext.setEnabled(hasNext);

        int displayTotalPages = totalPages == 0 ? 1 : totalPages;
        tvOrdersPageInfo.setText("Page " + (currentPage + 1) + " / " + displayTotalPages);

        if (orderPaymentItems.isEmpty()) {
            tvOrdersState.setText("No transactions found.");
            tvOrdersState.setVisibility(View.VISIBLE);
        } else {
            tvOrdersState.setVisibility(View.GONE);
        }
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnOrderSearch.setEnabled(true);
                btnOrderRefresh.setEnabled(true);
                btnOrderPrevious.setEnabled(hasPrevious);
                btnOrderNext.setEnabled(hasNext);
                tvOrdersState.setText(message);
                tvOrdersState.setVisibility(View.VISIBLE);
                Toast.makeText(OrdersActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OrdersActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                sessionManager.clearSession();
                openLogin();
            }
        });
    }
}
