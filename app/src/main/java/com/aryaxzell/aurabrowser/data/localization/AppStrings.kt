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
        AppLanguage.ID -> "Selesai"
        AppLanguage.EN -> "Start Browsing"
        AppLanguage.RU -> "Завершить"
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

    // Settings
    val settingsTitle: String = when (language) {
        AppLanguage.ID -> "Pengaturan"
        AppLanguage.EN -> "Settings"
        AppLanguage.RU -> "Настройки"
    }

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

    val clearAll: String = when (language) {
        AppLanguage.ID -> "Hapus Semua"
        AppLanguage.EN -> "Clear All"
        AppLanguage.RU -> "Очистить всё"
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
        AppLanguage.ID -> "Tautan disalin"
        AppLanguage.EN -> "Link copied"
        AppLanguage.RU -> "Ссылка скопирована"
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
}
