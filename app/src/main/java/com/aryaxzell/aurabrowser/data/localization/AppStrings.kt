package com.aryaxzell.aurabrowser.data.localization

import com.aryaxzell.aurabrowser.data.model.AppLanguage

class AppStrings(val language: AppLanguage) {

    // Onboarding & Welcome
    val welcomeTitle: String = when (language) {
        AppLanguage.ID -> "Selamat Datang di Aura"
        AppLanguage.EN -> "Welcome to Aura"
        AppLanguage.RU -> "Добро пожаловать в Aura"
    }

    val welcomeSubtitle: String = when (language) {
        AppLanguage.ID -> "Peramban Web Cepat, Privat & Elegan"
        AppLanguage.EN -> "Fast, Private & Customizable Web Browser"
        AppLanguage.RU -> "Быстрый, приватный и удобный веб-браузер"
    }

    val selectLanguage: String = when (language) {
        AppLanguage.ID -> "Pilih Bahasa"
        AppLanguage.EN -> "Select Language"
        AppLanguage.RU -> "Выберите язык"
    }

    val builtInPrivacyTitle: String = when (language) {
        AppLanguage.ID -> "Privasi Terpasang"
        AppLanguage.EN -> "Built-in Privacy"
        AppLanguage.RU -> "Встроенная защита"
    }

    val builtInPrivacyDesc: String = when (language) {
        AppLanguage.ID -> "Blokir pelacak dan iklan mengganggu otomatis."
        AppLanguage.EN -> "Block trackers and intrusive ads automatically."
        AppLanguage.RU -> "Блокировка трекеров и навязчивой рекламы."
    }

    val personalizedThemesTitle: String = when (language) {
        AppLanguage.ID -> "Tema Kustom"
        AppLanguage.EN -> "Personalized Themes"
        AppLanguage.RU -> "Персонализация"
    }

    val personalizedThemesDesc: String = when (language) {
        AppLanguage.ID -> "Wallpaper kustom, warna aksen, dan pintasan."
        AppLanguage.EN -> "Custom wallpapers, accent colors and widgets."
        AppLanguage.RU -> "Обои, акцентные цвета и виджеты."
    }

    val getStarted: String = when (language) {
        AppLanguage.ID -> "Mulai Sekarang"
        AppLanguage.EN -> "Get Started"
        AppLanguage.RU -> "Начать"
    }

    val continueButton: String = when (language) {
        AppLanguage.ID -> "Lanjutkan"
        AppLanguage.EN -> "Continue"
        AppLanguage.RU -> "Продолжить"
    }

    val finishButton: String = when (language) {
        AppLanguage.ID -> "Mulai Menjelajah"
        AppLanguage.EN -> "Start Browsing"
        AppLanguage.RU -> "Начать работу"
    }

    val whatsNew: String = when (language) {
        AppLanguage.ID -> "Yang Baru di Aura"
        AppLanguage.EN -> "What's New in Aura"
        AppLanguage.RU -> "Что нового в Aura"
    }

    val permissionsTitle: String = when (language) {
        AppLanguage.ID -> "Izin Sistem"
        AppLanguage.EN -> "App Permissions"
        AppLanguage.RU -> "Разрешения"
    }

    val permissionsSubtitle: String = when (language) {
        AppLanguage.ID -> "Aktifkan izin untuk pengalaman browsing yang optimal."
        AppLanguage.EN -> "Enable permissions for the best browsing experience."
        AppLanguage.RU -> "Включите разрешения для лучшей работы браузера."
    }

    val customizationTitle: String = when (language) {
        AppLanguage.ID -> "Personalisasi Aura"
        AppLanguage.EN -> "Personalize Aura"
        AppLanguage.RU -> "Персонализация Aura"
    }

    val customizationSubtitle: String = when (language) {
        AppLanguage.ID -> "Pilih tema dan warna aksen sesuai seleramu."
        AppLanguage.EN -> "Choose your preferred appearance and accent color."
        AppLanguage.RU -> "Выберите оформление и акцентный цвет."
    }

    // Common Actions
    val back: String = when (language) {
        AppLanguage.ID -> "Kembali"
        AppLanguage.EN -> "Back"
        AppLanguage.RU -> "Назад"
    }

    val done: String = when (language) {
        AppLanguage.ID -> "Selesai"
        AppLanguage.EN -> "Done"
        AppLanguage.RU -> "Готово"
    }

    val edit: String = when (language) {
        AppLanguage.ID -> "Ubah"
        AppLanguage.EN -> "Edit"
        AppLanguage.RU -> "Изменить"
    }

    val save: String = when (language) {
        AppLanguage.ID -> "Simpan"
        AppLanguage.EN -> "Save"
        AppLanguage.RU -> "Сохранить"
    }

    val cancel: String = when (language) {
        AppLanguage.ID -> "Batal"
        AppLanguage.EN -> "Cancel"
        AppLanguage.RU -> "Отмена"
    }

    val delete: String = when (language) {
        AppLanguage.ID -> "Hapus"
        AppLanguage.EN -> "Delete"
        AppLanguage.RU -> "Удалить"
    }

    val close: String = when (language) {
        AppLanguage.ID -> "Tutup"
        AppLanguage.EN -> "Close"
        AppLanguage.RU -> "Закрыть"
    }

    val clearAll: String = when (language) {
        AppLanguage.ID -> "Hapus Semua"
        AppLanguage.EN -> "Clear All"
        AppLanguage.RU -> "Очистить всё"
    }

    val reload: String = when (language) {
        AppLanguage.ID -> "Muat Ulang"
        AppLanguage.EN -> "Reload"
        AppLanguage.RU -> "Перезагрузить"
    }

    // Settings
    val settingsTitle: String = when (language) {
        AppLanguage.ID -> "Pengaturan"
        AppLanguage.EN -> "Settings"
        AppLanguage.RU -> "Настройки"
    }

    val tabGeneral: String = when (language) {
        AppLanguage.ID -> "Umum"
        AppLanguage.EN -> "General"
        AppLanguage.RU -> "Основные"
    }

    val tabAppearance: String = when (language) {
        AppLanguage.ID -> "Tampilan"
        AppLanguage.EN -> "Appearance"
        AppLanguage.RU -> "Внешний вид"
    }

    val tabPrivacy: String = when (language) {
        AppLanguage.ID -> "Privasi"
        AppLanguage.EN -> "Privacy"
        AppLanguage.RU -> "Приватность"
    }

    val tabBrowser: String = when (language) {
        AppLanguage.ID -> "Peramban"
        AppLanguage.EN -> "Browser"
        AppLanguage.RU -> "Браузер"
    }

    val languageSettingHeader: String = when (language) {
        AppLanguage.ID -> "BAHASA"
        AppLanguage.EN -> "LANGUAGE"
        AppLanguage.RU -> "ЯЗЫК"
    }

    val languageSettingFooter: String = when (language) {
        AppLanguage.ID -> "Pilih bahasa tampilan untuk aplikasi Aura Browser."
        AppLanguage.EN -> "Choose the display language for Aura Browser."
        AppLanguage.RU -> "Выберите язык интерфейса приложения Aura Browser."
    }

    val searchEngineHeader: String = when (language) {
        AppLanguage.ID -> "MESIN PENCARI UTAMA"
        AppLanguage.EN -> "DEFAULT SEARCH ENGINE"
        AppLanguage.RU -> "ПОИСКОВАЯ СИСТЕМА"
    }

    val searchEngineFooter: String = when (language) {
        AppLanguage.ID -> "Kueri yang diketik pada bilah alamat akan mencari dengan penyedia ini."
        AppLanguage.EN -> "Queries typed into the address bar will search with the selected provider."
        AppLanguage.RU -> "Поисковые запросы из адресной строки будут использовать эту систему."
    }

    val greetingNameTitle: String = when (language) {
        AppLanguage.ID -> "Nama Panggilan"
        AppLanguage.EN -> "Greeting Name"
        AppLanguage.RU -> "Имя пользователя"
    }

    val greetingNameFooter: String = when (language) {
        AppLanguage.ID -> "Nama panggilan ditampilkan pada halaman beranda peramban."
        AppLanguage.EN -> "Your greeting name appears on the browser home screen."
        AppLanguage.RU -> "Имя отображается на главной странице браузера."
    }

    val appearanceThemeHeader: String = when (language) {
        AppLanguage.ID -> "TEMA & TAMPILAN"
        AppLanguage.EN -> "THEME & APPEARANCE"
        AppLanguage.RU -> "ТЕМА И ОФОРМЛЕНИЕ"
    }

    val themeFollowSystem: String = when (language) {
        AppLanguage.ID -> "Ikuti Sistem"
        AppLanguage.EN -> "Follow System"
        AppLanguage.RU -> "Как в системе"
    }

    val themeLight: String = when (language) {
        AppLanguage.ID -> "Terang"
        AppLanguage.EN -> "Light"
        AppLanguage.RU -> "Светлая"
    }

    val themeDark: String = when (language) {
        AppLanguage.ID -> "Gelap"
        AppLanguage.EN -> "Dark"
        AppLanguage.RU -> "Тёмная"
    }

    val accentColorHeader: String = when (language) {
        AppLanguage.ID -> "WARNA AKSEN"
        AppLanguage.EN -> "ACCENT COLOR"
        AppLanguage.RU -> "АКЦЕНТНЫЙ ЦВЕТ"
    }

    val tabSwitcherHeader: String = when (language) {
        AppLanguage.ID -> "TAMPILAN PENGALIH TAB"
        AppLanguage.EN -> "TAB SWITCHER LAYOUT"
        AppLanguage.RU -> "ВИД ПЕРЕКЛЮЧАТЕЛЯ ВКЛАДОК"
    }

    val homeWidgetsHeader: String = when (language) {
        AppLanguage.ID -> "WIDGET BERANDA"
        AppLanguage.EN -> "HOMEPAGE WIDGETS"
        AppLanguage.RU -> "ВИДЖЕТЫ НА ГЛАВНОЙ"
    }

    val showShortcutsSetting: String = when (language) {
        AppLanguage.ID -> "Tampilkan Pintasan"
        AppLanguage.EN -> "Show Shortcuts"
        AppLanguage.RU -> "Показывать закладки"
    }

    val showRecentHistorySetting: String = when (language) {
        AppLanguage.ID -> "Tampilkan Riwayat Terakhir"
        AppLanguage.EN -> "Show Recent History"
        AppLanguage.RU -> "Показывать недавнюю историю"
    }

    val wallpaperHeader: String = when (language) {
        AppLanguage.ID -> "WALLPAPER BERANDA"
        AppLanguage.EN -> "HOMEPAGE WALLPAPER"
        AppLanguage.RU -> "ОБОИ ГЛАВНОЙ СТРАНИЦЫ"
    }

    val blurWallpaper: String = when (language) {
        AppLanguage.ID -> "Efek Buram (Blur)"
        AppLanguage.EN -> "Blur Wallpaper"
        AppLanguage.RU -> "Размытие обоев"
    }

    val changeWallpaper: String = when (language) {
        AppLanguage.ID -> "Pilih Foto Wallpaper"
        AppLanguage.EN -> "Choose Wallpaper"
        AppLanguage.RU -> "Выбрать обои"
    }

    val removeWallpaper: String = when (language) {
        AppLanguage.ID -> "Hapus Wallpaper"
        AppLanguage.EN -> "Remove Wallpaper"
        AppLanguage.RU -> "Удалить обои"
    }

    val contentProtectionHeader: String = when (language) {
        AppLanguage.ID -> "PERLINDUNGAN KONTEN & PRIVASI"
        AppLanguage.EN -> "CONTENT & PRIVACY PROTECTION"
        AppLanguage.RU -> "ЗАЩИТА И ПРИВАТНОСТЬ"
    }

    val blockAdsTitle: String = when (language) {
        AppLanguage.ID -> "Blokir Iklan & Pelacak"
        AppLanguage.EN -> "Block Ads & Trackers"
        AppLanguage.RU -> "Блокировка рекламы и трекеров"
    }

    val blockAdsSubtitle: String = when (language) {
        AppLanguage.ID -> "Menyaring iklan intrusif dan pelacak analitik"
        AppLanguage.EN -> "Filters intrusive ads and trackers"
        AppLanguage.RU -> "Фильтрация навязчивой рекламы и трекеров"
    }

    val doNotTrackTitle: String = when (language) {
        AppLanguage.ID -> "Jangan Lacak (Do Not Track)"
        AppLanguage.EN -> "Do Not Track (DNT)"
        AppLanguage.RU -> "Не отслеживать (DNT)"
    }

    val doNotTrackSubtitle: String = when (language) {
        AppLanguage.ID -> "Kirim permintaan DNT ke situs yang dikunjungi"
        AppLanguage.EN -> "Sends DNT request header to websites"
        AppLanguage.RU -> "Отправляет сайтам заголовок «Не отслеживать»"
    }

    val clearDataTitle: String = when (language) {
        AppLanguage.ID -> "Hapus Data Penjelajahan"
        AppLanguage.EN -> "Clear Browsing Data"
        AppLanguage.RU -> "Очистить данные браузера"
    }

    val linkPreviewsTitle: String = when (language) {
        AppLanguage.ID -> "Pratinjau Link (Tahan Link)"
        AppLanguage.EN -> "Link Previews"
        AppLanguage.RU -> "Предпросмотр ссылок"
    }

    val linkPreviewsSubtitle: String = when (language) {
        AppLanguage.ID -> "Tampilkan kartu pratinjau halaman saat link ditahan. Membutuhkan memori ekstra."
        AppLanguage.EN -> "Show a page preview when you hold a link. Uses extra data and memory."
        AppLanguage.RU -> "Показывать карточку предпросмотра при удержании ссылки."
    }

    val desktopDefaultTitle: String = when (language) {
        AppLanguage.ID -> "Situs Desktop sebagai Default"
        AppLanguage.EN -> "Desktop Site by Default"
        AppLanguage.RU -> "Версия для ПК по умолчанию"
    }

    val enableJavaScriptTitle: String = when (language) {
        AppLanguage.ID -> "Aktifkan JavaScript"
        AppLanguage.EN -> "Enable JavaScript"
        AppLanguage.RU -> "Включить JavaScript"
    }

    val developerMode: String = when (language) {
        AppLanguage.ID -> "Mode Pengembang"
        AppLanguage.EN -> "Developer Mode"
        AppLanguage.RU -> "Режим разработчика"
    }

    val developerModeSubtitle: String = when (language) {
        AppLanguage.ID -> "Aktifkan inspeksi konsol JS dan log jaringan"
        AppLanguage.EN -> "Enables JS console and network inspection"
        AppLanguage.RU -> "Включает консоль JS и сетевой журнал"
    }

    // Home screen
    fun getGreeting(hour: Int, name: String): String = when (language) {
        AppLanguage.ID -> when (hour) {
            in 4..11 -> "Selamat pagi, $name"
            in 12..14 -> "Selamat siang, $name"
            in 15..18 -> "Selamat sore, $name"
            else -> "Selamat malam, $name"
        }
        AppLanguage.EN -> when (hour) {
            in 4..11 -> "Good morning, $name"
            in 12..17 -> "Good afternoon, $name"
            else -> "Good evening, $name"
        }
        AppLanguage.RU -> when (hour) {
            in 4..11 -> "Доброе утро, $name"
            in 12..17 -> "Добрый день, $name"
            else -> "Добрый вечер, $name"
        }
    }

    val searchPlaceholder: String = when (language) {
        AppLanguage.ID -> "Cari atau ketik URL..."
        AppLanguage.EN -> "Search or enter URL..."
        AppLanguage.RU -> "Поиск или адрес URL..."
    }

    val shortcutsTitle: String = when (language) {
        AppLanguage.ID -> "Pintasan"
        AppLanguage.EN -> "Shortcuts"
        AppLanguage.RU -> "Закладки"
    }

    val recentHistoryTitle: String = when (language) {
        AppLanguage.ID -> "Riwayat Terakhir"
        AppLanguage.EN -> "Recent History"
        AppLanguage.RU -> "Недавняя история"
    }

    val bookmarksTitle: String = when (language) {
        AppLanguage.ID -> "Markah Buku"
        AppLanguage.EN -> "Bookmarks"
        AppLanguage.RU -> "Избранное"
    }

    val historyTitle: String = when (language) {
        AppLanguage.ID -> "Riwayat"
        AppLanguage.EN -> "History"
        AppLanguage.RU -> "История"
    }

    val downloadsTitle: String = when (language) {
        AppLanguage.ID -> "Unduhan"
        AppLanguage.EN -> "Downloads"
        AppLanguage.RU -> "Загрузки"
    }

    val noBookmarks: String = when (language) {
        AppLanguage.ID -> "Belum ada markah buku tersimpan."
        AppLanguage.EN -> "No bookmarks saved yet."
        AppLanguage.RU -> "Закладок пока нет."
    }

    val noHistory: String = when (language) {
        AppLanguage.ID -> "Belum ada riwayat penjelajahan."
        AppLanguage.EN -> "No browsing history yet."
        AppLanguage.RU -> "Истории браузера пока нет."
    }

    val noDownloads: String = when (language) {
        AppLanguage.ID -> "Belum ada file unduhan."
        AppLanguage.EN -> "No downloaded files yet."
        AppLanguage.RU -> "Загруженных файлов пока нет."
    }

    val searchBookmarks: String = when (language) {
        AppLanguage.ID -> "Cari markah..."
        AppLanguage.EN -> "Search bookmarks..."
        AppLanguage.RU -> "Поиск в закладках..."
    }

    val searchHistory: String = when (language) {
        AppLanguage.ID -> "Cari riwayat..."
        AppLanguage.EN -> "Search history..."
        AppLanguage.RU -> "Поиск в истории..."
    }

    val searchDownloads: String = when (language) {
        AppLanguage.ID -> "Cari unduhan..."
        AppLanguage.EN -> "Search downloads..."
        AppLanguage.RU -> "Поиск в загрузках..."
    }

    // Top Bar & Menu Actions
    val newTab: String = when (language) {
        AppLanguage.ID -> "Tab Baru"
        AppLanguage.EN -> "New Tab"
        AppLanguage.RU -> "Новая вкладка"
    }

    val newIncognitoTab: String = when (language) {
        AppLanguage.ID -> "Tab Samaran Baru"
        AppLanguage.EN -> "New Incognito Tab"
        AppLanguage.RU -> "Новая вкладка инкогнито"
    }

    val addBookmark: String = when (language) {
        AppLanguage.ID -> "Tambah Markah"
        AppLanguage.EN -> "Add Bookmark"
        AppLanguage.RU -> "Добавить закладку"
    }

    val removeBookmark: String = when (language) {
        AppLanguage.ID -> "Hapus Markah"
        AppLanguage.EN -> "Remove Bookmark"
        AppLanguage.RU -> "Удалить закладку"
    }

    val desktopSite: String = when (language) {
        AppLanguage.ID -> "Situs Desktop"
        AppLanguage.EN -> "Desktop Site"
        AppLanguage.RU -> "Версия для ПК"
    }

    val mobileSite: String = when (language) {
        AppLanguage.ID -> "Situs Seluler"
        AppLanguage.EN -> "Mobile Site"
        AppLanguage.RU -> "Мобильная версия"
    }

    val share: String = when (language) {
        AppLanguage.ID -> "Bagikan"
        AppLanguage.EN -> "Share"
        AppLanguage.RU -> "Поделиться"
    }

    val sharePage: String = when (language) {
        AppLanguage.ID -> "Bagikan Halaman"
        AppLanguage.EN -> "Share Page"
        AppLanguage.RU -> "Поделиться страницей"
    }

    val moreOptions: String = when (language) {
        AppLanguage.ID -> "Lainnya..."
        AppLanguage.EN -> "More..."
        AppLanguage.RU -> "Ещё..."
    }

    val developerTools: String = when (language) {
        AppLanguage.ID -> "Alat Pengembang"
        AppLanguage.EN -> "Developer Tools"
        AppLanguage.RU -> "Инструменты разработчика"
    }

    val viewPageSource: String = when (language) {
        AppLanguage.ID -> "Lihat Sumber Halaman"
        AppLanguage.EN -> "View Page Source"
        AppLanguage.RU -> "Исходный код страницы"
    }

    val networkLog: String = when (language) {
        AppLanguage.ID -> "Log Jaringan"
        AppLanguage.EN -> "Network Log"
        AppLanguage.RU -> "Сетевой журнал"
    }

    val console: String = when (language) {
        AppLanguage.ID -> "Konsol JavaScript"
        AppLanguage.EN -> "JavaScript Console"
        AppLanguage.RU -> "Консоль JavaScript"
    }

    val siteInfo: String = when (language) {
        AppLanguage.ID -> "Info Situs"
        AppLanguage.EN -> "Site Info"
        AppLanguage.RU -> "О сайте"
    }

    // Link Preview Actions
    val open: String = when (language) {
        AppLanguage.ID -> "Buka"
        AppLanguage.EN -> "Open"
        AppLanguage.RU -> "Открыть"
    }

    val openInNewTab: String = when (language) {
        AppLanguage.ID -> "Buka di Tab Baru"
        AppLanguage.EN -> "Open in New Tab"
        AppLanguage.RU -> "Открыть в новой вкладке"
    }

    val openInIncognitoTab: String = when (language) {
        AppLanguage.ID -> "Buka di Tab Samaran"
        AppLanguage.EN -> "Open in Incognito Tab"
        AppLanguage.RU -> "Открыть в режиме инкогнито"
    }

    val downloadLinkedFile: String = when (language) {
        AppLanguage.ID -> "Unduh File Tautan"
        AppLanguage.EN -> "Download Linked File"
        AppLanguage.RU -> "Загрузить файл по ссылке"
    }

    val addToBookmarks: String = when (language) {
        AppLanguage.ID -> "Tambah ke Markah"
        AppLanguage.EN -> "Add to Bookmarks"
        AppLanguage.RU -> "Добавить в закладки"
    }

    val removeFromBookmarks: String = when (language) {
        AppLanguage.ID -> "Hapus dari Markah"
        AppLanguage.EN -> "Remove from Bookmarks"
        AppLanguage.RU -> "Удалить из закладок"
    }

    val copyLink: String = when (language) {
        AppLanguage.ID -> "Salin Tautan"
        AppLanguage.EN -> "Copy Link"
        AppLanguage.RU -> "Скопировать ссылку"
    }

    val hidePreview: String = when (language) {
        AppLanguage.ID -> "Sembunyikan pratinjau"
        AppLanguage.EN -> "Hide preview"
        AppLanguage.RU -> "Скрыть предпросмотр"
    }

    val showPreview: String = when (language) {
        AppLanguage.ID -> "Tampilkan pratinjau"
        AppLanguage.EN -> "Show preview"
        AppLanguage.RU -> "Показать предпросмотр"
    }

    val notSecure: String = when (language) {
        AppLanguage.ID -> "Tidak aman"
        AppLanguage.EN -> "Not secure"
        AppLanguage.RU -> "Не защищено"
    }

    val linkCopied: String = when (language) {
        AppLanguage.ID -> "Tautan disalin ke papan klip"
        AppLanguage.EN -> "Link copied to clipboard"
        AppLanguage.RU -> "Ссылка скопирована в буфер"
    }

    val addedToBookmarksToast: String = when (language) {
        AppLanguage.ID -> "Ditambahkan ke markah"
        AppLanguage.EN -> "Added to bookmarks"
        AppLanguage.RU -> "Добавлено в закладки"
    }

    val removedFromBookmarksToast: String = when (language) {
        AppLanguage.ID -> "Dihapus dari markah"
        AppLanguage.EN -> "Removed from bookmarks"
        AppLanguage.RU -> "Удалено из закладок"
    }

    // Developer / Tools Screens
    val consoleTitle: String = when (language) {
        AppLanguage.ID -> "Konsol JavaScript"
        AppLanguage.EN -> "JavaScript Console"
        AppLanguage.RU -> "Консоль JavaScript"
    }

    fun logsRecorded(count: Int): String = when (language) {
        AppLanguage.ID -> "$count pesan log tercatat"
        AppLanguage.EN -> "$count log messages recorded"
        AppLanguage.RU -> "Записей журнала: $count"
    }

    val filterConsolePlaceholder: String = when (language) {
        AppLanguage.ID -> "Saring pesan konsol..."
        AppLanguage.EN -> "Filter console messages..."
        AppLanguage.RU -> "Фильтр сообщений..."
    }

    val noConsoleLogs: String = when (language) {
        AppLanguage.ID -> "Tidak ada pesan konsol"
        AppLanguage.EN -> "No console messages"
        AppLanguage.RU -> "Нет сообщений в консоли"
    }

    val clearLogs: String = when (language) {
        AppLanguage.ID -> "Hapus Log"
        AppLanguage.EN -> "Clear Logs"
        AppLanguage.RU -> "Очистить журнал"
    }

    val copyLogs: String = when (language) {
        AppLanguage.ID -> "Salin Log"
        AppLanguage.EN -> "Copy Logs"
        AppLanguage.RU -> "Скопировать журнал"
    }

    val networkLogTitle: String = when (language) {
        AppLanguage.ID -> "Log Jaringan"
        AppLanguage.EN -> "Network Log"
        AppLanguage.RU -> "Сетевой журнал"
    }

    fun requestsRecorded(count: Int): String = when (language) {
        AppLanguage.ID -> "$count permintaan tercatat"
        AppLanguage.EN -> "$count requests recorded"
        AppLanguage.RU -> "Запросов: $count"
    }

    val filterNetworkPlaceholder: String = when (language) {
        AppLanguage.ID -> "Saring permintaan jaringan..."
        AppLanguage.EN -> "Filter network requests..."
        AppLanguage.RU -> "Фильтр запросов..."
    }

    val noNetworkLogs: String = when (language) {
        AppLanguage.ID -> "Tidak ada permintaan jaringan"
        AppLanguage.EN -> "No network requests"
        AppLanguage.RU -> "Нет сетевых запросов"
    }

    val pageSourceTitle: String = when (language) {
        AppLanguage.ID -> "Kode Sumber Halaman"
        AppLanguage.EN -> "Page Source"
        AppLanguage.RU -> "Исходный код страницы"
    }

    val wrapLines: String = when (language) {
        AppLanguage.ID -> "Bungkus Baris"
        AppLanguage.EN -> "Wrap Lines"
        AppLanguage.RU -> "Перенос строк"
    }

    val copySource: String = when (language) {
        AppLanguage.ID -> "Salin Sumber"
        AppLanguage.EN -> "Copy Source"
        AppLanguage.RU -> "Скопировать код"
    }

    // Add Shortcut Dialog
    val addShortcut: String = when (language) {
        AppLanguage.ID -> "Tambah Pintasan"
        AppLanguage.EN -> "Add Shortcut"
        AppLanguage.RU -> "Добавить ярлык"
    }

    val shortcutTitle: String = when (language) {
        AppLanguage.ID -> "Nama Pintasan"
        AppLanguage.EN -> "Shortcut Name"
        AppLanguage.RU -> "Название ярлыка"
    }

    val shortcutUrl: String = when (language) {
        AppLanguage.ID -> "Alamat URL"
        AppLanguage.EN -> "URL Address"
        AppLanguage.RU -> "Адрес URL"
    }

    // Tab Switcher
    val closeAllTabs: String = when (language) {
        AppLanguage.ID -> "Tutup Semua Tab"
        AppLanguage.EN -> "Close All Tabs"
        AppLanguage.RU -> "Закрыть все вкладки"
    }

    fun tabsCount(count: Int): String = when (language) {
        AppLanguage.ID -> "$count Tab"
        AppLanguage.EN -> "$count Tabs"
        AppLanguage.RU -> "Вкладок: $count"
    }

    val incognito: String = when (language) {
        AppLanguage.ID -> "Samaran"
        AppLanguage.EN -> "Incognito"
        AppLanguage.RU -> "Инкогнито"
    }

    val translate: String = when (language) {
        AppLanguage.ID -> "Terjemahkan Halaman"
        AppLanguage.EN -> "Translate Page"
        AppLanguage.RU -> "Перевести страницу"
    }

    val translatorSettings: String = when (language) {
        AppLanguage.ID -> "Pengaturan Penerjemah"
        AppLanguage.EN -> "Translator Settings"
        AppLanguage.RU -> "Настройки переводчика"
    }

    val translateFrom: String = when (language) {
        AppLanguage.ID -> "Terjemahkan dari"
        AppLanguage.EN -> "Translate from"
        AppLanguage.RU -> "Перевести с"
    }

    val translateTo: String = when (language) {
        AppLanguage.ID -> "Terjemahkan ke"
        AppLanguage.EN -> "Translate to"
        AppLanguage.RU -> "Перевести на"
    }

    val translating: String = when (language) {
        AppLanguage.ID -> "Menerjemahkan..."
        AppLanguage.EN -> "Translating..."
        AppLanguage.RU -> "Перевод..."
    }

    val selectTranslatorEngine: String = when (language) {
        AppLanguage.ID -> "Pilih Mesin Penerjemah"
        AppLanguage.EN -> "Select Translator Engine"
        AppLanguage.RU -> "Выберите переводчик"
    }

    val geminiApiKeyLabel: String = when (language) {
        AppLanguage.ID -> "Kunci API Gemini"
        AppLanguage.EN -> "Gemini API Key"
        AppLanguage.RU -> "API-ключ Gemini"
    }

    val geminiApiKeyPlaceholder: String = when (language) {
        AppLanguage.ID -> "Masukkan Kunci API Gemini..."
        AppLanguage.EN -> "Enter Gemini API Key..."
        AppLanguage.RU -> "Введите API-ключ Gemini..."
    }

    val geminiModelLabel: String = when (language) {
        AppLanguage.ID -> "Model Gemini"
        AppLanguage.EN -> "Gemini Model"
        AppLanguage.RU -> "Модель Gemini"
    }

    val checkModels: String = when (language) {
        AppLanguage.ID -> "Periksa Model"
        AppLanguage.EN -> "Check Models"
        AppLanguage.RU -> "Проверить модели"
    }

    val modelsAvailable: String = when (language) {
        AppLanguage.ID -> "Daftar Model Tersedia"
        AppLanguage.EN -> "Available Models List"
        AppLanguage.RU -> "Список доступных моделей"
    }

    // About App
    val aboutTitle: String = when (language) {
        AppLanguage.ID -> "Tentang"
        AppLanguage.EN -> "About"
        AppLanguage.RU -> "О приложении"
    }

    val aboutSlogan: String = when (language) {
        AppLanguage.ID -> "Ringan. Cepat. Milik Anda."
        AppLanguage.EN -> "Light. Fast. Yours."
        AppLanguage.RU -> "Легкий. Быстрый. Твой."
    }

    val aboutIntroText: String = when (language) {
        AppLanguage.ID -> "Aura Browser adalah peramban web modern yang dibangun untuk kecepatan, privasi, dan pengalaman menjelajah yang lebih baik. Ringan, simpel, namun tetap bertenaga."
        AppLanguage.EN -> "Aura Browser is a modern web browser built for speed, privacy, and a better browsing experience. Lightweight, simple, yet powerful."
        AppLanguage.RU -> "Aura Browser — это современный веб-браузер, созданный для скорости, конфиденциальности и лучшего удобства работы в Интернете. Легкий, простой, но мощный."
    }

    val aboutBadgeFast: String = when (language) {
        AppLanguage.ID -> "Cepat"
        AppLanguage.EN -> "Fast"
        AppLanguage.RU -> "Быстрый"
    }

    val aboutBadgePrivate: String = when (language) {
        AppLanguage.ID -> "Privat"
        AppLanguage.EN -> "Private"
        AppLanguage.RU -> "Приватный"
    }

    val aboutBadgeOpenSource: String = when (language) {
        AppLanguage.ID -> "Sumber Terbuka"
        AppLanguage.EN -> "Open Source"
        AppLanguage.RU -> "Открытый код"
    }

    val aboutBadgeModern: String = when (language) {
        AppLanguage.ID -> "Modern"
        AppLanguage.EN -> "Modern"
        AppLanguage.RU -> "Современный"
    }

    val aboutChangelogTitle: String = when (language) {
        AppLanguage.ID -> "Catatan Rilis"
        AppLanguage.EN -> "Changelog"
        AppLanguage.RU -> "История изменений"
    }

    val aboutChangelogDesc: String = when (language) {
        AppLanguage.ID -> "Lihat perubahan di versi ini"
        AppLanguage.EN -> "View changes in this version"
        AppLanguage.RU -> "Посмотреть изменения в этой версии"
    }

    val aboutSourceCodeTitle: String = when (language) {
        AppLanguage.ID -> "Kode Sumber"
        AppLanguage.EN -> "Source Code"
        AppLanguage.RU -> "Исходный код"
    }

    val aboutSourceCodeDesc: String = when (language) {
        AppLanguage.ID -> "GitHub • Sumber Terbuka"
        AppLanguage.EN -> "GitHub • Open Source"
        AppLanguage.RU -> "GitHub • Открытый код"
    }

    val aboutPrivacyTitle: String = when (language) {
        AppLanguage.ID -> "Kebijakan Privasi"
        AppLanguage.EN -> "Privacy Policy"
        AppLanguage.RU -> "Политика конфиденциальности"
    }

    val aboutPrivacyDesc: String = when (language) {
        AppLanguage.ID -> "Bagaimana kami melindungi data Anda"
        AppLanguage.EN -> "How we protect your data"
        AppLanguage.RU -> "Как мы защищаем ваши данные"
    }

    val aboutSupportTitle: String = when (language) {
        AppLanguage.ID -> "Dukungan"
        AppLanguage.EN -> "Support"
        AppLanguage.RU -> "Поддержка"
    }

    val aboutSupportDesc: String = when (language) {
        AppLanguage.ID -> "Hubungi kami jika ada pertanyaan atau bug"
        AppLanguage.EN -> "Contact us if you have questions or bugs"
        AppLanguage.RU -> "Свяжитесь с нами при возникновении вопросов"
    }

    val aboutPassionTitle: String = when (language) {
        AppLanguage.ID -> "Dibuat dengan penuh semangat"
        AppLanguage.EN -> "Made with passion"
        AppLanguage.RU -> "Сделано с любовью"
    }

    val aboutPassionDesc: String = when (language) {
        AppLanguage.ID -> "Terima kasih sudah menggunakan Aura Browser. Dukungan kalian sangat berarti!"
        AppLanguage.EN -> "Thank you for using Aura Browser. Your support means the world to us!"
        AppLanguage.RU -> "Спасибо за использование Aura Browser. Ваша поддержка очень важна для нас!"
    }

    val aboutUpdateTitle: String = when (language) {
        AppLanguage.ID -> "Cek Pembaruan"
        AppLanguage.EN -> "Check for Updates"
        AppLanguage.RU -> "Проверить обновления"
    }

    val aboutUpdateDesc: String = when (language) {
        AppLanguage.ID -> "Periksa pembaruan di GitHub Actions"
        AppLanguage.EN -> "Check for updates on GitHub Actions"
        AppLanguage.RU -> "Проверить обновления на GitHub Actions"
    }

    val aboutCheckingUpdate: String = when (language) {
        AppLanguage.ID -> "Memeriksa..."
        AppLanguage.EN -> "Checking..."
        AppLanguage.RU -> "Проверка..."
    }

    val aboutUpToDateMessage: String = when (language) {
        AppLanguage.ID -> "Versi anda sudah versi terbaru"
        AppLanguage.EN -> "Your version is up to date"
        AppLanguage.RU -> "У вас установлена последняя версия"
    }

    val aboutDownloadPrompt: String = when (language) {
        AppLanguage.ID -> "download versi terbaru?"
        AppLanguage.EN -> "Download the latest version?"
        AppLanguage.RU -> "Скачать последнюю версию?"
    }

    val defaultBrowserTitle: String = when (language) {
        AppLanguage.ID -> "Browser Default"
        AppLanguage.EN -> "Default Browser"
        AppLanguage.RU -> "Основной браузер"
    }

    val defaultBrowserSubtitle: String = when (language) {
        AppLanguage.ID -> "Jadikan Aura Browser sebagai browser utama Anda untuk membuka tautan eksternal secara cepat, aman, dan tanpa iklan."
        AppLanguage.EN -> "Set Aura Browser as your default browser to open external links fast, securely, and ad-free."
        AppLanguage.RU -> "Сделайте Aura Browser браузером по умолчанию для быстрого и безопасного открытия ссылок без рекламы."
    }

    val defaultBrowserRecommended: String = when (language) {
        AppLanguage.ID -> "Disarankan untuk pengalaman terbaik"
        AppLanguage.EN -> "Recommended for the best experience"
        AppLanguage.RU -> "Рекомендуется для наилучшего удобства"
    }

    val setAsDefaultButton: String = when (language) {
        AppLanguage.ID -> "Jadikan Browser Default"
        AppLanguage.EN -> "Set as Default Browser"
        AppLanguage.RU -> "Сделать браузером по умолчанию"
    }

    val skipForNowButton: String = when (language) {
        AppLanguage.ID -> "Lewati untuk sekarang"
        AppLanguage.EN -> "Skip for now"
        AppLanguage.RU -> "Пропустить пока"
    }

    val isDefaultSuccess: String = when (language) {
        AppLanguage.ID -> "Aura Browser sudah menjadi browser default Anda"
        AppLanguage.EN -> "Aura Browser is set as your default browser"
        AppLanguage.RU -> "Aura Browser установлен браузером по умолчанию"
    }

    val defaultBrowserFeature1Title: String = when (language) {
        AppLanguage.ID -> "Privasi Terjamin & Bebas Iklan"
        AppLanguage.EN -> "Guaranteed Privacy & Ad-Free"
        AppLanguage.RU -> "Конфиденциальность и без рекламы"
    }

    val defaultBrowserFeature1Desc: String = when (language) {
        AppLanguage.ID -> "Tautan eksternal otomatis terbuka tanpa pelacak dan iklan pop-up yang mengganggu."
        AppLanguage.EN -> "External links open automatically without trackers or intrusive pop-up ads."
        AppLanguage.RU -> "Внешние ссылки открываются без трекеров и навязчивой рекламы."
    }

    val defaultBrowserFeature2Title: String = when (language) {
        AppLanguage.ID -> "Kecepatan Akses Seketika"
        AppLanguage.EN -> "Instant Access Speed"
        AppLanguage.RU -> "Мгновенная скорость доступа"
    }

    val defaultBrowserFeature2Desc: String = when (language) {
        AppLanguage.ID -> "Didukung mesin WebView pool optimal untuk pemuatan halaman super gesit."
        AppLanguage.EN -> "Powered by an optimized WebView pool for blazing-fast page loads."
        AppLanguage.RU -> "Оптимизированный движок WebView для сверхбыстрой загрузки страниц."
    }

    val defaultBrowserFeature3Title: String = when (language) {
        AppLanguage.ID -> "Integrasi Tautan Langsung"
        AppLanguage.EN -> "Seamless Link Integration"
        AppLanguage.RU -> "Прямая интеграция ссылок"
    }

    val defaultBrowserFeature3Desc: String = when (language) {
        AppLanguage.ID -> "Buka link dari pesan, email, atau sosial media tanpa repot memilih browser."
        AppLanguage.EN -> "Open links from messages, emails, or social apps directly without picking a browser."
        AppLanguage.RU -> "Открывайте ссылки из сообщений, почты или соцсетей сразу без лишних окон."
    }
}
