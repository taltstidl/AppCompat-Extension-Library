[![Latest Release](https://img.shields.io/github/release/TR4Android/AppCompat-Extension-Library.svg?label=JitPack)](https://jitpack.io/#TR4Android/AppCompat-Extension-Library)

# AppCompat-Extension-Library
![Header Graphics for the AppCompat Extension Library](https://raw.githubusercontent.com/TR4Android/AppCompat-Extension-Library/master/promo-images/Header.png)

Google's AppCompat Design Library provides some awesome components for your development and design needs. For some applications though those just aren't enough. This library tries to fill the gap and provides additional common components building on the official AppCompat Design Library. If you have any additional features you'd like to see as part of this ongoing effort feel free to open a new issue.
##### Currently there are the following components:
* [AccountHeaderView](https://github.com/TR4Android/AppCompat-Extension-Library#accountheaderview)
* [FloatingActionMenu](https://github.com/TR4Android/AppCompat-Extension-Library#floatingactionmenu)
* [CircleImageView](https://github.com/TR4Android/AppCompat-Extension-Library#circleimageview)
* [TypefaceCompat](https://github.com/TR4Android/AppCompat-Extension-Library#typefacecompat)

There are wikis for every component that explain the setup in more depth, so be sure to check them out. Here's a link to the [Wiki Home Page](https://github.com/TR4Android/AppCompat-Extension-Library/wiki)

#### Importing the library
This library is available as a gradle dependency via [JitPack.io](https://github.com/jitpack/jitpack.io). Just add the following lines to your app module `build.gradle`:
``` gradle
repositories { 
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.TR4Android:AppCompat-Extension-Library:v0.1.2'
}
```
The latest Release is [Release 0.1.1](https://github.com/TR4Android/AppCompat-Extension-Library/releases/tag/v0.1.2). You can download a [sample.apk](https://github.com/TR4Android/AppCompat-Extension-Library/releases/download/v0.1.2/sample.apk) with this release.

## AccountHeaderView
The `AccountHeaderView` is a component that allows easy switching between accounts in the navigation drawer by clicking on the avatars on the header or by choosing from the dropdown list.

##### Main features:
* Works seamlessly with the AppCompat Design Library's `NavigationView` so you have full access to its features and can easily switch to an official account header implementation as soon as Google releases it.
* Includes a dropdown list of accounts (and optionally account addition and managment items) by hooking up to the internal `ListView` of the AppCompat Design Library's `NavigationView` in an almost magical way!
* Automatically creates placeholder avatars with the name's initials when an image is not set to an `Account` (see Google's Gmail).

##### Basic setup:
Use the following layout as the `app:headerLayout` of the AppCompat Design Library's `NavigationView`:
```xml
<com.tr4android.support.extension.widget.AccountHeaderView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:background="@drawable/account_header_cover_background"
    android:layout_width="match_parent"
    android:layout_height="@dimen/account_header_height" />
```
Then add your accounts to the `AccountHeaderView` and add a listener for account selections:
```java
accountHeaderView.addAccounts(new Account().setName("TR4Android").setEmail("tr4android@example.com").setIconResource(R.drawable.account_drawer_profile_image_tr4android), ...);
accountHeaderView.setAccountSelectedListener(new AccountHeaderView.OnAccountSelectedListener() {
    @Override
    public void onAccountSelected(Account account) {   }

    @Override
    public void onAccountAddSelected() {   }

    @Override
    public void onAccountManageSelected() {   }
});
```

*For the full documentation and customization options head over to the [AccountHeaderView wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/AccountHeaderView).*

## FloatingActionMenu
The `FloatingActionMenu` is a wrapper for multiple `FloatingActionButton`s that takes the first `FloatingActionButton` as the main button that stays on screen and flings out all other `FloatingActionButton`s in speed dial fashion.

##### Main features:
* Uses the AppCompat Design Library `FloatingActionButton` and thus has native elevation on Lollipop and above devices and even a shadow animation on pre-Lollipop devices!
* Allows easy configuration of you main `FloatingActionButton`'s icon animation: apply any rotation and optionally an alpha transition to a second icon (see Google's Inbox).
* Works on all devices back to API level 7 just like the AppCompat Design Library!

##### Basic setup
```xml
<com.tr4android.support.extension.widget.FloatingActionMenu
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="end|bottom" >
    
    <!-- Floating Action Buttons -->
    
</com.tr4android.support.extension.widget.FloatingActionMenu>
```

*For the full documentation and customization options head over to the [FloatingActionMenu wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/FloatingActionMenu).*

## CircleImageView
The `CircleImageView` is a supercharged `ImageView` that provides the ability to set circular images as well as placeholders. 

##### Main features:
* Creates circular images using the AppCompat Support Library's `RoundedBitmapDrawable` which provides the best performance possible by using Romain Guys techniques!
* Allows easy creation of placeholders with a colored circle and a letter (or letters) or an icon if an image should not available (see Google's Gmail email avatars).

##### Basic setup:
Instead of the default `ImageView` use the following in your layouts:
```xml
<com.tr4android.support.extension.widget.CircleImageView
    android:layout_width="40dp"
    android:layout_height="40dp" />
```
Then use `setImage...()` to set a circular image or `setPlaceholder()` to set a placeholder.

*For the full documentation and customization options head over to the [CircleImageView wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/CircleImageView).*

## TypefaceCompat
The `TypefaceCompat` is a utility for supporting the newest [Typography](https://www.google.com/design/spec/style/typography.html). It automatically sets the text size, color, line spacing *and typeface* for the styles specified in the guidelines.

##### Main features:
* Easily setup with one line of code in your `Activity`.
* Use one of the `TextAppearance.AppCompat.xxx` styles or use your own styles!
* Automatically sets textSize, textColor and lineSpacing for the `TextAppearance.AppCompat.xxx` styles and loads the new Roboto typeface on pre-Lollipop devices using a cache!

##### Basic setup:
In your `Activity` (for ease of use in your `BaseActivity`, if you have one) add the following line *before* `super.onCreate()`:
```java
public class SampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TypefaceCompatFactory.installViewFactory(this);
        super.onCreate(savedInstanceState);
        ...
    }
}
```
Then use one of the `TextAppearance.AppCompat.xxx` styles via `android:textAppearance="@style/TextAppearance.AppCompat.xxx"` on your `TextView`s.

*For the full documentation and customization options head over to the [TypefaceCompat wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/TypefaceCompat).*

## License

Copyright 2015 Thomas Robert Altstidl & fountaingeyser

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*For the licenses of the dependencies check out the [Licenses wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/Licenses).*
