package com.enigmasolver.phonebook.shared.extensions

import java.util.Locale

fun String.turkishToLowerEnglish(): String {
    return this.replace('İ', 'i')
        .replace('ı', 'i')
        .replace('Ğ', 'g')
        .replace('ğ', 'g')
        .replace('Ü', 'u')
        .replace('ü', 'u')
        .replace('Ş', 's')
        .replace('ş', 's')
        .replace('Ö', 'o')
        .replace('ö', 'o')
        .replace('Ç', 'c')
        .replace('ç', 'c')
        .lowercase(Locale.ENGLISH)
}