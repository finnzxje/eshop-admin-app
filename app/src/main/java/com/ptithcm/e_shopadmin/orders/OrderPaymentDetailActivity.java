package com.ptithcm.e_shopadmin.orders;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.model.OrderPaymentDetail;
import com.ptithcm.e_shopadmin.model.PaymentCustomer;
import com.ptithcm.e_shopadmin.model.PaymentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class OrderPaymentDetailActivity extends AdminBaseActivity {
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";

    private TextView tvOrderDetailState;
    private TextView tvDetailTitle;
    private TextView tvDetailTransactionId;
    private TextView tvDetailOrderId;
    private TextView tvDetailOrderNumber;
    private TextView tvDetailAmount;
    private TextView tvDetailCapturedAmount;
    private TextView tvDetailStatus;
    private TextView tvDetailMethod;
    private TextView tvDetailProvider;
    private TextView tvDetailProviderTransactionId;
    private TextView tvDetailIdempotencyKey;
    private TextView tvDetailCreatedAt;
    private TextView tvDetailUpdatedAt;
    private TextView tvDetailCustomerName;
    private TextView tvDetailCustomerEmail;
    private TextView tvDetailCustomerId;
    private TextView tvDetailError;
    private TextView tvDetailRawResponse;
    private TextView tvRelatedTransactions;
    private Button btnOrderDetailRefresh;
    private String transactionId;
    private DateTimeFormatter dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        transactionId = getIntent().getStringExtra(EXTRA_TRANSACTION_ID);
        if (transactionId == null || transactionId.trim().isEmpty()) {
            Toast.makeText(this, "Transaction id is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_order_payment_detail);

        dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                .withZone(ZoneId.systemDefault());
        initViews();
        initListeners();
        loadDetail();
    }

    private void initViews() {
        tvOrderDetailState = findViewById(R.id.tvOrderDetailState);
        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailTransactionId = findViewById(R.id.tvDetailTransactionId);
        tvDetailOrderId = findViewById(R.id.tvDetailOrderId);
        tvDetailOrderNumber = findViewById(R.id.tvDetailOrderNumber);
        tvDetailAmount = findViewById(R.id.tvDetailAmount);
        tvDetailCapturedAmount = findViewById(R.id.tvDetailCapturedAmount);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailMethod = findViewById(R.id.tvDetailMethod);
        tvDetailProvider = findViewById(R.id.tvDetailProvider);
        tvDetailProviderTransactionId = findViewById(R.id.tvDetailProviderTransactionId);
        tvDetailIdempotencyKey = findViewById(R.id.tvDetailIdempotencyKey);
        tvDetailCreatedAt = findViewById(R.id.tvDetailCreatedAt);
        tvDetailUpdatedAt = findViewById(R.id.tvDetailUpdatedAt);
        tvDetailCustomerName = findViewById(R.id.tvDetailCustomerName);
        tvDetailCustomerEmail = findViewById(R.id.tvDetailCustomerEmail);
        tvDetailCustomerId = findViewById(R.id.tvDetailCustomerId);
        tvDetailError = findViewById(R.id.tvDetailError);
        tvDetailRawResponse = findViewById(R.id.tvDetailRawResponse);
        tvRelatedTransactions = findViewById(R.id.tvRelatedTransactions);
        btnOrderDetailRefresh = findViewById(R.id.btnOrderDetailRefresh);
    }

    private void initListeners() {
        btnOrderDetailRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDetail();
            }
        });
    }

    private void loadDetail() {
        setLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = sessionManager.getAccessToken();
                    ApiResponse response = ApiClient.getOrderPaymentDetail(token, transactionId);
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        return;
                    }

                    PaymentTransaction transaction = PaymentTransaction.fromJson(new JSONObject(response.getBody()));
                    final OrderPaymentDetail detail = new OrderPaymentDetail();
                    detail.setTransaction(transaction);

                    if (transaction.getOrderNumber() != null && !transaction.getOrderNumber().trim().isEmpty()) {
                        ApiResponse relatedResponse = ApiClient.getOrderTransactions(token, transaction.getOrderNumber());
                        if (relatedResponse.getStatusCode() == 401) {
                            handleUnauthorized();
                            return;
                        }
                        if (relatedResponse.isSuccessful()) {
                            detail.setRelatedTransactions(parseTransactions(relatedResponse.getBody()));
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDetail(detail);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot load transaction detail. Please try again.");
                }
            }
        });
        thread.start();
    }

    private ArrayList<PaymentTransaction> parseTransactions(String body) throws Exception {
        ArrayList<PaymentTransaction> transactions = new ArrayList<>();
        JSONArray array = new JSONArray(body);
        for (int i = 0; i < array.length(); i++) {
            transactions.add(PaymentTransaction.fromJson(array.getJSONObject(i)));
        }
        return transactions;
    }

    private void setLoading(final boolean loading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnOrderDetailRefresh.setEnabled(!loading);
                if (loading) {
                    tvOrderDetailState.setText("Loading transaction detail...");
                    tvOrderDetailState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showDetail(OrderPaymentDetail detail) {
        btnOrderDetailRefresh.setEnabled(true);
        tvOrderDetailState.setVisibility(View.GONE);

        PaymentTransaction transaction = detail.getTransaction();
        PaymentCustomer customer = transaction.getCustomer();

        tvDetailTitle.setText("Transaction: " + emptyToDash(transaction.getOrderNumber()));
        tvDetailTransactionId.setText("Transaction ID: " + emptyToDash(transaction.getId()));
        tvDetailOrderId.setText("Order ID: " + emptyToDash(transaction.getOrderId()));
        tvDetailOrderNumber.setText("Order Number: " + emptyToDash(transaction.getOrderNumber()));
        tvDetailAmount.setText("Amount: " + formatCurrency(transaction.getAmount(), transaction.getCurrency()));
        tvDetailCapturedAmount.setText("Captured Amount: " + formatCurrency(transaction.getCapturedAmount(), transaction.getCurrency()));
        tvDetailStatus.setText("Payment Status: " + emptyToDash(transaction.getStatus()));
        tvDetailMethod.setText("Payment Method: " + emptyToDash(transaction.getMethod()));
        tvDetailProvider.setText("Provider: " + emptyToDash(transaction.getProvider()));
        tvDetailProviderTransactionId.setText("Provider Transaction ID: " + emptyToDash(transaction.getProviderTransactionId()));
        tvDetailIdempotencyKey.setText("Idempotency Key: " + emptyToDash(transaction.getIdempotencyKey()));
        tvDetailCreatedAt.setText("Created At: " + formatDate(transaction.getCreatedAt()));
        tvDetailUpdatedAt.setText("Updated At: " + formatDate(transaction.getUpdatedAt()));

        if (customer == null) {
            tvDetailCustomerName.setText("Customer: -");
            tvDetailCustomerEmail.setText("Email: -");
            tvDetailCustomerId.setText("Customer ID: -");
        } else {
            tvDetailCustomerName.setText("Customer: " + emptyToDash(customer.getDisplayName()));
            tvDetailCustomerEmail.setText("Email: " + emptyToDash(customer.getEmail()));
            tvDetailCustomerId.setText("Customer ID: " + emptyToDash(customer.getId()));
        }

        String errorText = "";
        if (!isEmpty(transaction.getErrorCode())) {
            errorText += "Error Code: " + transaction.getErrorCode();
        }
        if (!isEmpty(transaction.getErrorMessage())) {
            if (!errorText.isEmpty()) {
                errorText += "\n";
            }
            errorText += "Error Message: " + transaction.getErrorMessage();
        }
        tvDetailError.setText(errorText.isEmpty() ? "Error: -" : errorText);

        tvDetailRawResponse.setText("Raw Response: " + emptyToDash(transaction.getRawResponse()));
        tvRelatedTransactions.setText(formatRelatedTransactions(detail.getRelatedTransactions()));
    }

    private String formatRelatedTransactions(ArrayList<PaymentTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "Related Transactions: -";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Related Transactions:");
        for (int i = 0; i < transactions.size(); i++) {
            PaymentTransaction transaction = transactions.get(i);
            builder.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(emptyToDash(transaction.getId()))
                    .append(" | ")
                    .append(emptyToDash(transaction.getStatus()))
                    .append(" | ")
                    .append(formatCurrency(transaction.getAmount(), transaction.getCurrency()));
        }
        return builder.toString();
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnOrderDetailRefresh.setEnabled(true);
                tvOrderDetailState.setText(message);
                tvOrderDetailState.setVisibility(View.VISIBLE);
                Toast.makeText(OrderPaymentDetailActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OrderPaymentDetailActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private String formatCurrency(double value, String currency) {
        try {
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
            format.setCurrency(java.util.Currency.getInstance(currency));
            return format.format(value);
        } catch (Exception ex) {
            return value + " " + emptyToDash(currency);
        }
    }

    private String formatDate(String value) {
        if (isEmpty(value)) {
            return "-";
        }

        try {
            return dateFormatter.format(Instant.parse(value));
        } catch (Exception ex) {
            return value;
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty() || "null".equals(value);
    }

    private String emptyToDash(String value) {
        if (isEmpty(value)) {
            return "-";
        }
        return value;
    }
}
