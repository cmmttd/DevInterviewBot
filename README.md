# DevInterviewBot
[Telegram Bot](https://web.telegram.org/#/im?p=@DevInterviewBot)

[Content source](https://github.com/enhorse/java-interview/)

Run local:
```shell
$ git clone https://github.com/cmmttd/DevInterviewBot.git
$ export BOT_TOKEN_QUESTIONS=your_token
$ export BOT_USERNAME_QUESTIONS=your_name
$ docker-compose up -d
```

Create your own 'Helloworld' bot:
```shell
$ git clone -b minimal-bot https://github.com/cmmttd/DevInterviewBot.git
$ export BOT_TOKEN=your_token
$ export BOT_USERNAME=your_name
gradle build
$ java -jar /build/libs/javabot-0.0.1.jar
```
