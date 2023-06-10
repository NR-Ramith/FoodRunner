package com.ramith.foodrunner.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.adapter.RecyclerFaqAdapter
import com.ramith.foodrunner.model.Faq

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FaqsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFaq: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerFaqAdapter: RecyclerFaqAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val faqList = listOf<Faq>(
        Faq("Is this app suitable for my business?","Absolutely! This app is for all those restaurants that have a compelling menu, but are lagging behind simply because they have no online presence. You can put your banners and select the theme at your whim."),
        Faq("If i have a query reagarding the app, whom can i get in touch with?","You can contact our support team by simply filling up the contact form, by calling us at 619-309-4653 or mailing us at info@foodrunner.com. Our team will get back to you at the earliest. For quick discussion, you can also chat with us 24/7.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faqs, container, false)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        if(activity != null){
            progressLayout.visibility = View.GONE
            recyclerFaq = view.findViewById(R.id.recyclerFaq)
            layoutManager = LinearLayoutManager(activity)
            recyclerFaqAdapter = RecyclerFaqAdapter(activity as Context, faqList)
            recyclerFaq.adapter = recyclerFaqAdapter
            recyclerFaq.layoutManager = layoutManager
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FaqsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}