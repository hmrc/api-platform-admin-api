#!/bin/bash

sm2 --start MONGO

sm2 --start THIRD_PARTY_ORCHESTRATOR THIRD_PARTY_DEVELOPER THIRD_PARTY_APPLICATION INTERNAL_AUTH --appendArgs '{"INTERNAL_AUTH": ["-Dapplication.router=testOnlyDoNotUseInAppConf.Routes"]}'

./run_local.sh
