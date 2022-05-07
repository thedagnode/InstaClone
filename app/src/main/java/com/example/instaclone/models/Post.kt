package com.example.instaclone.models

import android.content.ClipDescription
import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageURL: String = "",
    @get:PropertyName("creation_time_ms") @set:PropertyName("creation_time_ms")var creationTimeMs : Long = 0,
    var user: User? = null
)
