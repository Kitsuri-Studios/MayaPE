package io.kitsuri.mayape.utils

class FileParser {
    external fun ElfCheck(filepath: String?): Boolean

    companion object {
        init {
            try {
                System.loadLibrary("ElfHelper")
            } catch (e: UnsatisfiedLinkError) {
                throw RuntimeException("Failed to load native library: ${e.message}", e)
            }
        }
    }

    fun elfWrap(filepath: String?): Boolean {
        require(filepath != null) { "Filepath cannot be null" }
        return ElfCheck(filepath)
    }
}