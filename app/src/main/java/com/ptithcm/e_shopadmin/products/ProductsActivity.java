package com.ptithcm.e_shopadmin.products;

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
import com.ptithcm.e_shopadmin.adapter.ProductAdapter;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class ProductsActivity extends AdminBaseActivity {
    private EditText edtProductSearch;
    private Spinner spProductStatus;
    private Button btnSearchProducts;
    private Button btnPreviousPage;
    private Button btnNextPage;
    private TextView tvProductListStatus;
    private TextView tvProductPageInfo;
    private ListView lvProducts;
    private ArrayList<Product> productList;
    private ProductAdapter productAdapter;
    private int currentPage = 0;
    private int totalPages = 1;
    private int pageSize = 10;
    private String selectedStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!requireAdminSession()) {
            return;
        }

        setContentView(R.layout.activity_products);

        initViews();
        setupStatusSpinner();
        setupProductList();
        initListeners();
        loadProducts();
    }

    private void initViews() {
        edtProductSearch = findViewById(R.id.edtProductSearch);
        spProductStatus = findViewById(R.id.spProductStatus);
        btnSearchProducts = findViewById(R.id.btnSearchProducts);
        btnPreviousPage = findViewById(R.id.btnPreviousPage);
        btnNextPage = findViewById(R.id.btnNextPage);
        tvProductListStatus = findViewById(R.id.tvProductListStatus);
        tvProductPageInfo = findViewById(R.id.tvProductPageInfo);
        lvProducts = findViewById(R.id.lvProducts);
    }

    private void setupStatusSpinner() {
        String[] statusLabels = {"All statuses", "Active", "Draft", "Archived"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusLabels);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductStatus.setAdapter(statusAdapter);
    }

    private void setupProductList() {
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(productAdapter);
    }

    private void initListeners() {
        spProductStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    selectedStatus = "";
                } else if (position == 1) {
                    selectedStatus = "ACTIVE";
                } else if (position == 2) {
                    selectedStatus = "DRAFT";
                } else {
                    selectedStatus = "ARCHIVED";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSearchProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage = 0;
                loadProducts();
            }
        });

        btnPreviousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 0) {
                    currentPage--;
                    loadProducts();
                }
            }
        });

        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage + 1 < totalPages) {
                    currentPage++;
                    loadProducts();
                }
            }
        });

        lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Product product = productList.get(position);
                Toast.makeText(ProductsActivity.this, product.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProducts() {
        setLoading(true, "Loading products...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = buildProductsPath();
                    ApiResponse response = ApiClient.get(path, sessionManager.getAccessToken());

                    if (response.isSuccessful()) {
                        parseProducts(response.getBody());
                        showProducts();
                    } else if (response.getStatusCode() == 401) {
                        sessionManager.clearSession();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProductsActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                                openLogin();
                            }
                        });
                    } else if (response.getStatusCode() == 403) {
                        showError("Access denied. Product management requires ADMIN access.");
                    } else {
                        showError(response.getErrorMessage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load products. Please try again.");
                }
            }
        });
        thread.start();
    }

    private String buildProductsPath() throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("/api/admin/catalog/products?");
        builder.append("page=").append(currentPage);
        builder.append("&size=").append(pageSize);
        builder.append("&sort=").append(URLEncoder.encode("updatedAt,desc", "UTF-8"));

        String search = edtProductSearch.getText().toString().trim();
        if (!search.isEmpty()) {
            builder.append("&search=").append(URLEncoder.encode(search, "UTF-8"));
        }

        if (!selectedStatus.isEmpty()) {
            builder.append("&status=").append(URLEncoder.encode(selectedStatus, "UTF-8"));
        }

        return builder.toString();
    }

    private void parseProducts(String body) throws Exception {
        JSONObject object = new JSONObject(body);
        JSONArray content = object.optJSONArray("content");

        productList.clear();
        if (content != null) {
            for (int i = 0; i < content.length(); i++) {
                JSONObject productObject = content.getJSONObject(i);
                productList.add(Product.fromJson(productObject));
            }
        }

        totalPages = object.optInt("totalPages", 1);
        currentPage = object.optInt("page", currentPage);
        if (totalPages < 1) {
            totalPages = 1;
        }
    }

    private void showProducts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                productAdapter.notifyDataSetChanged();
                updatePageButtons();

                if (productList.isEmpty()) {
                    tvProductListStatus.setText("No products found.");
                    tvProductListStatus.setVisibility(View.VISIBLE);
                } else {
                    tvProductListStatus.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showError(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoading(false, "");
                productList.clear();
                productAdapter.notifyDataSetChanged();
                tvProductListStatus.setText(message);
                tvProductListStatus.setVisibility(View.VISIBLE);
                updatePageButtons();
            }
        });
    }

    private void setLoading(boolean loading, String message) {
        btnSearchProducts.setEnabled(!loading);
        btnPreviousPage.setEnabled(!loading && currentPage > 0);
        btnNextPage.setEnabled(!loading && currentPage + 1 < totalPages);

        if (loading) {
            tvProductListStatus.setText(message);
            tvProductListStatus.setVisibility(View.VISIBLE);
        }
    }

    private void updatePageButtons() {
        tvProductPageInfo.setText("Page " + (currentPage + 1) + " of " + totalPages);
        btnPreviousPage.setEnabled(currentPage > 0);
        btnNextPage.setEnabled(currentPage + 1 < totalPages);
    }
}
