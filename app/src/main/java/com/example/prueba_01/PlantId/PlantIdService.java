package com.example.prueba_01.PlantId;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PlantIdService {
    @POST("v2/health_assessment")
    @Headers({
            "Content-Type: application/json",
            "Api-Key: KPcHAQg3QkoRR8CwBbSVJahlVbFyZvKz0o32baXIBdp3OZWWo3"  // <-- reemplaza esto con tu clave real
    })
    Call<Map<String, Object>> analizarSaludPlanta(@Body RequestBody body);
}
