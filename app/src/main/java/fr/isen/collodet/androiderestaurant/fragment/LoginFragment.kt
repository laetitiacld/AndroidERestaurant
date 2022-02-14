package fr.isen.collodet.androiderestaurant.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.collodet.androiderestaurant.LoginActivity
import fr.isen.collodet.androiderestaurant.R
import fr.isen.collodet.androiderestaurant.databinding.FragmentLoginBinding
import fr.isen.collodet.androiderestaurant.model.Constants
import fr.isen.collodet.androiderestaurant.model.DataLogin
import fr.isen.collodet.androiderestaurant.model.LoginRequestResult
import org.json.JSONObject

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginToSignUp.setOnClickListener {
            (activity as? LoginActivity)?.loginToSignup()
        }

        binding.loginButton.setOnClickListener {
            val loginData = DataLogin(
                binding.loginEmailEditText.text.toString(),
                binding.loginPasswordEditText.text.toString()
            )

            login(loginData)
        }
    }

    private fun login(loginData : DataLogin) {
        val url = Constants.BASE_URL+"/user/login"

        val params = HashMap<String, Any>()
        params[Constants.KEY_SHOP] = Constants.ID_SHOP
        params[Constants.EMAIL] = loginData.email
        params[Constants.PASSWORD] = loginData.password
        val jsonObject = JSONObject(params as Map<*, *>)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            {
                val response = Gson().fromJson(it.toString(), LoginRequestResult::class.java)
                if (response.data != null) {
                    Toast.makeText(context, getString(R.string.successLogin), Toast.LENGTH_SHORT).show()
                    (activity as? LoginActivity)?.loginToCommand(439) // id de bob
                } else {
                    Toast.makeText(context, getString(R.string.failureLogin), Toast.LENGTH_SHORT).show()
                }
            }, {
                Log.e("API", it.toString())
                Toast.makeText(context, getString(R.string.APIfailure), Toast.LENGTH_SHORT).show()
            }
        )

        request.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f)

        Volley.newRequestQueue(context).add(request)
    }
}
