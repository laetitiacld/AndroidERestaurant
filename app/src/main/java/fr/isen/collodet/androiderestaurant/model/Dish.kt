package fr.isen.collodet.androiderestaurant.model

import java.io.Serializable

data class Dish(
    val name_fr : String,
    val images : List<String>,
    val ingredients : List<Ingredient>,
    val prices : List<Price>
) : Serializable
