package com.zenmo.orm.companysurvey

sealed interface DeleteProjectResult {
    val message: String


    data object Deleted : DeleteProjectResult {
        override val message = "Project deleted successfully."
    }

    data object NotFound : DeleteProjectResult {
        override val message = "Project not found."
    }

    data class InUse(
        override val message: String = "Project is in use and cannot be deleted.",
    ) : DeleteProjectResult

    data class Failure(
        val cause: Throwable,
        override val message: String = "Unable to delete the project: ${cause.message}",
    ) : DeleteProjectResult
}