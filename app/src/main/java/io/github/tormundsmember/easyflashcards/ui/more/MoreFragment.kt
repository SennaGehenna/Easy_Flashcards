package io.github.tormundsmember.easyflashcards.ui.more

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.tormundsmember.easyflashcards.BuildConfig
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.BuildVariant
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.debug_settings.DebugSettingsKey
import io.github.tormundsmember.easyflashcards.ui.duplicate_finder.DuplicateFinderKey
import io.github.tormundsmember.easyflashcards.ui.licenses.LicensesKey
import io.github.tormundsmember.easyflashcards.ui.settings.SettingsKey
import io.github.tormundsmember.easyflashcards.ui.util.*
import kotlinx.android.synthetic.main.listitem_set.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    override val titleText: String
        get() = getString(R.string.more)

    private lateinit var txtExportToCsv: AppCompatTextView
    private lateinit var txtImportFromCsvSubtitle: AppCompatTextView
    private lateinit var txtImportFromCsv: AppCompatTextView
    private lateinit var txtLicenses: AppCompatTextView
    private lateinit var txtSourceCode: AppCompatTextView
    private lateinit var txtIssueTracker: AppCompatTextView
    private lateinit var txtVersionCode: AppCompatTextView
    private lateinit var txtDuplicateFinder: AppCompatTextView
    private lateinit var progressBar: ProgressBar

    private val viewModel: MoreViewModel by lazy { getViewModel<MoreViewModel>() }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtExportToCsv = view.findViewById(R.id.txtExportToCsv)
        txtImportFromCsvSubtitle = view.findViewById(R.id.txtImportFromCsvSubtitle)
        txtImportFromCsv = view.findViewById(R.id.txtImportFromCsv)
        txtLicenses = view.findViewById(R.id.txtLicenses)
        txtSourceCode = view.findViewById(R.id.txtSourceCode)
        txtIssueTracker = view.findViewById(R.id.txtIssueTracker)
        txtVersionCode = view.findViewById(R.id.txtVersionCode)
        txtDuplicateFinder = view.findViewById(R.id.txtDuplicateFinder)
        progressBar = view.findViewById(R.id.progressBar)

        txtVersionCode.text = "v${BuildConfig.VERSION_NAME}"

        txtImportFromCsvSubtitle.setOnClickListener { importFromCsv() }
        txtImportFromCsv.setOnClickListener { importFromCsv() }
        txtExportToCsv.setOnClickListener { exportToCsv() }
        txtLicenses.setOnClickListener { goTo(LicensesKey()) }
        txtSourceCode.setOnClickListener { openUrlInCustomTabs(it.context, Uri.parse(getString(R.string.repository))) }
        txtIssueTracker.setOnClickListener { openUrlInCustomTabs(it.context, Uri.parse(getString(R.string.issueTracker))) }
        txtDuplicateFinder.setOnClickListener { goTo(DuplicateFinderKey()) }
    }

    private fun showLoadingSpinner() {
        progressBar.visible()
    }

    private fun hideLoadingSpinner() {
        progressBar.gone()
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
            showLoadingSpinner()
            CoroutineScope(Dispatchers.IO).launch {
                delay(300)
                activity?.applicationContext?.contentResolver?.openFileDescriptor(data1, "r")?.use {
                    val cardsImported = viewModel.importFromCsv(FileInputStream(it.fileDescriptor))
                    CoroutineScope(Dispatchers.Main).launch {
                        hideLoadingSpinner()
                        showCardsImportedMessage(cardsImported)
                    }
                }
            }
        }
    }

    private fun showCardsImportedMessage(cardsImported: Int) {
        Toast.makeText(context,getString(R.string.cardsImported, cardsImported), Toast.LENGTH_SHORT).show()
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