#!/usr/bin/env bash

set -e

function print_usage_and_exit {
  echo "Usage: ./goToExercise.sh <number>"
  exit 1
}

EX="$1"

if [ -z  "$EX" ]
then
  print_usage_and_exit
else
  git add .
  git commit -m "Some work"
  git checkout "ex${EX}"
fi
