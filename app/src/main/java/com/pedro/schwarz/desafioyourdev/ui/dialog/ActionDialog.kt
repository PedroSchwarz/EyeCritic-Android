package com.pedro.schwarz.desafioyourdev.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.pedro.schwarz.desafioyourdev.R

fun showDeleteMovieDialog(
    context: Context,
    title: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog.Builder(context).apply {
        setCancelable(false)
        setTitle(context.getString(R.string.delete_review_dialog_title))
        setMessage(
            context.getString(R.string.delete_review_dialog_message_entry) + title + context.getString(
                R.string.delete_review_dialog_message_final
            )
        )
        setPositiveButton(context.getString(R.string.delete_review_dialog_delete_action)) { _, _ -> onConfirm() }
        setNegativeButton(context.getString(R.string.delete_review_dialog_cancel_action)) { _, _ -> onCancel() }
    }.show()
}

fun showDeleteAllMoviesDialog(
    context: Context,
    onConfirm: () -> Unit
) {
    AlertDialog.Builder(context).apply {
        setCancelable(false)
        setTitle(context.getString(R.string.delete_all_reviews_dialog_title))
        setMessage(context.getString(R.string.delete_all_reviews_dialog_message))
        setPositiveButton(context.getString(R.string.delete_review_dialog_delete_action)) { _, _ -> onConfirm() }
        setNegativeButton(context.getString(R.string.delete_review_dialog_cancel_action)) { _, _ -> }
    }.show()
}