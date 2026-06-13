package com.financeasserflow.pfmapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun appShowsDashboardTitle() {
        composeRule.onNodeWithText("FinanceAssetFlow").assertIsDisplayed()
    }

    @Test
    fun dashboardShowsSummaryCards() {
        composeRule.onNodeWithText("순자산").assertIsDisplayed()
        composeRule.onNodeWithText("총자산").assertIsDisplayed()
        composeRule.onNodeWithText("총부채").assertIsDisplayed()
    }

    @Test
    fun dashboardShowsAssetListSection() {
        composeRule.onNodeWithText("자산 목록").assertIsDisplayed()
    }

    @Test
    fun searchButtonTogglesSearchBar() {
        composeRule.onNodeWithContentDescription("검색").performClick()
        composeRule.onNodeWithText("자산 검색").assertIsDisplayed()
    }

    @Test
    fun portfolioButtonNavigatesToPortfolioScreen() {
        composeRule.onNodeWithContentDescription("포트폴리오 목표 설정").performClick()
        composeRule.onNodeWithText("포트폴리오 목표 설정").assertIsDisplayed()
    }

    @Test
    fun addButtonNavigatesToEntryScreen() {
        composeRule.onNodeWithContentDescription("자산 추가").performClick()
        composeRule.onNodeWithText("자산 등록").assertIsDisplayed()
    }
}
