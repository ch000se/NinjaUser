package com.ch000se.ninjauser.patterns.creational.singleton

class Database private constructor() {

    companion object {
        private val lock = Any()
        private var instance: Database? = null


        fun getInstance(): Database {
            instance?.let { return it }

            synchronized(lock) {
                instance?.let { return it }
                return Database().also { instance = it }
            }
        }
    }

//    companion object {
//        private val instance by lazy {
//            Database()
//        }
//
//        fun getInstance(): Database {
//            return instance
//        }
//    }


//    companion object {
//        private val instance = Database()
//
//        fun getInstance(): Database {
//            return instance
//        }
//    }


//    companion object {
//        private lateinit var instance: Database
//
//        fun getInstance(): Database {
//            if (!::instance.isInitialized) {
//                instance = Database()
//            }
//            return instance
//        }
//    }
}

fun main() {
    val db1 = Database.getInstance()
    val db2 = Database.getInstance()

    println(db1 == db2)
}