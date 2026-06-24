package com.example.grupotres

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.grupotres.repository.ChallengeRepository
import com.example.grupotres.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HiltInjectionTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var challengeRepository: ChallengeRepository

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testInjection() {
        assertNotNull(userRepository)
        assertNotNull(challengeRepository)
    }
}