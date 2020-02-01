package io.github.tormundsmember.easyflashcards.ui.more

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Switch
import androidx.appcompat.widget.AppCompatTextView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.Dependencies
import io.github.tormundsmember.easyflashcards.ui.MainActivity
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.licenses.LicensesKey
import io.github.tormundsmember.easyflashcards.ui.util.hasPermission
import io.github.tormundsmember.easyflashcards.ui.util.openUrlInCustomTabs
import io.github.tormundsmember.easyflashcards.ui.util.prepareLinkText
import io.github.tormundsmember.easyflashcards.ui.util.showGeneralErrorMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MoreFragment : BaseFragment() {

    companion object {
        const val RQ_STORAGE = 1
        const val RQ_EXPORT = 2
        const val RQ_IMPORT = 3
    }

    override val layoutId: Int = R.layout.screen_more

    private lateinit var switchDarkMode: Switch
    private lateinit var switchSpatialRepetition: Switch
    private lateinit var txtExportToCsv: AppCompatTextView
    private lateinit var txtImportFromCsvSubtitle: AppCompatTextView
    private lateinit var txtImportFromCsv: AppCompatTextView
    private lateinit var txtLicenses: AppCompatTextView
    private lateinit var txtSourceCode: AppCompatTextView
    private lateinit var txtIssueTracker: AppCompatTextView
    private lateinit var hintSpatialRepetition: AppCompatTextView
    private lateinit var switchCrashUsageData: Switch
    private val viewModel: MoreViewModel by lazy { getViewModel<MoreViewModel>() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchSpatialRepetition = view.findViewById(R.id.switchSpatialRepetition)
        txtExportToCsv = view.findViewById(R.id.txtExportToCsv)
        txtImportFromCsvSubtitle = view.findViewById(R.id.txtImportFromCsvSubtitle)
        txtImportFromCsv = view.findViewById(R.id.txtImportFromCsv)
        txtLicenses = view.findViewById(R.id.txtLicenses)
        txtSourceCode = view.findViewById(R.id.txtSourceCode)
        txtIssueTracker = view.findViewById(R.id.txtIssueTracker)
        hintSpatialRepetition = view.findViewById(R.id.hintSpatialRepetition)
        switchCrashUsageData = view.findViewById(R.id.switchCrashUsageData)

        switchCrashUsageData.text = getString(R.string.enableCrashReporting).prepareLinkText(view.context)
        switchCrashUsageData.isChecked = Dependencies.userData.allowCrashReporting
        switchCrashUsageData.setOnCheckedChangeListener { _, isChecked ->
            Dependencies.userData.allowCrashReporting = isChecked
        }

        switchDarkMode.isChecked = Dependencies.userData.useDarkMode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            Dependencies.userData.useDarkMode = isChecked
            (activity as MainActivity?)?.setDarkMode()
        }

        switchSpatialRepetition.isChecked = Dependencies.userData.useSpacedRepetition
        switchSpatialRepetition.setOnCheckedChangeListener { _, isChecked ->
            Dependencies.userData.useSpacedRepetition = isChecked
        }

        hintSpatialRepetition.text = getString(R.string.explanationSpacedRepetition).prepareLinkText(view.context)
        hintSpatialRepetition.setOnClickListener {
            openUrlInCustomTabs(it.context, Uri.parse("https://en.wikipedia.org/wiki/Spaced_repetition"))
        }

        txtImportFromCsvSubtitle.setOnClickListener {
            importFromCsv()
        }
        txtImportFromCsv.setOnClickListener {
            importFromCsv()
        }

        txtExportToCsv.setOnClickListener {
            exportToCsv()
        }
        txtLicenses.setOnClickListener {
            goTo(LicensesKey())
        }
        txtSourceCode.setOnClickListener {
            openUrlInCustomTabs(it.context, Uri.parse(getString(R.string.repository)))
        }
        txtIssueTracker.setOnClickListener {
            openUrlInCustomTabs(it.context, Uri.parse(getString(R.string.issueTracker)))
        }
    }

    private fun continueWithExport() {
        try {
            val chooseFile = Intent(Intent.ACTION_CREATE_DOCUMENT)
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
            chooseFile.type = "text/csv"

            chooseFile.putExtra(
                Intent.EXTRA_TITLE,
                "export_flashcards_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.csv"
            )
            startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                RQ_EXPORT
            )
        } catch (e: Exception) {
            context?.showGeneralErrorMessage()
        }
    }

    private fun continueWithImport() {
        try {
            val chooseFile = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/*"
            }
            startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                RQ_IMPORT
            )

        } catch (e: java.lang.Exception) {
            context?.showGeneralErrorMessage()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val data1 = data?.data
        if (requestCode == RQ_EXPORT && data1 != null) {
            activity?.applicationContext?.contentResolver?.openFileDescriptor(data1, "w")?.use {
                viewModel.exportToCsv(FileOutputStream(it.fileDescriptor))
            }
        }
        if (requestCode == RQ_IMPORT && data1 != null) {
            activity?.applicationContext?.contentResolver?.openFileDescriptor(data1, "r")?.use {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.importFromCsv(FileInputStream(it.fileDescriptor))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RQ_STORAGE -> when (permissions[0]) {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    continueWithExport()
                }
                android.Manifest.permission.READ_EXTERNAL_STORAGE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    continueWithImport()
                }
            }
        }
    }

    private fun importFromCsv() {
        activity?.let {
            if (it.hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                continueWithImport()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), RQ_STORAGE)
            }
        }
    }

    private fun exportToCsv() {
        activity?.let {
            if (it.hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                continueWithExport()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), RQ_STORAGE)
            }
        }

    }
}