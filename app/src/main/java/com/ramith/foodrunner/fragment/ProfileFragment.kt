package com.ramith.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ramith.foodrunner.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtDisplay: TextView

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        txtDisplay = view.findViewById(R.id.txtDisplay)
        sharedPreferences =
            activity?.getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)!!
        txtDisplay.text = "Name - ${sharedPreferences.getString("Name",null)}\n" +
                "Email - ${sharedPreferences.getString("Email",null)}\n" +
                "Mobile Number - ${sharedPreferences.getString("Mobile Number","NA")}\n" +
                "Address - ${sharedPreferences.getString("Delivery Address","NA")}"
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}