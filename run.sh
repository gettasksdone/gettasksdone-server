#!/bin/bash
docker network create getTasksDone_db_net;
docker compose -f docker-compose.dev.yml up --build;