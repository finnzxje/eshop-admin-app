package com.ptithcm.e_shopadmin.products;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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
import com.ptithcm.e_shopadmin.adapter.ProductImageAdapter;
import com.ptithcm.e_shopadmin.common.AdminBaseActivity;
import com.ptithcm.e_shopadmin.common.ApiClient;
import com.ptithcm.e_shopadmin.common.ApiResponse;
import com.ptithcm.e_shopadmin.model.Color;
import com.ptithcm.e_shopadmin.model.ProductImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductMediaActivity extends AdminBaseActivity {
    public static final String EXTRA_PRODUCT_ID = "productId";
    private static final int REQUEST_PICK_IMAGE = 3001;

    private TextView tvMediaStatus;
    private TextView tvSelectedImageFile;
    private TextView tvSelectedProductImage;
    private EditText edtImageAltText;
    private EditText edtImageDisplayOrder;
    private Spinner spImageColor;
    private CheckBox chkImagePrimary;
    private Button btnBackToProductForm;
    private Button btnChooseImage;
    private Button btnUploadImage;
    private Button btnUpdateImage;
    private Button btnDeleteImage;
    private ListView lvProductImages;

    private ArrayList<Color> colorList = new ArrayList<>();
    private ArrayList<ProductImage> imageList = new ArrayList<>();
    private ArrayAdapter<String> colorAdapter;
    private ProductImageAdapter imageAdapter;
    private ProductImage selectedProductImage;
    private Uri selectedImageUri;
    private String selectedImageFileName = "";
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

        setContentView(R.layout.activity_product_media);

        initViews();
        setupColorSpinner();
        setupImageList();
        initListeners();
        setSelectedProductImage(null);
        loadColorsAndImages();
    }

    private void initViews() {
        tvMediaStatus = findViewById(R.id.tvMediaStatus);
        tvSelectedImageFile = findViewById(R.id.tvSelectedImageFile);
        tvSelectedProductImage = findViewById(R.id.tvSelectedProductImage);
        edtImageAltText = findViewById(R.id.edtImageAltText);
        edtImageDisplayOrder = findViewById(R.id.edtImageDisplayOrder);
        spImageColor = findViewById(R.id.spImageColor);
        chkImagePrimary = findViewById(R.id.chkImagePrimary);
        btnBackToProductForm = findViewById(R.id.btnBackToProductForm);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnUpdateImage = findViewById(R.id.btnUpdateImage);
        btnDeleteImage = findViewById(R.id.btnDeleteImage);
        lvProductImages = findViewById(R.id.lvProductImages);
    }

    private void setupColorSpinner() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("No color assigned");
        colorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spImageColor.setAdapter(colorAdapter);
    }

    private void setupImageList() {
        imageAdapter = new ProductImageAdapter(this, imageList);
        lvProductImages.setAdapter(imageAdapter);
    }

    private void initListeners() {
        btnBackToProductForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose product image"), REQUEST_PICK_IMAGE);
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        btnUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSelectedImage();
            }
        });

        btnDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedImage();
            }
        });

        lvProductImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                setSelectedProductImage(imageList.get(position));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            selectedImageFileName = getFileName(selectedImageUri);
            tvSelectedImageFile.setText("Selected: " + selectedImageFileName);
        }
    }

    private void loadColorsAndImages() {
        setLoading(true, "Loading media...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse colorsResponse = ApiClient.get("/api/admin/catalog/colors", sessionManager.getAccessToken());
                    if (!colorsResponse.isSuccessful()) {
                        handleRequestFailure(colorsResponse, "Could not load colors.");
                        return;
                    }

                    ApiResponse productResponse = ApiClient.get("/api/admin/catalog/products/" + productId, sessionManager.getAccessToken());
                    if (!productResponse.isSuccessful()) {
                        handleRequestFailure(productResponse, "Could not load product images.");
                        return;
                    }

                    parseColors(colorsResponse.getBody());
                    parseImages(productResponse.getBody());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateColorSpinner();
                            imageAdapter.notifyDataSetChanged();
                            setSelectedProductImage(null);
                            setLoading(false, "");
                            if (imageList.isEmpty()) {
                                tvMediaStatus.setText("No images yet.");
                                tvMediaStatus.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not load media. Please try again.");
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

    private void parseImages(String body) throws Exception {
        JSONObject product = new JSONObject(body);
        JSONArray images = product.optJSONArray("images");

        imageList.clear();
        if (images != null) {
            for (int i = 0; i < images.length(); i++) {
                imageList.add(ProductImage.fromJson(images.getJSONObject(i)));
            }
        }
    }

    private void updateColorSpinner() {
        colorAdapter.clear();
        colorAdapter.add("No color assigned");
        for (int i = 0; i < colorList.size(); i++) {
            colorAdapter.add(colorList.get(i).getDisplayName());
        }
        colorAdapter.notifyDataSetChanged();
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please choose an image file.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String altText = edtImageAltText.getText().toString().trim();
        final String displayOrder = getDisplayOrderText();
        final boolean primary = chkImagePrimary.isChecked();
        final int colorId = getSelectedColorId();
        final Uri imageUri = selectedImageUri;
        final String fileName = selectedImageFileName;

        setLoading(true, "Uploading image...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.uploadProductImage(sessionManager.getAccessToken(), productId,
                            imageUri, fileName, altText, displayOrder, primary, colorId);
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearImageForm();
                                Toast.makeText(ProductMediaActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadColorsAndImages();
                    } else {
                        handleRequestFailure(response, "Could not upload image.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not upload image.");
                }
            }
        });
        thread.start();
    }

    private void updateSelectedImage() {
        if (selectedProductImage == null) {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProductImage image = selectedProductImage;
        final String altText = edtImageAltText.getText().toString().trim();
        final int displayOrder = parseDisplayOrder();
        final boolean primary = chkImagePrimary.isChecked();
        final int colorId = getSelectedColorId();

        setLoading(true, "Updating image...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("altText", altText);
                    body.put("displayOrder", displayOrder);
                    body.put("primary", primary);
                    if (colorId > 0) {
                        body.put("colorId", colorId);
                    } else {
                        body.put("colorId", JSONObject.NULL);
                    }

                    ApiResponse response = ApiClient.patchJson("/api/admin/catalog/products/" + productId
                            + "/images/" + image.getId(), body, sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProductMediaActivity.this, "Image updated.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadColorsAndImages();
                    } else {
                        handleRequestFailure(response, "Could not update image.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not update image.");
                }
            }
        });
        thread.start();
    }

    private void deleteSelectedImage() {
        if (selectedProductImage == null) {
            Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProductImage image = selectedProductImage;
        setLoading(true, "Deleting image...");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ApiResponse response = ApiClient.delete("/api/admin/catalog/products/" + productId
                            + "/images/" + image.getId(), sessionManager.getAccessToken());
                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProductMediaActivity.this, "Image deleted.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadColorsAndImages();
                    } else {
                        handleRequestFailure(response, "Could not delete image.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Could not delete image.");
                }
            }
        });
        thread.start();
    }

    private void setSelectedProductImage(ProductImage image) {
        selectedProductImage = image;
        boolean hasImage = selectedProductImage != null;
        btnUpdateImage.setEnabled(hasImage);
        btnDeleteImage.setEnabled(hasImage);

        if (!hasImage) {
            tvSelectedProductImage.setText("Tap an image row to update metadata or delete it.");
            return;
        }

        tvSelectedProductImage.setText("Selected image: " + selectedProductImage.getImageUrl());
        edtImageAltText.setText(selectedProductImage.getAltText());
        edtImageDisplayOrder.setText(String.valueOf(selectedProductImage.getDisplayOrder()));
        chkImagePrimary.setChecked(selectedProductImage.isPrimary());
        selectColor(selectedProductImage.getColor());
    }

    private void selectColor(Color color) {
        if (color == null) {
            spImageColor.setSelection(0);
            return;
        }

        for (int i = 0; i < colorList.size(); i++) {
            if (colorList.get(i).getId() == color.getId()) {
                spImageColor.setSelection(i + 1);
                return;
            }
        }
        spImageColor.setSelection(0);
    }

    private int getSelectedColorId() {
        int position = spImageColor.getSelectedItemPosition();
        if (position <= 0 || position - 1 >= colorList.size()) {
            return 0;
        }
        return colorList.get(position - 1).getId();
    }

    private String getDisplayOrderText() {
        String value = edtImageDisplayOrder.getText().toString().trim();
        if (value.isEmpty()) {
            return "0";
        }
        return value;
    }

    private int parseDisplayOrder() {
        String value = edtImageDisplayOrder.getText().toString().trim();
        if (value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private void clearImageForm() {
        selectedImageUri = null;
        selectedImageFileName = "";
        tvSelectedImageFile.setText("No file selected.");
        edtImageAltText.setText("");
        edtImageDisplayOrder.setText("");
        spImageColor.setSelection(0);
        chkImagePrimary.setChecked(false);
    }

    private String getFileName(Uri uri) {
        if (uri == null) {
            return "image.jpg";
        }

        String result = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
            } finally {
                cursor.close();
            }
        }

        if (result == null || result.trim().isEmpty()) {
            result = "image.jpg";
        }
        return result;
    }

    private void handleRequestFailure(ApiResponse response, String fallbackMessage) {
        if (response.getStatusCode() == 401) {
            sessionManager.clearSession();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ProductMediaActivity.this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
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
                tvMediaStatus.setText(message);
                tvMediaStatus.setVisibility(View.VISIBLE);
                Toast.makeText(ProductMediaActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean loading, String message) {
        btnBackToProductForm.setEnabled(!loading);
        btnChooseImage.setEnabled(!loading);
        btnUploadImage.setEnabled(!loading);
        btnUpdateImage.setEnabled(!loading && selectedProductImage != null);
        btnDeleteImage.setEnabled(!loading && selectedProductImage != null);

        if (loading) {
            tvMediaStatus.setText(message);
            tvMediaStatus.setVisibility(View.VISIBLE);
        } else if (message == null || message.trim().isEmpty()) {
            tvMediaStatus.setText("");
            tvMediaStatus.setVisibility(View.GONE);
        }
    }
}
