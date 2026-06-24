package com.example.grupotres

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.grupotres.ui.HomeViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HomeViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var viewModel: HomeViewModel

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testViewModelInjection() {
        assertNotNull(viewModel)
    }

    @Test
    fun testInitialState() {
        assertNotNull(viewModel.isSoundOn.value)
    }
}