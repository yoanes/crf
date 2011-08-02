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
# Define default vars.
echoCmd="echo"
catCmd="cat"
findCmd="D:/Software/Cygwin/bin/find.exe"
rmCmd="rm"
rmDirCmd="rmdir"
debug=1
lastRunPropertiesFile="gimages-last-run.properties"

defaultUiResourcesDir="src/web/uiresources"
uiResourcesDir="$defaultUiResourcesDir"

# ==============================================================================
# Functions.

function echoUsedVariables {
    $echoCmd "Using variables:"
    displayUsedVariable "uiResourcesDir" "$uiResourcesDir" "$defaultUiResourcesDir"
    $echoCmd 
}

function displayUsedVariable {
    local variableLabel="$1"
    local variableValue="$2"
    local defaultVariableValue="$3"
    if [ "$variableValue" = "$defaultVariableValue" ]
    then
        $echoCmd "    $variableLabel: '$variableValue' (default)"
    else 
        $echoCmd "    $variableLabel: '$variableValue'"
    fi
}

function usage {
    $echoCmd "Usage: $0 [-r <uiresources directory>]"
}

function clobberGeneratedImagesAndDirs {
    local generatedImageDirRegex=".*[/\\\\]w[0-9]+"
    local generatedImageFileRegex="${generatedImageDirRegex}[/\\\\]h[0-9]+[/\\\\].*"
    local imagesResourcesDir="$uiResourcesDir/images"

    local clobberingPerformed="false";

    $echoCmd "Images to be clobbered: "
    $findCmd $imagesResourcesDir -regex $generatedImageFileRegex -o -name "*.md5"|tee $$.tmp.foundImages
    local numFoundImages=`wc -l $$.tmp.foundImages`
    local numFoundImages=`echo $numFoundImages|cut -f 1 -d" "|tr -d "\r"`
    if [ "$numFoundImages" -gt 0 ]
    then
        $echoCmd ""
        read -p "Confirm delete? [y/n]" confirmation
        if [ "$confirmation" = "y" -o "$confirmation" = "Y" ]
        then
            $echoCmd ""
            $echoCmd "Clobbering images ..."
            $rmCmd `$catCmd $$.tmp.foundImages|tr -d " "`
            $echoCmd "Done"
            local clobberingPerformed="true";
        else
            $echoCmd ""
            $echoCmd "Aborting clobber at users request."
        fi
    else 
        $echoCmd "none"
    fi

    $echoCmd ""
    $echoCmd "Dirs to be clobbered: "
    $findCmd $imagesResourcesDir -regex "$generatedImageDirRegex" |tee $$.tmp.foundImageDirs
    $echoCmd ""
    read -p "Confirm delete? [y/n]" confirmation
    if [ "$confirmation" = "y" -o "$confirmation" = "Y" ]
    then
        $echoCmd ""
        $echoCmd "Clobbering dirs ..."
        $rmCmd -rf `$catCmd $$.tmp.foundImageDirs|tr -d " "`
        $echoCmd "Done"
        local clobberingPerformed="true";
    else
        $echoCmd ""
        $echoCmd "Aborting clobber at users request."
    fi

    if [ -e "$uiResourcesDir/$lastRunPropertiesFile" -a $clobberingPerformed = "true" ]
    then
        $echoCmd ""
        $echoCmd "Clobbering $uiResourcesDir/$lastRunPropertiesFile ..."
        $rmCmd $uiResourcesDir/$lastRunPropertiesFile
        $echoCmd "Done"
    fi
}

# ==============================================================================
# Processing

while getopts ":r:hd" option 
do  case "$option" in
        r) uiResourcesDir=$OPTARG
           ;;
        d) debug=0
           ;;
        h|*) usage
           exit 1
           ;;
    esac
done

echoUsedVariables
clobberGeneratedImagesAndDirs
