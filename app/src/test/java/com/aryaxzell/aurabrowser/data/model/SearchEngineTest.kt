package com.aryaxzell.aurabrowser.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SearchEngineTest {

    private val engine = SearchEngine.GOOGLE

    @Test
    fun testBuildQueryUrl_SearchQueries() {
        assertEquals("https://www.google.com/search?q=cat", engine.buildQueryUrl("cat"))
        assertEquals("https://www.google.com/search?q=youtube", engine.buildQueryUrl("youtube"))
        assertEquals("https://www.google.com/search?q=node.js+tutorial", engine.buildQueryUrl("node.js tutorial"))
        assertEquals("https://www.google.com/search?q=what+is+3.14", engine.buildQueryUrl("what is 3.14"))
        assertEquals("https://www.google.com/search?q=3.14", engine.buildQueryUrl("3.14"))
        assertEquals("https://www.google.com/search?q=v1.2.3", engine.buildQueryUrl("v1.2.3"))
        assertEquals("https://www.google.com/search?q=test.", engine.buildQueryUrl("test."))
        assertEquals("https://www.google.com/search?q=mail%40test.com", engine.buildQueryUrl("mail@test.com"))
    }

    @Test
    fun testBuildQueryUrl_DomainsAndPaths() {
        assertEquals("https://google.com", engine.buildQueryUrl("google.com"))
        assertEquals("https://dev.to/post", engine.buildQueryUrl("dev.to/post"))
        assertEquals("https://t.co", engine.buildQueryUrl("t.co"))
        assertEquals("https://go.id", engine.buildQueryUrl("go.id"))
    }

    @Test
    fun testBuildQueryUrl_ExplicitSchemes() {
        assertEquals("https://example.com", engine.buildQueryUrl("https://example.com"))
        assertEquals("HTTP://EXAMPLE.COM", engine.buildQueryUrl("HTTP://EXAMPLE.COM"))
        assertEquals("about:blank", engine.buildQueryUrl("about:blank"))
        assertEquals("file:///sdcard/a.html", engine.buildQueryUrl("file:///sdcard/a.html"))
    }

    @Test
    fun testBuildQueryUrl_LocalhostAndIPv4() {
        assertEquals("http://localhost:8080", engine.buildQueryUrl("localhost:8080"))
        assertEquals("http://192.168.1.1:8080/admin", engine.buildQueryUrl("192.168.1.1:8080/admin"))
    }
}
