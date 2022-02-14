package fr.isen.collodet.androiderestaurant.model

import java.io.Serializable

data class ListBasket(
    val data : List<DishBasket>
) : Serializable
