package com.equationl.motortest.compose.util

import android.content.Context
import android.content.Intent
import android.text.method.LinkMovementMethod
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AlertDialog
import com.equationl.motortest.R
import com.equationl.motortest.VisualizationActivity
import com.equationl.motortest.adapter.MainDiyDialogItemAdapter
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.database.DatabaseHelper
import com.equationl.motortest.database.VibrationEffects
import com.equationl.motortest.util.Utils
import com.equationl.motortest.util.VibratorHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.dialog_advanced_help.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

object AdvancedUtil {
    private const val TAG = "Advanced"

    fun clickHelpBtn(context: Context) {
        val layout = View.inflate(context, R.layout.dialog_advanced_help, null)
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.advanced_fab_help_title)
            .setPositiveButton(R.string.advanced_fab_help_btn_close, null)
            .setView(layout)
            .show()
        layout.advanced_dialog_text_content.movementMethod = LinkMovementMethod.getInstance()
        layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(R.string.advanced_dialog_help_content_summary))
        layout.advanced_dialog_tab_layout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(
                            R.string.advanced_dialog_help_content_summary))
                    }
                    1 -> {
                        layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(
                            R.string.advanced_dialog_help_content_usage))
                    }
                    2 -> {
                        layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(
                            R.string.advanced_dialog_help_content_about))
                    }
                    3 -> {
                        layout.advanced_dialog_text_content.text = Utils.text2html(context.getString(
                            R.string.advanced_dialog_help_content_update))
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun diyClickOpen(context: Context, viewMode: MyViewMode) {
        var alertDialog: AlertDialog? = null
        val databaseHelper = DatabaseHelper.getInstance(context)
        val list = databaseHelper.getAll()
        val nameItems = mutableListOf<String>()
        val dateItems = mutableListOf<String>()
        for (i in list.indices) {
            nameItems.add(list[i].name)
            dateItems.add(SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.CHINA).format(list[i].createTime))
        }

        val mainDiyDialogItemAdapter = MainDiyDialogItemAdapter(nameItems, dateItems, context)
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
                viewMode.timingsText = list[position].timings
                viewMode.amplitudesText = list[position].amplitude
                viewMode.repeatText = list[position].repeate.toString()
            }
        })

        alertDialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.advanced_diy_open_choise_title))
            .setPositiveButton(context.getString(R.string.advanced_diy_open_choise_btn_close), null)
            .setAdapter(mainDiyDialogItemAdapter, null)
            .show()
    }

    fun clickDiyStart(context: Context, viewMode: MyViewMode) {
        viewMode.clearDiyInputError()

        val data = checkDiyData(context, viewMode) ?: return
        val timing = data[0]
        val amplitude = data[1]
        val repeateI = data[2]

        val timingsL = timing.split(",").map { it.toLong() }.toLongArray()
        val amplitudeI = amplitude.split(",").map { it.toInt() }.toIntArray()

        try {
            VibratorHelper.instance.vibrate(timingsL, amplitudeI, Integer.parseInt(repeateI))
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "error in try create vibrationEffect", e)
            viewMode.repeatOnError = true
            viewMode.repeatErrorText = context.getString(R.string.advanced_diy_start_fail, e.message)
            return
        }
    }

    private fun checkDiyData(context: Context, viewMode: MyViewMode): List<String>? {
        val timings = viewMode.timingsText
        val amplitudes = viewMode.amplitudesText
        val repeat = viewMode.repeatText

        if (timings.isBlank()) {
            viewMode.timingsOnError = true
            viewMode.timingsErrorText = context.getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }

        if (amplitudes.isBlank()) {
            viewMode.amplitudesOnError = true
            viewMode.amplitudesErrorText = context.getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }
        if (repeat.isBlank()) {
            viewMode.repeatOnError = true
            viewMode.repeatErrorText = context.getString(R.string.advanced_edit_diy_tip_text_empty)
            return null
        }

        val timing = timings
            .replace(" ".toRegex(), "")
            .replace("\n".toRegex(), "")
        val timingsT = timing.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val amplitude = amplitudes
            .replace(" ".toRegex(), "")
            .replace("\n".toRegex(), "")
        val amplitudeT = amplitude.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val repeateI: Int
        try {
            repeateI = repeat.toInt()
        } catch (e: NumberFormatException) {
            viewMode.repeatOnError = true
            viewMode.repeatErrorText = context.getString(R.string.advanced_edit_diy_tip_number_error)
            return null
        }

        for (dataT in timingsT) {
            val data: Long
            try {
                data =dataT.toLong()
            } catch (e: NumberFormatException) {
                viewMode.timingsOnError = true
                viewMode.timingsErrorText = context.getString(R.string.advanced_edit_diy_tip_number_error)
                return null
            }
            if (data < 0) {
                viewMode.timingsOnError = true
                viewMode.timingsErrorText = context.getString(R.string.advanced_diy_check_timings_less_zero)
                return null
            }
        }

        for (dataT in amplitudeT) {
            val data: Int
            try {
                data =dataT.toInt()
            } catch (e: NumberFormatException) {
                viewMode.setAmplitudesError(context.getString(R.string.advanced_edit_diy_tip_number_error))
                return null
            }
            if (data < 0 || data > 255) {
                viewMode.setAmplitudesError(context.getString(R.string.advanced_diy_check_amplitude_number_error))
                return null
            }
        }

        if (amplitudeT.size != timingsT.size) {
            return if (amplitudeT.size > timingsT.size) {
                viewMode.setAmplitudesError(context.getString(R.string.advanced_diy_check_amp_bigThan_tim))
                null
            } else {
                viewMode.setTimingsError(context.getString(R.string.advanced_diy_check_tim_bigThan_amp))
                null
            }
        }

        if (repeateI >= amplitudeT.size) {
            viewMode.setRepeatError(context.getString(R.string.advanced_diy_check_reapeat_outOfIndex))
            return null
        }
        if (repeateI < -1) {
            viewMode.setRepeatError(context.getString(R.string.advanced_diy_check_repeat_wrong_index))
            return null
        }

        return listOf(timing, amplitude, repeateI.toString())
    }

    fun diyClickSave(
        context: Context,
        viewMode: MyViewMode,
        isFromVisualization: Boolean = false,
        timingsT: String? = "",
        amplitudeT: String? = ""
    ) {
        val timings: String
        val amplitude: String
        val repeateI: String
        if (isFromVisualization) {
            if (timingsT == null || amplitudeT == null) {
                Log.w(TAG, "diyClickSave: result data is null!")
                return
            }
            timings = timingsT
            amplitude = amplitudeT
            repeateI = "-1"
            viewMode.timingsText = timings
            viewMode.amplitudesText = amplitude
            viewMode.repeatText = repeateI
        }
        else {
            val data = checkDiyData(context, viewMode) ?: return

            timings = data[0]
            amplitude = data[1]
            repeateI = data[2]
        }

        val editText = EditText(context)
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.advanced_diy_save_fileName_title))
            .setView(editText)
            .setPositiveButton(context.getString(R.string.advanced_diy_save_fileName_btn_sure)) { _, _ ->
                val text = editText.text.toString()
                if (text == "") {
                    Toast.makeText(context, context.getString(R.string.advanced_diy_save_fileName_isEmpty),
                        Toast.LENGTH_LONG).show()
                }
                else {
                    val databaseHelper = DatabaseHelper.getInstance(context)
                    databaseHelper.insert(VibrationEffects(null, timings, amplitude, Integer.parseInt(repeateI), text, System.currentTimeMillis()))
                    Toast.makeText(context, context.getString(R.string.advanced_diy_save_success),
                        Toast.LENGTH_LONG).show()
                }
            }
            .show()
    }

    private fun diyClickShare(context: Context, viewMode: MyViewMode) {
        val data = checkDiyData(context, viewMode) ?: return

        val timings = data[0]
        val amplitude = data[1]
        val repeateI = data[2]

        var text = "{\"timings\": \"$timings\",\"amplitude\": \"$amplitude\",\"repeate\": \"$repeateI\"}"
        text = Base64.encodeToString(text.toByteArray(), Base64.DEFAULT)

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "text/plain"
        context.startActivity(Intent.createChooser(intent, context.getText(R.string.app_name)))
    }

    private fun diyClickImport(context: Context, viewMode: MyViewMode) {
        val editText = EditText(context)
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.advanced_diy_import_dialog_title))
            .setView(editText)
            .setPositiveButton(context.getString(R.string.advanced_diy_import_dialog_btn_sure)) { _, _ ->
                var text = editText.text.toString()
                if (text == "") {
                    Toast.makeText(context, context.getString(R.string.advanced_diy_import_text_isEmpty),
                        Toast.LENGTH_LONG).show()
                }
                else {
                    try {
                        text = String(Base64.decode(text, Base64.DEFAULT))
                        val array = JSONObject(text)
                        val timings = array.getString("timings")
                        val amplitude = array.getString("amplitude")
                        val repeate = array.getString("repeate")
                        viewMode.timingsText = timings
                        viewMode.amplitudesText = amplitude
                        viewMode.repeatText = repeate
                    }
                    catch (e: Exception) {
                        Log.e("el", "import fail:", e)
                        Toast.makeText(context, context.getString(R.string.advanced_diy_import_fail),
                            Toast.LENGTH_LONG).show()
                    }
                }
            }
            .show()
    }

    fun onDiyMoreSelected(
        context: Context,
        index: Int,
        launcherVisualization: ManagedActivityResultLauncher<Intent, ActivityResult>,
        viewMode: MyViewMode
    ) {
        when (index) {
            0 -> {
                launcherVisualization.launch(Intent(context, VisualizationActivity::class.java))
            }
            1 -> {
                diyClickSave(context, viewMode)
            }
            2 -> {
                diyClickOpen(context, viewMode)
            }
            3 -> {
                diyClickImport(context, viewMode)
            }
            4 -> {
                diyClickShare(context, viewMode)
            }
        }
    }
}