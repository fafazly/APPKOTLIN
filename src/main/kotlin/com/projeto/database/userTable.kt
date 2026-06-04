package com.projeto.database

import org.jetbrains.exposed.sql.table
import org.jetbrains.exposed.sql.javatime.datetime

object usersTable : Table("Users") {
    val id = integer("id").autoIncrement()

}
