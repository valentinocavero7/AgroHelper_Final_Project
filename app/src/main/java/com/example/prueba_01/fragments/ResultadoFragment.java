package com.example.prueba_01.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prueba_01.R;
import com.example.prueba_01.databinding.FragmentResultadoBinding;

public class ResultadoFragment extends Fragment {
    private FragmentResultadoBinding binding;
    private String respuesta;

    public static ResultadoFragment newInstance(String respuesta) {

        Bundle args = new Bundle();
        args.putString("respuesta", respuesta);
        ResultadoFragment fragment = new ResultadoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ResultadoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultadoBinding.inflate(inflater, container, false);
        respuesta = getArguments().getString("respuesta");
        binding.textResultado.setText(respuesta);

        return binding.getRoot();
    }
}