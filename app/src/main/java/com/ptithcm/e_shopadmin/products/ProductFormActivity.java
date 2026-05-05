package com.ptithcm.e_shopadmin.products;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ptithcm.e_shopadmin.R;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.model.Category;
import com.ptithcm.e_shopadmin.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ProductFormActivity extends AdminBaseActivity {
    public static final String EXTRA_PRODUCT_ID = "productId";

    private TextView tvProductFormTitle;
    private TextView tvProductFormStatus;
    private EditText edtProductName;
    private EditText edtProductSlug;
    private EditText edtProductDescription;
    private EditText edtProductBasePrice;
    private EditText edtProductType;
    private EditText edtProductTags;
    private Spinner spProductCategory;
    private Spinner spProductFormStatus;
    private Spinner spProductGender;
    private CheckBox chkProductFeatured;
    private Button btnSaveProduct;
    private Button btnManageVariants;
    private Button btnManageMedia;
    private Button btnCancelProductForm;

    private ArrayList<Category> categoryList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private String productId;
    private boolean editMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_product_form);

        productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        editMode = productId != null && !productId.trim().isEmpty();

        initViews();
        setupStaticSpinners();
        setupCategorySpinner();
        initListeners();
        showInitialState();
        loadFormData();
    }

    private void initViews() {
        tvProductFormTitle = findViewById(R.id.tvProductFormTitle);
        tvProductFormStatus = findViewById(R.id.tvProductFormStatus);
        edtProductName = findViewById(R.id.edtProductName);
        edtProductSlug = findViewById(R.id.edtProductSlug);
        edtProductDescription = findViewById(R.id.edtProductDescription);
        edtProductBasePrice = findViewById(R.id.edtProductBasePrice);
        edtProductType = findViewById(R.id.edtProductType);
        edtProductTags = findViewById(R.id.edtProductTags);
        spProductCategory = findViewById(R.id.spProductCategory);
        spProductFormStatus = findViewById(R.id.spProductFormStatus);
        spProductGender = findViewById(R.id.spProductGender);
        chkProductFeatured = findViewById(R.id.chkProductFeatured);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnManageVariants = findViewById(R.id.btnManageVariants);
        btnManageMedia = findViewById(R.id.btnManageMedia);
        btnCancelProductForm = findViewById(R.id.btnCancelProductForm);
    }

    private void setupStaticSpinners() {
        String[] statusLabels = {"Draft", "Active", "Archived"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductFormStatus.setAdapter(statusAdapter);

        String[] genderLabels = {"Unisex", "Men's", "Women's"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genderLabels);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductGender.setAdapter(genderAdapter);
    }

    private void setupCategorySpinner() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("-- Select a category --");
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductCategory.setAdapter(categoryAdapter);
    }

    private void initListeners() {
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
            }
        });

        btnCancelProductForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnManageVariants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductFormActivity.this, ProductVariantsActivity.class);
                intent.putExtra(ProductVariantsActivity.EXTRA_PRODUCT_ID, productId);
                startActivity(intent);
            }
        });

        btnManageMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductFormActivity.this, ProductMediaActivity.class);
                intent.putExtra(ProductMediaActivity.EXTRA_PRODUCT_ID, productId);
                startActivity(intent);
            }
        });
    }

    private void showInitialState() {
        if (editMode) {
            tvProductFormTitle.setText("Edit Product");
            btnSaveProduct.setText("Save Changes");
            btnManageVariants.setVisibility(View.VISIBLE);
            btnManageMedia.setVisibility(View.VISIBLE);
        } else {
            tvProductFormTitle.setText("Create Product");
            btnSaveProduct.setText("Create Product");
            btnManageVariants.setVisibility(View.GONE);
            btnManageMedia.setVisibility(View.GONE);
            edtProductType.setText("tops");
        }
        spProductFormStatus.setSelection(0);
        spProductGender.setSelection(0);
    }

    private void loadFormData() {
        setLoading(true, "Loading form data...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse categoriesResponse = ApiClient.get("/api/catalog/categories", sessionManager.getAccessToken());
                    if (!categoriesResponse.isSuccessful()) {
                        handleRequestFailure(categoriesResponse, "Could not load category list.");
                        return;
                    }

                    parseCategories(categoriesResponse.getBody());

                    Product product = null;
                    if (editMode) {
                        ApiResponse productResponse = ApiClient.get("/api/admin/catalog/products/" + productId, sessionManager.getAccessToken());
                        if (!productResponse.isSuccessful()) {
                            handleRequestFailure(productResponse, "Could not load product.");
                            return;
                        }
                        product = Product.fromJson(new JSONObject(productResponse.getBody()));
                    }

                    final Product loadedProduct = product;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateCategorySpinner();
                            if (loadedProduct != null) {
                                fillProductForm(loadedProduct);
                            }
                            setLoading(false, "");
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load product form. Please try again.");
                }
            }
        });
        thread.start();
    }

    private void parseCategories(String body) throws Exception {
        JSONArray array = new JSONArray(body);
        categoryList.clear();
        for (int i = 0; i < array.length(); i++) {
            categoryList.add(Category.fromJson(array.getJSONObject(i)));
        }
    }

    private void updateCategorySpinner() {
        categoryAdapter.clear();
        categoryAdapter.add("-- Select a category --");
        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            categoryAdapter.add(category.getName());
        }
        categoryAdapter.notifyDataSetChanged();
    }

    private void fillProductForm(Product product) {
        edtProductName.setText(product.getName());
        edtProductSlug.setText(product.getSlug());
        edtProductDescription.setText(product.getDescription());
        edtProductBasePrice.setText(String.format(Locale.US, "%.2f", product.getBasePrice()));
        edtProductType.setText(product.getProductType());
        chkProductFeatured.setChecked(product.isFeatured());
        selectStatus(product.getStatus());
        selectGender(product.getGender());
        selectCategory(product.getCategoryId());
        edtProductTags.setText(joinTags(product.getTags()));
    }

    private void selectStatus(String status) {
        String value = status == null ? "" : status.toUpperCase(Locale.US);
        if ("ACTIVE".equals(value)) {
            spProductFormStatus.setSelection(1);
        } else if ("ARCHIVED".equals(value)) {
            spProductFormStatus.setSelection(2);
        } else {
            spProductFormStatus.setSelection(0);
        }
    }

    private void selectGender(String gender) {
        String value = gender == null ? "" : gender.toLowerCase(Locale.US);
        if ("mens".equals(value)) {
            spProductGender.setSelection(1);
        } else if ("womens".equals(value)) {
            spProductGender.setSelection(2);
        } else {
            spProductGender.setSelection(0);
        }
    }

    private void selectCategory(int categoryId) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == categoryId) {
                spProductCategory.setSelection(i + 1);
                return;
            }
        }
        spProductCategory.setSelection(0);
    }

    private String joinTags(ArrayList<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(tags.get(i));
        }
        return builder.toString();
    }

    private void saveProduct() {
        try {
            if (!validateForm()) {
                return;
            }

            final JSONObject payload = buildPayload();
            setLoading(true, editMode ? "Saving product..." : "Creating product...");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ApiResponse response;
                        if (editMode) {
                            response = ApiClient.putJson("/api/admin/catalog/products/" + productId, payload, sessionManager.getAccessToken());
                        } else {
                            response = ApiClient.postJson("/api/admin/catalog/products", payload, sessionManager.getAccessToken());
                        }

                        if (response.isSuccessful()) {
                            showSaveSuccess();
                        } else {
                            handleRequestFailure(response, "Could not save product.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showError("Could not save product. Please try again.");
                    }
                }
            });
            thread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Please check the form values.");
        }
    }

    private boolean validateForm() {
        if (edtProductName.getText().toString().trim().isEmpty()) {
            edtProductName.setError("Product name is required");
            edtProductName.requestFocus();
            return false;
        }

        if (edtProductSlug.getText().toString().trim().isEmpty()) {
            edtProductSlug.setError("Slug is required");
            edtProductSlug.requestFocus();
            return false;
        }

        if (edtProductBasePrice.getText().toString().trim().isEmpty()) {
            edtProductBasePrice.setError("Base price is required");
            edtProductBasePrice.requestFocus();
            return false;
        }

        if (getSelectedCategoryId() == 0) {
            Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private JSONObject buildPayload() throws Exception {
        JSONObject object = new JSONObject();
        object.put("name", edtProductName.getText().toString().trim());
        object.put("slug", edtProductSlug.getText().toString().trim());
        object.put("description", edtProductDescription.getText().toString().trim());
        object.put("basePrice", Double.parseDouble(edtProductBasePrice.getText().toString().trim()));
        object.put("categoryId", getSelectedCategoryId());
        object.put("status", getSelectedStatusValue());
        object.put("featured", chkProductFeatured.isChecked());
        object.put("gender", getSelectedGenderValue());
        object.put("productType", edtProductType.getText().toString().trim());
        object.put("tags", buildTagsArray());
        return object;
    }

    private int getSelectedCategoryId() {
        int position = spProductCategory.getSelectedItemPosition();
        if (position <= 0 || position - 1 >= categoryList.size()) {
            return 0;
        }
        return categoryList.get(position - 1).getId();
    }

    private String getSelectedStatusValue() {
        int position = spProductFormStatus.getSelectedItemPosition();
        if (position == 1) {
            return "active";
        }
        if (position == 2) {
            return "archived";
        }
        return "draft";
    }

    private String getSelectedGenderValue() {
        int position = spProductGender.getSelectedItemPosition();
        if (position == 1) {
            return "mens";
        }
        if (position == 2) {
            return "womens";
        }
        return "unisex";
    }

    private JSONArray buildTagsArray() {
        JSONArray array = new JSONArray();
        String tagsText = edtProductTags.getText().toString().trim();
        if (tagsText.isEmpty()) {
            return array;
        }

        String[] parts = tagsText.split(",");
        for (int i = 0; i < parts.length; i++) {
            String tag = parts[i].trim();
            if (!tag.isEmpty()) {
                array.put(tag);
            }
        }
        return array;
    }

    private void handleRequestFailure(ApiResponse response, String fallbackMessage) {
        if (response.getStatusCode() == 401) {
            sessionManager.clearSession();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ProductFormActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
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

    private void showSaveSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                Toast.makeText(ProductFormActivity.this, editMode ? "Product updated." : "Product created.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                tvProductFormStatus.setText(message);
                tvProductFormStatus.setVisibility(View.VISIBLE);
                Toast.makeText(ProductFormActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading, String message) {
        btnSaveProduct.setEnabled(!loading);
        btnManageVariants.setEnabled(!loading);
        btnManageMedia.setEnabled(!loading);
        btnCancelProductForm.setEnabled(!loading);

        if (loading) {
            tvProductFormStatus.setText(message);
            tvProductFormStatus.setVisibility(View.VISIBLE);
        } else if (message == null || message.trim().isEmpty()) {
            tvProductFormStatus.setText("");
            tvProductFormStatus.setVisibility(View.GONE);
        }
    }
}
