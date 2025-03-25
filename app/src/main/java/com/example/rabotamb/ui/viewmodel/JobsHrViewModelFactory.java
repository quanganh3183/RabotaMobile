package com.example.rabotamb.ui.viewmodel;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class JobsHrViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public JobsHrViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(JobHrViewModel.class)) {
            return (T) new JobHrViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}