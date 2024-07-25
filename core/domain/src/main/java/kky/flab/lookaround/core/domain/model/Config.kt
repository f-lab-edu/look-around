package kky.flab.lookaround.core.domain.model

data class Config(
    val requestFineLocation: Boolean,
    val requestBackgroundLocation: Boolean,
    val requestReadStorage: Boolean,
    val darkTheme: Boolean,
) {
    companion object {
        val Default = Config(
            requestFineLocation = false,
            requestBackgroundLocation = false,
            requestReadStorage = false,
            darkTheme = false
        )
    }
}
