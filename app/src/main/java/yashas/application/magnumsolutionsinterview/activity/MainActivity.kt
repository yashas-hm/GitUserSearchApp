package yashas.application.magnumsolutionsinterview.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import yashas.application.magnumsolutionsinterview.R
import yashas.application.magnumsolutionsinterview.SearchFragment

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)

        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame,
                SearchFragment()
            )
            .commit()
        supportActionBar?.title = "Search"
        drawerLayout.closeDrawers()
        navigationView.setCheckedItem(R.id.search)


        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                it.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId) {
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            SearchFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Search"
                    drawerLayout.closeDrawers()
                    navigationView.setCheckedItem(R.id.search)
                }

                R.id.result -> {
                    startActivity(Intent(this@MainActivity, ResultActivity::class.java))
                }

                R.id.exit -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Exit")
                    dialog.setMessage("Sure you want to exit?")
                    dialog.setPositiveButton("Yes") { _, _ ->
                        finishAffinity()
                    }
                    dialog.setNegativeButton("Exit") { _, _ ->

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
        supportActionBar?.title = "Search"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        val dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("Exit")
        dialog.setMessage("Sure you want to exit?")
        dialog.setPositiveButton("Yes") { _, _ ->
            finish()
        }
        dialog.setNegativeButton("Exit") { _, _ ->

        }
        dialog.create()
        dialog.show()
    }

}