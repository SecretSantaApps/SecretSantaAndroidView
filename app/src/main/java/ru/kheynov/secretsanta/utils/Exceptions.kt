package ru.kheynov.secretsanta.utils

open class SantaException : Exception()

class UserNotExistsException : SantaException()
class RoomNotExistsException : SantaException()
class UserAlreadyExistsException : SantaException()
class RoomAlreadyExistsException : SantaException()
class ForbiddenException : SantaException()
class GameAlreadyStartedException : SantaException()
class GameAlreadyStoppedException : SantaException()
class UserAlreadyInRoomException : SantaException()
class UserNotInTheRoomException : SantaException()
class NotEnoughPlayersException : SantaException()
