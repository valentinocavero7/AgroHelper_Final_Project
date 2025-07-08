package com.example.prueba_01.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.prueba_01.Adapter.AdapterOpciones;
import com.example.prueba_01.modelo.Opcion;
import com.example.prueba_01.modelo.Pregunta;
import com.example.prueba_01.R;
import com.example.prueba_01.databinding.FragmentEvaluacionBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class EvaluacionFragment extends Fragment implements View.OnClickListener {

    private FragmentEvaluacionBinding binding;
    private ArrayList<Pregunta> preguntas = new ArrayList<>();
    private int numberPregunta = 0;
    private RadioGroup opcionesGroup;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEvaluacionBinding.inflate(inflater, container, false);
        CargarPreguntas();
        opcionesGroup = binding.radioGroup;
        mostrarPregunta();

        binding.btnNext.setOnClickListener(v -> {
            if(numberPregunta < preguntas.size() - 1) {
                numberPregunta++;
                if(numberPregunta == preguntas.size() - 1) {
                    binding.textBtnNext.setText("Finalizar");
                }
                mostrarPregunta();
            } else {
                Toast.makeText(getContext(), "Evaluacion terminada", Toast.LENGTH_SHORT).show();
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

        AdapterOpciones adapter = new AdapterOpciones(getContext(), opciones);
        binding.lvRespuestas.setAdapter(adapter);
    }

    public void CargarPreguntas() {
        preguntas.add(new Pregunta(
                "¿Qué parte de la planta presenta problemas?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Hojas"),
                        new Opcion("Tallo"),
                        new Opcion("Raíces"),
                        new Opcion("Flores")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Qué tipo de manchas o daños observas?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Manchas marrones"),
                        new Opcion("Manchas negras"),
                        new Opcion("Manchas amarillas"),
                        new Opcion("Agujeros")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Hay presencia de polvo, pelusa o sustancia extraña en la planta?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, blanca"),
                        new Opcion("Sí, gris o negra"),
                        new Opcion("Sí, anaranjada o roja"),
                        new Opcion("No")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Cómo están las hojas?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Enrolladas"),
                        new Opcion("Caídas"),
                        new Opcion("Secas o quebradizas"),
                        new Opcion("Con bordes marrones"),
                        new Opcion("Normales")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Ha notado insectos en la planta?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("Sí, pequeños y verdes/blancos"),
                        new Opcion("Sí, negros o marrones"),
                        new Opcion("Sí, saltan o vuelan"),
                        new Opcion("No")
                ))
        ));

        preguntas.add(new Pregunta(
                "¿Con qué frecuencia riegas la planta?",
                new ArrayList<>(Arrays.asList(
                        new Opcion("A diario"),
                        new Opcion("2–3 veces por semana"),
                        new Opcion("1 vez por semana o menos"),
                        new Opcion("No lo sé")
                ))
        ));
    }



    @Override
    public void onClick(View view) {

    }
}