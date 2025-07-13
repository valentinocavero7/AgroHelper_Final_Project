package com.example.prueba_01.fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.prueba_01.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class InicioFragment extends Fragment {

    public static InicioFragment newInstance() {
        return new InicioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RelativeLayout animatedButton = view.findViewById(R.id.animatedButton);
        View circle = view.findViewById(R.id.circle);
        ImageView arrowLeft = view.findViewById(R.id.arrowLeft);
        ImageView arrowRight = view.findViewById(R.id.arrowRight);
        TextView btnText = view.findViewById(R.id.btnText);

        animatedButton.setOnClickListener(v -> {
            // Expandir círculo
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(circle, "scaleX", 1f, 10f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(circle, "scaleY", 1f, 10f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(circle, "alpha", 0f, 1f);

            AnimatorSet circleAnim = new AnimatorSet();
            circleAnim.playTogether(scaleX, scaleY, alpha);
            circleAnim.setDuration(600);

            // Mover flechas
            arrowLeft.animate().translationX(0).setDuration(600).start();
            arrowRight.animate().translationX(60).setDuration(600).start();

            // Mover texto
            btnText.animate().translationX(20).setDuration(600).start();

            // Al terminar la animación, cambiar de pestaña
            circleAnim.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
                    navView.setSelectedItemId(R.id.evaluacion);
                }
            });

            circleAnim.start();
        });
    }
}
