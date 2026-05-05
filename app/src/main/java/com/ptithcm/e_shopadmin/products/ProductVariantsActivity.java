package com.ptithcm.e_shopadmin.products;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.adapter.ProductVariantAdapter;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.model.Color;
import com.ptithcm.e_shopadmin.model.ProductVariant;
import com.ptithcm.e_shopadmin.model.StockAdjustment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductVariantsActivity extends AdminBaseActivity {
    public static final String EXTRA_PRODUCT_ID = "productId";

    private TextView tvVariantsStatus;
    private TextView tvSelectedVariant;
    private TextView tvStockAdjustments;
    private Spinner spVariantColor;
    private EditText edtVariantSize;
    private EditText edtVariantSku;
    private EditText edtVariantPrice;
    private EditText edtVariantQuantity;
    private EditText edtStockNewQuantity;
    private EditText edtStockReason;
    private EditText edtStockNotes;
    private CheckBox chkVariantActive;
    private Button btnBackToProductForm;
    private Button btnCreateVariant;
    private Button btnToggleVariantStatus;
    private Button btnAdjustStock;
    private ListView lvProductVariants;

    private ArrayList<Color> colorList = new ArrayList<>();
    private ArrayList<ProductVariant> variantList = new ArrayList<>();
    private ArrayAdapter<String> colorAdapter;
    private ProductVariantAdapter variantAdapter;
    private ProductVariant selectedVariant;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId == null || productId.trim().isEmpty()) {
            Toast.makeText(this, "Missing product ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_product_variants);

        initViews();
        setupColorSpinner();
        setupVariantList();
        initListeners();
        setSelectedVariant(null);
        loadColorsAndVariants();
    }

    private void initViews() {
        tvVariantsStatus = findViewById(R.id.tvVariantsStatus);
        tvSelectedVariant = findViewById(R.id.tvSelectedVariant);
        tvStockAdjustments = findViewById(R.id.tvStockAdjustments);
        spVariantColor = findViewById(R.id.spVariantColor);
        edtVariantSize = findViewById(R.id.edtVariantSize);
        edtVariantSku = findViewById(R.id.edtVariantSku);
        edtVariantPrice = findViewById(R.id.edtVariantPrice);
        edtVariantQuantity = findViewById(R.id.edtVariantQuantity);
        edtStockNewQuantity = findViewById(R.id.edtStockNewQuantity);
        edtStockReason = findViewById(R.id.edtStockReason);
        edtStockNotes = findViewById(R.id.edtStockNotes);
        chkVariantActive = findViewById(R.id.chkVariantActive);
        btnBackToProductForm = findViewById(R.id.btnBackToProductForm);
        btnCreateVariant = findViewById(R.id.btnCreateVariant);
        btnToggleVariantStatus = findViewById(R.id.btnToggleVariantStatus);
        btnAdjustStock = findViewById(R.id.btnAdjustStock);
        lvProductVariants = findViewById(R.id.lvProductVariants);
    }

    private void setupColorSpinner() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("-- Select color --");
        colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVariantColor.setAdapter(colorAdapter);
    }

    private void setupVariantList() {
        variantAdapter = new ProductVariantAdapter(this, variantList);
        lvProductVariants.setAdapter(variantAdapter);
    }

    private void initListeners() {
        btnBackToProductForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnCreateVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createVariant();
            }
        });

        btnToggleVariantStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVariantStatus();
            }
        });

        btnAdjustStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adjustStock();
            }
        });

        lvProductVariants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                setSelectedVariant(variantList.get(position));
                loadStockAdjustments();
            }
        });
    }

    private void loadColorsAndVariants() {
        setLoading(true, "Loading variants...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse colorsResponse = ApiClient.get("/api/admin/catalog/colors", sessionManager.getAccessToken());
                    if (!colorsResponse.isSuccessful()) {
                        handleRequestFailure(colorsResponse, "Could not load color list.");
                        return;
                    }

                    ApiResponse variantsResponse = ApiClient.get("/api/admin/catalog/products/" + productId + "/variants", sessionManager.getAccessToken());
                    if (!variantsResponse.isSuccessful()) {
                        handleRequestFailure(variantsResponse, "Could not load variants.");
                        return;
                    }

                    parseColors(colorsResponse.getBody());
                    parseVariants(variantsResponse.getBody());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateColorSpinner();
                            variantAdapter.notifyDataSetChanged();
                            setSelectedVariant(null);
                            setLoading(false, "");
                            if (variantList.isEmpty()) {
                                tvVariantsStatus.setText("No variants yet.");
                                tvVariantsStatus.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load variants. Please try again.");
                }
            }
        });
        thread.start();
    }

    private void parseColors(String body) throws Exception {
        JSONArray array = new JSONArray(body);
        colorList.clear();
        for (int i = 0; i < array.length(); i++) {
            colorList.add(Color.fromJson(array.getJSONObject(i)));
        }
    }

    private void parseVariants(String body) throws Exception {
        JSONArray array = new JSONArray(body);
        variantList.clear();
        for (int i = 0; i < array.length(); i++) {
            variantList.add(ProductVariant.fromJson(array.getJSONObject(i)));
        }
    }

    private void updateColorSpinner() {
        colorAdapter.clear();
        colorAdapter.add("-- Select color --");
        for (int i = 0; i < colorList.size(); i++) {
            colorAdapter.add(colorList.get(i).getDisplayName());
        }
        colorAdapter.notifyDataSetChanged();
    }

    private void createVariant() {
        if (!validateVariantForm()) {
            return;
        }

        final int colorId = getSelectedColorId();
        final String size = edtVariantSize.getText().toString().trim();
        final String sku = edtVariantSku.getText().toString().trim();
        final double price = Double.parseDouble(edtVariantPrice.getText().toString().trim());
        final int quantity = Integer.parseInt(edtVariantQuantity.getText().toString().trim());
        final boolean active = chkVariantActive.isChecked();

        setLoading(true, "Creating variant...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject variant = new JSONObject();
                    variant.put("size", size);
                    variant.put("sku", sku);
                    variant.put("price", price);
                    variant.put("quantity", quantity);
                    variant.put("active", active);
                    variant.put("currency", "USD");

                    JSONArray variants = new JSONArray();
                    variants.put(variant);

                    JSONObject body = new JSONObject();
                    body.put("colorId", colorId);
                    body.put("variants", variants);

                    ApiResponse response = ApiClient.postJson("/api/admin/catalog/products/" + productId + "/variants", body, sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearVariantForm();
                                Toast.makeText(ProductVariantsActivity.this, "Variant created.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadColorsAndVariants();
                    } else {
                        handleRequestFailure(response, "Could not create variant.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not create variant.");
                }
            }
        });
        thread.start();
    }

    private boolean validateVariantForm() {
        if (getSelectedColorId() == 0) {
            Toast.makeText(this, "Please select a color.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtVariantSize.getText().toString().trim().isEmpty()) {
            edtVariantSize.setError("Size is required");
            edtVariantSize.requestFocus();
            return false;
        }
        if (edtVariantSku.getText().toString().trim().isEmpty()) {
            edtVariantSku.setError("SKU is required");
            edtVariantSku.requestFocus();
            return false;
        }
        if (edtVariantPrice.getText().toString().trim().isEmpty()) {
            edtVariantPrice.setError("Price is required");
            edtVariantPrice.requestFocus();
            return false;
        }
        if (edtVariantQuantity.getText().toString().trim().isEmpty()) {
            edtVariantQuantity.setError("Quantity is required");
            edtVariantQuantity.requestFocus();
            return false;
        }
        return true;
    }

    private int getSelectedColorId() {
        int position = spVariantColor.getSelectedItemPosition();
        if (position <= 0 || position - 1 >= colorList.size()) {
            return 0;
        }
        return colorList.get(position - 1).getId();
    }

    private void clearVariantForm() {
        spVariantColor.setSelection(0);
        edtVariantSize.setText("");
        edtVariantSku.setText("");
        edtVariantPrice.setText("");
        edtVariantQuantity.setText("");
        chkVariantActive.setChecked(true);
    }

    private void setSelectedVariant(ProductVariant variant) {
        selectedVariant = variant;
        boolean hasVariant = selectedVariant != null;
        btnToggleVariantStatus.setEnabled(hasVariant);
        btnAdjustStock.setEnabled(hasVariant);

        if (!hasVariant) {
            tvSelectedVariant.setText("Tap a variant to manage status and stock.");
            tvStockAdjustments.setText("Stock adjustment history appears after selecting a variant.");
            return;
        }

        tvSelectedVariant.setText("Selected: " + selectedVariant.getColorName()
                + " / Size " + selectedVariant.getSize()
                + " / SKU " + selectedVariant.getVariantSku());
        edtStockNewQuantity.setText(String.valueOf(selectedVariant.getQuantityInStock()));
        btnToggleVariantStatus.setText(selectedVariant.isActive() ? "Mark Variant Inactive" : "Mark Variant Active");
    }

    private void toggleVariantStatus() {
        if (selectedVariant == null) {
            Toast.makeText(this, "Please select a variant first.", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProductVariant variant = selectedVariant;
        setLoading(true, "Updating variant status...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("active", !variant.isActive());

                    ApiResponse response = ApiClient.patchJson("/api/admin/catalog/products/" + productId
                            + "/variants/" + variant.getId() + "/status", body, sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProductVariantsActivity.this, "Variant status updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadColorsAndVariants();
                    } else {
                        handleRequestFailure(response, "Could not update variant status.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not update variant status.");
                }
            }
        });
        thread.start();
    }

    private void adjustStock() {
        if (selectedVariant == null) {
            Toast.makeText(this, "Please select a variant first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (edtStockNewQuantity.getText().toString().trim().isEmpty()) {
            edtStockNewQuantity.setError("New quantity is required");
            edtStockNewQuantity.requestFocus();
            return;
        }

        if (edtStockReason.getText().toString().trim().isEmpty()) {
            edtStockReason.setError("Reason is required");
            edtStockReason.requestFocus();
            return;
        }

        final ProductVariant variant = selectedVariant;
        final int newQuantity = Integer.parseInt(edtStockNewQuantity.getText().toString().trim());
        final String reason = edtStockReason.getText().toString().trim();
        final String notes = edtStockNotes.getText().toString().trim();

        setLoading(true, "Adjusting stock...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("newQuantity", newQuantity);
                    body.put("reason", reason);
                    body.put("notes", notes);

                    ApiResponse response = ApiClient.postJson("/api/admin/catalog/products/" + productId
                            + "/variants/" + variant.getId() + "/stock-adjustments", body, sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                edtStockReason.setText("");
                                edtStockNotes.setText("");
                                Toast.makeText(ProductVariantsActivity.this, "Stock adjusted.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadColorsAndVariants();
                    } else {
                        handleRequestFailure(response, "Could not adjust stock.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not adjust stock.");
                }
            }
        });
        thread.start();
    }

    private void loadStockAdjustments() {
        if (selectedVariant == null) {
            return;
        }

        final ProductVariant variant = selectedVariant;
        tvStockAdjustments.setText("Loading stock adjustment history...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.get("/api/admin/catalog/products/" + productId
                            + "/variants/" + variant.getId() + "/stock-adjustments", sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        final String text = buildAdjustmentsText(response.getBody());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvStockAdjustments.setText(text);
                            }
                        });
                    } else {
                        handleRequestFailure(response, "Could not load stock adjustments.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load stock adjustments.");
                }
            }
        });
        thread.start();
    }

    private String buildAdjustmentsText(String body) throws Exception {
        JSONArray array = new JSONArray(body);
        if (array.length() == 0) {
            return "No stock adjustments for this variant.";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            StockAdjustment adjustment = StockAdjustment.fromJson(array.getJSONObject(i));
            if (i > 0) {
                builder.append("\n\n");
            }
            builder.append(adjustment.getSummary());
        }
        return builder.toString();
    }

    private void handleRequestFailure(ApiResponse response, String fallbackMessage) {
        if (response.getStatusCode() == 401) {
            sessionManager.clearSession();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ProductVariantsActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                    openLogin();
                }
            });
        } else {
            String message = response.getErrorMessage();
            if (message == null || message.trim().isEmpty()) {
                message = fallbackMessage;
            }
            showError(message);
        }
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                tvVariantsStatus.setText(message);
                tvVariantsStatus.setVisibility(View.VISIBLE);
                Toast.makeText(ProductVariantsActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading, String message) {
        btnBackToProductForm.setEnabled(!loading);
        btnCreateVariant.setEnabled(!loading);
        btnToggleVariantStatus.setEnabled(!loading && selectedVariant != null);
        btnAdjustStock.setEnabled(!loading && selectedVariant != null);

        if (loading) {
            tvVariantsStatus.setText(message);
            tvVariantsStatus.setVisibility(View.VISIBLE);
        } else if (message == null || message.trim().isEmpty()) {
            tvVariantsStatus.setText("");
            tvVariantsStatus.setVisibility(View.GONE);
        }
    }
}
