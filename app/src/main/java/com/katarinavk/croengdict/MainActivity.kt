package net.katarinavk.croengdict

import android.content.res.AssetManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import net.katarinavk.croengdict.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private val dictionaryFragment = DictionaryFragment()
    private val quizFragment = QuizFragment()
    private val historyActivity = HistoryActivity()
    private val savedActivity = SavedActivity()
    private val homeFragment = HomeFragment()
    var dictionary: Dictionary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dictionary = Dictionary(
            resources.assets.open("en_hr.txt", AssetManager.ACCESS_STREAMING)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.main_nav_host) //Initialising navController
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.home,
            R.id.history, R.id.saved, R.id.Dictionary, R.id.Quiz,
        ) //Pass the ids of fragments from nav_graph which you dont want to show back button in toolbar
            .setOpenableLayout(binding.mainDrawerLayout) //Pass the drawer layout id from activity xml
            .build()

        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        ) //Setup toolbar with back button and drawer icon according to appBarConfiguration

        visibilityNavElements(navController) //If you want to hide drawer or bottom navigation configure that in this function

        binding.mainBottomNavigationView.menu.getItem(0).isCheckable = false
        binding.mainBottomNavigationView.setOnItemSelectedListener { item ->
            binding.mainBottomNavigationView.menu.getItem(0).isCheckable = true
            when (item.itemId) {
                R.id.Dictionary -> {
                    // novi fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_nav_host, dictionaryFragment).commit()
                    true
                }
                R.id.Quiz -> {
                    // novi fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_nav_host, quizFragment).commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun visibilityNavElements(navController: NavController) {

        //Listen for the change in fragment (navigation) and hide or show drawer or bottom navigation accordingly if required
        //Modify this according to your need
        //If you want you can implement logic to deselect(styling) the bottom navigation menu item when drawer item selected using listener

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.saved -> hideBottomNavigation()
                R.id.history -> hideBottomNavigation()
                else -> showBothNavigation()
            }
        }
    }


    private fun hideBottomNavigation() { //Hide bottom navigation
        binding.mainBottomNavigationView.visibility = View.GONE
        binding.mainNavigationView.visibility = View.VISIBLE
        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) //To unlock navigation drawer
        binding.mainNavigationView.setupWithNavController(navController) //Setup Drawer navigation with navController
    }

    private fun showBothNavigation() {
        binding.mainBottomNavigationView.visibility = View.VISIBLE
        binding.mainNavigationView.visibility = View.VISIBLE
        binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        setupNavControl() //To configure navController with drawer and bottom navigation
    }

    private fun setupNavControl() {
        binding.mainNavigationView.setupWithNavController(navController) //Setup Drawer navigation with navController
        binding.mainBottomNavigationView.setupWithNavController(navController) //Setup Bottom navigation with navController
    }

    fun exitApp() { //To exit the application call this function from fragment
        this.finishAffinity()
    }

    override fun onSupportNavigateUp(): Boolean { //Setup appBarConfiguration for back arrow
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        when { //If drawer layout is open close that on back pressed
            binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START) -> {
                binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
                super.onBackPressed() //If drawer is already in closed condition then go back
            }
        }
    }
}


