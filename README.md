# Ad Skipper

Ad Skipper is a lightweight helper for **YouTube**. When a skippable ad appears, it automatically presses the skip button as soon as it becomes available (typically after about five seconds), then continues running quietly in the background.

## Requirements

- Android 8.1 (API level 27) or newer
- Official YouTube app installed (`com.google.android.youtube`)
- User permission in system settings

## Installation

1. Download the latest APK from [GitHub Releases](https://github.com/anar-bastanov/youtube-ad-skipper/releases/latest), or build the app with Android Studio.

2. Install the APK on your device, and enable “Install unknown apps” if prompted.

3. Launch the app and choose "Open Settings".

4. In accessibility settings, locate Ad Skipper and turn it on.

5. Enjoy YouTube as usual. Skippable ads will be dismissed automatically.

## Troubleshooting

**If you encounter warnings or setup issues, read below.**

Ad Skipper is open source, so you can inspect the code yourself. The app runs entirely on your device and does not require internet access. It does not collect, store, or transmit any personal data. It does exactly what is described and nothing more.

> [!WARNING]
> **Google Play Protect alerts**
>
> Because Ad Skipper uses Android’s Accessibility Service to detect and press the Skip Ad button, Play Protect may show a warning such as “This app may be harmful”. This is a generic warning applied to any app with Accessibility permissions that can read screen contents.
>
> If Play Protect blocks installation, you can safely choose "Install anyway" once you have verified you downloaded it from the official [GitHub Releases](https://github.com/anar-bastanov/youtube-ad-skipper/releases/latest).

> [!WARNING]
> **Restricted Settings on Android 13+**
>
> On Android 13 and newer, Google blocks Accessibility for apps installed outside the Play Store. If you see **“App was denied access | Controlled by Restricted Setting”**, follow these steps:
>
> 1. Open `Settings > Apps > Ad Skipper`.
>
> 2. Tap the three-dot menu in the top-right corner.
>
> 3. Select "Allow restricted settings".
>
> 4. If you don’t see this option:
>
>    - Enable "Developer options": go to `Settings > About phone` and tap “Build number” seven times.
>
>    - Enter your PIN, then repeat the steps above.
>
>    - If the option still doesn’t appear, try opening Ad Skipper once, then check again.
>
>    - On some phones, you may need to reinstall the app after enabling Developer options.

After this one-time setup, Ad Skipper will work normally in the background.

## License

Copyright &copy; 2025 Anar Bastanov  
Distributed under the [MIT License](http://www.opensource.org/licenses/mit-license.php).
