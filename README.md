#PlayAppInstaller

root権限無しでplay storeから任意のアプリをインストールできることを示す実証アプリです。

このアプリは下記の3つのパーミッションしか持っていませんが、play storeの任意のアプリをインストールさせることができますので、例えば、連絡先を読み取ってネットに送信するという機能を持ったアプリをplay storeからインストールさせることができます。そして、インストール後にすぐにそのアプリを起動させ、連絡先を盗み取るというようなことができてしまいます。

ただし、このPlayAppInstallerの手法によるインストールを防ぐ方法がありますので、是非下記の解説をお読みください。

#### お詫び

8/29深夜のツイートではplay storeからインストールした後、そのアプリは自ら自動的に起動することができるとツイートしましたが、Android 3.1以降?ではそのようなことはできなくなったようです。しかしながら、インストールしたアプリは、このアプリから起動させることができるので、実質、同等のことが可能です。

### ダウンロード

* [PlayAppInstaller.apk](https://github.com/downloads/virifi/PlayAppInstaller/PlayAppInstaller.apk) 


### パーミッション

* android.permission.INTERNET 
* android.permission.GET_ACCOUNTS 
* android.permission.USE_CREDENTIALS 

### スクリーンショット

![スクリーンショット](https://raw.github.com/virifi/PlayAppInstaller/master/readme_imgs/screenshot1.png)

### ライセンス

```
 Copyright (C) 2012 virifi 

 Licensed under the Apache License, Version 2.0 (the "License"); 
 you may not use this file except in compliance with the License. 
 You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software 
 distributed under the License is distributed on an "AS IS" BASIS, 
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 See the License for the specific language governing permissions and 
 limitations under the License. 
 ```

### 解説

#### このアプリの動作条件

このアプリはplay storeの任意のアプリをインストールすることができますが、インストール処理を実行するために必ず必要な条件があります。それは、インストール処理を開始する際、左のスクリーンショットのような画面が表示されるのですが、この画面の「アクセスの許可」をユーザーに押させる必要があります。ユーザーが「アクセスの許可」を押さない限り、インストール処理は実行できません。

#### アクセスを許可すると何が起きるのか

アクセスの許可を押すと、AccountManagerを通じてWeb版play storeにログインするために必要なアクセストークンが発行されます。このアクセストークンを用いると、ユーザーにGoogleのアカウントのユーザー名やパスワードを入力させずに、play storeにログインすることができます。

また、play storeにログイン後、GMailやGoogle Calendarのサイトを読み込むと引き続きログインした状態でサイトが表示されますので、メールや予定を盗み読んだりすることが可能です。

以下で解説しますが、実質的にこのアプリに対し任意のパーミッションを許すことにもなります。

#### どうやってアプリをインストールしているのか

Web版のplay storeの各アプリのページにはインストールボタンがあります。このインストールボタンを押すと、端末にリモートでアプリをインストールすることができます。また、Web版のplay storeはAndroidのWebViewで表示した場合も正常に動作します。

このアプリではWebViewを用いて、まずユーザーのアカウントでplay storeにログインし、インストールしたいアプリのページを開き、インストールボタンを押すという処理を自動化することによりインストール処理を実現しています。

#### 任意のアプリをインストールされてしまうことの危険性

Androidのアプリは連絡先の読み取りなど、センシティブな動作をする場合は、アプリにパーミッションを付与する必要があります。そして、ユーザーはアプリをインストールする際、そのアプリがどのようなパーミッションを要求するかを確認してインストールすることができます。

連絡先の読み取りのパーミッションを要求していなければ、そのアプリによって連絡先を読み取られる心配がないのです。

しかしながら、任意のアプリをインストールし起動させることができると、連絡先読み取りのパーミッションを持ったアプリをユーザーの同意なしに強制的にインストールさせ、自動的に起動させ、連絡先をネットに送信するというようなことができてしまうのです。

つまり本来では、INTERNET、GET_ACCOUNTS、USE_CREDENTIALSの3つのパーミッションしか要求してないから、連絡先を読み取られる心配が無いはずなのに、この手法を用いることにより、その安全性が破綻してしまっているのです。

上記3つのパーミッションと「アクセスの許可」をユーザーに押させることにより、このアプリは任意のパーミッションを付与されたアプリも同然なのです。

#### PlayAppInstallerの説明

このアプリを起動させインストールボタンを押すと、play storeから[AutoStartApp](https://play.google.com/store/apps/details?id=net.virifi.android.autostartapp)をインストールします。インストール後、AutoStartAppを自動的に起動させます。

AutoStartAppは現在のwifiの状態を表示するだけのアプリです。wifiの状態を取得するにはandroid.permission.ACCESS_WIFI_STATEがアプリに付与されている必要があります。もちろん、AutoStartAppはそれを持っています。

しかし、PlayAppInstallerはそのパーミッションを持っていません。したがって、本来であればwifiの状態を取得して画面に表示するということは実現できません。しかしながら、上で説明した手法を用いることにより、それが実現できてしまっています。

#### 対策

上記の画像のように、「weblogin」で始まりgoogleという文字列が含まれる権限を要求されたら拒否してください。このリクエストはアクセストークンを初めて取得する際に表示されますが、一度許可してしまうとそれ以降はこの画面を経由しないでアクセストークンを取得できます。したがって、アクセスの許可を押したときは何もしなくても、押した1か月後に不正な動作を行うことができるのです。

もし許可してしまった場合は、[https://accounts.google.com/b/0/IssuedAuthSubTokens](https://accounts.google.com/b/0/IssuedAuthSubTokens) このページでアクセス許可を取り消してください。

#### 注意

上記のようなアクセスの許可を要求するアプリが必ずしも不正な動作を行なっているわけでは無いはずです。しかし、そのようなアプリをインストールするということは、Androidが定義している全パーミッションを持ったアプリをインストールするということと同等と考えてインストールすべきです。

もし、全パーミッションを要求するようなアプリがあったらインストールするはず無いですよね。この手法を知らなければ、それと同等のことをいつのまにか行なってしまっていたかもしれません。

今回述べたことを是非頭にいれていただき、Androidをより安全に使って行きましょう。
