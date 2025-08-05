package com.kitsuri.msa.rapidfetch


data class UserInfo(
    val displayName: String,
    val uuid: String,
    val hasRealmsAccess: Boolean
)