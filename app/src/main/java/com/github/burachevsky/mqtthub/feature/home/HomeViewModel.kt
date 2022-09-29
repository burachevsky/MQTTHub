package com.github.burachevsky.mqtthub.feature.home

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    args: HomeFragmentArgs
) : ViewModel() {

    val text = args.text
}