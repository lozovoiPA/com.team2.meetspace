package com.team2.meetspace.data.entities

abstract class Result (var code: Int) { }

class MeetingPlanned(code: Int, var identifier: String): Result(code) { }

class ErrorResult(code: Int, var errorText: String): Result(code) { }

class MeetingCreated(code: Int, var meeting: Meeting): Result(code) {}

class MeetingsRetrieved(code: Int, var meetings: List<Meeting>): Result(code) {}

data class ServerError(val errorText: String) : Result(0)
