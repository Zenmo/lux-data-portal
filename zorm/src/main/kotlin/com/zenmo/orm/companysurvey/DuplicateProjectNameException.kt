package com.zenmo.orm.companysurvey

class DuplicateProjectNameException(name: String) : Exception("A project with the name \"$name\" already exists.")
