# LINE Message Sender

LINE に Push Notification API を使用してメッセージを送信するシンプルなJavaコンソールアプリケーションです。

## 機能概要

あらかじめ登録したプッシュメッセージデータリストを元にLINE Messaging API を使用してLINEアプリにメッセージをプッシュします。

## 動作環境

* Java 8+
* MySQL 5.6+

## ビルド環境

* Java 8+
* Apache Maven 3.0+

## 主に使用しているライブラリ

* Spring Boot 2.x

## ビルド手順

Maven を使用してビルドします。

```bash
mvn package
```

## 実行手順
### データベースの作成

MySQL データベースを作成してください。
データベース名は任意ですがここでは `line_integration` という名称にします。

```sql
CREATE DATABASE line_integration;
```

### 環境変数の設定

アプリケーションを実行する場合は以下の環境変数を設定してください。

```bash
SPRING_DATASOURCE_URL="MySQLデータベース 接続文字列 (jdbc:mysql://localhost:3306/line_integration)"
SPRING_DATASOURCE_USERNAME="MySQL データベース ユーザ名"
SPRING_DATASOURCE_PASSWORD="MySQL データベース パスワード"
SPRING_FLYWAY_ENABLED="Flywayによるデータベースの自動マイグレーションを有効化するか (true|false)"
LINE_CHANNEL_ID="使用するLINEチャンネルID"
LINE_PUSH_RATE_PER_MINUTE="1分間あたりのPush回数の上限"
```

* `SPRING_FLYWAY_ENABLED` に true を指定した場合、プログラムの起動時に必要なテーブルが自動的に作成されます
    * もし手動でデータベーステーブルを作成する場合は `SPRING_FLYWAY_ENABLED` に `false` を指定して `src/main/resources/db/migration` 内の SQL ファイルをV1から順番に実行してください
* `LINE_PUSH_RATE_PER_MINUTE` に例えば 10 を指定した場合、10通のメッセージをプッシュした後に **1分間 - 10通のメッセージをプッシュするのにかかった時間** だけ、処理を一時停止します
* 環境変数ではなくファイルで設定したい場合はJavaアプリケーションの実行時引数に `--spring.config.location=<ファイルパス>` を指定してください
    * 設定ファイルのサンプルは `docs/examples/application.yml` です

### 初期データの登録

データベースに以下の初期データを登録してください。データのサンプルは `docs/examples` に入っています。

* チャンネル認証情報 (line_channel_credential.csv)
    * 認証情報IDの形式は任意です
    * チャンネルの情報は [LINE Developers](https://developers.line.me/) に登録したチャンネルのページから取得します
    * 一つのチャンネルIDに対して複数の認証情報を登録することができます。複数登録した場合、最後に登録したチャンネル認証情報を使用します

```csv
channel_credential_id,channel_id,channel_secret
<チャンネル認証情報ID>,<チャンネルID>,<チャンネルシークレット>
```

* メッセージテンプレート (line_message_template.csv)
    * メッセージテンプレートIDの形式は任意です

```csv
template_id,payload_type,payload
<メッセージテンプレートID>,<現在は text で固定>,<メッセージ本文>
```

* プッシュメッセージ (line_push_message.csv)
    * プッシュメッセージIDの形式は任意です
    * ターゲットIDは LINE Webhook で取得できるユーザID, グループID, ルームIDのいずれかを指定します
        * LINE アプリに表示される LINE ID ではないので注意してください
        * [line-echo-bot](https://github.com/tdc-yamada-ya/line-echo-bot) というシンプルなボットのサンプルを公開しているのでそちらを使用してIDを取得してください
    * タグには任意の文字列を指定してください
        * 実際に送信処理を行う際に、タグ文字列によるフィルタリングを行います
        * 例えば1日ごとに送信するメッセージを切り替えたい場合はタグに日付(YYYYMMDD)を指定して、送信を実行する際に日付をパラメータに含めます

```csv
push_message_id,channel_id,target_type,target,template_id,tag
<プッシュメッセージID>,<チャンネルID>,<現在は to で固定>,<ターゲットID>,<メッセージテンプレートID>,<タグ>
```

### 短期アクセストークンを取得する

プッシュ送信の前に短期アクセストークンを取得する必要があります。
LINEの短期アクセストークンの有効期限は30日です。
そのためこのコマンドは週に一度実行するなどのバッチスケジューリングを行ってください。

```bash
java -jar target/line-message-sender-1.0.0.jar refresh-token
```

短期アクセストークンは `line_channel_token` テーブルに新たに追加されます。
プッシュ送信では最新のトークンが使用されます。

### プッシュ送信を実行する

以下のコマンドを実行するとプッシュ送信が実行されます。

```bash
java -jar target/line-message-sender-1.0.0.jar send --tag=<タグ>
```

* 送信対象のメッセージはチャンネルID(`channel_id`)およびタグ(`tag`)がパラメータと一致する、かつ送信済み(`sent_at IS NULL`)あるいはエラー発生済み(`error_at IS NULL`)ではないものです
* 送信が完了すると `line_push_message` の `sent_at` に現在時刻が入ります
* エラーが発生すると `line_push_message` の `error_at` に現在時刻が入ります
    * LINE API の呼び出しは最大３回、バックオフディレイ 1000ms のリトライ処理が含まれています

### HTTPプロキシを有効にする

HTTPプロキシ経由でLINE APIにアクセスする必要がある場合は以下のパラメータをJavaアプリケーションの実行時引数に追加してください。

```bash
-Dhttps.proxyHost=<プロキシホスト名> -Dhttps.proxyPort=<プロキシポート>
```
