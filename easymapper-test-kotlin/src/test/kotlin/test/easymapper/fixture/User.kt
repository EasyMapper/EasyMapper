package test.easymapper.fixture

import java.beans.ConstructorProperties

internal class User
    @ConstructorProperties(value = ["id", "username", "passwordHash"])
    constructor(
        val id: Long,
        val username: String,
        val passwordHash: String)
