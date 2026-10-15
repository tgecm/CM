# Teleshop Admin Native (Android)

100% Pure Android Native E-Commerce Admin Panel built with **Kotlin**, **Jetpack Compose**, and **Material 3**.

## Key Features
- **Dashboard**: Real-time revenue analytics, pending order alerts, low-stock metrics, and quick actions.
- **Product Catalog**: Search, category filtering, stock management, availability toggles, and full CRUD dialogs.
- **Category Management**: Create and organize store categories with live item counts.
- **Order & Slip Verification**: Filter by order status, inspect order breakdowns, verify KBZPay/AYA Pay/WavePay slips, and advance order workflows.
- **Customer Management**: View Telegram subscriber profiles, order history totals, and block/unblock accounts.
- **Telegram Broadcast**: Dispatch announcements to active subscribers and review broadcast logs.
- **Store & Bot Settings**: Configure shop name, Telegram Bot Token, Admin Chat ID, API Base URL, delivery fees, maintenance mode, and payment gateways.

## Automated APK Build via GitHub Actions
Push this repository to GitHub. The workflow in `.github/workflows/build.yml` automatically compiles the Debug APK using JDK 17 and Gradle 8.7 and uploads it as a downloadable artifact (`Teleshop-Admin-Native-APK`).
