package io.github.tormundsmember.easyflashcards.ui.licenses

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tormundsmember.easyflashcards.R
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseAdapter
import io.github.tormundsmember.easyflashcards.ui.base_ui.BaseFragment
import io.github.tormundsmember.easyflashcards.ui.licenses.model.License
import io.github.tormundsmember.easyflashcards.ui.util.gone
import io.github.tormundsmember.easyflashcards.ui.util.prepareLinkText
import io.github.tormundsmember.easyflashcards.ui.util.setDivider
import io.github.tormundsmember.easyflashcards.ui.util.visible

class LicensesFragment : BaseFragment() {

    override val layoutId: Int = R.layout.screen_licenses

    private val adapter = LicenseAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listLicenses: RecyclerView = view.findViewById(R.id.list_licenses)
        listLicenses.layoutManager = LinearLayoutManager(view.context)
        listLicenses.adapter = adapter
        listLicenses.setDivider(R.drawable.recycler_view_divider)
        adapter.items = listOf(
            License.IconLicense(
                licenseText = "Icon made by <a href=\"https://www.flaticon.com/authors/freepik\">freepik</a> from <a href=\"www.flaticon.com\">www.flaticon.com</a>",
                icons = listOf(R.drawable.ic_ace, R.drawable.ic_flipped_ace)
            ),
            License.TextLicence("SimpleStack", "Gabor Varadi", 2017, 2020)
        )
    }


    class LicenseAdapter : BaseAdapter<License>() {
        override fun getItemLayoutId(viewType: Int) = viewType


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.let { view ->
                with(items[position]) {
                    when (this) {
                        is License.IconLicense -> {
                            val icon1: ImageView = view.findViewById(R.id.icon1)
                            val icon2: ImageView = view.findViewById(R.id.icon2)
                            val txtAttribution: AppCompatTextView = view.findViewById(R.id.txtAttribution)
                            if (icons.isNotEmpty()) {
                                icon1.setImageResource(icons[0])
                                icon1.visible()
                                if (icons.size > 1) {
                                    icon2.setImageResource(icons[1])
                                    icon2.visible()
                                } else {
                                    icon2.gone()
                                }
                            } else {
                                icon1.gone()
                                icon2.gone()
                            }
                            txtAttribution.text = licenseText.prepareLinkText(context = view.context)

                        }
                        is License.TextLicence -> {
                            val txtLicenseTitle: AppCompatTextView = view.findViewById(R.id.txtLicenseTitle)
                            val txtLicenseText: AppCompatTextView = view.findViewById(R.id.txtLicenseText)
                            txtLicenseTitle.text = title
                            txtLicenseText.text = getLicenseText()
                        }
                    }
                }
            }
        }


        override fun getItemViewType(position: Int): Int {
            return when (items[position]) {
                is License.IconLicense -> R.layout.listitem_iconlicense
                is License.TextLicence -> R.layout.listitem_textlicense
            }
        }
    }
}