package com.rmsr.myguard.domain.entity.errors

sealed class InvalidSearchQueryException(message: String) : ErrorEntity.ValidationError(message) {
    class InvalidEmailException() : InvalidSearchQueryException("Invalid Email.")

    class InvalidDomainException() : InvalidSearchQueryException("Invalid Domain.")

    class InvalidDomainNameException() :
        InvalidSearchQueryException("Invalid Domain Name.")

    class InvalidPhoneNumberException() : InvalidSearchQueryException(
        "Invalid Phone Number."
    )

}

class InvalidPasswordException(message: String = "Password can not be blank.") :
    ErrorEntity.ValidationError(message)