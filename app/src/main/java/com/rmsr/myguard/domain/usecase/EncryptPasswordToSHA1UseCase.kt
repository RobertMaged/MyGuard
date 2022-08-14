package com.rmsr.myguard.domain.usecase

import com.rmsr.myguard.domain.entity.errors.InvalidPasswordException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class EncryptPasswordToSHA1UseCase {

    companion object {
        @JvmStatic
        operator fun invoke(text: String): String {
            if (text.isEmpty()) throw InvalidPasswordException()

            return try {
                val md = MessageDigest.getInstance("SHA-1")
                val textBytes = text.toByteArray(StandardCharsets.UTF_8)
                md.update(textBytes, 0, textBytes.size)
                val sha1hash = md.digest()
                convertToHex(sha1hash).uppercase()
            } catch (noSuchAlgorithmException: NoSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace()
                throw RuntimeException(noSuchAlgorithmException.message)
            }
        }

        private fun convertToHex(data: ByteArray): String = buildString {
            data.forEach {
                val b = it.toInt()
                var halfByte: Int = (b ushr 4) and 0x0F
                var twoHalfs = 0
                do {
                    append(
                        if (halfByte in 0..9)
                            ('0'.code + halfByte).toChar()
                        else
                            ('a'.code + (halfByte - 10)).toChar()
                    )
                    halfByte = b and 0x0F
                } while (twoHalfs++ < 1)
            }
        }
    }
}