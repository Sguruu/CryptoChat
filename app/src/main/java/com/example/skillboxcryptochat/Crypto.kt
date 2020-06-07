package com.example.skillboxcryptochat

import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

//класс для шифрования сообщения


//код из секретного слова делает спецификацию секретного ключа
//блок кода внутри которого будет шифровать
object Crypto {
    //секретный ключ
    private var keySpec: SecretKeySpec
    //секретное слово
    private var pass = "ме"

    init {

        println("Объект прониницилизирован")
        //алгоритм хэширования SHA-256
        //позволит из секретного слова сделать хэш секретное слово
        var shaDigest: MessageDigest = MessageDigest.getInstance("SHA-256")
        //секретное слово преобразовываем в массив байт
        val bytes = pass.toByteArray()
        println("bytes " + bytes.forEach {
            print(" $it")
        })
        //предать массив байтов от нуля до его длины
        shaDigest.update(bytes, 0, bytes.size)
        //получаем байт хэша
        val hash: ByteArray = shaDigest.digest()
        //создаем из хэша спеификацию ключа
        //передаем ключ и указываем тип алгоритма шифрования
        keySpec = SecretKeySpec(hash, "AES")


    }


    //функция для шифрования
    fun encrypt(unencryptedText: String): String {
//создаем объект отвечающий за алгоритм шифрования
        val cipher: Cipher = Cipher.getInstance("AES")
        //переключится в режим шифрования +ключ
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        //применить финальное шифрование

        val encrypted: ByteArray = cipher.doFinal(unencryptedText.toByteArray())

        println("Объект Crypto  fun encrypt " + encrypted.forEach {
            print(" $it")
        })

        //приводим к строки получифшийся масств байт
        return Base64.getEncoder().encodeToString(encrypted)
    }

    //функция декодирования
    fun decrypt(decryptedText: String): String {
        val ciphered: ByteArray =
            Base64.getDecoder().decode(decryptedText).toString().toByteArray()
        val cipher: Cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val rawText: ByteArray = cipher.doFinal(ciphered)
        return rawText.toString(charset("UTF-8"))

    }


}


//хэширование это односторення операция
//хер -> 123qwe
//хер -> 123qwe
//123qwe -> ???


