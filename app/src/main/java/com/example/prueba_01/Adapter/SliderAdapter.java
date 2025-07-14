package com.example.prueba_01.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prueba_01.R;
import com.example.prueba_01.modelo.CardModel;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<CardModel> items;

    public SliderAdapter(List<CardModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider_card, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        int actualPosition = position % items.size(); // para efecto infinito
        holder.bind(items.get(actualPosition));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // para scroll infinito
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCard;

        SliderViewHolder(View itemView) {
            super(itemView);
            imageCard = itemView.findViewById(R.id.rvSlider);
        }

        void bind(CardModel model) {
            imageCard.setImageResource(model.getImageResId());
        }
    }
}