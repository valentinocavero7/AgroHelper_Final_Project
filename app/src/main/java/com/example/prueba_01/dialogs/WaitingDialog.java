package com.example.prueba_01.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.prueba_01.databinding.WaitingDialogBinding;

public class WaitingDialog extends DialogFragment {
    private WaitingDialogBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = WaitingDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
