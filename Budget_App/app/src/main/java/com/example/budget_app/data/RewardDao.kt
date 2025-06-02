package com.example.budget_app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RewardDao {

    @Query("SELECT * FROM rewards WHERE userId = :userId")
    suspend fun getAllRewardsForUser(userId: Long): List<Reward>

    @Query("SELECT * FROM rewards WHERE userId = :userId AND obtained = 1")
    suspend fun getObtainedRewards(userId: Long): List<Reward>

    @Query("SELECT * FROM rewards WHERE userId = :userId AND obtained = 0")
    suspend fun getUnobtainedRewards(userId: Long): List<Reward>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward)

    @Query("SELECT * FROM rewards WHERE userId = :userId AND title = :title LIMIT 1")
    suspend fun getRewardByTitle(userId: Long, title: String): Reward?
}


