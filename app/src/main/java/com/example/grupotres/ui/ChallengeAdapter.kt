package com.example.grupotres.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grupotres.R
import com.example.grupotres.data.Challenge

class ChallengeAdapter(
    private val challenges: List<Challenge>,
    private val onEdit: (Challenge) -> Unit,
    private val onDelete: (Challenge) -> Unit
) : RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>() {

    class ChallengeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDesc: TextView = view.findViewById(R.id.tv_challenge_desc)
        val ivEdit: ImageView = view.findViewById(R.id.iv_edit_challenge)
        val ivDelete: ImageView = view.findViewById(R.id.iv_delete_challenge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_challenge, parent, false)
        return ChallengeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val challenge = challenges[position]
        holder.tvDesc.text = challenge.description
        
        holder.ivEdit.setOnClickListener { onEdit(challenge) }
        holder.ivDelete.setOnClickListener { onDelete(challenge) }
    }

    override fun getItemCount(): Int = challenges.size
}