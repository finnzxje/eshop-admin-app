package com.ptithcm.e_shopadmin.support;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.model.SupportConversation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupportActivity extends AdminBaseActivity {
    private static final int PAGE_SIZE = 20;
    private Spinner spSupportListType;
    private Spinner spSupportStatus;
    private Button btnSupportRefresh;
    private Button btnSupportPrevious;
    private Button btnSupportNext;
    private TextView tvSupportState;
    private TextView tvSupportPageInfo;
    private ListView lvSupportConversations;
    private ArrayList<SupportConversation> conversations;
    private SupportConversationAdapter conversationAdapter;
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

        setContentView(R.layout.activity_support);

        initViews();
        setupSpinners();
        setupList();
        initListeners();
        loadConversations(0);
    }

    private void initViews() {
        spSupportListType = findViewById(R.id.spSupportListType);
        spSupportStatus = findViewById(R.id.spSupportStatus);
        btnSupportRefresh = findViewById(R.id.btnSupportRefresh);
        btnSupportPrevious = findViewById(R.id.btnSupportPrevious);
        btnSupportNext = findViewById(R.id.btnSupportNext);
        tvSupportState = findViewById(R.id.tvSupportState);
        tvSupportPageInfo = findViewById(R.id.tvSupportPageInfo);
        lvSupportConversations = findViewById(R.id.lvSupportConversations);
    }

    private void setupSpinners() {
        String[] listLabels = new String[]{"Inbox", "Assigned to me"};
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listLabels);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSupportListType.setAdapter(listAdapter);

        statusValues = new String[]{"", "OPEN", "WAITING_CUSTOMER", "WAITING_STAFF", "CLOSED"};
        String[] statusLabels = new String[]{"Active default", "Open", "Waiting Customer", "Waiting Staff", "Closed"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSupportStatus.setAdapter(statusAdapter);
    }

    private void setupList() {
        conversations = new ArrayList<>();
        conversationAdapter = new SupportConversationAdapter(this, conversations);
        lvSupportConversations.setAdapter(conversationAdapter);
    }

    private void initListeners() {
        btnSupportRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadConversations(currentPage);
            }
        });

        btnSupportPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPrevious) {
                    loadConversations(currentPage - 1);
                }
            }
        });

        btnSupportNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasNext) {
                    loadConversations(currentPage + 1);
                }
            }
        });

        spSupportListType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spSupportStatus != null) {
                    spSupportStatus.setEnabled(position == 0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        lvSupportConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SupportConversation conversation = conversations.get(position);
                Intent intent = new Intent(SupportActivity.this, SupportMessagesActivity.class);
                intent.putExtra(SupportMessagesActivity.EXTRA_CONVERSATION_ID, conversation.getId());
                intent.putExtra(SupportMessagesActivity.EXTRA_SUBJECT, conversation.getSubject());
                intent.putExtra(SupportMessagesActivity.EXTRA_STATUS, conversation.getStatus());
                if (conversation.getCustomer() != null) {
                    intent.putExtra(SupportMessagesActivity.EXTRA_CUSTOMER_EMAIL, conversation.getCustomer().getEmail());
                }
                startActivity(intent);
            }
        });
    }

    private void loadConversations(final int page) {
        final boolean assignedOnly = spSupportListType.getSelectedItemPosition() == 1;
        final String status = assignedOnly ? "" : statusValues[spSupportStatus.getSelectedItemPosition()];
        setLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.getSupportConversations(sessionManager.getAccessToken(), assignedOnly, status, page, PAGE_SIZE);
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        return;
                    }

                    JSONObject object = new JSONObject(response.getBody());
                    final ArrayList<SupportConversation> parsedConversations = parseConversations(object.optJSONArray("content"));
                    final int parsedPage = object.optInt("page", page);
                    final int parsedTotalPages = object.optInt("totalPages", 0);
                    final boolean parsedHasNext = object.optBoolean("hasNext", false);
                    final boolean parsedHasPrevious = object.optBoolean("hasPrevious", false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showConversations(parsedConversations, parsedPage, parsedTotalPages, parsedHasNext, parsedHasPrevious);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot load support conversations. Please try again.");
                }
            }
        });
        thread.start();
    }

    private ArrayList<SupportConversation> parseConversations(JSONArray array) throws Exception {
        ArrayList<SupportConversation> items = new ArrayList<>();
        if (array == null) {
            return items;
        }

        for (int i = 0; i < array.length(); i++) {
            items.add(SupportConversation.fromJson(array.getJSONObject(i)));
        }
        return items;
    }

    private void setLoading(final boolean loading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSupportRefresh.setEnabled(!loading);
                btnSupportPrevious.setEnabled(!loading && hasPrevious);
                btnSupportNext.setEnabled(!loading && hasNext);
                if (loading) {
                    tvSupportState.setText("Loading conversations...");
                    tvSupportState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void showConversations(ArrayList<SupportConversation> parsedConversations, int parsedPage, int parsedTotalPages, boolean parsedHasNext, boolean parsedHasPrevious) {
        currentPage = parsedPage;
        totalPages = parsedTotalPages;
        hasNext = parsedHasNext;
        hasPrevious = parsedHasPrevious;

        conversations.clear();
        conversations.addAll(parsedConversations);
        conversationAdapter.notifyDataSetChanged();

        btnSupportRefresh.setEnabled(true);
        btnSupportPrevious.setEnabled(hasPrevious);
        btnSupportNext.setEnabled(hasNext);

        int displayTotalPages = totalPages == 0 ? 1 : totalPages;
        tvSupportPageInfo.setText("Page " + (currentPage + 1) + " / " + displayTotalPages);

        if (conversations.isEmpty()) {
            tvSupportState.setText("No support conversations found.");
            tvSupportState.setVisibility(View.VISIBLE);
        } else {
            tvSupportState.setVisibility(View.GONE);
        }
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSupportRefresh.setEnabled(true);
                btnSupportPrevious.setEnabled(hasPrevious);
                btnSupportNext.setEnabled(hasNext);
                tvSupportState.setText(message);
                tvSupportState.setVisibility(View.VISIBLE);
                Toast.makeText(SupportActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SupportActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                sessionManager.clearSession();
                openLogin();
            }
        });
    }
}
