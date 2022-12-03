package ru.kheynov.secretsanta.utils

class UserNotExistsException : Exception()
class RoomNotExistsException : Exception()
class UserAlreadyExistsException : Exception()
class RoomAlreadyExistsException : Exception()
class ForbiddenException : Exception()
class GameAlreadyStartedException : Exception()
class GameAlreadyStoppedException : Exception()
class UserAlreadyInRoomException : Exception()
class UserNotInTheRoomException : Exception()
class NotEnoughPlayersException : Exception()
