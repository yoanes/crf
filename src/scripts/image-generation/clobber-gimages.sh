#!/bin/bash
#
# Content Rendering Framework script for clobbering all generated images.
#
# CRF Version: @CRFVERSION@
#
# $Revision$
#
# Note that this script was developed to run on multiple platforms, including Windows. As such, you will notice a 
# common idiom of '|tr -d "\r"' is used to strip out carriage return characters that Windows is so fond of.
#
#set -x

# ==============================================================================
# Signal traps.

trap "echo -e \"\n\nCleanup ...removing temporary files ... \" && rm -f $$.tmp*" EXIT

# ==============================================================================
# Source includes.

scriptDir=`dirname "$0"|tr -d "\r"`
source "$scriptDir/common.sh"

# ==============================================================================
# Default variables.

# Remember that 1 in shell is false. So forcing clobbering is disabled by default.
force=1

# ==============================================================================
# Functions.

function echoUsedVariables {
    $echoCmd "Using variables:"
    displayUsedVariable "uiResourcesDir" "$uiResourcesDir" "$defaultUiResourcesDir"
    displayUsedVariable "force" "$force" "1"
    $echoCmd 
}

function usage {
    $echoCmd "Usage: $0 [-r <uiresources directory>] [-f]"
}

# ==============================================================================
# Processing

while getopts ":r:hf" option 
do  case "$option" in
        r) uiResourcesDir=$OPTARG
           ;;
        f) force=0
           ;;
        h|*) usage
           exit 1
           ;;
    esac
done

echoUsedVariables
initDerivedVariables
clobberGeneratedImagesAndDirs "$force"
