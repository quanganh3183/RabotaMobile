package com.example.rabotamb.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.history.ApplicationHistoryResponse;
import com.example.rabotamb.data.models.history.ApplicationItem;
import com.example.rabotamb.ui.adapter.ApplicationHistoryAdapter;
import com.example.rabotamb.ui.auth.LoginActivity;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicationHistoryActivity extends AppCompatActivity {
    private static final String TAG = "ApplicationHistory";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ApplicationHistoryAdapter adapter;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_history);

        userPreferences = new UserPreferences(this);

        if (!userPreferences.isLoggedIn()) {
            handleUnauthorized("Vui lòng đăng nhập để xem lịch sử ứng tuyển");
            return;
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadApplicationHistory();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerview);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);

        swipeRefresh.setOnRefreshListener(this::loadApplicationHistory);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lịch sử ứng tuyển");
        }
    }

    private void setupRecyclerView() {
        adapter = new ApplicationHistoryAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadApplicationHistory() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        String token = userPreferences.getToken();
        if (token == null) {
            handleUnauthorized("Phiên đăng nhập đã hết hạn");
            return;
        }

        Log.d(TAG, "Token: " + token);

        // Tạo body rỗng cho POST request
        Map<String, Object> body = new HashMap<>();

        ApiClient.getInstance()
                .getApplicationHistory(token, body)
                .enqueue(new Callback<ApplicationHistoryResponse>() {
                    @Override
                    public void onResponse(Call<ApplicationHistoryResponse> call, Response<ApplicationHistoryResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {
                            handleSuccessResponse(response.body().getData());
                        } else {
                            handleErrorResponse(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApplicationHistoryResponse> call, Throwable t) {
                        handleFailure(t);
                    }
                });
    }

    private void handleSuccessResponse(List<ApplicationItem> applications) {
        if (applications == null || applications.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            emptyView.setText("Chưa có lịch sử ứng tuyển");
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setApplications(applications);
        }
    }

    private void handleErrorResponse(Response<ApplicationHistoryResponse> response) {
        try {
            String errorMessage;
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e(TAG, "Error body: " + errorBody);

                JSONObject errorJson = new JSONObject(errorBody);
                errorMessage = errorJson.optString("message", "Unknown error");
            } else {
                errorMessage = "Unknown error";
            }

            if (response.code() == 401) {
                handleUnauthorized(errorMessage);
            } else {
                showError("Lỗi: " + errorMessage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error response", e);
            showError("Có lỗi xảy ra");
        }
    }

    private void handleFailure(Throwable t) {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        Log.e(TAG, "API call failed", t);
        showError("Lỗi kết nối: " + t.getMessage());
    }

    private void handleUnauthorized(String message) {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);

        userPreferences.clearAll();

        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        loginIntent.putExtra("message", message);
        startActivity(loginIntent);
        finish();
    }

    private void showError(String message) {
        if (recyclerView != null) {
            Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show();
        }
    }

}