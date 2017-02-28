#!/bin/bash

IFS="
"
for f in $( ls -1 --file-type | grep -v "/" | grep -v "\.rar" | grep -v "\.sh" ); do
  NAME=$(basename "$f" | sed 's/\.[^.]*$//' )
  echo $NAME

  rar m -m5 "$NAME" "$f"
done
