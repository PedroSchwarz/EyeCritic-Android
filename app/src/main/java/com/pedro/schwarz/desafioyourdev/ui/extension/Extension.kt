package com.pedro.schwarz.desafioyourdev.ui.extension

import android.content.res.ColorStateList
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pedro.schwarz.desafioyourdev.R

fun Fragment.showMessage(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun CardView.setAgeColor(age: String) {
    if (age.isNotEmpty()) {
        when (age) {
            "PG-13" -> {
                setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.colorWarning)))
            }
            "R" -> {
                setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.colorError)))
            }
            else -> {
                setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.colorSuccess)))
            }
        }
    } else {
        setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.colorLightGrey)))
    }
}

fun RecyclerView.setContent(
    linear: Boolean,
    orientation: Int,
    reverse: Boolean,
    itemsAdapter: RecyclerView.Adapter<*>
) {
    setHasFixedSize(true)
    layoutManager = if (linear) {
        LinearLayoutManager(context, orientation, reverse)
    } else {
        StaggeredGridLayoutManager(2, orientation)
    }
    adapter = itemsAdapter
}