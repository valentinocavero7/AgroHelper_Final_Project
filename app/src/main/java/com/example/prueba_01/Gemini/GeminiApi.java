package com.example.prueba_01.Gemini;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.prueba_01.databinding.WaitingDialogBinding;
import com.example.prueba_01.dialogs.WaitingAnswerDialog;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiApi {
    private static final String MODEL_NAME= "gemini-2.5-flash-lite-preview-06-17";
    private static final String API_KEY = "AIzaSyBn1YGNly3F8X_Jz_YYTm_pTY-kmq-6S68";
    private GenerativeModel gm;
    private GenerativeModelFutures model;

    public GeminiApi() {
        gm = new GenerativeModel(MODEL_NAME, API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

    private Executor executor = Executors.newSingleThreadExecutor();

    public void getAnswerFromGemini(String userPrompt, GeminiCallBack callback) {
        Content content = new Content.Builder().addText(userPrompt).build();
        Futures.addCallback(model.generateContent(content), new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (result != null && !result.getCandidates().isEmpty()) {
                    String respuesta = result.getText();
                    callback.onGeminiResponse(respuesta);
                } else {
                    Log.d("ERROR", "ERROR");
                    //callback.onGeminiError("Respuesta vacía de Gemini");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onGeminiError(t.getMessage());
            }
        }, executor);
    }
}
