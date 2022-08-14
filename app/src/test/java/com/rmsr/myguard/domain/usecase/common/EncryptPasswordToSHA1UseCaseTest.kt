package com.rmsr.myguard.domain.usecase.common

import com.google.common.truth.Truth.assertThat
import com.rmsr.myguard.domain.entity.errors.InvalidPasswordException
import com.rmsr.myguard.domain.usecase.EncryptPasswordToSHA1UseCase
import com.rmsr.myguard.domain.utils.HashConverter
import org.junit.Test
import java.io.File
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class EncryptPasswordToSHA1UseCaseTest {
    private val newEncryptor = EncryptPasswordToSHA1UseCase.Companion
    private val oldEncryptor = HashConverter::SHA1

    //    private val alphas = (('A'..'Z') + ('a'..'z')).toTypedArray()
    private val alphas: CharRange = ' '..'~'

    @Test
    fun `given valid password - old and new should behave the same`() {
        val pass = "hi"

        val l = List(50) {
            buildString { (0..Random.nextInt(0, 200)).map { append(alphas.random()) } }
        }
        assertThat(newEncryptor(pass)).isEqualTo(oldEncryptor(pass))

        val (newList, oldList) = mutableListOf<String>() to mutableListOf<String>()
        l.forEach {
            newList += newEncryptor(it)
            oldList += oldEncryptor(it)
        }
        assertThat(newList).containsExactlyElementsIn(oldList)
    }

    @Test(expected = InvalidPasswordException::class)
    fun `given blank password no return and throw exception`() {
        newEncryptor("")
    }

    @Test
    fun `given list of valid passwords encryptor returned list equals given global list`() {
        val encrypted = plainPasswords.map { newEncryptor(it) }

        assertThat(encrypted).containsExactlyElementsIn(plainPasswordsEncrypted)
    }


    @Test
    fun `test response`() {
//        "2DC183F740EE76F27B78EB39C8AD972A757:83129\n"
        val restOfHash = "2DC183F740EE76F27B78EB39C8AD972A757"
        val response = File(
            "src/test/java/com/rmsr/myguard/domain/usecase/common",
            "PasswordApiFakeResponse.txt"
        ).readText()

        val time = measureTimeMillis {
            val count = res(response, restOfHash)
            assertThat(count).isEqualTo(83129)
        }
        println("newTime: $time")

//        val oldTime = measureTimeMillis {
//            val exist: String? = HashConverter.handlePasswordsResponse(response).get(restOfHash)
//            val count = exist?.toIntOrNull()
//            assertThat(count).isEqualTo(83129)
//        }
//        println("old time: $oldTime")

    }

    private fun res(response: String, restOfHash: String): Int? {

        return response.substringAfter("$restOfHash:", "null")
            .takeIf { it != "null" }
            ?.takeWhile { it.isDigit() }
//            ?.takeWhile { it != '\n' }?.trimEnd()
            ?.toInt()
    }

    companion object {
        private val plainPasswords = arrayOf(
            "iouiouioui",
            "iortertee",
            "xz.,cnzkl'aNFE",
            "LSDFNM;LSFGNIKF'KDNFKGL",
            "ksnfsjkl;dfns;.dkjn/s",
            "nlnloINOLI1O4HN3K2L",
            "slkdfnm;sldkfm;sdlm;",
            "sdfwewrdsfw456657@",
            "sdfsefwefwefwefwefwee",
            "65wr4566=p\\yt=-=-lpalp'",
            "5468498498498954345!@#${'$'}",
            "سيمشةيبسنبمذ1214323430-4"
        )


        private val plainPasswordsEncrypted = arrayOf(
            "0225AEE29B1317850774585B4BDF79D3B4304B43",
            "1A3DF35DB2F102E67223D032DDDFB1411C726532",
            "17B5A9F3AA07A91B6E91FA80C594ECBD49817002",
            "CC282DEDA5DD6DDE1348F35372BB0572D963E0E4",
            "499F017AA938046A6FDD60750963C54B10D2EE53",
            "2A9A61C6854C30E29C90F6A038C59CF797C35EA4",
            "BC69F9E77541CBC50C9B42001E731506B80C0517",
            "8E978122494F880497A036AD2EC0D1B0027F595B",
            "175201CC0C84821F7AA74C019FD64AC3E66E5E7D",
            "1941E15F2941C106C04E9C177FE36C7B346179BD",
            "BAAEAA4CBF44134CEA62C94F808438CBC2DE6E2F",
            "653BCAA116393929497C182C572636190100A07F"
        )

    }

}