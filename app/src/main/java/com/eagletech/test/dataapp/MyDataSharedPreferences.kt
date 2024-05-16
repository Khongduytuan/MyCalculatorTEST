package com.eagletech.test.dataapp

import android.content.Context
import android.content.SharedPreferences

class MyDataSharedPreferences constructor(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
    }

    companion object {
        @Volatile
        private var instance: MyDataSharedPreferences? = null

        fun getInstance(context: Context): MyDataSharedPreferences {
            return instance ?: synchronized(this) {
                instance ?: MyDataSharedPreferences(context).also { instance = it }
            }
        }
    }

    // Lấy ra thông tin mua theo lượt
    fun getTimesCalculate(): Int {
        return sharedPreferences.getInt("calculate", 0)
    }

    fun setTimesCalculate(lives: Int) {
        sharedPreferences.edit().putInt("calculate", lives).apply()
    }

    fun addTimesCalculate(amount: Int) {
        val currentLives = getTimesCalculate()
        setTimesCalculate(currentLives + amount)
    }

    fun removeTimesCalculate() {
        val currentLives = getTimesCalculate()
        if (currentLives > 0) {
            setTimesCalculate(currentLives - 1)
        }
    }

    // Lấy thông tin mua premium
    var isPremiumCalculate: Boolean?
        get() {
            val userId = sharedPreferences.getString("UserId", "")
            return sharedPreferences.getBoolean("PremiumPlan_\$userId$userId", false)
        }
        set(state) {
            val userId = sharedPreferences.getString("UserId", "")
            sharedPreferences.edit().putBoolean("PremiumPlan_\$userId$userId", state!!).apply()
        }

    // Lưu thông tin người dùng
    fun currentUserId(userid: String?) {
        sharedPreferences.edit().putString("UserId", userid).apply()
//        sharedPreferences.edit().apply()
    }

    // Lấy ra thông tin id người dùng
    fun getCurrentUserId(): String? {
        return sharedPreferences.getString("UserId", null)
    }

}