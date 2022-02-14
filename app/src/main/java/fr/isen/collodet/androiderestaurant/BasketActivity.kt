package fr.isen.collodet.androiderestaurant


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import fr.isen.collodet.androiderestaurant.adapter.BasketAdapter
import fr.isen.collodet.androiderestaurant.databinding.ActivityBasketBinding
import fr.isen.collodet.androiderestaurant.model.DishBasket
import fr.isen.collodet.androiderestaurant.model.ListBasket
import java.io.File

class BasketActivity : MenuActivity() {
    private lateinit var binding : ActivityBasketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBasketBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val file = File(cacheDir.absolutePath + "/basket.json")
        if (file.exists()) {
            val dishesBasket : List<DishBasket> = Gson().fromJson(file.readText(), ListBasket::class.java).data
            display(dishesBasket)
        }

        val quantity = getString(R.string.basketTotalQuantity) + this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).getInt(getString(R.string.spTotalQuantity), 0).toString()
        binding.basketTotalQuantity.text = quantity

        val price = getString(R.string.totalPrice) + this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).getFloat(getString(R.string.spTotalPrice), 0.0f).toString()
        binding.basketTotalPrice.text = price
        binding.basketButtonBuy.setOnClickListener {
            val userId = this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).getInt(getString(R.string.spUserId), 0)

            if (userId == 0) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                startActivity(Intent(this, CommandActivity::class.java))
            }
        }

        binding.basketButtonDeleteAll.setOnClickListener {
            deleteBasketData()
            finish()
        }
    }
    private fun display(dishesList : List<DishBasket>) {
        binding.basketList.layoutManager = LinearLayoutManager(this)
        binding.basketList.adapter = BasketAdapter(dishesList) {
            deleteDishBasket(it)
        }
    }

    private fun deleteDishBasket(dish : DishBasket) {
        val file = File(cacheDir.absolutePath + "/basket.json")
        var dishesBasket: List<DishBasket> = ArrayList()
        if (file.exists()) {
            dishesBasket = Gson().fromJson(file.readText(), ListBasket::class.java).data
            dishesBasket = dishesBasket - dish
            updateSharedPreferences(dish.quantity, dish.dish.prices[0].price.toFloat())
        }

        file.writeText(Gson().toJson(ListBasket(dishesBasket)))

        finish()
        this.recreate()
    }
    private fun deleteBasketData() {
        File(cacheDir.absolutePath + "/basket.json").delete()
        this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).edit().remove(getString(R.string.spTotalPrice)).apply()
        this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).edit().remove(getString(R.string.spTotalQuantity)).apply()
        Toast.makeText(this, getString(R.string.basketDeleteAllTxt), Toast.LENGTH_SHORT).show()
    }

    private fun updateSharedPreferences(quantity: Int, price: Float) {
        val sharedPreferences = this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE)
        val previousQuantity = sharedPreferences.getInt(getString(R.string.spTotalQuantity), 0)
        val nextQuantity = previousQuantity + quantity
        sharedPreferences.edit().putInt(getString(R.string.spTotalQuantity), nextQuantity).apply()

        val oldPrice = sharedPreferences.getFloat(getString(R.string.spTotalPrice), 0.0f)
        val newPrice = oldPrice - price
        sharedPreferences.edit().putFloat(getString(R.string.spTotalPrice), newPrice).apply()
    }
}