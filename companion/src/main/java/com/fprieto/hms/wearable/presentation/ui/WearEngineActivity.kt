package com.fprieto.hms.wearable.presentation.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.fprieto.hms.wearable.R
import com.fprieto.hms.wearable.databinding.ActivityWearEngineBinding
import com.fprieto.hms.wearable.di.PREF_KEY_BASE_URL
import com.fprieto.hms.wearable.model.audiobookshelf.User
import com.fprieto.hms.wearable.net.AuthManager // Assuming a new class for token management
import com.huawei.hihealthkit.data.HiHealthExtendScope
import com.huawei.hms.common.ApiException
import com.huawei.hms.hihealth.HuaweiHiHealth
import com.huawei.hms.hihealth.SettingController
import com.huawei.hms.hihealth.data.Scopes
import com.huawei.hms.hihealth.data.Scopes.HEALTHKIT_STEP_READ
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService
import com.huawei.wearengine.HiWear
import com.huawei.wearengine.auth.AuthCallback
import com.huawei.wearengine.auth.AuthClient
import com.huawei.wearengine.auth.Permission
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private val hiWearPermissions = arrayOf(Permission.DEVICE_MANAGER, Permission.NOTIFY)
private val scopes = arrayOf(
    Scopes.HEALTHKIT_STEP_READ,
    Scopes.HEALTHKIT_CALORIES_READ,
    Scopes.HEALTHKIT_ACTIVITY_READ,
    Scopes.HEALTHKIT_LOCATION_READ,
    Scopes.HEALTHKIT_SLEEP_READ,
    Scopes.HEALTHKIT_HEARTRATE_READ,
    Scopes.HEALTHKIT_OXYGENSTATURATION_READ,
    Scopes.HEALTHKIT_BLOODGLUCOSE_READ,
    Scopes.HEALTHKIT_ACTIVITY_RECORD_READ
)

private val extendedScopes = arrayListOf(
    Scope(HiHealthExtendScope.HEALTHKIT_EXTEND_SPORT_READ)
)

class WearEngineActivity : DaggerAppCompatActivity() {

    private val authClient: AuthClient by lazy {
        HiWear.getAuthClient(this)
    }

    private val settingController: SettingController by lazy {
        HuaweiHiHealth.getSettingController(this)
    }

    private lateinit var binding: ActivityWearEngineBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.fragmentFactory = fragmentFactory
        binding = ActivityWearEngineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        // checkPermissions() // Keep HiWear permissions if still needed for other features - Commenting out for now
        // requestAuthorization( settingController.requestAuthorizationIntent(scopes, true)) // Keep HealthKit if needed - Commenting out for now
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.mainNavigationFragment
        ) as NavHostFragment

        navController = navHostFragment.navController

        if (authManager.isLoggedIn()) {
            // If logged in, set the graph's start destination to library list
            // This requires the graph to be set first, then navigate
            val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)
            navGraph.setStartDestination(R.id.libraryListFragment)
            navController.graph = navGraph
        } else {
            // Default start destination is LoginFragment as set in XML
            // Or explicitly set it if there are issues with XML not being primary
             val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)
             navGraph.setStartDestination(R.id.loginFragment)
             navController.graph = navGraph
        }

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> binding.bottomNavigation.visibility = View.GONE
                else -> binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }


    /* private fun requestExtendedAuth() {
        val idAuthParamsHelper =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
        val idAuthParams = idAuthParamsHelper
            .setIdToken()
            .setAccessToken()
            .setScopeList(extendedScopes)
            .createParams()

        val authService = HuaweiIdAuthManager.getService(applicationContext, idAuthParams)
        val authHuaweiIdTask = authService.silentSignIn()
        authHuaweiIdTask.addOnSuccessListener {
            "Authorization success".logResult()
        }.addOnFailureListener {
            "The silent sign-in fails. This indicates that the authorization has not been granted by the current account. Error: ${it.localizedMessage}".logResult()
            if (it is ApiException) {
                "sign failed status: ${(it as? ApiException)?.statusCode}".logResult()
                "begin sign in by intent".logResult()
                requestAuthorization(authService.signInIntent)
            }
        }
    }

    private fun setBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.mainNavigationFragment
        ) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun requestAuthorization(requestAuthorizationIntent: Intent) {
        registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                "HealthKit Result OK".logResult()
            } else {
                "HealthKit Result NOK".logResult()
            }
        }.launch(requestAuthorizationIntent)
    }

    private fun checkPermissions() {
        try {
            authClient.checkPermissions(hiWearPermissions).isSuccessful.let { allGranted ->
                "All permission granted: $allGranted".logResult()
                if (!allGranted) {
                    askForHiWearPermissions()
                }
            }
        } catch (e: Exception) {
            "Failed to ask for permissions".logResult()
            onNotAllPermissionGranted()
        }
    }

    private fun askForHiWearPermissions() {
        authClient.requestPermission(object : AuthCallback {
            override fun onOk(permissions: Array<Permission>) {
                if (!permissions.map { it.name }.containsAll(hiWearPermissions.map { it.name })) {
                    onNotAllPermissionGranted()
                }
            }

            override fun onCancel() {
                onNotAllPermissionGranted()
            }
        }, *hiWearPermissions)
    }

    private fun onNotAllPermissionGranted() {
        runOnUiThread {
            "Not all permission are granted for HiWear!".logResult()
            finish()
        }
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.mainNavigationFragment).navigateUp()
}

private fun String.logResult() {
    Timber.d(this)
}
