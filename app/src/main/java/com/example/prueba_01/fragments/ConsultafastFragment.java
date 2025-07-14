package com.example.prueba_01.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prueba_01.R;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultafastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultafastFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 100;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConsultafastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultafastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultafastFragment newInstance(String param1, String param2) {
        ConsultafastFragment fragment = new ConsultafastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ImageView imgUploadIcon = requireView().findViewById(R.id.imgUploadIcon);
                        TextView tvUploadText = requireView().findViewById(R.id.tvUploadText);

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                            imgUploadIcon.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
                        }
                        imgUploadIcon.clearColorFilter();
                        tvUploadText.setText("Imagen seleccionada");
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consultafast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout layoutUpload = view.findViewById(R.id.layoutUpload);

        LinearLayout layoutRespuestas = view.findViewById(R.id.layoutRespuestas);

        String[] respuestas = {
                "✅ Puedes sembrar en clima templado.",
                "💡 Usa fertilizante orgánico.",
                "🌱 Regar cada 3 días en verano.",
        };

        for (String respuesta : respuestas) {
            TextView tv = new TextView(getContext());
            tv.setText(respuesta);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_dark_green));
            tv.setTextSize(16);
            tv.setPadding(0, 8, 0, 8);
            layoutRespuestas.addView(tv);
        }

        ImageView imgUploadIcon = view.findViewById(R.id.imgUploadIcon);
        TextView tvUploadText = view.findViewById(R.id.tvUploadText);
        Button btnEnviar = view.findViewById(R.id.btnEnviar);

        layoutUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnEnviar.setOnClickListener(v -> {
            String mensaje = ((EditText) view.findViewById(R.id.etMensaje)).getText().toString();

            // Validación simple
            if (mensaje.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
                // Aquí puedes procesar la imagen seleccionada si se necesita
            }
        });
    }

}