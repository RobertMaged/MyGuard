package com.rmsr.myguard.domain.entity

import java.util.*
import java.util.regex.Pattern

sealed class SearchQuery(val query: String, val hint: String?) {

    protected abstract val isValid: Boolean
    abstract fun copy(query: String = this.query, hint: String? = this.hint): SearchQuery
    abstract fun queryType(): QueryType

    operator fun component1(): String = query
    operator fun component2(): QueryType = queryType()
    operator fun component3(): String? = hint

    class Email(query: String, hint: String? = null) : SearchQuery(query, hint) {
        public override val isValid: Boolean
            get() = isValidEmail(query)

        override fun copy(query: String, hint: String?): Email = Email(query, hint)

        override fun queryType(): QueryType = QueryType.EMAIL
    }

    class Phone(query: String, hint: String? = null) : SearchQuery(query, hint) {
        public override val isValid: Boolean
            get() = isValidPhone(query)

        override fun copy(query: String, hint: String?): Phone = Phone(query, hint)

        override fun queryType(): QueryType = QueryType.Phone
    }

    class Domain(query: String, hint: String? = null) : SearchQuery(query, hint) {
        public override val isValid: Boolean
            get() = isValidDomain(query)

        override fun copy(query: String, hint: String?): Domain = Domain(query, hint)

        override fun queryType(): QueryType = QueryType.DOMAIN
    }

    class DomainName(query: String, hint: String? = null) : SearchQuery(query, hint) {
        public override val isValid: Boolean
            get() = isValidDomainName(query)

        override fun copy(query: String, hint: String?): DomainName = DomainName(query, hint)

        override fun queryType(): QueryType = QueryType.DOMAIN_NAME
    }


    companion object {
        @JvmStatic
        @JvmOverloads
        fun from(query: String, queryType: QueryType, hint: String? = null): SearchQuery =
            when (queryType) {
                QueryType.EMAIL -> Email(query, hint)
                QueryType.Phone -> Phone(query, hint)
                QueryType.DOMAIN_NAME -> DomainName(query, hint)
                QueryType.DOMAIN -> Domain(query, hint)
            }

        @JvmStatic
        fun isValidEmail(email: String): Boolean {
            if (email.isBlank()) return false

            val emailRegex =
                """^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$"""

            val pattern = Pattern.compile(emailRegex)
            return pattern.matcher(email).matches()
        }

        @JvmStatic
        fun isValidPhone(phone: String): Boolean {
            if (phone.isBlank()) return false

//    val phoneRegex = Regex("(201)(0|1|2|5)[0-9]{8}")
            val phoneRegex = Regex("""(201)([0125])[0-9]{8}""")

            val pattern = Pattern.compile(phoneRegex.pattern)
            return pattern.matcher(phone).matches()
        }

        @JvmStatic
        fun isValidDomain(domain: String): Boolean {
            if (domain.isBlank() || !domain.contains(".")) return false

//            val domainRegex = "^((?!-)[A-Za-z0–9-]{1,63}(?<!-)\\." + ")+[A-Za-z]{2,6}\$"
            val domainRegex =
                """^(((?!-))(xn--|_{1,1})?[a-z0-9-]{0,61}[a-z0-9]{1,1}\.)*(xn--)?([a-z0-9][a-z0-9\-]{0,60}|[a-z0-9-]{1,30}\.[a-z]{2,})$"""

            val pattern = Pattern.compile(domainRegex)
            return pattern.matcher(domain.trim().lowercase(Locale.getDefault())).matches()
        }

        @JvmStatic
        fun isValidDomainName(domainName: String): Boolean {
            return !(domainName.isBlank() || domainName.contains("@") || domainName.contains("."))
        }
    }
}

fun SearchQuery.Companion.isValidEGPhone(phone: String): Boolean {
    if (phone.isBlank()) return false

    val phoneRegex = Regex("""(01)([0125])[0-9]{8}""")

    val pattern = Pattern.compile(phoneRegex.pattern)
    return pattern.matcher(phone).matches()
}

val SearchQuery.Phone.isValidEGPhoneNumber: Boolean
    get() = SearchQuery.isValidEGPhone(query)

enum class QueryType {
    EMAIL, Phone, DOMAIN_NAME, DOMAIN
}