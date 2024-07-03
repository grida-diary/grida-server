package org.grida.domain.profileimage

import org.grida.domain.image.Image

data class ProfileImage(
    val id: Long = 0,
    val userId: Long,
    val image: Image,
    val appearance: Appearance
)