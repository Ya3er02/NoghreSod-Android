package com.noghre.sod

import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Test activity با Hilt dependency injection
 * 
 * برای use کردن در instrumentation tests:
 * 
 * @HiltAndroidTest
 * class MyTest {
 *     @get:Rule
 *     val hiltRule = HiltAndroidRule(this)
 *     
 *     @Before
 *     fun setup() {
 *         hiltRule.inject()
 *     }
 * }
 */
@AndroidEntryPoint
class HiltTestActivity : ComponentActivity()
