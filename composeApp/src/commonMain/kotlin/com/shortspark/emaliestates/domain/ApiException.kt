class ApiException(
    val statusCode: Int,
    val errorMessage: String,
    val detail: String? = null
) : Exception("$statusCode: $errorMessage${detail?.let { " - $it" } ?: ""}")