package com.stevi.moneyminder.entity

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(val username: String) {
}