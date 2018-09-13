# Twin Cities Pride Android

**Twin Cities Pride Android** is a mobile application for the yearly [Twin Cities Pride](https://www.tcpride.org/) festival and parade. It is a native Android app written in Kotlin. An iOS version written in Swift is available
[here](https://github.com/softwareforgood/pride-ios). The app downloads data from a [Parse Server](https://github.com/softwareforgood/pride-festival-parse) instance. It is persisted locally, so it can be viewed without internet connectivity.

[![Get it on Google Play](media/google-play-badge.svg)](https://play.google.com/store/apps/details?id=com.softwareforgood.pridefestival.release&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1)

## Building

Debug builds should build by default once the project is imported into Android Studio. For release builds provide the necessary information in [signing.gradle.kts](gradle/signing.gradle.kts). Using the debug signing key is a valid option just for testing. To run all checks and tests simply run `./gradlew build cAT` with an Android device plugged in.

## Contributing

Software for Good welcomes open source contributions to the Twin Cities Pride Android app. You can pull down the app code and run it as is. The app is configured to connect to our Parse Server instances to pull down data, so you don't need to run your own server. If you would like to make a change, fork this repository and submit a pull request.

Everyone interacting in this repository is expected to follow the [code of conduct](code-of-conduct.md).

## Environment switching

To switch to staging environment on the production version of the app click [here](https://applinktest.appspot.com/app-link.html?url=pride%3A%2F%2Fenv%2Fstage&packageId=com.softwareforgood.pridefestival).

To switch back to production click [here](https://applinktest.appspot.com/app-link.html?url=pride%3A%2F%2Fenv%2Fprod&packageId=com.softwareforgood.pridefestival).
