#!/bin/bash

sm2 --start MONGO

sm2 --start THIRD_PARTY_ORCHESTRATOR THIRD_PARTY_DEVELOPER THIRD_PARTY_APPLICATION

./run_local.sh