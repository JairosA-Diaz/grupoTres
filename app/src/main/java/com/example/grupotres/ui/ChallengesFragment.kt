package com.example.grupotres.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grupotres.R
import com.example.grupotres.data.AppDatabase
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
                onEdit = { /* HU 8.0 */ },
                onDelete = { challenge -> viewModel.deleteChallenge(challenge) }
            )
        }

        ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        fabAdd.setOnClickListener {
            // HU 7.0: Abrir diálogo para agregar
        }
    }
}