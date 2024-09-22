#!/bin/bash

ENV_FILE="./.env"

# Обновление кода и деплой backend приложения
pushd ~/Documents/work-projects/FileSharingBot/ || exit

# Переходим на ветку develop
git checkout develop

# Обновляем ветку develop
git pull origin develop

# Останавливаем старые контейнеры микросервисов и запускаем новые с обновлённым кодом
docker compose -f docker-compose.yml --env-file $ENV_FILE down --timeout=60 --remove-orphans
docker compose -f docker-compose.yml --env-file $ENV_FILE up --build --detach

popd || exit