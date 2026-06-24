package com.openlog.model

import androidx.compose.ui.graphics.Color

private val SEQ1C = Color(0xFF8957e5)
private val SEQ2C = Color(0xFFf0883e)

val SEQ_DEFS_A = listOf(
    SequenceDef("def_activity", "onResume",           false, 1, SEQ1C),
    SequenceDef("def_network",  "Connection timeout", false, 2, SEQ2C),
)

val SEQ_DEFS_B = listOf(
    SequenceDef("def_pkg",      "scanning installed", false, 1, SEQ1C),
    SequenceDef("def_watchdog", "Watchdog:",          false, 2, SEQ2C),
)

val LOG_A = listOf(
    LogEntry(1,  "10:00:01.234", LogLevel.V, "ActivityLifecycle", "onCreate — LoginActivity"),
    LogEntry(2,  "10:00:01.456", LogLevel.D, "LoginActivity",     "initializing UI components"),
    LogEntry(3,  "10:00:01.789", LogLevel.I, "ActivityLifecycle", "onResume — LoginActivity"),
    LogEntry(4,  "10:00:02.123", LogLevel.D, "NetworkManager",    "checking connectivity"),
    LogEntry(5,  "10:00:02.456", LogLevel.I, "NetworkManager",    "connected — SSID: CoffeeShop_5G"),
    LogEntry(6,  "10:00:03.001", LogLevel.V, "InputDispatcher",   "dispatchTouchEvent ACTION_DOWN x=540 y=892"),
    LogEntry(7,  "10:00:03.002", LogLevel.D, "LoginActivity",     "onClick: button_login"),
    LogEntry(8,  "10:00:03.100", LogLevel.D, "AuthService",       "initiating token refresh — user=jsmith@acme.com"),
    LogEntry(9,  "10:00:03.200", LogLevel.D, "NetworkManager",    "POST /api/auth/refresh timeout=3000ms"),
    LogEntry(10, "10:00:06.201", LogLevel.E, "NetworkManager",    "Connection timeout after 3 retries (3000ms)"),
    LogEntry(11, "10:00:06.202", LogLevel.E, "AuthService",       "Token refresh failed — null response body"),
    LogEntry(12, "10:00:06.203", LogLevel.W, "LoginActivity",     "login attempt failed, showing error dialog"),
    LogEntry(13, "10:00:07.001", LogLevel.D, "GC",                "GC_CONCURRENT freed 2.1MB, 15% free"),
    LogEntry(14, "10:00:07.100", LogLevel.I, "ActivityLifecycle", "onResume — LoginActivity"),
    LogEntry(15, "10:00:07.200", LogLevel.D, "NetworkManager",    "retrying connection (attempt 1/3)"),
    LogEntry(16, "10:00:08.000", LogLevel.E, "NetworkManager",    "Connection timeout after 3 retries (3000ms)"),
    LogEntry(17, "10:00:08.001", LogLevel.V, "ActivityLifecycle", "onPause — LoginActivity"),
    LogEntry(18, "10:00:08.050", LogLevel.V, "ActivityLifecycle", "onStop — LoginActivity"),
    LogEntry(19, "10:00:08.100", LogLevel.D, "TokenStorage",      "clearing 2 expired tokens from keychain"),
    LogEntry(20, "10:00:09.000", LogLevel.I, "ActivityLifecycle", "onResume — LoginActivity"),
    LogEntry(21, "10:00:09.100", LogLevel.D, "NetworkManager",    "POST /api/auth/login timeout=5000ms"),
    LogEntry(22, "10:00:09.200", LogLevel.V, "InputDispatcher",   "dispatchTouchEvent ACTION_UP"),
    LogEntry(23, "10:00:10.001", LogLevel.W, "NetworkManager",    "slow response detected (2100ms > 2000ms threshold)"),
    LogEntry(24, "10:00:11.500", LogLevel.E, "NetworkManager",    "Connection timeout after 3 retries (5000ms)"),
    LogEntry(25, "10:00:11.501", LogLevel.E, "AuthService",       "Login failed — server unreachable"),
    LogEntry(26, "10:00:11.502", LogLevel.E, "LoginActivity",     "FATAL: login flow failed, user notified"),
    LogEntry(27, "10:00:11.600", LogLevel.I, "ActivityLifecycle", "onResume — LoginActivity"),
    LogEntry(28, "10:00:12.000", LogLevel.D, "Analytics",         "event=login_failure reason=network_timeout duration_ms=8300"),
    LogEntry(29, "10:00:12.100", LogLevel.W, "LoginActivity",     "showing fallback offline mode UI"),
    LogEntry(30, "10:00:12.500", LogLevel.V, "ActivityLifecycle", "onDestroy — LoginActivity"),
)

val LOG_B = listOf(
    LogEntry(1,  "09:00:00.001", LogLevel.I, "Zygote",              "Zygote started — fork server running"),
    LogEntry(2,  "09:00:00.123", LogLevel.I, "SystemServer",        "Starting Android Runtime Services"),
    LogEntry(3,  "09:00:00.234", LogLevel.D, "PackageManager",      "scanning installed packages (247)"),
    LogEntry(4,  "09:00:00.456", LogLevel.V, "PackageManager",      "package: com.google.android.gms v22.40.13"),
    LogEntry(5,  "09:00:00.789", LogLevel.W, "PackageManager",      "stale cache detected, rebuilding index…"),
    LogEntry(6,  "09:00:01.100", LogLevel.I, "ActivityManager",     "system_server ready, starting app layer"),
    LogEntry(7,  "09:00:01.234", LogLevel.D, "WindowManager",       "initializing display — 1080x2400 @ 120Hz"),
    LogEntry(8,  "09:00:01.456", LogLevel.I, "WindowManager",       "display ready"),
    LogEntry(9,  "09:00:01.789", LogLevel.D, "AudioService",        "initializing audio subsystem"),
    LogEntry(10, "09:00:02.001", LogLevel.I, "AudioService",        "audio subsystem ready"),
    LogEntry(11, "09:00:02.234", LogLevel.D, "BatteryService",      "battery level 87%, charging: false"),
    LogEntry(12, "09:00:02.456", LogLevel.W, "Watchdog",            "Watchdog: 3 threads blocking on barrier"),
    LogEntry(13, "09:00:02.789", LogLevel.E, "Watchdog",            "Watchdog: thread blocked 1000ms — ANR risk"),
    LogEntry(14, "09:00:03.001", LogLevel.I, "Watchdog",            "Watchdog: barrier released, continuing"),
    LogEntry(15, "09:00:03.234", LogLevel.D, "Launcher",            "launcher process starting"),
    LogEntry(16, "09:00:03.567", LogLevel.I, "Launcher",            "launcher ready — home screen displayed"),
    LogEntry(17, "09:00:03.789", LogLevel.V, "InputReader",         "input device registered: touchscreen-0"),
    LogEntry(18, "09:00:04.001", LogLevel.D, "NetworkStack",        "wifi interface up: wlan0"),
    LogEntry(19, "09:00:04.234", LogLevel.I, "NetworkStack",        "ip acquired: 192.168.1.102"),
    LogEntry(20, "09:00:04.456", LogLevel.D, "ConnectivityService", "network validated — internet reachable"),
)

fun mkRmap(data: List<LogEntry>): Map<Int, LogEntry> = data.associateBy { it.id }
