package kky.flab.lookaround.core.domain.model

data class Setting(
    val requestFineLocation: Boolean,
    val requestBackgroundLocation: Boolean,
    val requestReadStorage: Boolean,
    val darkTheme: Boolean
) {
    companion object {
        val Default = Setting(
            requestFineLocation = false,
            requestBackgroundLocation = false,
            requestReadStorage = false,
            darkTheme = false
        )
    }
}
