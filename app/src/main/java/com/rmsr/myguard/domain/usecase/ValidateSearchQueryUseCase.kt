package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.domain.entity.errors.InvalidSearchQueryException
import com.rmsr.myguard.domain.entity.isValidEGPhoneNumber
import com.rmsr.myguard.domain.utils.Validator
import javax.inject.Inject

open class ValidateSearchQueryUseCase @Inject constructor() {

    operator fun invoke(
        searchQuery: SearchQuery,
        autoCorrect: Boolean = false,
    ): Validator<SearchQuery, InvalidSearchQueryException> = when (searchQuery) {
        is SearchQuery.Email -> validateEmail(searchQuery)
        is SearchQuery.Phone -> validatePhone(searchQuery, autoCorrect)
        is SearchQuery.Domain -> validateDomain(searchQuery)
        is SearchQuery.DomainName -> validateDomainName(searchQuery)
    }

    protected open fun validateEmail(
        email: SearchQuery.Email,
    ): Validator<SearchQuery.Email, InvalidSearchQueryException.InvalidEmailException> {
        return if (email.isValid)
            Validator.Valid
        else Validator.Invalid(exception = InvalidSearchQueryException.InvalidEmailException())
    }

    protected open fun validatePhone(
        phoneNumber: SearchQuery.Phone,
        autoCorrect: Boolean,
    ): Validator<SearchQuery.Phone, InvalidSearchQueryException.InvalidPhoneNumberException> {
        val correctedPhone =
            if (autoCorrect) phoneNumber.copy(phoneNumber.query.filter { it.isDigit() }) else phoneNumber
        return when {
            phoneNumber.isValid -> Validator.Valid
            correctedPhone.isValid -> Validator.AutoCorrected(correctedData = correctedPhone)
            correctedPhone.isValidEGPhoneNumber && autoCorrect -> Validator.AutoCorrected(
                correctedData = correctedPhone.copy(
                    query = "2${correctedPhone.query}"
                )
            )
            else -> Validator.Invalid(exception = InvalidSearchQueryException.InvalidPhoneNumberException())
        }
    }

    protected open fun validateDomain(
        domain: SearchQuery.Domain,
    ): Validator<SearchQuery.Domain, InvalidSearchQueryException.InvalidDomainException> {
        return if (domain.isValid)
            Validator.Valid
        else Validator.Invalid(exception = InvalidSearchQueryException.InvalidDomainException())
    }

    protected open fun validateDomainName(
        domainName: SearchQuery.DomainName,
    ): Validator<SearchQuery.DomainName, InvalidSearchQueryException.InvalidDomainNameException> {
        return if (domainName.isValid)
            Validator.Valid
        else Validator.Invalid(exception = InvalidSearchQueryException.InvalidDomainNameException())
    }

}