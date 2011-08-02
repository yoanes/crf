#!/bin/bash
# Simple script for ant build to call to fix shell script permissions. We use this weird indirection
# because passing shell glob patterns directly to Ant's exec task does not seem to work.

chmod u+x test/scripts/image-generation/*.sh 
chmod u+x src/scripts/image-generation/*.sh 
