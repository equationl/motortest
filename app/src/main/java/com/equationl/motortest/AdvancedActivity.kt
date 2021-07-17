package com.equationl.motortest

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.method.LinkMovementMethod
import android.transition.Explode
import android.transition.Fade
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import com.equationl.motortest.adapter.MainDiyDialogItemAdapter
import com.equationl.motortest.database.DatabaseHelper
import com.equationl.motortest.database.VibrationEffects
import com.equationl.motortest.sharedPreferences.Preference
import com.equationl.motortest.util.Utils
import com.equationl.motortest.util.VibratorHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_advanced.*
import kotlinx.android.synthetic.main.content_advanced.*
import kotlinx.android.synthetic.main.dialog_advanced_help.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class AdvancedActivity : AppCompatActivity() {
    var amplitude = 255
    var rate = 50
    private var hasAmplitudeControl = true
    lateinit var vibrator: VibratorHelper
    private var isUseHighAccuracy: Boolean by Preference(this, "isUseHighAccuracy", false)
    private var isRunInBackground: Boolean by Preference(this, "isRunInBackground", false)
    private var isUseCustomizeSystemDefault: Boolean by Preference(this, "isUseCustomizeSystemDefault", false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.enterTransition = Explode()
        window.exitTransition = Fade()

        setContentView(R.layout.activity_advanced)
        setSupportActionBar(advanced_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        vibrator = VibratorHelper(getSystemService(VIBRATOR_SERVICE) as Vibrator)

        checkDevice()
        initPreVibrator()
        initHighAccuracy()
        listenerSeekBar()
        listenerBtn()

        advanced_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator.cancel()
    }

    override fun onPause() {
        super.onPause()

        if (!isRunInBackground) {
            vibrator.cancel()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_advanced, menu)

        menu.findItem(R.id.advanced_action_high_accuracy).isChecked = isUseHighAccuracy
        menu.findItem(R.id.advanced_action_run_background).isChecked = isRunInBackground
        menu.findItem(R.id.advanced_action_customize_system_default).isChecked = isUseCustomizeSystemDefault

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.advanced_action_high_accuracy -> {
                if (item.isChecked) {
                    isUseHighAccuracy = false
                    item.isChecked = isUseHighAccuracy
                    main_pro_amplitude2.visibility = View.VISIBLE
                    main_pro_rate2.visibility = View.VISIBLE
                    main_pro_amplitude.visibility = View.INVISIBLE
                    main_pro_rate.visibility = View.INVISIBLE
                }
                else {
                    isUseHighAccuracy = true
                    item.isChecked = isUseHighAccuracy
                    main_pro_amplitude.visibility = View.VISIBLE
                    main_pro_rate.visibility = View.VISIBLE
                    main_pro_amplitude2.visibility = View.INVISIBLE
                    main_pro_rate2.visibility = View.INVISIBLE
                }
            }
            R.id.advanced_action_run_background -> {
                if (item.isChecked) {
                    isRunInBackground = false
                    item.isChecked = isRunInBackground
                }
                else {
                    isRunInBackground = true
                    item.isChecked = isRunInBackground
                }
            }
            R.id.advanced_action_customize_system_default -> {
                if (item.isChecked) {
                    setSystemDefaultState(View.VISIBLE)
                    isUseCustomizeSystemDefault = false
                    item.isChecked = isUseCustomizeSystemDefault
                }
                else {
                    setSystemDefaultState(View.GONE)
                    isUseCustomizeSystemDefault = true
                    item.isChecked = isUseCustomizeSystemDefault
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setSystemDefaultState(isVisibility: Int) {
        main_layout_system_customize.visibility = if (isVisibility==View.GONE) View.VISIBLE else View.GONE
        main_btn_system_customize_start.visibility = if (isVisibility==View.GONE) View.VISIBLE else View.GONE
        main_button_click.visibility = isVisibility
        main_button_double_click.visibility = isVisibility
        main_button_heavy_click.visibility = isVisibility
        main_button_tick.visibility = isVisibility
        val appPredefinedParams = main_text_app_predefined.layoutParams as ConstraintLayout.LayoutParams
        appPredefinedParams.topToBottom = if (isVisibility==View.GONE) main_layout_system_customize.id else main_button_click.id
        main_text_app_predefined.requestLayout()
    }

    private fun checkDevice() {
        val hasVibrator = vibrator.hasVibrator()
        if (!hasVibrator) {
            Toast.makeText(applicationContext, R.string.advanced_toast_notSupportVibrator, Toast.LENGTH_LONG).show()
            finish()
        }

        if (!vibrator.hasAmplitudeControl()) {
            hasAmplitudeControl = false
            main_text_amplitude.text = getString(R.string.advanced_text_notSupport_amplitude)
            main_text_amplitude.setTextColor(Color.RED)
        }
    }

    private fun initPreVibrator() {
        if (isUseCustomizeSystemDefault) {
            setSystemDefaultState(View.GONE)
        }
        if (Build.VERSION.SDK_INT < 29) {
            main_text_predefined.text = getString(R.string.advanced_text_predefined_notSupport)
            main_text_predefined.setTextColor(Color.RED)
            main_button_click.isClickable = false
            main_button_heavy_click.isClickable = false
            main_button_double_click.isClickable = false
            main_button_tick.isClickable = false
        }
        else {
            main_button_click.setOnClickListener {
                vibrator.vibratePredefined(VibrationEffect.EFFECT_CLICK)
            }

            main_button_double_click.setOnClickListener {
                vibrator.vibratePredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
            }

            main_button_heavy_click.setOnClickListener {
                vibrator.vibratePredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            }

            main_button_tick.setOnClickListener {
                vibrator.vibratePredefined(VibrationEffect.EFFECT_TICK)
            }
            main_btn_system_customize_start.setOnClickListener {
                try {
                    vibrator.vibratePredefined(main_edit_system_customize.text.toString().toInt())
                } catch (e: java.lang.IllegalArgumentException) {
                    Toast.makeText(applicationContext, R.string.advanced_toast_systemCustomize_notSupportVlue, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initHighAccuracy() {
        if (isUseHighAccuracy) {
            main_pro_amplitude.visibility = View.VISIBLE
            main_pro_rate.visibility = View.VISIBLE
            main_pro_amplitude2.visibility = View.INVISIBLE
            main_pro_rate2.visibility = View.INVISIBLE
        }
    }

    private fun listenerSeekBar() {
        main_pro_amplitude.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                main_pro_text.text = progress.toString()
                amplitude = if (progress!=0) progress else 1
                vibrator.vibrateOneShot(1, amplitude)
            }
        })

        main_pro_amplitude2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressl = progress * 10
                main_pro_text.text = progressl.toString()
                amplitude = if (progress!=0) progress*10 else 10
                vibrator.vibrateOneShot(10, amplitude)
            }
        })

        main_pro_rate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rate = progress
                vibrator.vibrateOneShot(1, amplitude)
            }
        })

        main_pro_rate2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                rate = progress*10
                vibrator.vibrateOneShot(10, amplitude)
            }
        })
    }

    private fun listenerBtn() {
        main_btn_free_stop.setOnClickListener {
            vibrator.cancel()
        }

        main_btn_free_start.setOnClickListener {
            var rate2 = 100 - rate
            rate2 = if (rate2 < 1) 1 else rate2
            val timings = longArrayOf(rate2 * 10.toLong(), rate2.toLong())
            if (rate2 == 100) {
                timings[0] = 3_600_000
                timings[1] = 3_600_000
            }
            val amplitudes = intArrayOf(amplitude, 0)
            vibrator.vibrate(timings, amplitudes, 0)
        }

        main_btn_pre_start.setOnClickListener {
            when (main_spinner_pre.selectedItemPosition) {
                0 -> createHeartBeat()
                1 -> createOffBeat()
                2 -> createRock()
                3 -> createTossCoin()
                4 -> Toast.makeText(applicationContext, getString(R.string.advanced_btn_pre_wait), Toast.LENGTH_LONG).show()
            }
        }

        main_btn_pre_stop.setOnClickListener {
            vibrator.cancel()
        }

        main_btn_diy_stop.setOnClickListener {
            vibrator.cancel()
        }

        main_edit_diy_timings.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                main_layout_diy_timings.isErrorEnabled = false
            } else {
                if (main_edit_diy_timings.text.toString() == "") {
                    main_layout_diy_timings.error = getString(R.string.advanced_edit_diy_tip_text_empty)
                }
            }
        }

        main_edit_diy_amplitudes.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                main_layout_diy_amplitudes.isErrorEnabled = false
            } else {
                if (main_edit_diy_amplitudes.text.toString() == "") {
                    main_layout_diy_amplitudes.error = getString(R.string.advanced_edit_diy_tip_text_empty)
                }
            }
        }

        main_edit_diy_repeat.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                main_layout_diy_repeat.isErrorEnabled = false
            } else {
                if (main_edit_diy_repeat.text.toString() == "") {
                    main_layout_diy_repeat.error = getString(R.string.advanced_edit_diy_tip_text_empty)
                }
            }
        }

        main_btn_diy_menu.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_advanced_btn_diy , popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.main_menu_btn_diy_save -> {
                        diyClickSave()
                    }
                    R.id.main_menu_btn_diy_open -> {
                        diyClickOpen()
                    }
                    R.id.main_menu_btn_diy_share -> {
                        diyClickShare()
                    }
                    R.id.main_menu_btn_diy_import -> {
                        diyClickImport()
                    }
                    R.id.main_menu_btn_diy_visualization -> {
                        //TODO
                        startActivity(Intent().setClass(this, VisualizationActivity::class.java))
                    }
                }
                return@setOnMenuItemClickListener false
            }
            popup.show()
        }

        main_btn_diy_start.setOnClickListener {
            main_layout_diy_repeat.isErrorEnabled = false
            main_layout_diy_amplitudes.isErrorEnabled = false
            main_layout_diy_timings.isErrorEnabled = false

            val data = checkDiyData() ?: return@setOnClickListener
            val timings = data[0]
            val amplitude = data[1]
            val repeateI = data[2]

            val timingsL = timings.split(",").map { it.toLong() }.toLongArray()
            val amplitudeI = amplitude.split(",").map { it.toInt() }.toIntArray()

            try {
                vibrator.vibrate(timingsL, amplitudeI, Integer.parseInt(repeateI))
            } catch (e: IllegalArgumentException) {
                Log.e("el", "error in try create vibrationEffect", e)
                main_layout_diy_repeat.error = "创建振动失败:$e"
                return@setOnClickListener
            }
        }

        main_fab_help.setOnClickListener {
            //val inflater = LayoutInflater.from(this)
            val layout = View.inflate(this, R.layout.dialog_advanced_help, null)
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.advanced_fab_help_title))
                .setPositiveButton(getString(R.string.advanced_fab_help_btn_close), null)
                .setView(layout)
                .show()
            layout.advanced_dialog_text_content.movementMethod = LinkMovementMethod.getInstance()
            layout.advanced_dialog_text_content.text = Utils.text2html(getString(R.string.advanced_dialog_help_content_summary))
            layout.advanced_dialog_tab_layout.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> {
                            layout.advanced_dialog_text_content.text = Utils.text2html(getString(R.string.advanced_dialog_help_content_summary))
                        }
                        1 -> {
                            layout.advanced_dialog_text_content.text = Utils.text2html(getString(R.string.advanced_dialog_help_content_usage))
                        }
                        2 -> {
                            layout.advanced_dialog_text_content.text = Utils.text2html(getString(R.string.advanced_dialog_help_content_about))
                        }
                        3 -> {
                            layout.advanced_dialog_text_content.text = Utils.text2html(getString(R.string.advanced_dialog_help_content_update))
                        }
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun createHeartBeat() {
        val timings = longArrayOf(150, 50, 150, 650)
        val amplitudes = intArrayOf(255, 0, 150, 0)
        vibrator.vibrate(timings, amplitudes, 0)
    }

    private fun createOffBeat() {
        val timings = longArrayOf(130,70,130,70,130,70,130,70,130,70,130)
        val amplitudes = intArrayOf(100,0,100,0,255,0,200,0,100,0,100)
        vibrator.vibrate(timings, amplitudes, 0)
    }

    private fun createRock() {
        val timings = longArrayOf(150,150,150,75,75,650)
        val amplitudes = intArrayOf(150,0,150,0,255,0)
        vibrator.vibrate(timings, amplitudes, 0)
    }

    private fun createTossCoin() {
        val timings = longArrayOf(10,180,10,90, 4,  90, 7, 80,2, 120,    4,50,2,40,1,40,     4,50,2,40,1,40,      4,50,2,40,1,40)
        val amplitudes = intArrayOf(255,0, 255,0, 240,0, 240,0, 240,0,    230,0,230,0,230,0,   220,0,220,0,220,0,   210,0,210,0,210,0)
        vibrator.vibrate(timings, amplitudes, -1)
    }

    private fun diyClickOpen() {
        var alertDialog: AlertDialog? = null
        val databaseHelper = DatabaseHelper.getInstance(applicationContext)
        val list = databaseHelper.getAll()
        val nameItems = mutableListOf<String>()
        val dateItems = mutableListOf<String>()
        for (i in list.indices) {
            nameItems.add(list[i].name)
            dateItems.add(SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.CHINA).format(list[i].createTime))
        }

        val mainDiyDialogItemAdapter = MainDiyDialogItemAdapter(nameItems, dateItems, this)
        mainDiyDialogItemAdapter.setOnItemClickListener(object: MainDiyDialogItemAdapter.OnItemClickListener {
            override fun onClickDelete(position: Int) {
                databaseHelper.delete(list[position])
                nameItems.removeAt(position)
                dateItems.removeAt(position)
                mainDiyDialogItemAdapter.notifyDataSetChanged()
            }

            override fun onClickItem(position: Int) {
                if (alertDialog != null) {
                    if (alertDialog!!.isShowing) {
                        alertDialog!!.dismiss()
                    }
                }
                main_edit_diy_timings.setText(list[position].timings)
                main_edit_diy_amplitudes.setText(list[position].amplitude)
                main_edit_diy_repeat.setText(list[position].repeate.toString())
            }
        })

        alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.advanced_diy_open_choise_title))
            .setPositiveButton(getString(R.string.advanced_diy_open_choise_btn_close), null)
            .setAdapter(mainDiyDialogItemAdapter, null)
            .show()
    }

    private fun checkDiyData(): List<String>? {
        if (main_edit_diy_timings.text.toString() == "") {
            main_layout_diy_timings.error = getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }

        if (main_edit_diy_amplitudes.text.toString() == "") {
            main_layout_diy_amplitudes.error = getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }
        if (main_edit_diy_repeat.text.toString() == "") {
            main_layout_diy_repeat.error = getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }

        val timings = main_edit_diy_timings.text.toString()
            .replace(" ".toRegex(), "")
            .replace("\n".toRegex(), "")
        val timingsT = timings.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val amplitude = main_edit_diy_amplitudes.text.toString()
            .replace(" ".toRegex(), "")
            .replace("\n".toRegex(), "")
        val amplitudeT = amplitude.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val repeateI: Int
        try {
            repeateI = main_edit_diy_repeat.text.toString().toInt()
        } catch (e: NumberFormatException) {
            main_layout_diy_repeat.error = getString(R.string.advanced_edit_diy_tip_number_error)
            return null
        }

        for (dataT in timingsT) {
            val data: Long
            try {
                data =dataT.toLong()
            } catch (e: NumberFormatException) {
                main_layout_diy_timings.error = getString(R.string.advanced_edit_diy_tip_number_error)
                return null
            }
            if (data < 0) {
                main_layout_diy_timings.error = getString(R.string.advanced_diy_check_timings_less_zero)
                return null
            }
        }

        for (dataT in amplitudeT) {
            val data: Int
            try {
                data =dataT.toInt()
            } catch (e: NumberFormatException) {
                main_layout_diy_amplitudes.error = getString(R.string.advanced_edit_diy_tip_number_error)
                return null
            }
            if (data < 0 || data > 255) {
                main_layout_diy_amplitudes.error = getString(R.string.advanced_diy_check_amplitude_number_error)
                return null
            }
        }

        if (amplitudeT.size != timingsT.size) {
            return if (amplitudeT.size > timingsT.size) {
                main_layout_diy_amplitudes.error = getString(R.string.advanced_diy_check_amp_bigThan_tim)
                null
            } else {
                main_layout_diy_timings.error = getString(R.string.advanced_diy_check_tim_bigThan_amp)
                null
            }
        }

        if (repeateI >= amplitudeT.size) {
            main_layout_diy_repeat.error = getString(R.string.advanced_diy_check_reapeat_outOfIndex)
            return null
        }
        if (repeateI < -1) {
            main_layout_diy_repeat.error = getString(R.string.advanced_diy_check_repeat_wrong_index)
            return null
        }

        return listOf(timings, amplitude, repeateI.toString())
    }

    private fun diyClickSave() {
        val data = checkDiyData() ?: return

        val timings = data[0]
        val amplitude = data[1]
        val repeateI = data[2]

        val editText = EditText(applicationContext)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.advanced_diy_save_fileName_title))
            .setView(editText)
            .setPositiveButton(getString(R.string.advanced_diy_save_fileName_btn_sure)) { _, _ ->
                val text = editText.text.toString()
                if (text == "") {
                    Toast.makeText(applicationContext, getString(R.string.advanced_diy_save_fileName_isEmpty),Toast.LENGTH_LONG).show()
                }
                else {
                    val databaseHelper = DatabaseHelper.getInstance(applicationContext)
                    databaseHelper.insert(VibrationEffects(null, timings, amplitude, Integer.parseInt(repeateI), text, System.currentTimeMillis()))
                    Toast.makeText(applicationContext, getString(R.string.advanced_diy_save_success),Toast.LENGTH_LONG).show()
                }
            }
            .show()
    }

    private fun diyClickShare() {
        val data = checkDiyData() ?: return

        val timings = data[0]
        val amplitude = data[1]
        val repeateI = data[2]

        var text = "{\"timings\": \"$timings\",\"amplitude\": \"$amplitude\",\"repeate\": \"$repeateI\"}"
        text = Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)

        intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, getText(R.string.app_name)))
    }

    private fun diyClickImport() {
        val editText = EditText(applicationContext)
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.advanced_diy_import_dialog_title))
            .setView(editText)
            .setPositiveButton(getString(R.string.advanced_diy_import_dialog_btn_sure)) { _, _ ->
                var text = editText.text.toString()
                if (text == "") {
                    Toast.makeText(applicationContext, getString(R.string.advanced_diy_import_text_isEmpty),Toast.LENGTH_LONG).show()
                }
                else {
                    try {
                        text = String(Base64.decode(text, Base64.DEFAULT))
                        val array = JSONObject(text)
                        val timings = array.getString("timings")
                        val amplitude = array.getString("amplitude")
                        val repeate = array.getString("repeate")
                        main_edit_diy_timings.setText(timings)
                        main_edit_diy_amplitudes.setText(amplitude)
                        main_edit_diy_repeat.setText(repeate)
                    }
                    catch (e: Exception) {
                        Log.e("el", "import fail:", e)
                        Toast.makeText(applicationContext, getString(R.string.advanced_diy_import_fail),Toast.LENGTH_LONG).show()
                    }
                }
            }
            .show()
    }
}
