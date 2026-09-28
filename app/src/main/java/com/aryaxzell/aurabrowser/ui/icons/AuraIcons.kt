package com.aryaxzell.aurabrowser.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private fun createIcon(name: String, pathBuilder: ImageVector.Builder.() -> Unit): ImageVector {
    return ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply(pathBuilder).build()
}

val Icons.Filled.Download: ImageVector
    get() {
        if (_download != null) return _download!!
        _download = createIcon("Filled.Download") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.0f, 9.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(6.0f)
                horizontalLineTo(5.0f)
                lineToRelative(7.0f, 7.0f)
                lineToRelative(7.0f, -7.0f)
                close()
                moveTo(5.0f, 18.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(14.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineTo(5.0f)
                close()
            }
        }
        return _download!!
    }
private var _download: ImageVector? = null

val Icons.Filled.Bookmark: ImageVector
    get() {
        if (_bookmark != null) return _bookmark!!
        _bookmark = createIcon("Filled.Bookmark") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17.0f, 3.0f)
                horizontalLineTo(7.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(16.0f)
                lineToRelative(7.0f, -3.0f)
                lineToRelative(7.0f, 3.0f)
                verticalLineTo(5.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
            }
        }
        return _bookmark!!
    }
private var _bookmark: ImageVector? = null

val Icons.Filled.BookmarkBorder: ImageVector
    get() {
        if (_bookmarkBorder != null) return _bookmarkBorder!!
        _bookmarkBorder = createIcon("Filled.BookmarkBorder") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17.0f, 3.0f)
                horizontalLineTo(7.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(16.0f)
                lineToRelative(7.0f, -3.0f)
                lineToRelative(7.0f, 3.0f)
                verticalLineTo(5.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(17.0f, 18.0f)
                lineToRelative(-5.0f, -2.18f)
                lineTo(7.0f, 18.0f)
                verticalLineTo(5.0f)
                horizontalLineToRelative(10.0f)
                verticalLineToRelative(13.0f)
                close()
            }
        }
        return _bookmarkBorder!!
    }
private var _bookmarkBorder: ImageVector? = null

val Icons.Filled.Bookmarks: ImageVector
    get() {
        if (_bookmarks != null) return _bookmarks!!
        _bookmarks = createIcon("Filled.Bookmarks") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.0f, 18.0f)
                lineToRelative(2.0f, 1.0f)
                verticalLineTo(3.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                horizontalLineTo(8.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(15.0f)
                lineToRelative(2.0f, -1.0f)
                verticalLineTo(3.0f)
                horizontalLineToRelative(11.0f)
                verticalLineToRelative(15.0f)
                close()
                moveTo(15.0f, 5.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(16.0f)
                lineToRelative(6.5f, -2.8f)
                lineTo(15.0f, 23.0f)
                verticalLineTo(7.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
            }
        }
        return _bookmarks!!
    }
private var _bookmarks: ImageVector? = null

val Icons.Filled.Shield: ImageVector
    get() {
        if (_shield != null) return _shield!!
        _shield = createIcon("Filled.Shield") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 1.0f)
                lineTo(3.0f, 5.0f)
                verticalLineToRelative(6.0f)
                curveToRelative(0.0f, 5.55f, 3.84f, 10.74f, 9.0f, 12.0f)
                curveToRelative(5.16f, -1.26f, 9.0f, -6.45f, 9.0f, -12.0f)
                verticalLineTo(5.0f)
                lineToRelative(-9.0f, -4.0f)
                close()
            }
        }
        return _shield!!
    }
private var _shield: ImageVector? = null

val Icons.Filled.Security: ImageVector
    get() {
        if (_security != null) return _security!!
        _security = createIcon("Filled.Security") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 1.0f)
                lineTo(3.0f, 5.0f)
                verticalLineToRelative(6.0f)
                curveToRelative(0.0f, 5.55f, 3.84f, 10.74f, 9.0f, 12.0f)
                curveToRelative(5.16f, -1.26f, 9.0f, -6.45f, 9.0f, -12.0f)
                verticalLineTo(5.0f)
                lineToRelative(-9.0f, -4.0f)
                close()
                moveTo(12.0f, 11.99f)
                horizontalLineToRelative(7.0f)
                curveToRelative(-0.53f, 4.12f, -3.28f, 7.79f, -7.0f, 8.94f)
                verticalLineTo(12.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(6.3f)
                lineToRelative(7.0f, -3.11f)
                verticalLineToRelative(8.8f)
                close()
            }
        }
        return _security!!
    }
private var _security: ImageVector? = null

val Icons.Filled.Block: ImageVector
    get() {
        if (_block != null) return _block!!
        _block = createIcon("Filled.Block") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                curveToRelative(0.0f, 5.52f, 4.48f, 10.0f, 10.0f, 10.0f)
                curveToRelative(5.52f, 0.0f, 10.0f, -4.48f, 10.0f, -10.0f)
                curveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(4.0f, 12.0f)
                curveToRelative(0.0f, -4.42f, 3.58f, -8.0f, 8.0f, -8.0f)
                curveToRelative(1.85f, 0.0f, 3.55f, 0.63f, 4.9f, 1.69f)
                lineTo(5.69f, 16.9f)
                curveTo(4.63f, 15.55f, 4.0f, 13.85f, 4.0f, 12.0f)
                close()
                moveTo(12.0f, 20.0f)
                curveToRelative(-1.85f, 0.0f, -3.55f, -0.63f, -4.9f, -1.69f)
                lineTo(18.31f, 7.1f)
                curveToRelative(1.06f, 1.35f, 1.69f, 3.05f, 1.69f, 4.9f)
                curveToRelative(0.0f, 4.42f, -3.58f, 8.0f, -8.0f, 8.0f)
                close()
            }
        }
        return _block!!
    }
private var _block: ImageVector? = null

val Icons.Filled.DeleteOutline: ImageVector
    get() {
        if (_deleteOutline != null) return _deleteOutline!!
        _deleteOutline = createIcon("Filled.DeleteOutline") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.0f, 9.0f)
                verticalLineToRelative(10.0f)
                horizontalLineTo(8.0f)
                verticalLineTo(9.0f)
                horizontalLineToRelative(8.0f)
                moveToRelative(-1.5f, -5.0f)
                lineToRelative(-1.0f, -1.0f)
                horizontalLineToRelative(-5.0f)
                lineToRelative(-1.0f, 1.0f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(14.0f)
                verticalLineTo(4.0f)
                horizontalLineToRelative(-3.5f)
                close()
                moveTo(18.0f, 7.0f)
                horizontalLineTo(6.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(8.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(7.0f)
                close()
            }
        }
        return _deleteOutline!!
    }
private var _deleteOutline: ImageVector? = null

val Icons.Filled.Palette: ImageVector
    get() {
        if (_palette != null) return _palette!!
        _palette = createIcon("Filled.Palette") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 2.0f)
                curveTo(6.49f, 2.0f, 2.0f, 6.49f, 2.0f, 12.0f)
                curveToRelative(0.0f, 3.31f, 2.69f, 6.0f, 6.0f, 6.0f)
                curveToRelative(0.9f, 0.0f, 1.5f, -0.67f, 1.5f, -1.5f)
                curveToRelative(0.0f, -0.39f, -0.15f, -0.74f, -0.39f, -1.01f)
                curveToRelative(-0.23f, -0.26f, -0.38f, -0.61f, -0.38f, -1.0f)
                curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
                horizontalLineTo(12.0f)
                curveToRelative(3.31f, 0.0f, 6.0f, -2.69f, 6.0f, -6.0f)
                curveToRelative(0.0f, -4.31f, -2.69f, -7.0f, -6.0f, -7.0f)
                close()
                moveTo(6.5f, 11.5f)
                curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
                curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
                curveToRelative(0.83f, 0.0f, 1.5f, 0.67f, 1.5f, 1.5f)
                curveToRelative(0.0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
                close()
                moveTo(9.5f, 7.5f)
                curveTo(8.67f, 7.5f, 8.0f, 6.83f, 8.0f, 6.0f)
                curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
                curveToRelative(0.83f, 0.0f, 1.5f, 0.67f, 1.5f, 1.5f)
                curveToRelative(0.0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
                close()
                moveTo(14.5f, 7.5f)
                curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
                curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
                curveToRelative(0.83f, 0.0f, 1.5f, 0.67f, 1.5f, 1.5f)
                curveToRelative(0.0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
                close()
                moveTo(17.5f, 11.5f)
                curveToRelative(-0.83f, 0.0f, -1.5f, -0.67f, -1.5f, -1.5f)
                curveToRelative(0.0f, -0.83f, 0.67f, -1.5f, 1.5f, -1.5f)
                curveToRelative(0.83f, 0.0f, 1.5f, 0.67f, 1.5f, 1.5f)
                curveToRelative(0.0f, 0.83f, -0.67f, 1.5f, -1.5f, 1.5f)
                close()
            }
        }
        return _palette!!
    }
private var _palette: ImageVector? = null

val Icons.Filled.ColorLens: ImageVector
    get() = Icons.Filled.Palette

val Icons.Filled.GridView: ImageVector
    get() {
        if (_gridView != null) return _gridView!!
        _gridView = createIcon("Filled.GridView") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(4.0f, 8.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(4.0f)
                horizontalLineTo(4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(10.0f, 20.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(4.0f, 20.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(4.0f, 14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(10.0f, 14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(16.0f, 4.0f)
                verticalLineToRelative(4.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(4.0f)
                horizontalLineToRelative(-4.0f)
                close()
                moveTo(10.0f, 8.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(4.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(16.0f, 14.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(4.0f)
                close()
                moveTo(16.0f, 20.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(4.0f)
                close()
            }
        }
        return _gridView!!
    }
private var _gridView: ImageVector? = null

val Icons.Filled.Apps: ImageVector
    get() = Icons.Filled.GridView

val Icons.Filled.Wallpaper: ImageVector
    get() {
        if (_wallpaper != null) return _wallpaper!!
        _wallpaper = createIcon("Filled.Wallpaper") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(4.0f, 4.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(16.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(4.0f)
                close()
                moveTo(10.0f, 13.0f)
                lineToRelative(2.5f, 3.01f)
                lineTo(16.0f, 11.0f)
                lineToRelative(4.0f, 5.0f)
                horizontalLineTo(4.0f)
                lineToRelative(6.0f, -3.0f)
                close()
            }
        }
        return _wallpaper!!
    }
private var _wallpaper: ImageVector? = null

val Icons.Filled.BlurOn: ImageVector
    get() {
        if (_blurOn != null) return _blurOn!!
        _blurOn = createIcon("Filled.BlurOn") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14.0f, 3.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(3.0f)
                close()
                moveTo(18.0f, 3.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(3.0f)
                close()
                moveTo(14.0f, 19.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(18.0f, 19.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(21.0f, 11.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(21.0f, 15.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(21.0f, 7.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(7.0f)
                close()
                moveTo(7.0f, 3.0f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(3.0f)
                close()
                moveTo(3.0f, 11.0f)
                horizontalLineTo(1.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(3.0f, 15.0f)
                horizontalLineTo(1.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(3.0f, 7.0f)
                horizontalLineTo(1.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(7.0f)
                close()
                moveTo(11.0f, 3.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(3.0f)
                close()
                moveTo(7.0f, 19.0f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(11.0f, 19.0f)
                horizontalLineTo(9.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(3.0f, 19.0f)
                horizontalLineTo(1.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(12.0f, 8.0f)
                curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
                curveToRelative(0.0f, 2.21f, 1.79f, 4.0f, 4.0f, 4.0f)
                curveToRelative(2.21f, 0.0f, 4.0f, -1.79f, 4.0f, -4.0f)
                curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
                close()
            }
        }
        return _blurOn!!
    }
private var _blurOn: ImageVector? = null

val Icons.Filled.Image: ImageVector
    get() {
        if (_image != null) return _image!!
        _image = createIcon("Filled.Image") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.0f, 19.0f)
                verticalLineTo(5.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(14.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                close()
                moveTo(8.5f, 13.5f)
                lineToRelative(2.5f, 3.01f)
                lineTo(14.5f, 13.0f)
                lineToRelative(4.5f, 6.0f)
                horizontalLineTo(5.0f)
                lineToRelative(3.5f, -5.5f)
                close()
            }
        }
        return _image!!
    }
private var _image: ImageVector? = null

val Icons.Filled.RestartAlt: ImageVector
    get() {
        if (_restartAlt != null) return _restartAlt!!
        _restartAlt = createIcon("Filled.RestartAlt") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 5.0f)
                verticalLineTo(1.0f)
                lineToRelative(-5.0f, 5.0f)
                lineToRelative(5.0f, 5.0f)
                verticalLineTo(7.0f)
                curveToRelative(3.31f, 0.0f, 6.0f, 2.69f, 6.0f, 6.0f)
                curveToRelative(0.0f, 3.31f, -2.69f, 6.0f, -6.0f, 6.0f)
                curveToRelative(-3.31f, 0.0f, -6.0f, -2.69f, -6.0f, -6.0f)
                horizontalLineTo(4.0f)
                curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
                curveToRelative(4.42f, 0.0f, 8.0f, -3.58f, 8.0f, -8.0f)
                curveToRelative(0.0f, -4.42f, -3.58f, -8.0f, -8.0f, -8.0f)
                close()
            }
        }
        return _restartAlt!!
    }
private var _restartAlt: ImageVector? = null

val Icons.Filled.Dashboard: ImageVector
    get() {
        if (_dashboard != null) return _dashboard!!
        _dashboard = createIcon("Filled.Dashboard") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3.0f, 13.0f)
                horizontalLineToRelative(8.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(3.0f)
                verticalLineToRelative(10.0f)
                close()
                moveTo(3.0f, 21.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(-6.0f)
                horizontalLineTo(3.0f)
                verticalLineToRelative(6.0f)
                close()
                moveTo(13.0f, 21.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(-10.0f)
                horizontalLineToRelative(-8.0f)
                verticalLineToRelative(10.0f)
                close()
                moveTo(13.0f, 3.0f)
                verticalLineToRelative(6.0f)
                horizontalLineToRelative(8.0f)
                verticalLineTo(3.0f)
                horizontalLineToRelative(-8.0f)
                close()
            }
        }
        return _dashboard!!
    }
private var _dashboard: ImageVector? = null

val Icons.Filled.Code: ImageVector
    get() {
        if (_code != null) return _code!!
        _code = createIcon("Filled.Code") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9.4f, 16.6f)
                lineTo(4.8f, 12.0f)
                lineToRelative(4.6f, -4.6f)
                lineTo(8.0f, 6.0f)
                lineToRelative(-6.0f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(1.4f, -1.4f)
                close()
                moveTo(14.6f, 16.6f)
                lineToRelative(4.6f, -4.6f)
                lineToRelative(-4.6f, -4.6f)
                lineTo(16.0f, 6.0f)
                lineToRelative(6.0f, 6.0f)
                lineToRelative(-6.0f, 6.0f)
                lineToRelative(-1.4f, -1.4f)
                close()
            }
        }
        return _code!!
    }
private var _code: ImageVector? = null

val Icons.Filled.Laptop: ImageVector
    get() {
        if (_laptop != null) return _laptop!!
        _laptop = createIcon("Filled.Laptop") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20.0f, 18.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(6.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(10.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineTo(0.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(24.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(-4.0f)
                close()
                moveTo(4.0f, 6.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(10.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(6.0f)
                close()
            }
        }
        return _laptop!!
    }
private var _laptop: ImageVector? = null

val Icons.AutoMirrored.Filled.OpenInNew: ImageVector
    get() {
        if (_openInNew != null) return _openInNew!!
        _openInNew = createIcon("AutoMirrored.Filled.OpenInNew") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.0f, 19.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(5.0f)
                horizontalLineToRelative(7.0f)
                verticalLineTo(3.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(14.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineToRelative(-7.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(7.0f)
                close()
                moveTo(14.0f, 3.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(3.59f)
                lineToRelative(-9.83f, 9.83f)
                lineToRelative(1.41f, 1.41f)
                lineTo(19.0f, 6.41f)
                verticalLineTo(10.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(3.0f)
                horizontalLineToRelative(-7.0f)
                close()
            }
        }
        return _openInNew!!
    }
private var _openInNew: ImageVector? = null

val Icons.Filled.OpenInNew: ImageVector
    get() = Icons.AutoMirrored.Filled.OpenInNew

val Icons.Filled.Layers: ImageVector
    get() {
        if (_layers != null) return _layers!!
        _layers = createIcon("Filled.Layers") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.99f, 18.54f)
                lineToRelative(-7.37f, -5.73f)
                lineTo(3.0f, 14.07f)
                lineToRelative(9.0f, 7.0f)
                lineToRelative(9.0f, -7.0f)
                lineToRelative(-1.63f, -1.27f)
                lineToRelative(-7.38f, 5.74f)
                close()
                moveTo(12.0f, 16.0f)
                lineToRelative(7.36f, -5.73f)
                lineTo(21.0f, 9.0f)
                lineToRelative(-9.0f, -7.0f)
                lineToRelative(-9.0f, 7.0f)
                lineToRelative(1.63f, 1.27f)
                lineTo(12.0f, 16.0f)
                close()
            }
        }
        return _layers!!
    }
private var _layers: ImageVector? = null

val Icons.Filled.Settings: ImageVector
    get() {
        if (_settings != null) return _settings!!
        _settings = createIcon("Filled.Settings") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.14f, 12.94f)
                curveToRelative(0.04f, -0.3f, 0.06f, -0.61f, 0.06f, -0.94f)
                curveToRelative(0.0f, -0.32f, -0.02f, -0.64f, -0.07f, -0.94f)
                lineToRelative(2.03f, -1.58f)
                curveToRelative(0.18f, -0.14f, 0.23f, -0.41f, 0.12f, -0.61f)
                lineToRelative(-1.92f, -3.32f)
                curveToRelative(-0.12f, -0.22f, -0.37f, -0.29f, -0.59f, -0.22f)
                lineToRelative(-2.39f, 0.96f)
                curveToRelative(-0.5f, -0.38f, -1.03f, -0.7f, -1.62f, -0.94f)
                lineToRelative(-0.36f, -2.54f)
                curveToRelative(-0.04f, -0.24f, -0.24f, -0.41f, -0.48f, -0.41f)
                horizontalLineTo(9.25f)
                curveToRelative(-0.24f, 0.0f, -0.43f, 0.17f, -0.47f, 0.41f)
                lineTo(8.42f, 5.35f)
                curveToRelative(-0.59f, 0.24f, -1.13f, 0.57f, -1.62f, 0.94f)
                lineToRelative(-2.39f, -0.96f)
                curveToRelative(-0.22f, -0.08f, -0.47f, 0.0f, -0.59f, 0.22f)
                lineTo(1.9f, 8.87f)
                curveToRelative(-0.11f, 0.2f, -0.06f, 0.47f, 0.12f, 0.61f)
                lineToRelative(2.03f, 1.58f)
                curveToRelative(-0.05f, 0.3f, -0.09f, 0.63f, -0.09f, 0.94f)
                curveToRelative(0.0f, 0.31f, 0.04f, 0.64f, 0.09f, 0.94f)
                lineToRelative(-2.03f, 1.58f)
                curveToRelative(-0.18f, 0.14f, -0.23f, 0.41f, -0.12f, 0.61f)
                lineToRelative(1.92f, 3.32f)
                curveToRelative(0.12f, 0.22f, 0.37f, 0.29f, 0.59f, 0.22f)
                lineToRelative(2.39f, -0.96f)
                curveToRelative(0.5f, 0.38f, 1.03f, 0.7f, 1.62f, 0.94f)
                lineToRelative(0.36f, 2.54f)
                curveToRelative(0.05f, 0.24f, 0.24f, 0.41f, 0.48f, 0.41f)
                horizontalLineToRelative(3.84f)
                curveToRelative(0.24f, 0.0f, 0.44f, -0.17f, 0.47f, -0.41f)
                lineToRelative(0.36f, -2.54f)
                curveToRelative(0.59f, -0.24f, 1.13f, -0.56f, 1.62f, -0.94f)
                lineToRelative(2.39f, 0.96f)
                curveToRelative(0.22f, 0.08f, 0.47f, 0.0f, 0.59f, -0.22f)
                lineToRelative(1.92f, -3.32f)
                curveToRelative(0.12f, -0.2f, 0.07f, -0.47f, -0.12f, -0.61f)
                lineToRelative(-2.01f, -1.58f)
                close()
                moveTo(12.0f, 15.6f)
                curveToRelative(-1.98f, 0.0f, -3.6f, -1.62f, -3.6f, -3.6f)
                curveToRelative(0.0f, -1.98f, 1.62f, -3.6f, 3.6f, -3.6f)
                curveToRelative(1.98f, 0.0f, 3.6f, 1.62f, 3.6f, 3.6f)
                curveToRelative(0.0f, 1.98f, -1.62f, 3.6f, -3.6f, 3.6f)
                close()
            }
        }
        return _settings!!
    }
private var _settings: ImageVector? = null

val Icons.Filled.Language: ImageVector
    get() {
        if (_language != null) return _language!!
        _language = createIcon("Filled.Language") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.99f, 2.0f)
                curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                curveToRelative(0.0f, 5.52f, 4.47f, 10.0f, 9.99f, 10.0f)
                curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                curveTo(22.0f, 6.48f, 17.52f, 2.0f, 11.99f, 2.0f)
                close()
                moveTo(18.92f, 8.0f)
                horizontalLineToRelative(-2.95f)
                curveToRelative(-0.32f, -1.25f, -0.78f, -2.45f, -1.38f, -3.56f)
                curveToRelative(1.84f, 0.63f, 3.37f, 1.91f, 4.33f, 3.56f)
                close()
                moveTo(12.0f, 4.04f)
                curveToRelative(0.83f, 1.2f, 1.48f, 2.53f, 1.91f, 3.96f)
                horizontalLineTo(10.09f)
                curveToRelative(0.43f, -1.43f, 1.08f, -2.76f, 1.91f, -3.96f)
                close()
                moveTo(4.26f, 14.0f)
                curveToRelative(-0.16f, -0.64f, -0.26f, -1.31f, -0.26f, -2.0f)
                curveToRelative(0.0f, -0.69f, 0.1f, -1.36f, 0.26f, -2.0f)
                horizontalLineToRelative(3.38f)
                curveToRelative(-0.08f, 0.66f, -0.14f, 1.32f, -0.14f, 2.0f)
                curveToRelative(0.0f, 0.68f, 0.06f, 1.34f, 0.14f, 2.0f)
                horizontalLineTo(4.26f)
                close()
                moveTo(5.08f, 16.0f)
                horizontalLineToRelative(2.95f)
                curveToRelative(0.32f, 1.25f, 0.78f, 2.45f, 1.38f, 3.56f)
                curveTo(7.57f, 18.93f, 6.04f, 17.65f, 5.08f, 16.0f)
                close()
                moveTo(8.03f, 8.0f)
                horizontalLineTo(5.08f)
                curveToRelative(0.96f, -1.65f, 2.49f, -2.93f, 4.33f, -3.56f)
                curveToRelative(-0.6f, 1.11f, -1.06f, 2.31f, -1.38f, 3.56f)
                close()
                moveTo(12.0f, 19.96f)
                curveToRelative(-0.83f, -1.2f, -1.48f, -2.53f, -1.91f, -3.96f)
                horizontalLineToRelative(3.82f)
                curveToRelative(-0.43f, 1.43f, -1.08f, 2.76f, -1.91f, 3.96f)
                close()
                moveTo(14.34f, 14.0f)
                horizontalLineTo(9.66f)
                curveToRelative(-0.09f, -0.66f, -0.16f, -1.32f, -0.16f, -2.0f)
                curveToRelative(0.0f, -0.68f, 0.07f, -1.35f, 0.16f, -2.0f)
                horizontalLineToRelative(4.68f)
                curveToRelative(0.09f, 0.65f, 0.16f, 1.32f, 0.16f, 2.0f)
                curveToRelative(0.0f, 0.68f, -0.07f, 1.34f, -0.16f, 2.0f)
                close()
                moveTo(14.59f, 19.56f)
                curveToRelative(0.6f, -1.11f, 1.06f, -2.31f, 1.38f, -3.56f)
                horizontalLineToRelative(2.95f)
                curveToRelative(-0.96f, 1.65f, -2.49f, 2.93f, -4.33f, 3.56f)
                close()
                moveTo(16.36f, 14.0f)
                curveToRelative(0.08f, -0.66f, 0.14f, -1.32f, 0.14f, -2.0f)
                curveToRelative(0.0f, -0.68f, -0.06f, -1.34f, -0.14f, -2.0f)
                horizontalLineToRelative(3.38f)
                curveToRelative(0.16f, 0.64f, 0.26f, 1.31f, 0.26f, 2.0f)
                curveToRelative(0.0f, 0.69f, -0.1f, 1.36f, -0.26f, 2.0f)
                horizontalLineToRelative(-3.38f)
                close()
            }
        }
        return _language!!
    }
private var _language: ImageVector? = null

val Icons.Filled.CloudOff: ImageVector
    get() {
        if (_cloudOff != null) return _cloudOff!!
        _cloudOff = createIcon("Filled.CloudOff") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.35f, 10.04f)
                curveTo(18.67f, 6.59f, 15.64f, 4.0f, 12.0f, 4.0f)
                curveToRelative(-1.48f, 0.0f, -2.85f, 0.43f, -4.01f, 1.17f)
                lineToRelative(1.46f, 1.46f)
                curveTo(10.21f, 6.23f, 11.08f, 6.0f, 12.0f, 6.0f)
                curveToRelative(3.04f, 0.0f, 5.5f, 2.46f, 5.5f, 5.5f)
                verticalLineToRelative(0.5f)
                horizontalLineTo(19.0f)
                curveToRelative(1.66f, 0.0f, 3.0f, 1.34f, 3.0f, 3.0f)
                curveToRelative(0.0f, 1.13f, -0.64f, 2.11f, -1.59f, 2.62f)
                lineToRelative(1.45f, 1.45f)
                curveTo(22.9f, 17.58f, 24.0f, 15.93f, 24.0f, 14.0f)
                curveToRelative(0.0f, -2.89f, -2.03f, -5.3f, -4.65f, -5.96f)
                close()
                moveTo(3.0f, 5.27f)
                lineToRelative(2.75f, 2.74f)
                curveTo(2.56f, 8.95f, 0.0f, 11.18f, 0.0f, 14.0f)
                curveToRelative(0.0f, 3.31f, 2.69f, 6.0f, 6.0f, 6.0f)
                horizontalLineToRelative(11.73f)
                lineToRelative(2.0f, 2.0f)
                lineToRelative(1.41f, -1.41f)
                lineTo(4.41f, 3.86f)
                lineTo(3.0f, 5.27f)
                close()
                moveTo(7.73f, 10.0f)
                lineToRelative(8.0f, 8.0f)
                horizontalLineTo(6.0f)
                curveToRelative(-2.21f, 0.0f, -4.0f, -1.79f, -4.0f, -4.0f)
                curveToRelative(0.0f, -2.21f, 1.79f, -4.0f, 4.0f, -4.0f)
                curveToRelative(0.58f, 0.0f, 1.14f, 0.13f, 1.73f, 0.35f)
                close()
            }
        }
        return _cloudOff!!
    }
private var _cloudOff: ImageVector? = null

val Icons.Filled.ErrorOutline: ImageVector
    get() {
        if (_errorOutline != null) return _errorOutline!!
        _errorOutline = createIcon("Filled.ErrorOutline") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.0f, 15.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-2.0f)
                close()
                moveTo(11.0f, 7.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(6.0f)
                horizontalLineToRelative(-2.0f)
                close()
                moveTo(12.0f, 2.0f)
                curveTo(6.47f, 2.0f, 2.0f, 6.47f, 2.0f, 12.0f)
                curveToRelative(0.0f, 5.53f, 4.47f, 10.0f, 10.0f, 10.0f)
                curveToRelative(5.53f, 0.0f, 10.0f, -4.47f, 10.0f, -10.0f)
                curveTo(22.0f, 6.47f, 17.53f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(12.0f, 20.0f)
                curveToRelative(-4.41f, 0.0f, -8.0f, -3.59f, -8.0f, -8.0f)
                curveToRelative(0.0f, -4.41f, 3.59f, -8.0f, 8.0f, -8.0f)
                curveToRelative(4.41f, 0.0f, 8.0f, 3.59f, 8.0f, 8.0f)
                curveToRelative(0.0f, 4.41f, -3.59f, 8.0f, -8.0f, 8.0f)
                close()
            }
        }
        return _errorOutline!!
    }
private var _errorOutline: ImageVector? = null

val Icons.Filled.AudioFile: ImageVector
    get() {
        if (_audioFile != null) return _audioFile!!
        _audioFile = createIcon("Filled.AudioFile") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14.0f, 2.0f)
                horizontalLineTo(6.0f)
                curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(4.0f, 20.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
                horizontalLineTo(18.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                lineToRelative(-6.0f, -6.0f)
                close()
                moveTo(16.0f, 15.0f)
                horizontalLineToRelative(-3.0f)
                verticalLineToRelative(-1.5f)
                curveToRelative(0.0f, -1.38f, -1.12f, -2.5f, -2.5f, -2.5f)
                curveTo(9.12f, 11.0f, 8.0f, 12.12f, 8.0f, 13.5f)
                curveTo(8.0f, 14.88f, 9.12f, 16.0f, 10.5f, 16.0f)
                curveToRelative(0.34f, 0.0f, 0.66f, -0.07f, 0.96f, -0.19f)
                verticalLineTo(18.0f)
                curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
                curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
                horizontalLineToRelative(0.04f)
                verticalLineToRelative(-1.0f)
                horizontalLineTo(11.0f)
                curveToRelative(-1.66f, 0.0f, -3.0f, 1.34f, -3.0f, 3.0f)
                curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
                curveToRelative(1.66f, 0.0f, 3.0f, -1.34f, 3.0f, -3.0f)
                verticalLineToRelative(-5.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                close()
            }
        }
        return _audioFile!!
    }
private var _audioFile: ImageVector? = null

val Icons.Filled.Description: ImageVector
    get() {
        if (_description != null) return _description!!
        _description = createIcon("Filled.Description") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14.0f, 2.0f)
                horizontalLineTo(6.0f)
                curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(4.0f, 20.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
                horizontalLineTo(18.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                lineToRelative(-6.0f, -6.0f)
                close()
                moveTo(16.0f, 18.0f)
                horizontalLineTo(8.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(2.0f)
                close()
                moveTo(16.0f, 14.0f)
                horizontalLineTo(8.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(2.0f)
                close()
                moveTo(13.0f, 9.0f)
                verticalLineTo(3.5f)
                lineTo(18.5f, 9.0f)
                horizontalLineTo(13.0f)
                close()
            }
        }
        return _description!!
    }
private var _description: ImageVector? = null

val Icons.Filled.FolderZip: ImageVector
    get() {
        if (_folderZip != null) return _folderZip!!
        _folderZip = createIcon("Filled.FolderZip") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20.0f, 6.0f)
                horizontalLineToRelative(-8.0f)
                lineToRelative(-2.0f, -2.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(2.0f, 18.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(16.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(18.0f, 12.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineTo(8.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                close()
            }
        }
        return _folderZip!!
    }
private var _folderZip: ImageVector? = null

val Icons.Filled.InsertDriveFile: ImageVector
    get() {
        if (_insertDriveFile != null) return _insertDriveFile!!
        _insertDriveFile = createIcon("Filled.InsertDriveFile") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6.0f, 2.0f)
                curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(4.0f, 20.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
                horizontalLineTo(18.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                lineToRelative(-6.0f, -6.0f)
                horizontalLineTo(6.0f)
                close()
                moveTo(13.0f, 9.0f)
                verticalLineTo(3.5f)
                lineTo(18.5f, 9.0f)
                horizontalLineTo(13.0f)
                close()
            }
        }
        return _insertDriveFile!!
    }
private var _insertDriveFile: ImageVector? = null

val Icons.Filled.OpenInBrowser: ImageVector
    get() {
        if (_openInBrowser != null) return _openInBrowser!!
        _openInBrowser = createIcon("Filled.OpenInBrowser") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.0f, 4.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineTo(5.0f)
                verticalLineTo(8.0f)
                horizontalLineToRelative(14.0f)
                verticalLineToRelative(10.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(4.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(6.0f)
                curveToRelative(0.0f, -1.1f, -0.89f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(12.0f, 10.0f)
                lineToRelative(-4.0f, 4.0f)
                horizontalLineToRelative(3.0f)
                verticalLineToRelative(6.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-6.0f)
                horizontalLineToRelative(3.0f)
                lineToRelative(-4.0f, -4.0f)
                close()
            }
        }
        return _openInBrowser!!
    }
private var _openInBrowser: ImageVector? = null

val Icons.Filled.VideoFile: ImageVector
    get() {
        if (_videoFile != null) return _videoFile!!
        _videoFile = createIcon("Filled.VideoFile") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14.0f, 2.0f)
                horizontalLineTo(6.0f)
                curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(4.0f, 20.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 1.99f, 2.0f)
                horizontalLineTo(18.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                lineToRelative(-6.0f, -6.0f)
                close()
                moveTo(15.0f, 13.0f)
                lineToRelative(-3.0f, 2.25f)
                verticalLineTo(13.0f)
                lineToRelative(3.0f, 2.25f)
                verticalLineToRelative(-4.5f)
                close()
                moveTo(13.0f, 9.0f)
                verticalLineTo(3.5f)
                lineTo(18.5f, 9.0f)
                horizontalLineTo(13.0f)
                close()
            }
        }
        return _videoFile!!
    }
private var _videoFile: ImageVector? = null

val Icons.Filled.DeleteSweep: ImageVector
    get() {
        if (_deleteSweep != null) return _deleteSweep!!
        _deleteSweep = createIcon("Filled.DeleteSweep") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(15.0f, 16.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-4.0f)
                close()
                moveTo(15.0f, 8.0f)
                horizontalLineToRelative(7.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-7.0f)
                close()
                moveTo(15.0f, 12.0f)
                horizontalLineToRelative(6.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-6.0f)
                close()
                moveTo(3.0f, 18.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(6.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(8.0f)
                horizontalLineTo(3.0f)
                verticalLineToRelative(10.0f)
                close()
                moveTo(14.0f, 5.0f)
                horizontalLineToRelative(-3.0f)
                lineToRelative(-1.0f, -1.0f)
                horizontalLineTo(6.0f)
                lineToRelative(-1.0f, 1.0f)
                horizontalLineTo(2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(12.0f)
                verticalLineTo(5.0f)
                close()
            }
        }
        return _deleteSweep!!
    }
private var _deleteSweep: ImageVector? = null

val Icons.Filled.LockOpen: ImageVector
    get() {
        if (_lockOpen != null) return _lockOpen!!
        _lockOpen = createIcon("Filled.LockOpen") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 17.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                close()
                moveTo(18.0f, 8.0f)
                horizontalLineToRelative(-1.0f)
                verticalLineTo(6.0f)
                curveToRelative(0.0f, -2.76f, -2.24f, -5.0f, -5.0f, -5.0f)
                curveToRelative(-2.28f, 0.0f, -4.22f, 1.53f, -4.78f, 3.63f)
                curveToRelative(-0.14f, 0.53f, 0.18f, 1.07f, 0.72f, 1.21f)
                curveToRelative(0.54f, 0.14f, 1.08f, -0.18f, 1.22f, -0.72f)
                curveToRelative(0.32f, -1.22f, 1.45f, -2.12f, 2.84f, -2.12f)
                curveToRelative(1.65f, 0.0f, 3.0f, 1.35f, 3.0f, 3.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(6.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(10.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(12.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(10.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(18.0f, 20.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(10.0f)
                horizontalLineToRelative(12.0f)
                verticalLineToRelative(10.0f)
                close()
            }
        }
        return _lockOpen!!
    }
private var _lockOpen: ImageVector? = null

val Icons.Filled.Terminal: ImageVector
    get() {
        if (_terminal != null) return _terminal!!
        _terminal = createIcon("Filled.Terminal") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20.0f, 4.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(12.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(16.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(6.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(20.0f, 18.0f)
                horizontalLineTo(4.0f)
                verticalLineTo(8.0f)
                horizontalLineToRelative(16.0f)
                verticalLineToRelative(10.0f)
                close()
                moveTo(7.5f, 11.0f)
                lineToRelative(-1.4f, 1.4f)
                lineTo(8.2f, 14.0f)
                lineToRelative(-2.1f, 1.6f)
                lineTo(7.5f, 17.0f)
                lineTo(11.0f, 14.0f)
                close()
                moveTo(12.0f, 15.0f)
                horizontalLineToRelative(5.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(-5.0f)
                close()
            }
        }
        return _terminal!!
    }
private var _terminal: ImageVector? = null

val Icons.Filled.NetworkCheck: ImageVector
    get() {
        if (_networkCheck != null) return _networkCheck!!
        _networkCheck = createIcon("Filled.NetworkCheck") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(15.9f, 5.0f)
                curveToRelative(-0.2f, 0.0f, -0.4f, 0.1f, -0.5f, 0.2f)
                lineTo(12.0f, 8.6f)
                lineTo(8.6f, 5.2f)
                curveToRelative(-0.3f, -0.3f, -0.8f, -0.3f, -1.1f, 0.0f)
                lineToRelative(-4.3f, 4.3f)
                curveToRelative(-0.3f, 0.3f, -0.3f, 0.8f, 0.0f, 1.1f)
                lineToRelative(8.2f, 8.2f)
                curveToRelative(0.3f, 0.3f, 0.8f, 0.3f, 1.1f, 0.0f)
                lineToRelative(8.2f, -8.2f)
                curveToRelative(0.3f, -0.3f, 0.3f, -0.8f, 0.0f, -1.1f)
                lineTo(16.4f, 5.2f)
                curveToRelative(-0.1f, -0.1f, -0.3f, -0.2f, -0.5f, -0.2f)
                close()
            }
        }
        return _networkCheck!!
    }
private var _networkCheck: ImageVector? = null

val Icons.Filled.BugReport: ImageVector
    get() {
        if (_bugReport != null) return _bugReport!!
        _bugReport = createIcon("Filled.BugReport") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.0f, 8.0f)
                horizontalLineToRelative(-1.81f)
                curveToRelative(-0.45f, -0.78f, -1.07f, -1.45f, -1.82f, -1.96f)
                lineToRelative(1.28f, -1.28f)
                curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                lineToRelative(-1.59f, 1.59f)
                curveToRelative(-0.62f, -0.25f, -1.28f, -0.39f, -1.97f, -0.39f)
                curveToRelative(-0.69f, 0.0f, -1.35f, 0.14f, -1.97f, 0.39f)
                lineTo(8.75f, 3.35f)
                curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                curveToRelative(-0.39f, 0.39f, -0.39f, 1.02f, 0.0f, 1.41f)
                lineToRelative(1.28f, 1.28f)
                curveTo(7.88f, 6.55f, 7.26f, 7.22f, 6.81f, 8.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                horizontalLineToRelative(1.13f)
                curveToRelative(-0.08f, 0.32f, -0.13f, 0.65f, -0.13f, 1.0f)
                verticalLineToRelative(1.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                horizontalLineToRelative(1.0f)
                verticalLineToRelative(1.0f)
                curveToRelative(0.0f, 0.35f, 0.05f, 0.68f, 0.13f, 1.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-0.55f, 0.0f, -1.0f, 0.45f, -1.0f, 1.0f)
                curveToRelative(0.0f, 0.55f, 0.45f, 1.0f, 1.0f, 1.0f)
                horizontalLineToRelative(1.81f)
                curveToRelative(1.04f, 1.79f, 2.97f, 3.0f, 5.19f, 3.0f)
                curveToRelative(2.22f, 0.0f, 4.15f, -1.21f, 5.19f, -3.0f)
                horizontalLineTo(19.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                horizontalLineToRelative(-1.13f)
                curveToRelative(0.08f, -0.32f, 0.13f, -0.65f, 0.13f, -1.0f)
                verticalLineToRelative(-1.0f)
                horizontalLineToRelative(1.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                horizontalLineToRelative(-1.0f)
                verticalLineToRelative(-1.0f)
                curveToRelative(0.0f, -0.35f, -0.05f, -0.68f, -0.13f, -1.0f)
                horizontalLineTo(19.0f)
                curveToRelative(0.55f, 0.0f, 1.0f, -0.45f, 1.0f, -1.0f)
                curveToRelative(0.0f, -0.55f, -0.45f, -1.0f, -1.0f, -1.0f)
                close()
            }
        }
        return _bugReport!!
    }
private var _bugReport: ImageVector? = null

val Icons.Filled.Explore: ImageVector
    get() {
        if (_explore != null) return _explore!!
        _explore = createIcon("Filled.Explore") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                curveToRelative(0.0f, 5.52f, 4.48f, 10.0f, 10.0f, 10.0f)
                curveToRelative(5.52f, 0.0f, 10.0f, -4.48f, 10.0f, -10.0f)
                curveTo(22.0f, 6.48f, 17.52f, 2.0f, 12.0f, 2.0f)
                close()
                moveTo(14.19f, 14.19f)
                lineTo(6.5f, 17.5f)
                lineToRelative(3.31f, -7.69f)
                lineTo(17.5f, 6.5f)
                lineToRelative(-3.31f, 7.69f)
                close()
                moveTo(12.0f, 10.9f)
                curveToRelative(-0.61f, 0.0f, -1.1f, 0.49f, -1.1f, 1.1f)
                curveToRelative(0.0f, 0.61f, 0.49f, 1.1f, 1.1f, 1.1f)
                curveToRelative(0.61f, 0.0f, 1.1f, -0.49f, 1.1f, -1.1f)
                curveToRelative(0.0f, -0.61f, -0.49f, -1.1f, -1.1f, -1.1f)
                close()
            }
        }
        return _explore!!
    }
private var _explore: ImageVector? = null

val Icons.Filled.AddBox: ImageVector
    get() {
        if (_addBox != null) return _addBox!!
        _addBox = createIcon("Filled.AddBox") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.0f, 3.0f)
                horizontalLineTo(5.0f)
                curveToRelative(-1.11f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(14.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(5.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(17.0f, 13.0f)
                horizontalLineToRelative(-4.0f)
                verticalLineToRelative(4.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineTo(7.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(4.0f)
                verticalLineTo(7.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(4.0f)
                horizontalLineToRelative(4.0f)
                verticalLineToRelative(2.0f)
                close()
            }
        }
        return _addBox!!
    }
private var _addBox: ImageVector? = null

val Icons.Filled.IosShare: ImageVector
    get() {
        if (_iosShare != null) return _iosShare!!
        _iosShare = createIcon("Filled.IosShare") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.0f, 5.0f)
                lineToRelative(-1.42f, 1.42f)
                lineToRelative(-1.59f, -1.59f)
                verticalLineTo(16.0f)
                horizontalLineToRelative(-1.98f)
                verticalLineTo(4.83f)
                lineTo(9.42f, 6.42f)
                lineTo(8.0f, 5.0f)
                lineToRelative(4.0f, -4.0f)
                lineToRelative(4.0f, 4.0f)
                close()
                moveTo(20.0f, 10.0f)
                verticalLineToRelative(11.0f)
                curveToRelative(0.0f, 1.1f, -0.9f, 2.0f, -2.0f, 2.0f)
                horizontalLineTo(6.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, -0.9f, -2.0f, -2.0f)
                verticalLineTo(10.0f)
                curveToRelative(0.0f, -1.1f, 0.9f, -2.0f, 2.0f, -2.0f)
                horizontalLineToRelative(3.0f)
                verticalLineToRelative(2.0f)
                horizontalLineTo(6.0f)
                verticalLineToRelative(11.0f)
                horizontalLineToRelative(12.0f)
                verticalLineTo(10.0f)
                horizontalLineToRelative(-3.0f)
                verticalLineTo(8.0f)
                horizontalLineToRelative(3.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, 0.9f, 2.0f, 2.0f)
                close()
            }
        }
        return _iosShare!!
    }
private var _iosShare: ImageVector? = null

val Icons.Filled.ContentCopy: ImageVector
    get() {
        if (_contentCopy != null) return _contentCopy!!
        _contentCopy = createIcon("Filled.ContentCopy") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.0f, 1.0f)
                horizontalLineTo(4.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(14.0f)
                horizontalLineToRelative(2.0f)
                verticalLineTo(3.0f)
                horizontalLineToRelative(12.0f)
                verticalLineTo(1.0f)
                close()
                moveTo(19.0f, 5.0f)
                horizontalLineTo(8.0f)
                curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(11.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(7.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(19.0f, 21.0f)
                horizontalLineTo(8.0f)
                verticalLineTo(7.0f)
                horizontalLineToRelative(11.0f)
                verticalLineToRelative(14.0f)
                close()
            }
        }
        return _contentCopy!!
    }
private var _contentCopy: ImageVector? = null

