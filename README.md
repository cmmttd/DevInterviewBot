# DevInterviewBot
[Telegram Bot](https://web.telegram.org/#/im?p=@DevInterviewBot)

[Content source](https://github.com/enhorse/java-interview/)

Some of the code looks like crap, sorry.
Feel free to rise PR or someday I'll redo it)

How to run:
```shell
$ git clone https://github.com/cmmttd/DevInterviewBot.git
$ export BOT_TOKEN_QUESTIONS=your_token
$ export BOT_NAME_QUESTIONS=your_bot_name
$ docker-compose up -d
```

How to build:
```shell
$ git clone https://github.com/cmmttd/DevInterviewBot.git
$ mvn install
$ docker build -t {your_app_name} .
$ export BOT_TOKEN=your_token
$ export BOT_NAME=your_bot_name
(change docker-compose image name from "cmmttd/dev-interview-bot:latest" to {your_app_name})
$ docker-compose up -d
```

Create your own 'Helloworld' bot:
```shell
$ git clone -b minimal-bot https://github.com/cmmttd/DevInterviewBot.git
$ export BOT_TOKEN=your_token
$ export BOT_USERNAME=your_name
$ gradle build
$ java -jar /build/libs/javabot-0.0.1.jar
```