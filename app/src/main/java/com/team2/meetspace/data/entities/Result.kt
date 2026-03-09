package com.team2.meetspace.data.entities

sealed class Result

data class MeetingPlanned(val identifier: String) : Result()

data class ServerError(val errorText: String) : Result()

data class MeetingCreated(val meeting: Meeting) : Result()