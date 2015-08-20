[![Latest Release](https://img.shields.io/github/release/TR4Android/AppCompat-Extension-Library.svg?label=JitPack)](https://jitpack.io/#TR4Android/AppCompat-Extension-Library)

# AppCompat-Extension-Library
The AppCompat Design Libray provides some awesome components for your development and design needs. For some applications though those just aren't enough. This library tries to fill the gap and provides additional common components building on the official AppCompat Design Library. If you have any additional features you'd like to see as part of this ongoing effort feel free to open a new issue.
##### Currently there are the following components:
* [AccountHeaderView](https://github.com/TR4Android/AppCompat-Extension-Library#accountheaderview)
* [FloatingActionMenu](https://github.com/TR4Android/AppCompat-Extension-Library#floatingactionmenu)
* [CircleImageView](https://github.com/TR4Android/AppCompat-Extension-Library#circleimageview)

There are wikis for every component that explain the setup in more depth, so be sure to check them out. Here's a link to the [Wiki Home Page](https://github.com/TR4Android/AppCompat-Extension-Library/wiki)

#### Importing the library
This library is available as a gradle dependency via [JitPack.io](https://github.com/jitpack/jitpack.io). Just add the following lines to your app module `build.gradle`:
``` gradle
repositories { 
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.TR4Android:AppCompat-Extension-Library:v0.1.0'
}
```
The latest Release is [Release 0.1.0 (Initial Release)](https://github.com/TR4Android/AppCompat-Extension-Library/releases/tag/v0.1.0). You can download a [sample.apk](https://github.com/TR4Android/AppCompat-Extension-Library/releases/download/v0.1.0/sample.apk) with this release.

## AccountHeaderView
The `AccountHeaderView` provides an account header layout that can be easily used with the design library's `NavigationView`.
Just use the following layout as your `app:headerLayout`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.tr4android.support.extension.widget.AccountHeaderView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/account_header"
    android:background="@drawable/account_drawer_cover_background"
    android:layout_width="match_parent"
    android:layout_height="@dimen/account_header_height" />
```
Then add your accounts to the `AccountHeaderView` and add a listener for account selections. That's all there is to it.
```java
AccountHeaderView accountHeaderView = (AccountHeaderView) findViewById(R.id.account_header);
accountHeaderView.addAccounts(new Account().setName("TR4Android").setEmail("tr4android@example.com").setIconResource(R.drawable.account_drawer_profile_image_tr4android), ...);
accountHeaderView.setAccountSelectedListener(new AccountHeaderView.OnAccountSelectedListener() {
    @Override
    public void onAccountSelected(Account account) {
        // Called when an account is selected
    }

    @Override
    public void onAccountAddSelected() {
        // Called when the "Add account" item is clicked
    }

    @Override
    public void onAccountManageSelected() {
        // Called when the "Manage accounts" item is clicked
    }
});
```

For additional information and customization options check out the [AccountHeaderView wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/AccountHeaderView).

## FloatingActionMenu
The `FloatingActionMenu` is a wrapper for multiple `FloatingActionButton`s that takes the first `FloatingActionButton` as the main button that stays on screen and flings out all other `FloatingActionButton`s. A simple layout would look like this:
```xml
<com.tr4android.support.extension.widget.FloatingActionMenu
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="end|bottom" >
    <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="@dimen/fab_margin"
        android:src="@drawable/ic_add_white_24dp" />

    <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_insert_drive_file_white_24dp" />

    <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_grid_on_white_24dp" />

    <android.support.design.widget.FloatingActionButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_insert_chart_white_24dp" />
</com.tr4android.support.extension.widget.FloatingActionMenu>
```

For additional information and customization options check out the [FloatingActionMenu wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/FloatingActionMenu).

## CircleImageView
The `CircleImageView` is a by-product of the `AccountHeaderView`. It provides the ability to set circular images as well as placeholders. Instead of the default `ImageView` use the following in your layouts:
```xml
<com.tr4android.support.extension.widget.CircleImageView
    android:layout_width="40dp"
    android:layout_height="40dp" />
```
Then use `setCircleImage...()` to set a circular image or `setPlaceholder()` to set a placeholder.

For additional information and customization options check out the [CircleImageView wiki](https://github.com/TR4Android/AppCompat-Extension-Library/wiki/CircleImageView).

## License

Copyright 2015 Thomas Robert Altstidl

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

**The library uses the AppCompat Design Library with the following license:**

Copyright 2015 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
