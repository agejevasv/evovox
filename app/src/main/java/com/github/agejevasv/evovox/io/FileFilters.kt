package com.github.agejevasv.evovox.io

import java.io.File
import java.io.FileFilter
import java.util.*

object FileFilters {

    private val imageExtensions = Arrays.asList(
        "jpg",
        "jpeg",
        "png",
        "bmp",
        "gif"
    )

    private val audioExtensions = arrayOf(
        "3gp",
        "aac",
        "aif",
        "awb",
        "flac",
        "imy",
        "m4a",
        "m4b",
        "mid",
        "mka",
        "mkv",
        "mp3",
        "mp3package",
        "mp4",
        "opus",
        "mxmf",
        "oga",
        "ogg",
        "ota",
        "rtttl",
        "rtx",
        "wav",
        "webm",
        "wma",
        "xmf"
    )

    val image = FileFilter {
        imageExtensions.contains(ext(it))
    }

    val audio = FileFilter {
        audioExtensions.contains(ext(it))
    }

    val folderOrAudio = FileFilter {
        it.isDirectory || audioExtensions.contains(ext(it))
    }

    private fun ext(file: File): String = file.extension.toLowerCase()
}