package com.zenmo.vallum

class ProjectNotFound(name: String) : Exception("Project $name not found. It may not exist or you don't have access to it.")
