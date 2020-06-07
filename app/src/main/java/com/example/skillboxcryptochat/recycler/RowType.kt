package com.example.skillboxcryptochat.recycler

/**
 * Абстрактный класс для классов разных типов View
 */

abstract class RowType {
    val VIEW_MESSAGE_CONTROLLER = 0
    val INCOMING_VIEW_MESSAGE_CONTROLLER = 1
    //создан для получения доступа полям абстрактного класса
    companion object : RowType()
}