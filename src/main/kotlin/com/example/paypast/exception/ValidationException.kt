package com.example.paypast.exception

class ValidationException(message: String) : RuntimeException(message) {
    var content: String = ""

    constructor(message: String, detail: String) : this(message) {
        this.content = detail
    }
}