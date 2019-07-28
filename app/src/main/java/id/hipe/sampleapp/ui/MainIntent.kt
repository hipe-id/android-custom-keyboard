package id.hipe.sampleapp.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// TODO: Add companion object and reference this to the conventions
// TODO: Install @Parcelize as stated on README.md for kotlin only
@Parcelize
data class MainIntent(val id: Int) : Parcelable {
    // TODO: This model purpose was only to be used for intent
}
