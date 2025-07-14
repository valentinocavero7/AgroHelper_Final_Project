package com.example.prueba_01.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.prueba_01.Adapter.AdapterOpciones;
import com.example.prueba_01.dialogs.WaitingAnswerDialog;
import com.example.prueba_01.dialogs.WaitingDialog;
import com.example.prueba_01.modelo.Opcion;
import com.example.prueba_01.modelo.Pregunta;
import com.example.prueba_01.R;
import com.example.prueba_01.databinding.FragmentEvaluacionBinding;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class EvaluacionFragment extends Fragment implements View.OnClickListener {

    private FragmentEvaluacionBinding binding;
    private ArrayList<Pregunta> preguntas = new ArrayList<>();
    private int numberPregunta = 0;
    private RadioGroup opcionesGroup;
    private int opcionSelected = -1;
    private WaitingDialog dialog;
    private WaitingAnswerDialog dialog2;

    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EvaluacionFragment() {
        // Required empty public constructor
    }

    public static EvaluacionFragment newInstance(String param1, String param2) {
        EvaluacionFragment fragment = new EvaluacionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        String apiKey = "AIzaSyBn1YGNly3F8X_Jz_YYTm_pTY-kmq-6S68";
        GenerativeModel gm = new GenerativeModel(
                /* modelName */ "gemini-2.5-flash-lite-preview-06-17",
                /* apiKey */ apiKey
        );
        model = GenerativeModelFutures.from(gm);
        dialog = new WaitingDialog();
        dialog2 = new WaitingAnswerDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEvaluacionBinding.inflate(inflater, container, false);
        CargarPreguntas();
        opcionesGroup = binding.radioGroup;
        mostrarPregunta();

        binding.lvRespuestas.setOnItemClickListener((parent, view, pos, id) -> {
            opcionSelected = pos;
            Toast.makeText(getContext(), pos, Toast.LENGTH_SHORT).show();
        });

        binding.btnNext.setOnClickListener(v -> {
            if(numberPregunta < preguntas.size() - 1) {
                if(opcionSelected != - 1 || PreguntaRespondida()) {
                    numberPregunta++;
                    if(numberPregunta == preguntas.size() - 1) {
                        binding.textBtnNext.setText("Finalizar");
                    }
                    mostrarPregunta();
                } else {
                    Toast.makeText(getContext(), "Debe seleccionar una respuesta", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Evaluacion terminada", Toast.LENGTH_SHORT).show();
                //dialog.show(getParentFragmentManager(), "");
                dialog2.show(getParentFragmentManager(), "");
                sendAnswersToGemini();
            }
        });

        binding.btnPreview.setOnClickListener(v -> {
            if(numberPregunta > 0) {
                numberPregunta--;
                mostrarPregunta();
            }
        });

        return binding.getRoot();
    }


    @SuppressLint("ResourceType")
    public void mostrarPregunta() {
        binding.radioGroup.removeAllViews();
        binding.cntPreguntas.setText("Pregunta " + (numberPregunta + 1) + " de " + preguntas.size());
        Pregunta pregunta = preguntas.get(numberPregunta);
        binding.pregunta.setText(pregunta.getTexto());
        ArrayList<Opcion> opciones = preguntas.get(numberPregunta).getOpciones();

        AdapterOpciones adapter = new AdapterOpciones(getContext(), opciones, pos -> {
            opcionSelected = pos;
            //Toast.makeText(getContext(), "Pos: " + pos, Toast.LENGTH_SHORT).show();
        });
        binding.lvRespuestas.setAdapter(adapter);
        opcionSelected = -1;
    }

    public void CargarPreguntas() {
        preguntas.add(new Pregunta(
                "¿Qué parte de la planta está mal?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Hojas"),
                        new Opcion("Tallo"),
                        new Opcion("Raíces"),
                        new Opcion("Flores")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Qué ves en las hojas o tallo?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Manchas marrones"),
                        new Opcion("Manchas negras"),
                        new Opcion("Manchas amarillas"),
                        new Opcion("Agujeros")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Tiene polvo, pelusa o algo raro?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, blanco"),
                        new Opcion("Sí, gris o negro"),
                        new Opcion("Sí, naranja o rojo"),
                        new Opcion("No tiene nada")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Cómo están las hojas?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Enroscadas"),
                        new Opcion("Caídas"),
                        new Opcion("Secas o quebradas"),
                        new Opcion("Con los bordes marrones")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Hay bichos o insectos en la planta?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, pequeños y verdes o blancos"),
                        new Opcion("Sí, negros o marrones"),
                        new Opcion("Sí, saltan o vuelan"),
                        new Opcion("No he visto")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Cada cuánto la riegas?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Todos los días"),
                        new Opcion("2 o 3 veces por semana"),
                        new Opcion("1 vez por semana o menos"),
                        new Opcion("No estoy seguro")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Cómo está la tierra?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Muy seca"),
                        new Opcion("Húmeda"),
                        new Opcion("Mojada o con agua"),
                        new Opcion("No la he tocado")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Cuánta luz recibe al día?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sol directo todo el día"),
                        new Opcion("Sol solo en la mañana o tarde"),
                        new Opcion("Sombra casi todo el tiempo"),
                        new Opcion("Casi nada de luz")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Dónde tienes la planta?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Dentro con aire que corre"),
                        new Opcion("Dentro sin aire"),
                        new Opcion("Afuera (jardín o balcón)"),
                        new Opcion("Cerca de una fuente de calor")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿La has cambiado de lugar hace poco?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, hace unos días"),
                        new Opcion("Sí, hace semanas"),
                        new Opcion("No la he movido"),
                        new Opcion("No me acuerdo")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿La trasplantaste hace poco?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, hace menos de 1 mes"),
                        new Opcion("Sí, entre 1 y 3 meses"),
                        new Opcion("Hace más de 3 meses"),
                        new Opcion("Nunca o no me acuerdo")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Le pusiste abono o fertilizante?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, hace poco"),
                        new Opcion("Sí, hace más de un mes"),
                        new Opcion("Nunca"),
                        new Opcion("No me acuerdo")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Le pusiste veneno o algún producto?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, hace pocos días"),
                        new Opcion("Sí, hace semanas"),
                        new Opcion("Nunca"),
                        new Opcion("No me acuerdo")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Qué tipo de planta es?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("De interior"),
                        new Opcion("De exterior"),
                        new Opcion("Cactus o suculenta"),
                        new Opcion("No lo sé")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Pasó por calor fuerte o frío intenso?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, mucho calor"),
                        new Opcion("Sí, mucho frío"),
                        new Opcion("No"),
                        new Opcion("No estoy seguro")
                ))
        ));
    }

    public boolean PreguntaRespondida() {
        Pregunta pregunta = preguntas.get(numberPregunta);
        for(Opcion opcion : pregunta.getOpciones()) {
            if(opcion.getSeleccionada()) {
                return true;
            }
        }
        return false;
    }




    @Override
    public void onClick(View view) {

    }

    private String collectAnswersForGemini() {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Evalúa el estado de una planta basándote en las siguientes respuestas de un formulario. Identifica posibles problemas (plagas, enfermedades, problemas de riego, luz, etc.) y ofrece recomendaciones de cuidado. Que la respuesta sea a lo mucho en dos parrafos. Si la información es insuficiente para un diagnóstico preciso, sugiere qué más información se necesitaría.\n\n");

        for (int i = 0; i < preguntas.size(); i++) {
            Pregunta pregunta = preguntas.get(i);
            String questionText = pregunta.getTexto();
            String selectedAnswer = "No respondida"; // Default if no option is selected

            for (Opcion opcion : pregunta.getOpciones()) {
                if (opcion.getSeleccionada()) {
                    selectedAnswer = opcion.getDescripcion();
                    break;
                }
            }
            promptBuilder.append("Pregunta ").append(i + 1).append(": ").append(questionText).append("\n");
            promptBuilder.append("Respuesta: ").append(selectedAnswer).append("\n\n");
        }

        promptBuilder.append("Basado en estas respuestas, por favor, proporciona un análisis detallado del estado de la planta y recomendaciones específicas para su cuidado.");

        return promptBuilder.toString();
    }

    private void sendAnswersToGemini() {
        binding.btnNext.setEnabled(false); // Disable buttons
        binding.btnPreview.setEnabled(false);

        String userPrompt = collectAnswersForGemini();
        Content content = new Content.Builder().addText(userPrompt).build();

        Futures.addCallback(model.generateContent(content),
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        requireActivity().runOnUiThread(() -> {// Hide loading indicator
                            binding.btnNext.setEnabled(true);
                            binding.btnPreview.setEnabled(true);

                            String geminiResponse = result.getText();
                            if (geminiResponse != null && !geminiResponse.isEmpty()) {
                                // Display the Gemini response to the user
                                // You'll need to create a new Fragment or Dialog to show this
                                //dialog.dismiss();
                                dialog2.dismiss();
                                Toast.makeText(getContext(), "Listooo", Toast.LENGTH_SHORT).show();
                                showGeminiResponseDialog(geminiResponse);
                            } else {
                                Toast.makeText(getContext(), "Gemini no pudo generar una respuesta.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        requireActivity().runOnUiThread(() -> {
                            binding.btnNext.setEnabled(true);
                            binding.btnPreview.setEnabled(true);
                            Toast.makeText(getContext(), "Error al conectar con Gemini: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("ERROR", t.getMessage());
                            t.printStackTrace(); // Log the error for debugging
                        });
                    }
                }, executor);
    }

    private void showGeminiResponseDialog(String response) {
        // Here, you'll want to display the 'response' to the user in a new UI.
        // A simple way is to use an AlertDialog or navigate to a new Fragment.

        // Example using AlertDialog:
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Análisis de la Planta")
                .setMessage(response)
                .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                .show();

        // If you want to navigate to a new Fragment (e.g., ResultFragment):
        // ResultFragment resultFragment = ResultFragment.newInstance(response);
        // requireActivity().getSupportFragmentManager().beginTransaction()
        //         .replace(R.id.fragment_container, resultFragment) // Replace R.id.fragment_container with your actual container ID
        //         .addToBackStack(null)
        //         .commit();
    }


}