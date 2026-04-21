package com.example.moblab4

import androidx.annotation.DrawableRes

data class Place(
    val id: Int,
    val nameResId: Int,
    val descResId: Int,
    @DrawableRes val imageResId: Int,
    val addressResId: Int,
    val hoursResId: Int
)

data class Category(
    val nameResId: Int,
    @DrawableRes val iconResId: Int
)

object DetroitData {
    val categories = listOf(
        Category(R.string.category_coffee, R.drawable.ic_place_coffee),
        Category(R.string.category_restaurants, R.drawable.ic_place_restaurant),
        Category(R.string.category_parks, R.drawable.ic_place_park)
    )

    fun getPlacesForCategory(categoryNameResId: Int): List<Place> {
        return when (categoryNameResId) {
            R.string.category_coffee -> listOf(
                Place(1, R.string.coffee1_name, R.string.coffee1_desc, R.drawable.cf_1,
                    R.string.coffee1_address, R.string.coffee1_hours),
                Place(2, R.string.coffee2_name, R.string.coffee2_desc, R.drawable.cf_2,
                    R.string.coffee2_address, R.string.coffee2_hours),
                Place(3, R.string.coffee3_name, R.string.coffee3_desc, R.drawable.cf_3,
                    R.string.coffee3_address, R.string.coffee3_hours),
                Place(4, R.string.coffee4_name, R.string.coffee4_desc, R.drawable.cf_4,
                    R.string.coffee4_address, R.string.coffee4_hours)
            )
            R.string.category_restaurants -> listOf(
                Place(5, R.string.rest1_name, R.string.rest1_desc, R.drawable.ca_1,
                    R.string.rest1_address, R.string.rest1_hours),
                Place(6, R.string.rest2_name, R.string.rest2_desc, R.drawable.ca_2,
                    R.string.rest2_address, R.string.rest2_hours),
                Place(7, R.string.rest3_name, R.string.rest3_desc, R.drawable.ca_3,
                    R.string.rest3_address, R.string.rest3_hours),
                Place(8, R.string.rest4_name, R.string.rest4_desc, R.drawable.ca_4,
                    R.string.rest4_address, R.string.rest4_hours)
            )
            R.string.category_parks -> listOf(
                Place(9, R.string.park1_name, R.string.park1_desc, R.drawable.pk_1,
                    R.string.park1_address, R.string.park1_hours),
                Place(10, R.string.park2_name, R.string.park2_desc, R.drawable.pk_2,
                    R.string.park2_address, R.string.park2_hours),
                Place(11, R.string.park3_name, R.string.park3_desc, R.drawable.pk_3,
                    R.string.park3_address, R.string.park3_hours),
                Place(12, R.string.park4_name, R.string.park4_desc, R.drawable.pk_4,
                    R.string.park4_address, R.string.park4_hours)
            )
            else -> emptyList()
        }
    }

    fun getPlaceById(placeId: Int): Place? {
        return categories.flatMap { getPlacesForCategory(it.nameResId) }
            .find { it.id == placeId }
    }
}