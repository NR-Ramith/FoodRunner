package com.ramith.foodrunner.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.fragment.*

//App login details
//Name - Ramith , Phone - 9880819125, Email - Natakalaram1@gmail.com, Password - Ramith
class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: Toolbar
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frame)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        setUpToolbar()

        if(intent != null){
            if(intent.getBooleanExtra("open_history", false)){
                val fragment = OrderHistoryFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, fragment)
                transaction.commit()
                supportActionBar?.title = "Order History"
                navigationView.setCheckedItem(R.id.orderHistory)
            } else {
                openHome()
            }
        } else {
            openHome()
        }

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment()).commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FaqsFragment())
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Log out")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences =
                            getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                    dialog.setNegativeButton("No") { text, listener ->
                    }
                    dialog.create()
                    dialog.show()
                }
        }
        return@setNavigationItemSelectedListener true
    }
}

fun setUpToolbar() {
    setSupportActionBar(toolbar)
    supportActionBar?.title = "Toolbar"
    supportActionBar?.setHomeButtonEnabled(true)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun openHome() {
    val fragment = HomeFragment()
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.frame, fragment)
    transaction.commit()
    supportActionBar?.title = "Restaurants"
    navigationView.setCheckedItem(R.id.home)
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val id = item.itemId
    if (id == android.R.id.home) {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    return super.onOptionsItemSelected(item)
}

override fun onBackPressed() {
    val frag = supportFragmentManager.findFragmentById(R.id.frame)
    when (frag) {
        !is HomeFragment -> openHome()
        else -> super.onBackPressed()
    }
}
}