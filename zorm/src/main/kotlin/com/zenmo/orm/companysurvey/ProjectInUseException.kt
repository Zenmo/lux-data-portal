package com.zenmo.orm.companysurvey

class ProjectInUseException : Exception("This project is still linked to one or more surveys. Delete the surveys first before deleting the project.")
