package fr.isen.collodet.androiderestaurant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.isen.collodet.androiderestaurant.databinding.ActivityCommandBinding
import fr.isen.collodet.androiderestaurant.model.DishBasket
import fr.isen.collodet.androiderestaurant.model.ListBasket
import org.json.JSONObject
import java.io.File
import fr.isen.collodet.androiderestaurant.model.Constants

class CommandActivity : MenuActivity() {
    private lateinit var binding : ActivityCommandBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val handler = Handler()
        binding = ActivityCommandBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userid = this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).getInt(getString(R.string.spUserId), 0)

        if(userid == 0) {
            Toast.makeText(this, getString(R.string.warningUserLogout), Toast.LENGTH_SHORT).show()
            finish()
        } else {
            newCommandRequest(userid)
        }
        handler.postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
        }, 4500)
    }

    private fun newCommandRequest(userId : Int) {
        val url = Constants.BASE_URL+"/user/order"

        val params = HashMap<String, Any>()
        params[Constants.KEY_SHOP] = Constants.ID_SHOP
        params[Constants.ID_USER] = userId
        params[Constants.MSG] = basketRecupFile().toString()
        val jsonObject = JSONObject(params as Map<*, *>)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            {
                Toast.makeText(this, getString(R.string.successCommand), Toast.LENGTH_SHORT).show()
                binding.commandText.text = getString(R.string.successCommandTxt)
                basketDeleteData()
            }, {
                Log.e("API", it.toString())
                Toast.makeText(this, getString(R.string.APIfailure), Toast.LENGTH_SHORT).show()
                binding.commandText.text = getString(R.string.failureCommandTxt)
            }
        )

        request.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f)

        Volley.newRequestQueue(this).add(request)
    }

    private fun basketRecupFile() : List<DishBasket> {
        val file = File(cacheDir.absolutePath + "/basket.json")
        var dishesBasket: List<DishBasket> = ArrayList()

        if (file.exists()) {
            dishesBasket = Gson().fromJson(file.readText(), ListBasket::class.java).data
        }

        return dishesBasket
    }
    private fun basketDeleteData() {
        File(cacheDir.absolutePath + "/basket.json").delete()
        this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).edit().remove(getString(R.string.spTotalPrice)).apply()
        this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).edit().remove(getString(R.string.spTotalQuantity)).apply()
    }
}