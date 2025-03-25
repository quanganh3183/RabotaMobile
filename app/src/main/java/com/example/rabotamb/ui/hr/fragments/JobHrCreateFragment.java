package com.example.rabotamb.ui.hr.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rabotamb.R;
import com.example.rabotamb.api.ApiClient;
import com.example.rabotamb.data.models.job.JobHr;
import com.example.rabotamb.data.models.job.Level;
import com.example.rabotamb.data.models.job.Location;
import com.example.rabotamb.data.models.skill.Skill;
import com.example.rabotamb.data.models.skill.SkillResponse;
import com.example.rabotamb.ui.viewmodel.JobHrViewModel;
import com.example.rabotamb.ui.viewmodel.JobsHrViewModelFactory;
import com.example.rabotamb.utils.DateUtils;
import com.example.rabotamb.utils.UserPreferences;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobHrCreateFragment extends Fragment {
    private static final String TAG = "JobHrCreateFragment";
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MIN_DESCRIPTION_LENGTH = 10;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_SALARY = 1000000000;
    private static final int MAX_QUANTITY = 1000;
    private static final int MAX_MONTHS_DURATION = 6;

    private JobHrViewModel viewModel;
    private EditText nameEdit;
    private MultiAutoCompleteTextView skillsEdit;
    private Spinner locationSpinner;
    private EditText quantityEdit;
    private EditText salaryEdit;
    private Spinner levelSpinner;
    private EditText startDateEdit;
    private EditText endDateEdit;
    private TextInputEditText descriptionEdit;
    private ProgressBar progressBar;
    private Button submitButton;

    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private List<Skill> skillsList = new ArrayList<>();
    private Set<Skill> selectedSkills = new HashSet<>();
    private ArrayAdapter<Skill> skillsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_hr_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupViewModel();
        setupDatePickers();
        setupLocationSpinner();
        setupLevelSpinner();
        setupSkillsAutoComplete();
        setupTextWatchers();
    }

    private void setupSkillsAutoComplete() {
        skillsAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.skill_item,
                skillsList
        );

        skillsEdit.setAdapter(skillsAdapter);
        skillsEdit.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        skillsEdit.setThreshold(1);

        skillsEdit.setOnItemClickListener((parent, view, position, id) -> {
            Skill selectedSkill = (Skill) parent.getItemAtPosition(position);
            if (!selectedSkills.contains(selectedSkill)) {
                selectedSkills.add(selectedSkill);
                updateSkillsDisplay();
            }
        });

        skillsEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    selectedSkills.clear();
                }
            }
        });

        loadSkills();
    }

    private void updateSkillsDisplay() {
        StringBuilder displayText = new StringBuilder();
        List<Skill> sortedSkills = new ArrayList<>(selectedSkills);
        for (int i = 0; i < sortedSkills.size(); i++) {
            displayText.append(sortedSkills.get(i).getName());
            if (i < sortedSkills.size() - 1) {
                displayText.append(", ");
            }
        }
        skillsEdit.setText(displayText.toString());
        skillsEdit.setSelection(displayText.length());

        Log.d(TAG, "Selected skills: " + new Gson().toJson(sortedSkills));
    }

    private void loadSkills() {
        ApiClient.getInstance().getSkills(1, 100).enqueue(new Callback<SkillResponse>() {
            @Override
            public void onResponse(Call<SkillResponse> call, Response<SkillResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getData() != null &&
                        response.body().getData().getResult() != null) {

                    skillsList.clear();
                    skillsList.addAll(response.body().getData().getResult());
                    skillsAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded skills: " + new Gson().toJson(skillsList));
                }
            }

            @Override
            public void onFailure(Call<SkillResponse> call, Throwable t) {
                Toast.makeText(requireContext(),
                        "Không thể tải danh sách kỹ năng",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading skills", t);
            }
        });
    }

    private void initViews(View view) {
        nameEdit = view.findViewById(R.id.editJobName);
        skillsEdit = view.findViewById(R.id.editJobSkills);
        locationSpinner = view.findViewById(R.id.spinnerJobLocation);
        quantityEdit = view.findViewById(R.id.editJobQuantity);
        salaryEdit = view.findViewById(R.id.editJobSalary);
        levelSpinner = view.findViewById(R.id.spinnerJobLevel);
        startDateEdit = view.findViewById(R.id.editJobStartDate);
        endDateEdit = view.findViewById(R.id.editJobEndDate);
        descriptionEdit = view.findViewById(R.id.editJobDescription);
        progressBar = view.findViewById(R.id.progressBar);
        submitButton = view.findViewById(R.id.submitButton);

        quantityEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
        salaryEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

        startDateEdit.setFocusable(false);
        startDateEdit.setClickable(true);
        endDateEdit.setFocusable(false);
        endDateEdit.setClickable(true);

        submitButton.setOnClickListener(v -> createJob());
    }

    private void setupViewModel() {
        JobsHrViewModelFactory factory = new JobsHrViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, factory).get(JobHrViewModel.class);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                submitButton.setEnabled(!isLoading);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error, null);
                viewModel.clearError();
            }
        });

        viewModel.getIsCreated().observe(getViewLifecycleOwner(), isCreated -> {
            if (isCreated != null && isCreated) {
                Toast.makeText(requireContext(), "Tạo việc làm mới thành công", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setupDatePickers() {
        DatePickerDialog.OnDateSetListener startDateListener = (view, year, month, day) -> {
            startDateCalendar.set(Calendar.YEAR, year);
            startDateCalendar.set(Calendar.MONTH, month);
            startDateCalendar.set(Calendar.DAY_OF_MONTH, day);
            startDateEdit.setText(displayFormat.format(startDateCalendar.getTime()));
            endDateEdit.setText("");
        };

        DatePickerDialog.OnDateSetListener endDateListener = (view, year, month, day) -> {
            endDateCalendar.set(Calendar.YEAR, year);
            endDateCalendar.set(Calendar.MONTH, month);
            endDateCalendar.set(Calendar.DAY_OF_MONTH, day);
            endDateEdit.setText(displayFormat.format(endDateCalendar.getTime()));
        };

        startDateEdit.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    startDateListener,
                    startDateCalendar.get(Calendar.YEAR),
                    startDateCalendar.get(Calendar.MONTH),
                    startDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            dialog.show();
        });

        endDateEdit.setOnClickListener(v -> {
            if (startDateEdit.getText().toString().isEmpty()) {
                showError("Vui lòng chọn ngày bắt đầu trước", startDateEdit);
                return;
            }

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    endDateListener,
                    endDateCalendar.get(Calendar.YEAR),
                    endDateCalendar.get(Calendar.MONTH),
                    endDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
            dialog.show();
        });
    }

    private void setupLocationSpinner() {
        ArrayAdapter<Location> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Location.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);
    }

    private void setupLevelSpinner() {
        ArrayAdapter<Level> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Level.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(adapter);
    }

    private void setupTextWatchers() {
        TextWatcher numberWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.isEmpty()) {
                    try {
                        if (input.length() > 1 && input.startsWith("0")) {
                            s.replace(0, s.length(), input.substring(1));
                            return;
                        }
                        if (input.contains(".") || input.contains(",")) {
                            s.replace(0, s.length(), input.replaceAll("[.,]", ""));
                            Toast.makeText(requireContext(), "Vui lòng nhập số nguyên", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        quantityEdit.addTextChangedListener(numberWatcher);
        salaryEdit.addTextChangedListener(numberWatcher);
    }

    private boolean validateData(String name, String skills, String quantity, String salary,
                                 String startDate, String endDate, String description) {
        if (name.isEmpty() || name.length() < MIN_NAME_LENGTH) {
            showError("Tên công việc phải có ít nhất " + MIN_NAME_LENGTH + " ký tự", nameEdit);
            return false;
        }

        if (selectedSkills.isEmpty()) {
            showError("Vui lòng chọn ít nhất một kỹ năng", skillsEdit);
            return false;
        }

        try {
            int quantityValue = Integer.parseInt(quantity);
            if (quantityValue <= 0 || quantityValue > MAX_QUANTITY) {
                showError("Số lượng phải từ 1 đến " + MAX_QUANTITY, quantityEdit);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Số lượng không hợp lệ", quantityEdit);
            return false;
        }

        try {
            long salaryValue = Long.parseLong(salary);
            if (salaryValue <= 0 || salaryValue > MAX_SALARY) {
                showError("Lương phải từ 1 đến " + MAX_SALARY, salaryEdit);
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Lương không hợp lệ", salaryEdit);
            return false;
        }

        if (startDate.isEmpty() || endDate.isEmpty()) {
            showError("Vui lòng chọn ngày bắt đầu và kết thúc", startDateEdit);
            return false;
        }

        if (description.isEmpty() || description.length() < MIN_DESCRIPTION_LENGTH) {
            showError("Mô tả công việc phải có ít nhất " + MIN_DESCRIPTION_LENGTH + " ký tự", descriptionEdit);
            return false;
        }

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            showError("Mô tả công việc không được vượt quá " + MAX_DESCRIPTION_LENGTH + " ký tự", descriptionEdit);
            return false;
        }

        return true;
    }

    private void createJob() {
        String name = nameEdit.getText().toString().trim();
        String skillsText = skillsEdit.getText().toString().trim();
        Location location = (Location) locationSpinner.getSelectedItem();
        String quantity = quantityEdit.getText().toString().trim();
        String salary = salaryEdit.getText().toString().trim().replaceAll("[.,]", "");
        Level level = (Level) levelSpinner.getSelectedItem();
        String startDate = startDateEdit.getText().toString().trim();
        String endDate = endDateEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();

        if (!validateData(name, skillsText, quantity, salary, startDate, endDate, description)) {
            return;
        }

        try {
            UserPreferences userPreferences = new UserPreferences(requireContext());
            UserPreferences.UserInfo userInfo = userPreferences.getUserInfo();

            if (userInfo == null || userInfo.getCompany() == null) {
                showError("Không tìm thấy thông tin công ty", null);
                return;
            }

            JobHr newJob = new JobHr();
            newJob.setName(name);
            List<Skill> skillsList = new ArrayList<>(selectedSkills);
            newJob.setSkills(skillsList);
            newJob.setLocation(location.name());
            newJob.setQuantity(Integer.parseInt(quantity));
            newJob.setSalary(Integer.parseInt(salary));
            newJob.setLevel(level.name());

            // Format dates for API
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'", Locale.getDefault());
            Date startDateParsed = displayFormat.parse(startDate);
            Date endDateParsed = displayFormat.parse(endDate);

            newJob.setStartDate(apiFormat.format(startDateParsed));
            newJob.setEndDate(apiFormat.format(endDateParsed));

            newJob.setDescription(description);
            JobHr.Company company = new JobHr.Company();
            company.set_id(userInfo.getCompany().get_id());
            company.setName(userInfo.getCompany().getName());
            company.setLogo(userInfo.getCompany().getLogo());
            newJob.setCompany(company);
            newJob.setActive(true);

            Log.d(TAG, "Creating job with data: " + new Gson().toJson(newJob));
            viewModel.createJob(newJob);
        } catch (Exception e) {
            showError("Có lỗi xảy ra: " + e.getMessage(), null);
            Log.e(TAG, "Error creating job", e);
        }
    }

    private void showError(String message, EditText field) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        if (field != null) {
            field.requestFocus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        skillsList.clear();
        selectedSkills.clear();
        if (skillsAdapter != null) {
            skillsAdapter.clear();
        }
    }
}