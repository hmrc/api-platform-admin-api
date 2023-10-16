#!/bin/bash

sbt "~run -Drun.mode=Dev -Dhttp.port=15505 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes $*"
