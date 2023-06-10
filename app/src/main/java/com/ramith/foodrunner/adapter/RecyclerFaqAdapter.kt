package com.ramith.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.model.Faq

class RecyclerFaqAdapter(val context: Context, val faqList: List<Faq>): RecyclerView.Adapter<RecyclerFaqAdapter.FaqViewHolder>() {
    class FaqViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtQestion: TextView = view.findViewById(R.id.txtQestion)
        val txtAnswer: TextView = view.findViewById(R.id.txtAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_faq_sigle_row, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val faq = faqList[position]
        holder.txtQestion.text = "Q.${position+1} ${faq.qestion}"
        holder.txtAnswer.text = "A. ${faq.answer}"
    }

    override fun getItemCount(): Int {
        return faqList.size
    }
}