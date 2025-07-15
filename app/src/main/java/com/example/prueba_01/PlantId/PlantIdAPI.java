package com.example.prueba_01.PlantId;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PlantIdAPI {
    @POST("v2/health_assessment")
    @Headers({
            "Content-Type: application/json",
            "Api-Key: KPcHAQg3QkoRR8CwBbSVJahlVbFyZvKz0o32baXIBdp3OZWWo3"
    })
    Call<Map<String, Object>> analizarSaludPlanta(@Body RequestBody body);

}
