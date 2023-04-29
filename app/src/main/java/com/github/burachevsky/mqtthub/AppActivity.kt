package com.github.burachevsky.mqtthub

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.burachevsky.mqtthub.common.container.ViewController
import com.github.burachevsky.mqtthub.common.container.viewContainer
import com.github.burachevsky.mqtthub.databinding.ActivityAppBinding
import com.github.burachevsky.mqtthub.di.ViewModelFactory
import com.google.android.material.elevation.SurfaceColors
import javax.inject.Inject

class AppActivity : AppCompatActivity(), ViewController<AppViewModel> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AppViewModel>

    override val binding: ViewBinding by viewBinding(ActivityAppBinding::bind, R.id.appContainer)
    override val viewModel: AppViewModel by viewModels { viewModelFactory }
    override val container by viewContainer()

    var statusBarHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        (application as App).appComponent.inject(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.statusBarColor = ColorUtils.setAlphaComponent(
            SurfaceColors.SURFACE_0.getColor(this),
            10
        )
        findNavController().setGraph(R.navigation.app_graph)
    }

    fun findNavController(): NavController {
        return (supportFragmentManager.findFragmentById(R.id.appContainer) as NavHostFragment)
            .navController
    }
}