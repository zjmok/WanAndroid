package org.example.wan.android.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

inline fun <reified T> Context.newIntent(block: Intent.() -> Unit): Intent {
    return Intent(this, T::class.java).apply(block)
}

inline fun <reified T> Fragment.newIntent(block: Intent.() -> Unit): Intent {
    return this.requireContext().newIntent<T>(block)
}
