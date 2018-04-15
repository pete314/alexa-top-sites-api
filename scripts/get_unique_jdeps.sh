#!/bin/bash
## Author: Peter Nagy <peternagy.ie>
## Since: 04, 2018
## Description: Get the java core dependacies for project

# Check if jar path was passed
if [ $# -ge 1 ]
then
   JARPATH=$1
else
   APP_PATH="$(dirname "$(dirname "$(readlink -fnv "$0")")")"
   JARPATH=$APP_PATH/target/*.jar
fi

# Get list of unique deps
jdeps --class-path '../target/libs/*' -recursive $JARPATH | awk '{print $4}' | grep "java" | sort -u | tr '\n' ',' | sed -e 's/,$//g'
