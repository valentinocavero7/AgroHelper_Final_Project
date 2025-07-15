package com.example.prueba_01.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
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

import com.example.prueba_01.PlantId.ApiCliente;
import com.example.prueba_01.PlantId.PlantIdService;
import com.example.prueba_01.R;
import com.example.prueba_01.databinding.FragmentConsultafastBinding;
import com.example.prueba_01.databinding.FragmentResultadoBinding;
import com.example.prueba_01.dialogs.WaitingAnswerDialog;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultafastFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 100;
    private Uri selectedImageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FragmentConsultafastBinding binding;
    private WaitingAnswerDialog dialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConsultafastFragment() {
    }

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
        binding = FragmentConsultafastBinding.inflate(inflater, container, false);
        dialog = new WaitingAnswerDialog();
        return binding.getRoot();
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
                if(selectedImageUri != null) {
                    dialog = WaitingAnswerDialog.newInstance("Estamos analizando la imagen...");
                    dialog.show(getParentFragmentManager(), "WAITING_ANSWER_DIALOG");
                    analizarSaludPlanta(selectedImageUri);
                }
            }

        });
    }

    private void analizarSaludPlanta(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            JSONObject json = new JSONObject();
            JSONArray imagesArray = new JSONArray();
            imagesArray.put(base64Image);
            json.put("images", imagesArray);
            json.put("organs", new JSONArray().put("leaf")); // hoja por defecto

            JSONObject modifiers = new JSONObject();
            modifiers.put("disease", true);
            json.put("modifiers", modifiers);

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

            PlantIdService service = ApiCliente.getClient().create(PlantIdService.class);
            Call<Map<String, Object>> call = service.analizarSaludPlanta(body);

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        mostrarDiagnostico(response.body());
                    } else {
                        Toast.makeText(getContext(), "Error al analizar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error interno al procesar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDiagnostico(Map<String, Object> data) {
        try {
            if (!data.containsKey("health_assessment") || data.get("health_assessment") == null) {
                Toast.makeText(getContext(), "La API no devolvió resultados de evaluación de salud", Toast.LENGTH_LONG).show();
                return;
            }

            Map<String, Object> assessment = (Map<String, Object>) data.get("health_assessment");
            List<Map<String, Object>> diseases = (List<Map<String, Object>>) assessment.get("diseases");

            if (diseases.isEmpty()) {
                Toast.makeText(getContext(), "No se detectaron enfermedades visibles", Toast.LENGTH_LONG).show();
                return;
            }

            StringBuilder resultado = new StringBuilder();
            for (Map<String, Object> enfermedad : diseases) {
                String nombre = (String) enfermedad.get("name");
                double score = 0.0;

                Object probObj = enfermedad.get("probability");
                if (probObj instanceof Number) {
                    score = ((Number) probObj).doubleValue();
                }

                Map<String, Object> diseaseDetails = (Map<String, Object>) enfermedad.get("disease_details");
                String localName = diseaseDetails != null ? (String) diseaseDetails.get("local_name") : null;

                resultado.append("🦠 Enfermedad: ").append(localName != null ? localName : nombre != null ? nombre : "Desconocida").append("\n");
                resultado.append("🔍 Confianza: ").append(String.format("%.2f%%", score * 100)).append("\n");
            }

            obtenerTratamientoDesdeGemini(resultado.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ERROR", e.getMessage());
            Toast.makeText(getContext(), "Error al mostrar resultados", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerTratamientoDesdeGemini(String enfermedadesLista) {
        String apiKey = "AIzaSyBn1YGNly3F8X_Jz_YYTm_pTY-kmq-6S68";

        GenerativeModel model = new GenerativeModel(
                "gemini-2.5-flash-lite-preview-06-17",
                apiKey
        );

        GenerativeModelFutures modelFutures = GenerativeModelFutures.from(model);
        Executor executor = Executors.newSingleThreadExecutor();

        String prompt = "Sugiere un tratamiento para las siguientes tres enfermedades más graves que se haya encontrado en el diagnostico:\n"
                + enfermedadesLista + "\n\n"
                + "Responde de manera breve y concisa, en una respuesta máxima de un parrafo. La respuesta debe ser en tono amigable";

        Content content = new Content.Builder().addText(prompt).build();

        Futures.addCallback(modelFutures.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String respuesta = result.getText();
                requireActivity().runOnUiThread(() -> {
                    mostrarTratamientoSugerido(respuesta);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error al consultar Gemini: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }, executor);
    }

    private void mostrarTratamientoSugerido(String texto) {
        dialog.dismiss();
        binding.tvResultado.setText(parseMarkdownToBold(texto));
    }

    // esto es para pnoer en negrita los textos, pero creo que no funciona, pero al menos no sale : **Hola**
    public static Spannable parseMarkdownToBold(String markdownText) {
        SpannableString spannable = new SpannableString(markdownText);
        Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher matcher = pattern.matcher(markdownText);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            spannable.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    start + 2,
                    end - 2,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        return new SpannableString(spannable.toString().replaceAll("\\*\\*", ""));
    }


}