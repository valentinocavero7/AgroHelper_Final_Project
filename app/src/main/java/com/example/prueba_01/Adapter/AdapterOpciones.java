package com.example.prueba_01.Adapter;

import android.content.Context;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.prueba_01.R;
import com.example.prueba_01.databinding.ItemPreguntaListBinding;
import com.example.prueba_01.modelo.Opcion;

import java.util.ArrayList;

public class AdapterOpciones extends ArrayAdapter {
    private ItemPreguntaListBinding binding;
    private Context context;
    private ArrayList<Opcion> datos;
    private opcionSeleccionadaListenter listenter;

    public interface opcionSeleccionadaListenter {
        void opcionSeleccionada(int pos);
    }

    public AdapterOpciones(@NonNull Context context, ArrayList<Opcion> datos, opcionSeleccionadaListenter listenter) {
        super(context, 0, datos);
        this.context = context;
        this.datos = datos;
        this.listenter = listenter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemPreguntaListBinding binding;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            binding = ItemPreguntaListBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemPreguntaListBinding) convertView.getTag();
        }

        Opcion opcion = datos.get(position);
        binding.radioOpcion.setText(opcion.getDescripcion());
        binding.radioOpcion.setChecked(opcion.getSeleccionada());

        binding.radioOpcion.setOnClickListener(v -> {
            for (Opcion o : datos) {
                o.setSeleccionada(false);
            }
            opcion.setSeleccionada(true);
            notifyDataSetChanged();
            if(listenter != null) {
                listenter.opcionSeleccionada(position);
            }
        });

        return convertView;
    }
}
