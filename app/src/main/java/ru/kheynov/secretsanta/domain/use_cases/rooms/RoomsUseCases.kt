package ru.kheynov.secretsanta.domain.use_cases.rooms

data class RoomsUseCases(
    val createRoomUseCase: CreateRoomUseCase,
    val deleteRoomUseCase: DeleteRoomUseCase,
    val getRoomInfoUseCase: GetRoomInfoUseCase,
    val updateRoomUseCase: UpdateRoomUseCase,
)