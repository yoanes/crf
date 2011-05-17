#!/bin/bash
# CRF wrapper for verify-gimages.sh so that we can specify the non-standard uiresources dir.

DELEGATE_SCRIPT=./src/scripts/image-generation/verify-gimages.sh
/bin/chmod u+x $DELEGATE_SCRIPT
$DELEGATE_SCRIPT -r showcase/web/uiresources
