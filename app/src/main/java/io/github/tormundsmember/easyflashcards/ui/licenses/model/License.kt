package io.github.tormundsmember.easyflashcards.ui.licenses.model

sealed class License {

    class IconLicense(val licenseText: String, val icons: List<Int>) : License()

    class TextLicence(val title: String, val owner: String, val yearFrom: Int, val yearTo: Int) : License() {

        fun getLicenseText() = """|Copyright $yearFrom-$yearTo $owner
|
|Licensed under the Apache License, Version 2.0 (the "License");
|you may not use this file except in compliance with the License.
|You may obtain a copy of the License at
|
|   http://www.apache.org/licenses/LICENSE-2.0
|
|Unless required by applicable law or agreed to in writing, software
|distributed under the License is distributed on an "AS IS" BASIS,
|WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
|See the License for the specific language governing permissions and
|limitations under the License.""".trimMargin("|")

    }

}