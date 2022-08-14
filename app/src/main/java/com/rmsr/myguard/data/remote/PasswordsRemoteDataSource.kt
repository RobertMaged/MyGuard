package com.rmsr.myguard.data.remote

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface PasswordsRemoteDataSource {

    /**
     * Search for hashes that start with [first5hashChars]
     * When a password hash is found in the Pwned Passwords repository,
     *
     * the API will include the 'rest of hash chars' of every hash beginning with the specified [first5hashChars],
     * followed by a count of how many times it appears in the data set.
     *
     * So consumer can then search the results of the response for the presence of their source hash and if not found,
     * the password does not exist in the data set.
     *
     * @param first5hashChars first 5 hash characters to search.
     *
     * @return A range search typically returns approximately 500 hash suffixes.
     *
     * @sample {https://api.pwnedpasswords.com/range/21BD1}
     */
    @GET("range/{first5hash}")
    fun getHashSuffixes(@Path("first5hash") first5hashChars: String): Single<String>

    companion object {
        const val API_PASSWORDS_BASE_URL = "https://api.pwnedpasswords.com/"
    }
}