package ru.netology.neworkapp.activity

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.neworkapp.R
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.databinding.ActivityAppBinding
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<AuthViewModel>()

    private val userViewModel by viewModels<UserViewModel>()

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        navView.itemIconTintList = null

        val navController = findNavController(R.id.nav_host_fragment_activity_app)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_posts,
                R.id.nav_users,
                R.id.nav_events,
                R.id.nav_profile,
                -> {
                    navView.visibility = View.VISIBLE
                }
                else -> {
                    navView.visibility = View.GONE
                }
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_posts,
                R.id.nav_users,
                R.id.nav_events,
                R.id.nav_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)

        val itemIcon = navView.menu.findItem(R.id.nav_profile)

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
            if (it.id == 0L) {
                itemIcon.setIcon(R.drawable.ic_default_user_profile_image)
            } else {
                userViewModel.getUserById(it.id)
            }
        }

        userViewModel.user.observe(this) {
            Glide.with(this)
                .asBitmap()
                .load("${it.avatar}")
                .transform(CircleCrop())
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        itemIcon.icon = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
                )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.let {
            it.setGroupVisible(R.id.unauthentificated, !viewModel.authorized)
            it.setGroupVisible(R.id.authentificated, viewModel.authorized)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_in -> {
                findNavController(R.id.nav_host_fragment_activity_app)
                    .navigate(R.id.signInFragment)
                true
            }
            R.id.sign_up -> {
                findNavController(R.id.nav_host_fragment_activity_app)
                    .navigate(R.id.signUpFragment)
                true
            }
            R.id.sign_out -> {
                appAuth.removeAuth()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }
}