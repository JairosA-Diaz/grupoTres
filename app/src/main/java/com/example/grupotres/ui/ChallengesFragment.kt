package com.example.grupotres.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grupotres.R
import com.example.grupotres.data.AppDatabase
import com.example.grupotres.data.Challenge
import com.example.grupotres.repository.ChallengeRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChallengesFragment : Fragment() {

    private val viewModel: ChallengesViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = ChallengeRepository(database.challengeDao())
        ChallengesViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvChallenges = view.findViewById<RecyclerView>(R.id.rv_challenges)
        val ivBack = view.findViewById<ImageView>(R.id.iv_back_challenges)
        val fabAdd = view.findViewById<FloatingActionButton>(R.id.fab_add_challenge)

        rvChallenges.layoutManager = LinearLayoutManager(requireContext())

        viewModel.allChallenges.observe(viewLifecycleOwner) { challenges ->
            rvChallenges.adapter = ChallengeAdapter(
                challenges,
                onEdit = { challenge -> showEditChallengeDialog(challenge) },
                onDelete = { challenge -> showDeleteChallengeDialog(challenge) }
            )
        }

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        fabAdd.setOnClickListener {
            showAddChallengeDialog()
        }
    }

    private fun showAddChallengeDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Escribe el reto aquí"
        
        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Reto")
            .setView(editText)
            .setPositiveButton("Agregar") { _, _ ->
                val description = editText.text.toString()
                if (description.isNotBlank()) {
                    viewModel.addChallenge(description)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditChallengeDialog(challenge: Challenge) {
        val editText = EditText(requireContext())
        editText.setText(challenge.description)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Reto")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val description = editText.text.toString()
                if (description.isNotBlank()) {
                    viewModel.updateChallenge(challenge.copy(description = description))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showDeleteChallengeDialog(challenge: Challenge) {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_delete_challenge, null)
        builder.setView(view)
        builder.setCancelable(false)

        val dialog = builder.create()

        val tvDescription = view.findViewById<TextView>(R.id.tv_delete_description)
        val btnNo = view.findViewById<TextView>(R.id.btn_no)
        val btnSi = view.findViewById<TextView>(R.id.btn_si)

        tvDescription.text = challenge.description

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        btnSi.setOnClickListener {
            viewModel.deleteChallenge(challenge)
            dialog.dismiss()
        }

        dialog.show()
    }
}