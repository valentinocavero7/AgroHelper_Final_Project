package com.example.prueba_01.PlantId;

import java.util.ArrayList;

public class PlantIdResponse {
    public boolean is_healthy;
    public ArrayList<Suggestion> disease;

    public static class Suggestion {
        public String name;
        public double probability;
        public String description;
        public Treatment treatment;
    }

    public static class Treatment {
        public String biological;
        public String chemical;
    }
}
