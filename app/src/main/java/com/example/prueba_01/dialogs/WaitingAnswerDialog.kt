package com.example.prueba_01.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.prueba_01.R
import com.example.prueba_01.databinding.WaitingDialogBinding

class WaitingAnswerDialog : DialogFragment() {
    private var _binding : WaitingDialogBinding?= null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_MESSAGE = "ARG_MESSAGE"

        @JvmStatic
        fun newInstance(message: String): WaitingAnswerDialog {
            val dialog = WaitingAnswerDialog()
            val args = Bundle()
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WaitingDialogBinding.inflate(layoutInflater, container, false)
        val message = arguments?.getString(ARG_MESSAGE)
        _binding?.titulo?.text = message ?: "Espera un momento..."
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}