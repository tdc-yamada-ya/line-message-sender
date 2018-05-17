# LINE Message Sender

LINE に Push Notification API を使用してメッセージを送信するシンプルなアプリケーションです。

## インストール

## 環境変数

```
SPRING_DATASOURCE_URL="MySQLデータベースの接続文字列 (jdbc:mysql://localhost:3306/line_integration)"
SPRING_DATASOURCE_USERNAME="MySQLデータベースのユーザ名"
SPRING_DATASOURCE_PASSWORD="MySQLデータベースのパスワード"
SPRING_FLYWAY_ENABLED="Flywayにデータベースの自動マイグレーションを有効化するか (true|false)"
LINE_CHANNEL_ID="LINE Channel ID"
LINE_PUSH_RATE_PER_MINUTE="1分間あたりのPush回数の上限"
```

## データ準備

