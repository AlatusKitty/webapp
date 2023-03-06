#!/bin/bash
set -e
cd /tmp
nohup java -jar demo-1.0-SNAPSHOT.jar > /dev/null 2>&1 &
exit 0