package com.majid.androidassignment.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.majid.androidassignment.R
import com.majid.androidassignment.app.App
import com.majid.androidassignment.databinding.ActivityMainBinding
import com.majid.androidassignment.db.DBDEFINITIONS
import com.majid.androidassignment.db.SharedPref
import com.majid.androidassignment.service.LocationService
import com.majid.androidassignment.utils.ViewUtils
import com.majid.androidassignment.utils.hasLocationPermission
import com.majid.androidassignment.utils.hideVisibility
import com.majid.androidassignment.utils.showUnderlined
import com.majid.androidassignment.utils.showVisibility
import com.majid.androidassignment.vewmdels.MainViewModel
import com.majid.androidassignment.view.dialogs.BUTTON
import com.majid.androidassignment.view.dialogs.ConfirmDialogModel
import com.majid.androidassignment.view.dialogs.CustomDialog
import com.majid.androidassignment.view.fragments.CommentsFragment
import com.majid.androidassignment.view.fragments.LocationDetailsFragment
import com.majid.androidassignment.view.fragments.NotificationsFragment
import com.majid.androidassignment.view.fragments.PostDetailsFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var sharedPref: SharedPref

    private val CHANNEL_ID = "notification_channel"
    private val NOTIFICATION_ID = 101
    private val NOTIFICATION_REQUEST_CODE = 123

    private val MAX_LOCATIONS = 5
    private val locationList: ArrayList<String> by lazy { ArrayList<String>(MAX_LOCATIONS) }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        try {


            if (isGranted) {

                Log.w(TAGGED, "Notification Permissions Granted")
            } else {
                askNotificationPermission()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private val locationReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == "LOCATION_UPDATE") {
                    val location =
                        intent.getParcelableExtra("location",Location::class.java)
                    location?.let {
                        binding.toolbar.switchLocation.isChecked = true
                        updateLocationUI(it)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        openFragment(PostDetailsFragment.newInstance(), "")

        initView()
        checkPermissions()

        handleBackPress()
        askNotificationPermission()
        setListeners()
        setObservers()

        createNotificationChannel()
        handleNotificationClick(intent)


    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(locationReceiver, IntentFilter("LOCATION_UPDATE"))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(locationReceiver)
    }

    private fun updateLocationUI(location: Location) {
        val lat = location.latitude
        val long = location.longitude
        // Update any other UI elements with location details


        val formattedLocation = "Lat: ${location.latitude}, Long: ${location.longitude}"
        if (locationList.size == MAX_LOCATIONS) {
            locationList.removeFirst()
        }
        locationList.add(formattedLocation)
        updateLocationInView()
    }

    private fun updateLocationInView() {

        Log.w(TAGGED, "Last 5 Locations:\n")
        for (location in locationList) {
            Log.w(TAGGED, "Location = $location\n")
        }

        mainViewModel.locationList.postValue(locationList)
    }

    private fun initView() {

        binding.toolbar.tvText.showUnderlined()

    }

    private fun setListeners() {

        binding.toolbar.switchLocation.setOnCheckedChangeListener { buttonView, isChecked ->

            when (isChecked) {
                true -> {
                    startLocationService()
                }

                false -> {
                    stopLocationService()
                }
            }

        }

        binding.toolbar.ivNotification.setOnClickListener {
            // show dialog to send ot view notifications

            showNotificationsDialog()
        }


        binding.toolbar.tvText.setOnClickListener {
            openFragment(LocationDetailsFragment.newInstance(), "")
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNotificationClick(intent)
    }

    private fun handleNotificationClick(intent: Intent?) {
        intent?.let {
            Log.w(TAGGED, "Intent Extras = ${intent.extras}")
            Log.w(TAGGED, "Notification Clicked = ${intent?.action}")

            if (intent.action == "com.majid.androidassignment.NOTIFICATION_CLICK_ACTION") {
                // Notification clicked, open NotificationsFragment
                openFragment(NotificationsFragment.newInstance(), "")
            } else {
                // No notification clicked, open PostDetailsFragment
                openFragment(PostDetailsFragment.newInstance(), "")
            }

            try {
                sharedPref.getInteger(DBDEFINITIONS.Notification_Count).let {
                    sharedPref.setData(DBDEFINITIONS.Notification_Count, it + 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setObservers() {


        mainViewModel.getSnackbar().observe(this) { event ->
            val objects: Array<Any>? = event.getContentIfNotHandled()
            if (objects != null) {

                if (objects.isNotEmpty()) {

                    try {
                        var message = ""
                        var isError = false
                        if (objects[0] is String) {
                            message = objects[0] as String
                        }
                        if (objects[0] is Boolean) {
                            isError = objects[0] as Boolean
                        }

                        if (objects.size > 1) {
                            if (objects[1] is String) {
                                message = objects[1] as String
                            }
                            if (objects[1] is Boolean) {
                                isError = objects[1] as Boolean
                            }
                        }


                        ViewUtils.customSnackBar(this, message, isError)


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
        mainViewModel.getOpenCommentsFragment().observe(this) { event ->
            val objects: Array<Any>? = event.getContentIfNotHandled()
            if (objects != null) {
                openFragmentInSecondContainer(CommentsFragment.newInstance(), "")
            }
        }

        mainViewModel.getOpenNotificationsFragment().observe(this) { event ->
            val objects: Array<Any>? = event.getContentIfNotHandled()
            if (objects != null) {
                openFragment(NotificationsFragment.newInstance(), "")
            }
        }

        mainViewModel.getOpenLocationsFragment().observe(this) { event ->
            val objects: Array<Any>? = event.getContentIfNotHandled()
            if (objects != null) {
                openFragment(LocationDetailsFragment.newInstance(), "")
            }
        }
    }

    private fun checkPermissions() {
        if (!App.context.hasLocationPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS,
                ), 0
            )

        }
    }

    private fun startLocationService() {


        Intent(App.context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }

    private fun stopLocationService() {

        Intent(App.context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            startService(this)
        }
    }

    private fun openFragment(fragment: Fragment, tag: String) {

        try {

            binding.containerSecond.hideVisibility()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, fragment, tag)
            transaction.addToBackStack(tag)
            transaction.commit()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun openFragmentInSecondContainer(fragment: Fragment, tag: String) {

        try {

          showSecondFragmentContainer()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container_second, fragment, tag)
            transaction.addToBackStack(tag)
            transaction.commit()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var hasBackButtonClicked = false

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Back is pressed... Finishing the activity

                    val count = supportFragmentManager.backStackEntryCount


                    if (count <= 1) {
                        if (hasBackButtonClicked) {

                            finish()
                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                "Press BACK again to exit.",
                                Toast.LENGTH_SHORT
                            ).show()
                            hasBackButtonClicked = true
                            try {
                                val handler = Handler(Looper.myLooper()!!)
                                handler.postDelayed({
                                    hasBackButtonClicked = false

                                }, 1000)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    } else {

                        hideSecondFragmentContainer()
                        supportFragmentManager.popBackStack()
                    }
                }
            })
    }


    private fun showNotificationsDialog() {

        try {


            val model = ConfirmDialogModel()


            /**
             *         to show Status Dialog
             * */
//        model.isShowStatus = false
//        model.actionStatus = true
            model.isCancellable = true
//        model.isAutoHide =false
//        model.autoHideDuration = 3000

            model.listener = object : ConfirmDialogModel.IDialogListener {
                override fun onButtonClicked(model: Any, clickedButton: BUTTON) {

                    if (clickedButton == BUTTON.POSITIVE) {
                        Toast.makeText(this@MainActivity, "Send Notification", Toast.LENGTH_LONG)
                            .show()

                        showNotification()

                    }

                    if (clickedButton == BUTTON.NEGATIVE) {


                    }

                }


            }
            val dialog = CustomDialog.newInstance(model, this)
            dialog.isCancelable = model.isCancellable
            dialog.show(supportFragmentManager, "")


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun askNotificationPermission() {

        try {


            // This is only necessary for API level >= 33 (TIRAMISU)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermissions()


                    // FCM SDK (and your app) can post notifications.


                } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // TODO: display an educational UI explaining to the user the features that will be enabled
                    //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                    //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                    //       If the user selects "No thanks," allow the user to continue without notifications.
                } else {
                    // Directly ask for the permission
//                showNotificationPermissionDialog()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notificationPermissions() {

        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create channel to show notifications.
                val channelId = getString(R.string.default_notification_channel_id)
                val channelName = getString(R.string.app_name)
                val notificationManager = getSystemService(NotificationManager::class.java)

                notificationManager?.createNotificationChannel(
                    NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_HIGH,
                    ),
                )


            }

            // If a notification message is tapped, any data accompanying the notification
            // message is available in the intent extras. In this sample the launcher
            // intent is fired when the notification is tapped, so any accompanying data would
            // be handled here. If you want a different intent fired, set the click_action
            // field of the notification message to the desired intent. The launcher intent
            // is used when no click_action is specified.
            //
            // Handle possible data accompanying notification message.
            // [START handle_data_extras]
            intent.extras?.let {
                Log.w(TAGGED, "Notification Received")

//            mainViewModel.setOpenChatsFragment(arrayOf(1,2))
                Log.w(TAGGED, "" + it)
                for (key in it.keySet()) {
                    val value = intent.extras?.getString(key)
                    Log.w(TAGGED, "Key: $key Value: $value")
                }
            }
            // [END handle_data_extras]

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("MissingPermission")
    fun showNotification() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Set a unique action for the notification
            action = "com.majid.androidassignment.NOTIFICATION_CLICK_ACTION"
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = getCustomSoundUri()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("My Notification")
            .setContentText("This is a notification with custom sound.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(soundUri) // Set custom sound
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun getCustomSoundUri(): Uri? {
        return try {
            val resourceId = R.raw.custom_sound
            Uri.parse("android.resource://${packageName}/${resourceId}")
        } catch (e: Exception) {
            Log.e(TAGGED, "Failed to load custom sound: ${e.printStackTrace()}")
            null
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Channel Name"
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val TAGGED = "CustomLogStatus"
    }

    private fun hideSecondFragmentContainer(){
        binding.containerSecond.hideVisibility()
        mainViewModel.isCommentFragmentShown.postValue(false)
    }

    private fun showSecondFragmentContainer(){
        binding.containerSecond.showVisibility()
        mainViewModel.isCommentFragmentShown.postValue(true)
    }


}