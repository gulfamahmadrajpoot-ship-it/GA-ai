package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.AppStrings
import com.example.ui.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GA AI", appName)
  }

  @Test
  fun `verify urdu translation helper`() {
    val appNameUrdu = AppStrings.get(AppLanguage.URDU, "app_name")
    assertEquals("جی اے اے آئی", appNameUrdu)
    val generateBtn = AppStrings.get(AppLanguage.URDU, "generate_image_btn")
    assertTrue(generateBtn.isNotBlank())
  }

  @Test
  fun `verify navigation items non null`() {
    val items = Screen.bottomNavItems
    assertEquals(5, items.size)
    items.forEach { screen ->
      assertNotNull(screen)
      assertTrue(screen.route.isNotBlank())
      assertNotNull(screen.selectedIcon)
      assertNotNull(screen.unselectedIcon)
    }
  }

  @Test
  fun `launch MainActivity`() {
    val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
    val activity = controller.get()
    assertNotNull(activity)
  }
}
