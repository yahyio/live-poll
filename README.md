# Pulse — Live Poll

Realtime polling board built with Spring Boot and WebSocket/STOMP. Create polls, vote, and watch results update live across multiple browser windows.

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy)

## Features

- Create polls (1–200 char question, 2–6 options)
- Realtime vote broadcast via STOMP/SockJS
- Animated results UI
- REST API + WebSocket

## Tech Stack

Java · Spring Boot · WebSocket · STOMP · SockJS · Gradle

## Run Locally

```bash
./gradlew bootRun
# open http://localhost:8080
```

Open in two windows to see live updates.
