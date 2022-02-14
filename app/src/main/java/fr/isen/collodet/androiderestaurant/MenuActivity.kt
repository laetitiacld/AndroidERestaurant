package fr.isen.collodet.androiderestaurant

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

open class MenuActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        menu.findItem(R.id.menuButtonBasket).actionView.findViewById<TextView>(R.id.quantityTextMenu).text = (this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).getInt(getString(R.string.spTotalQuantity), 0)).toString()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuButtonBasket -> {
                startActivity(Intent(this, BasketActivity::class.java))
                true
            }
            R.id.menuButtonUser -> {
                val userId = this.getSharedPreferences(getString(R.string.spFileName), Context.MODE_PRIVATE).getInt(getString(R.string.spUserId), 0)

                if (userId == 0) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this, UserActivity::class.java))
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}