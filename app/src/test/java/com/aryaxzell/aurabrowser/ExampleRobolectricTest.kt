package com.aryaxzell.aurabrowser

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.aryaxzell.aurabrowser.data.preferences.BrowserPreferences
import com.aryaxzell.aurabrowser.viewmodel.BrowserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura Browser", appName)
    }

    @Test
    fun `link preview preferences toggling`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = BrowserPreferences(context)
        prefs.isLinkPreviewEnabled = true
        assertTrue(prefs.isLinkPreviewEnabled)
        assertTrue(prefs.isLinkPreviewEnabledFlow.value)

        prefs.isLinkPreviewEnabled = false
        assertFalse(prefs.isLinkPreviewEnabled)
        assertFalse(prefs.isLinkPreviewEnabledFlow.value)
    }

    @Test
    fun `link preview view model target state`() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = BrowserViewModel(application)

        assertNull(viewModel.linkPreviewTarget.value)

        // Show standard preview
        viewModel.showLinkPreview("https://example.com/test", isSourceIncognito = false, title = "Test Page")
        val target = viewModel.linkPreviewTarget.value
        assertNotNull(target)
        assertEquals("https://example.com/test", target?.url)
        assertEquals(false, target?.isSourceIncognito)
        assertEquals("Test Page", target?.title)

        // Dismiss
        viewModel.dismissLinkPreview()
        assertNull(viewModel.linkPreviewTarget.value)

        // Invalid schemes should not open preview
        viewModel.showLinkPreview("javascript:alert(1)", isSourceIncognito = false)
        assertNull(viewModel.linkPreviewTarget.value)

        viewModel.showLinkPreview("data:text/html,<h1>Hi</h1>", isSourceIncognito = false)
        assertNull(viewModel.linkPreviewTarget.value)
    }
}
