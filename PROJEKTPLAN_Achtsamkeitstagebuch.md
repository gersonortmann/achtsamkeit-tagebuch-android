# Achtsamkeitstagebuch – Android App
## Umfassender Projektplan & Architekturdokumentation

> **Version:** 1.0  
> **Erstellt:** 2026-04  
> **Tech Lead / Architekt / Engineer:** Claude (Anthropic)  
> **Projektsprache:** Deutsch (App-UI) / Englisch (Code & Kommentare)

---

## 📋 Inhaltsverzeichnis

1. [Projektziele & Scope](#1-projektziele--scope)
2. [Architecture Decision Records (ADRs)](#2-architecture-decision-records-adrs)
3. [Technischer Stack](#3-technischer-stack)
4. [Architektur & Modulstruktur](#4-architektur--modulstruktur)
5. [Feature-Übersicht & Priorisierung](#5-feature-übersicht--priorisierung)
6. [Phasenplan mit Aufgaben & Akzeptanzkriterien](#6-phasenplan-mit-aufgaben--akzeptanzkriterien)
7. [Test-Strategie](#7-test-strategie)
8. [Refactoring-Richtlinien](#8-refactoring-richtlinien)
9. [Git-Workflow & Versionierung](#9-git-workflow--versionierung)
10. [API-Keys & Konfiguration](#10-api-keys--konfiguration)
11. [Definition of Done (DoD)](#11-definition-of-done-dod)
12. [Bekannte Risiken & Mitigationen](#12-bekannte-risiken--mitigationen)
13. [Glossar](#13-glossar)

---

## 1. Projektziele & Scope

### Vision
Eine vollwertige, datenschutzfreundliche Achtsamkeitstagebuch-App für Android, die KI-gestützte Reflexion, Sprachsteuerung und Cloud-Backup vereint.

### Kernziele
- **Privat & sicher** – Biometrischer Schutz, lokale Datenspeicherung als Basis
- **Benutzerfreundlich** – Spracheingabe, intuitive Navigation auf Deutsch
- **KI-gestützt** – Gemini für Reflexionsfragen und Wochenrückblicke
- **Synchronisierbar** – Google Drive oder OneDrive nach Nutzerwahl
- **Teilbar** – APK für privaten Freundeskreis weitergeben

### Explizit NICHT im Scope (v1.0)
- Play Store Veröffentlichung
- iOS-Version
- Mehrsprachigkeit (nur Deutsch)
- Backend-Server / eigene API
- In-App-Käufe

---

## 2. Architecture Decision Records (ADRs)

> ADRs dokumentieren wichtige Architekturentscheidungen. **Hier offene Punkte eintragen und bestätigen bevor Phase 1 startet.**

### ADR-001: App-Name & Package
| Feld | Status | Wert |
|------|--------|------|
| App-Name | ✅ **BESTÄTIGT** | **Interaktives Achtsamkeitstagebuch** |
| Anzeigename (Launcher) | ✅ | **Achtsamkeit** *(kurzer Name für App-Icon-Label)* |
| Package Name | ✅ **BESTÄTIGT** | `com.achtsamkeit.tagebuch` |
| **Hinweis** | Package-Name ist nach Veröffentlichung nicht mehr änderbar | |

### ADR-002: Mindest-Android-Version
| Option | Geräteabdeckung | Vorteile | Status |
|--------|----------------|----------|--------|
| Android 11 (API 30) | ~95% | Scoped Storage | Verworfen |
| **Android 12 (API 31)** | **~88%** | **Material You (dynamische Farben), nativer Splash Screen, verbesserter BiometricPrompt** | ✅ **GEWÄHLT** |
| Android 13 (API 33) | ~80% | Saubere Notification-Permission, Per-App-Sprache | Overkill für v1.0 |
| Android 14+ (API 34+) | ~65% | Predictive Back, Health Connect | Zukünftige Version |

**Entscheidung:** `minSdk = 31`, `targetSdk = 35`, `compileSdk = 35`

**Begründung:** Material You passt die App-Farben automatisch ans Wallpaper an – ideal für ein persönliches Achtsamkeitstagebuch. ~88% Geräteabdeckung ist exzellent. Pixel 10 Pro und Samsung S24 unterstützen beide Android 12+ vollständig.

### ADR-003: Dark Mode
| Status | ✅ **BESTÄTIGT** |
|--------|-------------|
| Entscheidung | Dark Mode von Anfang an (Light / Dark / System-automatisch) |
| Umsetzung | Material3 + Material You dynamische Farben – minimaler Mehraufwand dank Compose |
| Bonus | Material You generiert automatisch harmonische Dark-Mode-Farben aus Wallpaper |

### ADR-004: Versionskontrolle
| Status | ✅ **BESTÄTIGT** |
|--------|-------------|
| Plattform | **GitHub** (privates Repository) |
| Empfehlung | Repository-Name: `achtsamkeit-tagebuch-android` |
| Branch-Schutz | `main` Branch schützen (kein direkter Push, nur via PR/Merge) |

### ADR-005: Microsoft-Account für OneDrive
| Status | ✅ **BESTÄTIGT** |
|--------|-------------|
| Microsoft-Account | Vorhanden |
| Entscheidung | OneDrive-Sync in Phase 8b vollständig umsetzen |
| Azure | App-Registration in Azure Portal (Anleitung bei Phase 8b) |

### ADR-006: Gemini Nano vs. Gemini API
| Kriterium | Gemini Nano (On-Device) | Gemini API (Cloud) |
|-----------|------------------------|-------------------|
| Kosten | Kostenlos | Kostenlos-Kontingent (60 req/min) |
| Datenschutz | ✅ Alles lokal | Daten gehen zu Google |
| Verfügbarkeit | Pixel 9+, Samsung S24+ | Alle Geräte |
| Qualität | Gut für kurze Texte | Besser für komplexe Analyse |
| **Entscheidung** | **Beide** – Nano bevorzugt, API als Fallback | |

---

## 3. Technischer Stack

### Programmiersprache & UI
```
Sprache:        Kotlin 2.x
UI-Framework:   Jetpack Compose (Material3 + Material You)
Navigation:     Navigation Compose
Theming:        Dynamic Color (Material You) – passt sich ans Wallpaper an
Dark Mode:      Light / Dark / System (automatisch via isSystemInDarkTheme())
Splash Screen:  Android 12 SplashScreen API (nativ, kein Workaround)
```

### Datenbank & Persistenz
```
Lokale DB:      Room (SQLite) + Kotlin Coroutines
Preferences:    DataStore Preferences (ersetzt SharedPreferences)
Dateisystem:    MediaStore API für Exporte
```

### Dependency Injection
```
Framework:      Hilt (Dagger-basiert, Google-empfohlen für Android)
```

### Netzwerk & Cloud
```
HTTP-Client:    Retrofit + OkHttp
Google Drive:   Google Drive Android API v3
OneDrive:       Microsoft Graph SDK für Android
JSON:           Kotlinx Serialization / Gson
```

### KI & Sprache
```
Gemini Nano:    Google AI Edge SDK (On-Device)
Gemini API:     Google Generative AI SDK (generativeai)
Spracheingabe:  Android SpeechRecognizer API
```

### Sicherheit
```
Biometrie:      AndroidX BiometricPrompt
Verschlüsselung: EncryptedSharedPreferences / SQLCipher für Room
PIN:            Custom PIN-Screen mit Hashing (BCrypt)
```

### Benachrichtigungen
```
Framework:      WorkManager (zuverlässige Hintergrundaufgaben)
Notification:   NotificationManager + NotificationChannel
```

### Testing
```
Unit Tests:     JUnit5 + MockK + Turbine (Flow-Testing)
UI Tests:       Compose Testing / Espresso
Integration:    Hilt Testing
Coverage:       JaCoCo
```

### Build & Tooling
```
Build System:   Gradle (Kotlin DSL) + Version Catalogs (libs.versions.toml)
Code-Qualität:  ktlint + Detekt
CI (optional):  GitHub Actions
```

---

## 4. Architektur & Modulstruktur

### Architekturmuster: Clean Architecture + MVVM

```
┌─────────────────────────────────────────────┐
│                   UI Layer                   │
│  Compose Screens → ViewModels → UI State     │
├─────────────────────────────────────────────┤
│                Domain Layer                  │
│  Use Cases → Repository Interfaces → Models  │
├─────────────────────────────────────────────┤
│                 Data Layer                   │
│  Room DB │ DataStore │ Drive API │ Graph API │
└─────────────────────────────────────────────┘
```

**Datenfluss (unidirektional):**
```
User Action → ViewModel → Use Case → Repository → DataSource
                ↑                                      ↓
            UI State ←─────────── Flow/Result ─────────┘
```

### Projektstruktur (Android-Module)
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/[name]/mindjournal/
│   │   │   ├── core/               # Basis-Utils, Extensions, Constants
│   │   │   │   ├── di/             # Hilt Module Definitionen
│   │   │   │   ├── navigation/     # NavGraph, Routes
│   │   │   │   └── utils/          # Datum, Formatierung, etc.
│   │   │   │
│   │   │   ├── data/               # Data Layer
│   │   │   │   ├── local/          # Room: DAOs, Entities, Database
│   │   │   │   ├── remote/         # Drive, OneDrive, Gemini API
│   │   │   │   ├── preferences/    # DataStore
│   │   │   │   └── repository/     # Repository Implementierungen
│   │   │   │
│   │   │   ├── domain/             # Domain Layer (kein Android-Import!)
│   │   │   │   ├── model/          # Domain-Datenklassen
│   │   │   │   ├── repository/     # Repository Interfaces
│   │   │   │   └── usecase/        # Business Logic
│   │   │   │
│   │   │   └── presentation/       # UI Layer
│   │   │       ├── auth/           # PIN, Biometrie
│   │   │       ├── home/           # Dashboard, Übersicht
│   │   │       ├── entry/          # Eintrag erstellen/bearbeiten
│   │   │       ├── archive/        # Alle Einträge, Suche
│   │   │       ├── statistics/     # Statistiken, Rückblicke
│   │   │       ├── settings/       # Einstellungen
│   │   │       └── common/         # Wiederverwendbare UI-Komponenten
│   │   │
│   │   └── res/
│   │       ├── values/             # strings.xml (Deutsch), colors.xml, themes.xml
│   │       └── ...
│   │
│   └── test/                       # Unit Tests (spiegelt main/ Struktur)
│   └── androidTest/                # UI & Integration Tests
│
├── build.gradle.kts
└── proguard-rules.pro

gradle/
└── libs.versions.toml              # Zentrales Dependency-Management
```

### Datenmodell (Kern-Entitäten)

```kotlin
// Tagebucheintrag
data class JournalEntry(
    val id: Long,
    val date: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
    val moodScore: Int,              // 1–5
    val moodEmoji: String,           // Unicode-Emoji
    val freeText: String,
    val gratitudeItems: List<String>, // Dankbarkeits-Notizen (max. 3)
    val guidedAnswers: Map<String, String>, // Geführte Fragen → Antworten
    val tags: List<String>,
    val audioTranscript: String?,    // Spracheingabe-Transkript
    val aiReflection: String?,       // Gemini-generierte Reflexion
    val isSynced: Boolean
)

// Geführte Frage (konfigurierbar)
data class GuidedQuestion(
    val id: Int,
    val text: String,
    val isActive: Boolean,
    val sortOrder: Int
)

// Stimmungs-Skala
enum class MoodLevel(val score: Int, val emoji: String, val label: String) {
    SEHR_SCHLECHT(1, "😔", "Sehr schlecht"),
    SCHLECHT(2, "😕", "Schlecht"),
    NEUTRAL(3, "😐", "Neutral"),
    GUT(4, "🙂", "Gut"),
    SEHR_GUT(5, "😄", "Sehr gut")
}
```

---

## 5. Feature-Übersicht & Priorisierung

| # | Feature | Priorität | Phase |
|---|---------|-----------|-------|
| F01 | Tagebucheintrag: Freitext | 🔴 Muss | 2 |
| F02 | Tagebucheintrag: Stimmungs-Skala (1–5) | 🔴 Muss | 2 |
| F03 | Tagebucheintrag: Stimmungs-Emoji | 🔴 Muss | 2 |
| F04 | Tagebucheintrag: Dankbarkeits-Notizen | 🔴 Muss | 2 |
| F05 | Tagebucheintrag: Geführte Fragen | 🔴 Muss | 2 |
| F06 | Einträge speichern / lesen / löschen | 🔴 Muss | 2 |
| F07 | Archiv & Suchfunktion | 🔴 Muss | 3 |
| F08 | Erinnerungs-Benachrichtigungen | 🟠 Soll | 4 |
| F09 | Biometrie & PIN-Schutz | 🟠 Soll | 5 |
| F10 | Spracheingabe (Diktat) | 🟠 Soll | 6 |
| F11 | Gemini: KI-Reflexionsfragen | 🟡 Kann | 7a |
| F12 | Gemini: Wochenrückblick | 🟡 Kann | 7b |
| F13 | Google Drive Sync | 🟡 Kann | 8a |
| F14 | OneDrive Sync | 🟡 Kann | 8b |
| F15 | Statistiken & Stimmungsverlauf | 🟡 Kann | 9 |
| F16 | Dark Mode | 🟠 Soll | Quer (von Anfang an) |
| F17 | Einstellungsscreen | 🔴 Muss | 3 |
| F18 | APK-Build & Verteilung | 🔴 Muss | 10 |

---

## 6. Phasenplan mit Aufgaben & Akzeptanzkriterien

> Jede Phase ist vollständig abgeschlossen, bevor die nächste beginnt.  
> Jede Phase endet mit einem **lauffähigen, testbaren App-Stand**.

---

### Phase 0 – Projektsetup & Grundgerüst
**Ziel:** Saubere Projektbasis, die alle Phasen trägt.  
**Geschätzter Aufwand:** 1 Session

#### Aufgaben
- [ ] Android Studio: Neues Projekt anlegen (Kotlin + Compose + Empty Activity)
- [ ] `libs.versions.toml` aufsetzen (alle Abhängigkeiten zentral)
- [ ] Ordnerstruktur gemäß Architektur anlegen (Packages anlegen)
- [ ] Hilt Dependency Injection einrichten und prüfen
- [ ] Material3 Theme (Light + Dark) einrichten
- [ ] Basis-Navigation (NavController) einrichten
- [ ] GitHub-Repository anlegen & initialen Commit pushen
- [ ] `.gitignore` mit Android-Standardausschlüssen
- [ ] `local.properties` in `.gitignore` aufnehmen (API-Keys schützen!)

#### Akzeptanzkriterien
- ✅ App startet auf Emulator UND echtem Gerät (Pixel 10 Pro / S24)
- ✅ Navigation zwischen Placeholder-Screens funktioniert
- ✅ Hilt-Injection ohne Fehler
- ✅ Kein Lint-Fehler beim Build

---

### Phase 1 – Datenbank & Domain-Schicht
**Ziel:** Solides Datenfundament für alle Features.  
**Geschätzter Aufwand:** 1–2 Sessions

#### Aufgaben
- [ ] Room-Datenbank definieren (`MindJournalDatabase`)
- [ ] Entity `JournalEntryEntity` (vollständiges Schema)
- [ ] Entity `GuidedQuestionEntity` mit Standard-Fragen (vorbefüllt)
- [ ] DAO: `JournalEntryDao` (CRUD + Flows für reaktive Updates)
- [ ] DAO: `GuidedQuestionDao`
- [ ] Domain-Modelle (keine Room-Abhängigkeit)
- [ ] Repository-Interfaces (Domain-Layer)
- [ ] Repository-Implementierungen (Data-Layer)
- [ ] Hilt-Module für Datenbank und Repositories
- [ ] DataStore für App-Einstellungen einrichten

#### Standard-Geführte Fragen (vorbefüllt)
```
1. Wofür bin ich heute dankbar?
2. Was hat mir heute Energie gegeben?
3. Was hat mir heute Energie genommen?
4. Was möchte ich morgen besser machen?
5. Welcher Moment heute war besonders schön?
```

#### Akzeptanzkriterien
- ✅ Unit Tests für alle DAOs laufen durch (In-Memory-DB)
- ✅ Unit Tests für Repository-Implementierungen
- ✅ Flow-Emissions korrekt (Turbine Tests)
- ✅ Datenbankmigrationen-Struktur vorbereitet (auch wenn noch leer)

---

### Phase 2 – Eintrag erstellen & anzeigen (Kern-UI)
**Ziel:** Herzstück der App – vollständiger Tagebucheintrag.  
**Geschätzter Aufwand:** 2–3 Sessions

#### Aufgaben
- [ ] `EntryViewModel` mit `StateFlow<EntryUiState>`
- [ ] `CreateEntryScreen` (Composable)
  - [ ] Freitext-Eingabefeld (mehrzeilig, autoscrolling)
  - [ ] Stimmungs-Skala (Slider oder 5 Buttons)
  - [ ] Emoji-Auswahl (5 vordefinierte + Bestätigungsanzeige)
  - [ ] Dankbarkeit: 3 Textfelder mit Labels
  - [ ] Geführte Fragen: dynamisch aus DB geladen, scrollbar
  - [ ] Tags: Chip-Eingabe
  - [ ] Datum/Uhrzeit (auto, überschreibbar)
  - [ ] Speichern-Button + Validierung
- [ ] `EntryDetailScreen` (Composable, readonly)
- [ ] `EditEntryScreen` (Composable, edit mode)
- [ ] Swipe-to-delete mit Undo-Snackbar
- [ ] Home-Screen: Heute's Eintrag oder CTA zum Erstellen

#### Akzeptanzkriterien
- ✅ Eintrag erstellen, speichern, wieder aufrufen
- ✅ Alle Felder werden korrekt angezeigt und gespeichert
- ✅ Validierung: kein leerer Eintrag speicherbar
- ✅ Stimmungs-Emoji reagiert auf Slider-Position
- ✅ Keyboard-Handling korrekt (kein überlapptes UI)
- ✅ UI Tests für Eingabefelder

---

### Phase 3 – Archiv, Suche & Einstellungen
**Ziel:** Alle Einträge findbar machen; App konfigurierbar.  
**Geschätzter Aufwand:** 1–2 Sessions

#### Aufgaben
- [ ] `ArchiveScreen` mit LazyColumn (alle Einträge, neueste zuerst)
  - [ ] Stimmungs-Emoji als visuelle Vorschau
  - [ ] Datum, ersten 100 Zeichen Freitext
- [ ] Suchfunktion (Volltextsuche in Room via FTS4)
- [ ] Filter: nach Stimmung, nach Datum-Bereich, nach Tags
- [ ] Kalenderansicht (MonthView, Einträge als farbige Punkte)
- [ ] `SettingsScreen`
  - [ ] Benachrichtigungszeit (Vorbereitung Phase 4)
  - [ ] Geführte Fragen verwalten (aktivieren, deaktivieren, hinzufügen)
  - [ ] Sprache (derzeit fix Deutsch)
  - [ ] Theme (Light / Dark / System)
  - [ ] Cloud-Sync-Auswahl (Vorbereitung Phase 8)

#### Akzeptanzkriterien
- ✅ Alle Einträge im Archiv sichtbar und scrollbar
- ✅ Volltextsuche findet Treffer in <300ms
- ✅ Filter kombinierbar
- ✅ Settings werden in DataStore persistiert

---

### Phase 4 – Benachrichtigungen & Erinnerungen
**Ziel:** Nutzer wird täglich zum Schreiben eingeladen.  
**Geschätzter Aufwand:** 1 Session

#### Aufgaben
- [ ] WorkManager: `ReminderWorker` (tägliche Ausführung)
- [ ] Time-Picker im Settings-Screen
- [ ] Mehrere Erinnerungen pro Tag konfigurierbar (optional)
- [ ] NotificationChannel einrichten ("Tägliche Erinnerung")
- [ ] Deep Link aus Notification → direkt zur Eintrag-Erstellung
- [ ] Berechtigung anfordern (Android 13+: `POST_NOTIFICATIONS`)
- [ ] Erinnerung bei bereits vorhandenem Eintrag des Tages unterdrücken

#### Akzeptanzkriterien
- ✅ Benachrichtigung erscheint zur eingestellten Zeit
- ✅ Tap auf Notification öffnet Erstellungs-Screen
- ✅ Nach Eintrag: keine erneute Erinnerung am selben Tag
- ✅ Funktioniert nach Geräte-Neustart (WorkManager-Persistenz)

---

### Phase 5 – Biometrie & PIN-Schutz
**Ziel:** Tagebuch vor unberechtigtem Zugriff schützen.  
**Geschätzter Aufwand:** 1 Session

#### Aufgaben
- [ ] `LockScreen` Composable (Basis-UI für PIN-Eingabe)
- [ ] PIN setzen / ändern / deaktivieren (Settings)
- [ ] PIN-Hashing (SHA-256 + Salt, gespeichert in EncryptedSharedPreferences)
- [ ] BiometricPrompt Integration (Fingerabdruck / Face)
- [ ] App-Lock-Logik: Sperre nach X Sekunden im Hintergrund
- [ ] Graceful Fallback: Biometrie nicht verfügbar → PIN
- [ ] Krypto-Schlüssel an BiometricPrompt binden (optional, Phase 5b)

#### Akzeptanzkriterien
- ✅ Ohne korrekten PIN/Fingerabdruck: kein Zugriff auf Inhalte
- ✅ Biometrie funktioniert auf Pixel 10 Pro (In-Display-FP)
- ✅ Biometrie funktioniert auf Samsung S24 (Side-FP)
- ✅ Nach 5 Fehlversuchen: 30s Sperrzeit
- ✅ App-Neustart erfordert erneute Authentifizierung

---

### Phase 6 – Spracheingabe
**Ziel:** Einträge per Sprache diktieren.  
**Geschätzter Aufwand:** 1 Session

#### Aufgaben
- [ ] Mikrofon-Berechtigung anfordern
- [ ] `SpeechRecognizer` API Integration
- [ ] Mikrofon-Button im Eintrag-Screen
- [ ] Live-Transkription anzeigen (während Diktat)
- [ ] Text wird in aktives Eingabefeld eingefügt
- [ ] Sprache: Deutsch (`de-DE`)
- [ ] Fehlerbehandlung: Kein Internet, kein Mikrofon, etc.
- [ ] Optionale Satzzeichen via Sprachbefehle ("Punkt", "neue Zeile")

#### Akzeptanzkriterien
- ✅ Diktat fügt Text korrekt in Freitext-Feld ein
- ✅ Deutschsprachige Erkennung ausreichend genau
- ✅ Sinnvolle Fehlermeldung bei fehlendem Mikrofon-Zugriff

---

### Phase 7a – Gemini: KI-Reflexionsfragen
**Ziel:** KI schlägt personalisierte Reflexionsfragen vor.  
**Geschätzter Aufwand:** 1–2 Sessions

#### Aufgaben
- [ ] Gemini API SDK einbinden (generativeai)
- [ ] API-Key sicher konfigurieren (local.properties → BuildConfig)
- [ ] `GeminiRepository` Interface + Implementierung
- [ ] Prompt-Engineering: Kontextbasierte Reflexionsfragen
- [ ] "Inspiration"-Button im Eintrag-Screen
- [ ] Loading-State anzeigen (Shimmer / Skeleton)
- [ ] Gemini Nano (On-Device) bevorzugen, API als Fallback
- [ ] Opt-In: Nutzer aktiviert KI-Features bewusst in Settings
- [ ] Datenschutzhinweis vor erstem KI-Feature-Aufruf

#### Beispiel-Prompts an Gemini
```
System: Du bist ein einfühlsamer Achtsamkeitscoach auf Deutsch.
        Antworte immer auf Deutsch. Sei warm, kurz und konkret.

User: Der Nutzer hat folgende Stimmung: [MOOD].
      Freitext: "[FREE_TEXT]"
      Schlage 3 tiefgründige Reflexionsfragen vor, 
      die zum Nachdenken einladen.
```

#### Akzeptanzkriterien
- ✅ KI-Fragen erscheinen innerhalb von 3 Sekunden (API) / 1s (Nano)
- ✅ Fragen sind kontextbezogen und auf Deutsch
- ✅ Fehler (kein Internet, API-Limit) werden sauber abgefangen
- ✅ KI-Feature deaktivierbar in Settings

---

### Phase 7b – Gemini: Wochenrückblick
**Ziel:** KI fasst die Woche zusammen und erkennt Muster.  
**Geschätzter Aufwand:** 1 Session

#### Aufgaben
- [ ] Wochenrückblick-Screen (eigener Tab in Statistiken)
- [ ] 7 Einträge der letzten Woche als Kontext an Gemini übergeben
- [ ] Rückblick generieren: Stimmungsmuster, Highlights, Empfehlungen
- [ ] Text-Anzeige mit Markdown-Rendering (Compose Markdown Library)
- [ ] Rückblick lokal cachen (einmal pro Woche generieren)
- [ ] Teilen-Funktion (Text als Screenshot oder Copy)

#### Akzeptanzkriterien
- ✅ Rückblick erscheint nach Aufruf des Wochenrückblick-Screens
- ✅ Inhalt ist auf Deutsch, einfühlsam, keine medizinischen Ratschläge
- ✅ Cache verhindert API-Mehrfachaufrufe

---

### Phase 8a – Cloud-Sync: Google Drive
**Ziel:** Daten in Google Drive sichern und wiederherstellen.  
**Geschätzter Aufwand:** 2 Sessions

#### Aufgaben
- [ ] Google API Console: Projekt anlegen, Drive API aktivieren, OAuth einrichten
- [ ] Google Sign-In SDK integrieren
- [ ] Drive API: `appDataFolder` nutzen (privater App-Ordner, nicht sichtbar im Drive-UI)
- [ ] Export-Format: JSON-Backup-Datei (alle Einträge)
- [ ] Backup: manuell via Button in Settings
- [ ] Backup: automatisch (wöchentlich via WorkManager, wenn eingestellt)
- [ ] Restore: Backup aus Drive herunterladen und importieren
- [ ] Konfliktauflösung: "Neuere Daten gewinnen" Strategie
- [ ] Sync-Status anzeigen (zuletzt synchronisiert: Datum/Uhrzeit)

#### Akzeptanzkriterien
- ✅ Backup-Datei erscheint im Google Drive AppData-Ordner
- ✅ Restore stellt alle Einträge korrekt wieder her
- ✅ Sync funktioniert auf Pixel 10 Pro und S24
- ✅ Fehler bei fehlendem Internet: Retry-Mechanismus

---

### Phase 8b – Cloud-Sync: OneDrive
**Ziel:** Alternativ zu Google Drive.  
**Voraussetzung:** Microsoft-Account bestätigt (ADR-005)  
**Geschätzter Aufwand:** 2 Sessions

#### Aufgaben
- [ ] Azure App Registration (Microsoft Graph)
- [ ] MSAL (Microsoft Authentication Library) einbinden
- [ ] Graph API: Files erstellen/lesen in `/Apps/MindJournal/`
- [ ] Gleiches Backup-Format wie Google Drive (JSON)
- [ ] Settings: Umschalten Google Drive ↔ OneDrive
- [ ] Nur EIN Cloud-Anbieter aktiv gleichzeitig

#### Akzeptanzkriterien
- ✅ OneDrive-Backup funktioniert äquivalent zu Google Drive
- ✅ Wechsel zwischen Anbietern ohne Datenverlust

---

### Phase 9 – Statistiken & Rückblicke
**Ziel:** Stimmungsmuster visuell erkennbar machen.  
**Geschätzter Aufwand:** 1–2 Sessions

#### Aufgaben
- [ ] Stimmungsverlauf: Liniendiagramm (letzte 7/30/90 Tage)
- [ ] Häufigste Stimmungen: Balkendiagramm
- [ ] Streak-Anzeige (consecutive days with entries)
- [ ] Durchschnittsstimmung (Woche / Monat)
- [ ] Häufigste Tags: Tag-Cloud
- [ ] Eintrags-Häufigkeit: Wochenübersicht (GitHub-ähnlich)
- [ ] Charting Library: Vico oder MPAndroidChart

#### Akzeptanzkriterien
- ✅ Alle Charts rendern korrekt mit Beispieldaten
- ✅ Charts reagieren auf Zeitraum-Filter
- ✅ Keine Performance-Probleme bei 365 Einträgen

---

### Phase 10 – APK-Build, Polishing & Verteilung
**Ziel:** Fertige, verteilbare APK für Freunde.  
**Geschätzter Aufwand:** 1 Session

#### Aufgaben
- [ ] App-Icon erstellen (Adaptive Icon: Foreground + Background Layer)
- [ ] Splash Screen (Android 12+ SplashScreen API)
- [ ] Onboarding (3-seitige Intro-Slideshow beim ersten Start)
- [ ] Alle Texte auf Deutsch prüfen
- [ ] ProGuard/R8 Konfiguration für Release-Build
- [ ] Keystore erstellen und sicher aufbewahren
- [ ] Release-APK signieren (Build → Generate Signed APK)
- [ ] APK auf eigenen Geräten installieren und Abnahmetests
- [ ] APK teilen (WhatsApp, E-Mail, etc.) – Empfänger: "Unbekannte Quellen" erlauben

#### Akzeptanzkriterien
- ✅ Release-APK < 50 MB
- ✅ Alle Kernfeatures in der Release-APK funktionsfähig
- ✅ Freunde können APK installieren und App nutzen
- ✅ Kein Absturz in ersten 10 Minuten Nutzung (Smoke Test)

---

## 7. Test-Strategie

### Testpyramide

```
        /\
       /  \        E2E / Manuelle Tests (wenige, kritische Pfade)
      /    \
     /──────\      UI Tests - Compose Testing (Screen-Level)
    /        \
   /──────────\    Integration Tests - Hilt + Room (In-Memory)
  /            \
 /──────────────\  Unit Tests - Use Cases, ViewModels, Utils (viele, schnell)
/________________\
```

### Unit Tests (Pflicht ab Phase 1)
- **Was:** Alle Use Cases, ViewModels, Repository-Implementierungen, Utils
- **Framework:** JUnit5 + MockK + Kotlin Coroutines Test + Turbine
- **Ziel:** >80% Coverage für Domain + Data Layer
- **Wo:** `src/test/`

```kotlin
// Beispiel: ViewModel-Test
@Test
fun `when entry saved, archive list updates`() = runTest {
    val viewModel = EntryViewModel(fakeRepository)
    viewModel.saveEntry(testEntry)
    
    viewModel.entries.test {
        val items = awaitItem()
        assertThat(items).contains(testEntry)
    }
}
```

### Integration Tests (ab Phase 1)
- **Was:** Room DAO mit echter SQLite (In-Memory), Repository + DB
- **Framework:** Hilt Testing + Room Testing
- **Wo:** `src/androidTest/`

### UI Tests (ab Phase 2)
- **Was:** Kritische User Journeys (Eintrag erstellen, Auth-Flow)
- **Framework:** Compose UI Testing
- **Wo:** `src/androidTest/`

### Manuelle Tests (jede Phase)
- Smoke Test auf Pixel 10 Pro
- Smoke Test auf Samsung S24
- Edge Cases: Kein Internet, leere DB, erstes App-Start

### Checkliste vor jedem Phasen-Abschluss
```
[ ] Unit Tests: alle grün
[ ] Kein Lint-Fehler (./gradlew lint)
[ ] Kein ktlint-Fehler (./gradlew ktlintCheck)
[ ] Manuelle Tests auf beiden Geräten
[ ] Commit mit aussagekräftiger Message
[ ] Phase-Tag in Git gesetzt (z.B. v0.3.0-phase3)
```

---

## 8. Refactoring-Richtlinien

### Wann refactoren?
- Bevor eine neue Phase beginnt (technische Schulden abtragen)
- Wenn eine Klasse >300 Zeilen hat
- Wenn ein Composable >150 Zeilen hat
- Wenn doppelter Code an >2 Stellen auftaucht
- Wenn Tests schwer schreibbar sind (Zeichen für schlechtes Design)

### Refactoring-Prozess
1. **Tests zuerst** – Nur refactoren, was durch Tests abgesichert ist
2. **Kleine Schritte** – Ein Refactoring = ein Commit
3. **Grüne Tests** – Nach jedem Schritt alle Tests grün
4. **Kein Scope Creep** – Refactoring ≠ neue Features

### Code-Qualitäts-Regeln

#### Kotlin & Compose
```kotlin
// ✅ Gut: Kleine, fokussierte Composables
@Composable
fun MoodSelector(
    selectedMood: MoodLevel,
    onMoodSelected: (MoodLevel) -> Unit
) { // Implementierung hier }

// ❌ Schlecht: Alles in einem Screen-Composable
@Composable
fun CreateEntryScreen( /* Zu vermeidender monolithischer UI-Code */ )}
    // 500 Zeilen UI-Code

```

#### ViewModel
```kotlin
// ✅ Gut: Immutable UI State
data class EntryUiState(
    val isLoading: Boolean = false,
    val entry: JournalEntry? = null,
    val error: String? = null
)
// ViewModel hält StateFlow<EntryUiState>

// ❌ Schlecht: Mehrere separate LiveData/StateFlows für alles
```

#### Repository
```kotlin
// ✅ Gut: Interface im Domain-Layer
interface JournalRepository {
    fun getEntries(): Flow<List<JournalEntry>>
    suspend fun saveEntry(entry: JournalEntry): Result<Unit>
}
// Implementierung im Data-Layer, nie umgekehrt importiert
```

### Naming Conventions
| Typ | Convention | Beispiel |
|-----|-----------|---------|
| Composable | PascalCase, kein -Screen Suffix für Subcomponents | `MoodSelector`, `EntryCard` |
| Screen | PascalCase + Screen-Suffix | `CreateEntryScreen` |
| ViewModel | Feature + ViewModel | `EntryViewModel` |
| Use Case | Verb + Nomen + UseCase | `SaveEntryUseCase` |
| Repository | Nomen + Repository | `JournalRepository` |
| Entity (Room) | Nomen + Entity | `JournalEntryEntity` |

---

## 9. Git-Workflow & Versionierung

### Branch-Strategie
```
main          ← Nur stabile, getestete Stände (nach jeder Phase)
develop       ← Laufende Entwicklung
feature/xxx   ← Einzelne Features (z.B. feature/mood-selector)
fix/xxx       ← Bugfixes
refactor/xxx  ← Refactoring-Branches
```

### Commit-Konventionen (Conventional Commits)
```
feat: Stimmungs-Slider im Eintrag-Screen hinzugefügt
fix: Keyboard überdeckt Eingabefeld auf S24
refactor: EntryViewModel in kleinere Use Cases aufgeteilt
test: Unit Tests für SaveEntryUseCase ergänzt
docs: README mit Setup-Anleitung aktualisiert
chore: Abhängigkeiten in libs.versions.toml aktualisiert
```

### Versions-Tags
```
v0.1.0  → Phase 0 abgeschlossen (Projektsetup)
v0.2.0  → Phase 1 (Datenbank)
v0.3.0  → Phase 2 (Kern-UI)
...
v1.0.0  → Phase 10 (Release-APK)
```

### .gitignore (wichtigste Einträge)
```
local.properties          # API Keys – NIEMALS committen!
*.keystore                # Signierungsschlüssel – NIEMALS committen!
.gradle/
build/
*.apk
*.aab
```

---

## 10. API-Keys & Konfiguration

### Sicherheits-Strategie (Secrets Gradle Plugin)

API-Keys werden **niemals** direkt im Quellcode hinterlegt. Wir nutzen das *Secrets Gradle Plugin für Android*, das Keys aus der `local.properties` liest und zur Kompilierzeit eine sichere `BuildConfig`-Variable generiert.

### Implementierungsschritte

**1. Key-Hinterlegung**

Trage deinen Key in die `local.properties` im Wurzelverzeichnis ein. Diese Datei wird von Git ignoriert und verlässt niemals deinen Rechner.

```properties
# local.properties (niemals committen – ist bereits in .gitignore!)
GEMINI_API_KEY=dein_key_hier
GOOGLE_CLIENT_ID=dein_client_id
ONEDRIVE_CLIENT_ID=dein_client_id
```

**2. Plugin-Konfiguration (`libs.versions.toml`)**

Füge das Plugin zu deinen Abhängigkeiten hinzu:

```toml
[versions]
secretsPlugin = "2.0.1"

[plugins]
secrets = { id = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin", version.ref = "secretsPlugin" }
```

**3. Gradle-Einbindung**

- **Project `build.gradle.kts`:** `alias(libs.plugins.secrets) apply false` im `plugins`-Block ergänzen.
- **Module `app/build.gradle.kts`:** `alias(libs.plugins.secrets)` im `plugins`-Block aktivieren.

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.secrets)
}
```

**4. Typsicherer Zugriff im Code**

Nach dem Gradle-Sync generiert das Plugin automatisch ein Feld in der `BuildConfig`. Unterstriche aus `local.properties` werden beibehalten:

```kotlin
// Im Code: Über BuildConfig zugreifen (nie hardcoden!)
val apiKey = BuildConfig.GEMINI_API_KEY
val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
```

### Benötigte Keys & Accounts (nach Phase)

| Phase | Service | Was wird benötigt | Kosten |
|-------|---------|------------------|--------|
| 7a | Gemini API | Google AI Studio API-Key | Pro-Plan (Pay-as-you-go) |
| 8a | Google Drive | Google Cloud Console, OAuth 2.0 | Kostenlos |
| 8b | OneDrive | Azure Portal App Registration | Kostenlos |

### Schritt-für-Schritt wird beim jeweiligen Phasenbeginn erklärt.

---

## 11. Definition of Done (DoD)

Eine Phase gilt als **abgeschlossen**, wenn ALLE Punkte erfüllt sind:

```
Code:
[ ] Feature vollständig implementiert gemäß Akzeptanzkriterien
[ ] Kein TODOs / FIXMEs ohne Ticket
[ ] Code reviewed (selbst-review oder durch Claude)
[ ] ktlint: keine Warnungen
[ ] Lint: keine Fehler, Warnungen dokumentiert

Tests:
[ ] Unit Tests: alle grün
[ ] Neue Logik mit Unit Tests abgedeckt
[ ] Manuelle Tests auf Pixel 10 Pro durchgeführt
[ ] Manuelle Tests auf Samsung S24 durchgeführt

Git:
[ ] Aussagekräftige Commits (Conventional Commits)
[ ] Feature-Branch in develop gemergt
[ ] Versions-Tag in Git gesetzt (z.B. v0.x.0-phaseX)
[ ] PROJEKTPLAN.md ggf. mit Learnings aktualisiert
```

---

## 12. Bekannte Risiken & Mitigationen

| Risiko | Wahrscheinlichkeit | Impact | Mitigation |
|--------|-------------------|--------|------------|
| Gemini Nano API ändert sich (Beta) | Mittel | Mittel | Immer API-Fallback implementieren |
| OneDrive MSAL-Integration komplex | Hoch | Niedrig | Phase 8b nach Phase 8a, Zeitpuffer einplanen |
| Room-Migration bei Schemaänderung | Mittel | Hoch | Migration-Klassen von Phase 1 an vorbereiten |
| BiometricPrompt Unterschiede Pixel/Samsung | Mittel | Mittel | Beide Geräte in Phase 5 testen |
| API-Key versehentlich in Git committed | Niedrig | Sehr hoch | `.gitignore` ab Phase 0, Pre-Commit-Hook |
| Keystore-Verlust nach Phase 10 | Niedrig | Sehr hoch | Keystore an 2 sicheren Orten sichern (USB-Stick + Cloud) |
| WorkManager-Benachrichtigungen auf Samsung unterdrückt | Hoch | Mittel | Samsung-spezifische Battery-Optimierung deaktivieren (Hinweis in App) |

---

## 13. Glossar

| Begriff | Erklärung |
|---------|-----------|
| **ADR** | Architecture Decision Record – dokumentierte Architekturentscheidung |
| **API** | Application Programming Interface – Schnittstelle zu externen Diensten |
| **Compose** | Jetpack Compose – Modernes deklaratives UI-Framework für Android |
| **DAO** | Data Access Object – Datenbankzugriffs-Schicht in Room |
| **DataStore** | Moderner Ersatz für SharedPreferences (Google) |
| **DoD** | Definition of Done – Kriterien für abgeschlossene Arbeit |
| **Gemini Nano** | On-Device KI-Modell von Google (läuft ohne Internet auf dem Gerät) |
| **Hilt** | Dependency Injection Framework für Android (basiert auf Dagger) |
| **Material You** | Googles Design-System ab Android 12: passt Farben automatisch ans Wallpaper an |
| **MSAL** | Microsoft Authentication Library – für OneDrive-Authentifizierung |
| **MVVM** | Model-View-ViewModel – Architekturmuster für Android |
| **Room** | SQLite-Abstraktions-Library von Google für Android |
| **StateFlow** | Reaktiver Datenstrom in Kotlin Coroutines (für UI-State) |
| **Use Case** | Einzelne Business-Logik-Einheit im Domain-Layer |
| **WorkManager** | Android-Library für zuverlässige Hintergrundaufgaben |

---

## ✅ Alle ADRs bestätigt – Projekt startklar!

```
ADR-001  App-Name & Package     ✅  Interaktives Achtsamkeitstagebuch
                                     com.achtsamkeit.tagebuch
ADR-002  Android-Version        ✅  minSdk 31 (Android 12) – Material You aktiv
ADR-003  Dark Mode              ✅  Light / Dark / System (automatisch)
ADR-004  Versionskontrolle      ✅  GitHub (privat)
ADR-005  OneDrive               ✅  Phase 8b – Microsoft-Account vorhanden
ADR-006  Gemini                 ✅  Nano (lokal) + API (Cloud-Fallback)
```

**Nächster Schritt: Phase 0 – Projektsetup in Android Studio 🚀**

> Sage "Phase 0 starten" und du erhältst den vollständigen Code
> sowie eine Schritt-für-Schritt-Anleitung für das Projekt-Grundgerüst.