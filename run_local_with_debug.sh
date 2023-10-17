#!/bin/bash

sbt -jvm-debug 5005 "run -Dhttp.port=15502 -Dmicroservice.services.api-platform-events.enabled=false$*"
