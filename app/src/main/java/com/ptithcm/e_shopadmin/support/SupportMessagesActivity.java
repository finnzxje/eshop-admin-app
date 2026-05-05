package com.ptithcm.e_shopadmin.support;

import android.os.Bundle;
import android.view.View;
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
import com.ptithcm.e_shopadmin.model.SendSupportMessageRequest;
import com.ptithcm.e_shopadmin.model.SupportConversation;
import com.ptithcm.e_shopadmin.model.SupportMessage;
import com.ptithcm.e_shopadmin.model.UpdateSupportStatusRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SupportMessagesActivity extends AdminBaseActivity {
    public static final String EXTRA_CONVERSATION_ID = "conversation_id";
    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_CUSTOMER_EMAIL = "customer_email";

    private TextView tvSupportMessagesTitle;
    private TextView tvSupportMessagesMeta;
    private TextView tvSupportMessagesState;
    private Spinner spConversationStatus;
    private Button btnSupportMessagesRefresh;
    private Button btnUpdateConversationStatus;
    private ListView lvSupportMessages;
    private EditText edtSupportMessage;
    private Button btnSendSupportMessage;
    private ArrayList<SupportMessage> messages;
    private SupportMessageAdapter messageAdapter;
    private String conversationId;
    private String subject;
    private String status;
    private String customerEmail;
    private String[] statusValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        if (conversationId == null || conversationId.trim().isEmpty()) {
            Toast.makeText(this, "Conversation id is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        subject = getIntent().getStringExtra(EXTRA_SUBJECT);
        status = getIntent().getStringExtra(EXTRA_STATUS);
        customerEmail = getIntent().getStringExtra(EXTRA_CUSTOMER_EMAIL);

        setContentView(R.layout.activity_support_messages);

        initViews();
        setupStatusSpinner();
        setupMessages();
        initListeners();
        showHeader();
        loadMessages();
    }

    private void initViews() {
        tvSupportMessagesTitle = findViewById(R.id.tvSupportMessagesTitle);
        tvSupportMessagesMeta = findViewById(R.id.tvSupportMessagesMeta);
        tvSupportMessagesState = findViewById(R.id.tvSupportMessagesState);
        spConversationStatus = findViewById(R.id.spConversationStatus);
        btnSupportMessagesRefresh = findViewById(R.id.btnSupportMessagesRefresh);
        btnUpdateConversationStatus = findViewById(R.id.btnUpdateConversationStatus);
        lvSupportMessages = findViewById(R.id.lvSupportMessages);
        edtSupportMessage = findViewById(R.id.edtSupportMessage);
        btnSendSupportMessage = findViewById(R.id.btnSendSupportMessage);
    }

    private void setupStatusSpinner() {
        statusValues = new String[]{"OPEN", "WAITING_CUSTOMER", "WAITING_STAFF", "CLOSED"};
        String[] labels = new String[]{"Open", "Waiting Customer", "Waiting Staff", "Closed"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConversationStatus.setAdapter(adapter);

        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i].equals(status)) {
                spConversationStatus.setSelection(i);
                break;
            }
        }
    }

    private void setupMessages() {
        messages = new ArrayList<>();
        messageAdapter = new SupportMessageAdapter(this, messages);
        lvSupportMessages.setAdapter(messageAdapter);
    }

    private void initListeners() {
        btnSupportMessagesRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMessages();
            }
        });

        btnSendSupportMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        btnUpdateConversationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus();
            }
        });
    }

    private void showHeader() {
        tvSupportMessagesTitle.setText(emptyToDash(subject));
        tvSupportMessagesMeta.setText("Customer: " + emptyToDash(customerEmail) + " | Status: " + emptyToDash(status));
    }

    private void loadMessages() {
        setLoading(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.getSupportMessages(sessionManager.getAccessToken(), conversationId);
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        return;
                    }

                    final ArrayList<SupportMessage> parsedMessages = parseMessages(response.getBody());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMessages(parsedMessages);
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot load messages. Please try again.");
                }
            }
        });
        thread.start();
    }

    private ArrayList<SupportMessage> parseMessages(String body) throws Exception {
        ArrayList<SupportMessage> parsedMessages = new ArrayList<>();
        JSONArray array = new JSONArray(body);
        for (int i = 0; i < array.length(); i++) {
            parsedMessages.add(SupportMessage.fromJson(array.getJSONObject(i)));
        }
        return parsedMessages;
    }

    private void sendMessage() {
        final String body = edtSupportMessage.getText().toString().trim();
        if (body.isEmpty()) {
            edtSupportMessage.setError("Please enter a message");
            edtSupportMessage.requestFocus();
            return;
        }

        setSending(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SendSupportMessageRequest request = new SendSupportMessageRequest(body);
                    ApiResponse response = ApiClient.sendSupportMessage(sessionManager.getAccessToken(), conversationId, request.toJson());
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        setSending(false);
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edtSupportMessage.setText("");
                            Toast.makeText(SupportMessagesActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                            setSending(false);
                            loadMessages();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot send message. Please try again.");
                    setSending(false);
                }
            }
        });
        thread.start();
    }

    private void updateStatus() {
        final String selectedStatus = statusValues[spConversationStatus.getSelectedItemPosition()];
        setUpdatingStatus(true);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UpdateSupportStatusRequest request = new UpdateSupportStatusRequest(selectedStatus);
                    ApiResponse response = ApiClient.updateSupportConversationStatus(sessionManager.getAccessToken(), conversationId, request.toJson());
                    if (response.getStatusCode() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    if (!response.isSuccessful()) {
                        showError(response.getErrorMessage());
                        setUpdatingStatus(false);
                        return;
                    }

                    SupportConversation updatedConversation = SupportConversation.fromJson(new JSONObject(response.getBody()));
                    status = updatedConversation.getStatus();
                    if (updatedConversation.getSubject() != null && !updatedConversation.getSubject().trim().isEmpty()) {
                        subject = updatedConversation.getSubject();
                    }
                    if (updatedConversation.getCustomer() != null && updatedConversation.getCustomer().getEmail() != null) {
                        customerEmail = updatedConversation.getCustomer().getEmail();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showHeader();
                            setUpdatingStatus(false);
                            Toast.makeText(SupportMessagesActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                            loadMessages();
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Cannot update status. Please try again.");
                    setUpdatingStatus(false);
                }
            }
        });
        thread.start();
    }

    private void setLoading(final boolean loading) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSupportMessagesRefresh.setEnabled(!loading);
                if (loading) {
                    tvSupportMessagesState.setText("Loading messages...");
                    tvSupportMessagesState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setSending(final boolean sending) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSendSupportMessage.setEnabled(!sending);
                edtSupportMessage.setEnabled(!sending);
            }
        });
    }

    private void setUpdatingStatus(final boolean updating) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnUpdateConversationStatus.setEnabled(!updating);
                spConversationStatus.setEnabled(!updating);
            }
        });
    }

    private void showMessages(ArrayList<SupportMessage> parsedMessages) {
        btnSupportMessagesRefresh.setEnabled(true);
        messages.clear();
        messages.addAll(parsedMessages);
        messageAdapter.notifyDataSetChanged();

        if (messages.isEmpty()) {
            tvSupportMessagesState.setText("No messages in this conversation.");
            tvSupportMessagesState.setVisibility(View.VISIBLE);
        } else {
            tvSupportMessagesState.setVisibility(View.GONE);
            lvSupportMessages.setSelection(messages.size() - 1);
        }
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSupportMessagesRefresh.setEnabled(true);
                tvSupportMessagesState.setText(message);
                tvSupportMessagesState.setVisibility(View.VISIBLE);
                Toast.makeText(SupportMessagesActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUnauthorized() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SupportMessagesActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                sessionManager.clearSession();
                openLogin();
            }
        });
    }

    private String emptyToDash(String value) {
        if (value == null || value.trim().isEmpty() || "null".equals(value)) {
            return "-";
        }
        return value;
    }
}
