#!/usr/bin/env bash

rm -rf ./containers/app/fs_content && cp -Lr ./containers/app/{_fs_content,fs_content} && \
docker compose up --build -d --remove-orphans