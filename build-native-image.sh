#!/usr/bin/env bash

native_reslt="f5-probe"

docker build -t graal-build -f native-image.docker . && \
    (tar -c . | docker run -i --rm graal-build > "$native_reslt" && chmod +x "$native_reslt")
