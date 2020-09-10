package com.pedro.schwarz.desafioyourdev.ui.extension

import android.content.res.ColorStateList
import android.graphics.drawable.Icon
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
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

fun ImageView.setImage(imageUrl: String) {
    Glide.with(this.context).load(imageUrl).error(R.drawable.image_placeholder)
        .placeholder(R.drawable.image_placeholder).into(this)
}

fun ImageButton.setImage(favorite: Boolean) {
    if (favorite) {
        setImageIcon(Icon.createWithResource(this.context, R.drawable.ic_favorite))
    } else {
        setImageIcon(Icon.createWithResource(this.context, R.drawable.ic_unfavorite))
    }
}

fun View.toggleVisibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE
    else View.INVISIBLE
}
