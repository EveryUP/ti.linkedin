<h1 align="center">ti.linkedin</h1>

<div align="center">
  <strong>An unofficial LinkedIn module for Axway Appcelerator Titanium</strong>
</div>
<div align="center">
  <sub>
  	Made with :heart: at <a href="https://everyup.it">EveryUP SRL</a>
  </sub>
</div>
<br />

<div align="center">
  <a href="https://img.shields.io/badge/platform-android-green.svg">
  	<img src="https://img.shields.io/badge/platform-android-brightgreen.svg?style=flat-square">
  </a>
  <a href="https://img.shields.io/badge/platform-android-green.svg">
  	<img src="https://img.shields.io/badge/platform-ios-blue.svg?style=flat-square">
  </a>
  <a href="https://img.shields.io/badge/platform-android-green.svg">
  	<img src="https://img.shields.io/badge/available_on-gittio-red.svg?style=flat-square">
  </a>
  <a href="https://opensource.org/licenses/Apache-2.0">
  	<img src="https://img.shields.io/badge/license-apache_2-lightgrey.svg?style=flat-square">
  </a>
</div>

<div align="center">
  <h3>
    <a href="#features">Features</a>
    <span> | </span>
    <a href="#installation">Installation</a>
    <span> | </span>
    <a href="#usage">Usage</a>
    <span> | </span>
    <a href="#api">API</a>
    <span> | </span>
    <a href="#contribution">Contribution</a>
  </h3>
</div>

## Features

In order to use all of the <strong>ti.linkedin</strong> module features, the official LinkedIn app must be installed on the device.

This module:
- [x] Authenticates with your Linkedin account
- [x] Gets information about the logged user after login phase
- [ ] Opens a specific user profile page
- [ ] Supports a generic authenticated request

## Installation

A few steps must be done to properly configure the LinkedIn SDK for both Android and iOS platforms.<br />

First of all create an application. If you have an existing LinkedIn application, configure it on the <a href="https://www.linkedin.com/developer/apps">Developer website</a>.

If you've already done these steps jump to the <a href="#usage">Usage section</a>.

### iOS setup notes

Go to the "Mobile" setting page, and configure your application Bundle ID value in your LinkedIn application settings.

<img style="width: 100%; max-width: 500px; display: block; margin: 0 auto" src="https://content.linkedin.com/content/dam/developer/global/en_US/site/img/ios-bundle-ids.png">

Now, pick the <i>Application Id</i> and write the following code inside your <i>tiapp.xml</i> under the `<ios>` section:

	<key>LIAppId</key>
	<string>{Your LinkedIn Application ID}</string>

	<key>CFBundleURLTypes</key>
	<array>
	<dict>
		<key>CFBundleURLSchemes</key>
		<array>
			<string>li{Your LinkedIn Application ID}</string>
		</array>
	</dict>
	</array>

	<key>NSAppTransportSecurity</key>
	<dict>
	    <key>NSExceptionDomains</key>
	    <dict>
	        <key>linkedin.com</key>
	        <dict>
	            <key>NSExceptionAllowsInsecureHTTPLoads</key>
	            <true/>
	            <key>NSIncludesSubdomains</key>
	            <true/>
	            <key>NSExceptionRequiresForwardSecrecy</key>
	            <false/>
	        </dict>
	    </dict>
	</dict>

If you are targeting your application also for iOS 9 devices add this further code:

	<key>LSApplicationQueriesSchemes</key>
	<array>
	<string>linkedin</string>
	<string>linkedin-sdk2</string>
	<string>linkedin-sdk</string>
	</array>

### Android setup notes

Like for iOS, you have to do some setup steps to make this module working on Android platform.

First of all, generate a SHA1 hash for your certificate:

	keytool -exportcert -alias yourkeystorealias -keystore /your/keystore/path/yourapp.keystore | openssl sha1 -binary | openssl base64

In a development scenario, you can use a development keystore shipped with the Titanium SDK:

	keytool -exportcert -alias tidev -keystore ~/Library/Application\ Support/Titanium/mobilesdk/osx/7.1.0.GA/android/dev_keystore | openssl sha1 -binary | openssl base64

Go to the "Mobile" setting page and, under the Android section, add your application package and the SHA1 generated in the previous step.

<img style="width: 100%; max-width: 500px; display: block; margin: 0 auto" src="https://content.linkedin.com/content/dam/developer/global/en_US/site/img/package_hash_values.png">

### Titanium setup

Pick the latest version of the module and unzip it inside your application module folder or install it automatically via <a href="http://gitt.io/">gitt.io</a>

	gittio install ti.linkedin

Check if in your application's <i>tiapp.xml</i> the <i>ti.linkedin</i> module is declared as follows:

	<modules>
		<module platform="iphone">ti.linkein</module>
		<module platform="android">ti.linkedin</module>
	</modules>

and you're ready to use it.

## Usage

The module was developed to have a parity with the [ti.facebook](https://github.com/appcelerator-modules/ti.facebook) module developed by [Appcelerator](https://www.appcelerator.com/).

Before to start the login phase, call the `initialize` method. After that you can call the `authorize` one. You will be redirected to the LinkedIn official application.

When the user tap on the <i>Accept</i> button, your app will comes to foreground and the `login` event will be fired with the user data as payload.

Here is a short login example:

	var linkedin = require('ti.linkedin');

	linkedin.addEventListener('login', onLogin);
    linkedin.addEventListener('logout', onLogout);

	linkedin.authorize();

	function onLogin(event) {
	    Ti.API.info('User logged in?', event.success);
	    Ti.API.info(JSON.stringify(event));

	    if (!event.success) {
	        return;
	    }

		// Do other stuff with event.data
	}

	function onLogout(event) {
	    Ti.API.info('User logged out.');
	}

Do you want a more complete example? Look a the sample app.js.

## API

#### Methods

- `initialize` initialize the module
- `authorize` starts the authorization phase opening the official LinkedIn application and ask the user if it wants to allow your application to fetch its profile data. It will fire a `login` event
- `logout` clears the current LinkedIn session and will fire a `logout` event.

#### Events

- `login` an event that is fired after an authorization attempt with these sub-properties
	- `success` Indicates if the user was logged in successfully. Returns `true` if request succeeded, `false` otherwise
	- `error` Error message, if any returned. Will be undefined if success is `true`
	- `code` Error code will be undefined if `success` is `true`, a specific error code otherwise
	- `uid` User ID returned by LinkedIn if the login was successful
	- `data` Data returned by LinkedIn when we query for the current logged user profile after a successful login. Data is in JSON format, and includes information such as name, surname and profile picture.
- `logout` Fired at session logout.

#### Properties

- `loggedIn` Indicates if the user is logged in
- `permissions` Array of permissions to request for your app. Be sure the permissions you want are set before calling authorize.

#### Constants

Here are all permissions you can use to authorize your app.
For a complete description about the fields you can obtain using a specific permission have a look [here](https://developer.linkedin.com/docs/fields).

- `PERMISSION_BASIC_PROFILE` for name, photo, headline and current user position
- `PERMISSION_COMPLETE_PROFILE` for full user profile including experience, education, skills and recommendations
- `PERMISSION_COMPANY_PERMISSION` for managing user company page and post updates
- `PERMISSION_EMAIL_ADDRESSES` for user email address
- `PERMISSION_CONTACT_INFO` for user contact info
- `PERMISSION_SHARE` to post updates, make comments and like directly from your app

## Contribution

Creating code takes time. We love to share our work to you because we believe in open source. This does mean however that we also have to spend time working contracts to pay the bills. This is where you can help: by chipping in you can ensure more time is spent improving this and other modules rather than dealing with distractions.

So please <u>feel free</u> to make a PR!

We promise they will be reviewed in a short time period and merged asap.
