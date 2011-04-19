#!/bin/bash
#
# Content Rendering Framework script for verifying that all candidate images for generating 
# scaled images have been processed.
#
# CRF Version: @CRFVERSION@
#
# $Revision$
#
# Note that this script was developed to run on multiple platforms, including Windows. As such, you will notice a 
# common idiom of '|tr -d "\r"' is used to strip out carriage return characters that Windows is so fond of.

#set -x

# ==============================================================================
# Signal traps.

trap "echo -e \"\n\nCleanup ...removing temporary files ... \" && rm -f $$.tmp*" EXIT

# ==============================================================================
# Source includes.

scriptDir=`dirname "$0"`
source "$scriptDir/common.sh"

# ==============================================================================
# Define default vars.
debug=1
debugFile=$$.tmp.debug

defaultUiResourcesDir="src/web/uiresources"
uiResourcesDir="$defaultUiResourcesDir"

# ==============================================================================
# Functions.

function echoUsedVariables {
    $echoCmd "Using variables:"
    displayUsedVariable "uiResourcesDir" "$uiResourcesDir" "$defaultUiResourcesDir"
    $echoCmd 
}

function usage {
    $echoCmd "Usage: $0 [-r <uiresources directory>]"
}

function verifyImageMd5Sums {
    # Verification of md5 sums is manual. We use the openssl command for better portability between linux and Mac OS X. 
    # The latter lacks md5sum and the md5 command that it ships with is very basic.  
    local sourceImages=`findAllSourceImagesToScale`
    local tempMd5File="$$.tmp.md5"
    local verificationPassed="true"

    for currImage in $sourceImages
    do
        local currImageMd5File="${currImage}.md5"
        local uiResourcesDirRelativeImageMd5File="${currImageMd5File#$uiResourcesDir}"
        $echoCmd
        $echoCmd -n "Checking if $currImageMd5File exists ..."
        if [ -e "$currImageMd5File" ]
        then
            $echoCmd "OK"

            # Compute actual md5.
            computeMd5 "$currImage" > "$tempMd5File"

            $echoCmd "Comparing computed checksum to $currImageMd5File ..."
            if $diffCmd "$tempMd5File" "$currImageMd5File" 1>&2
            then
                $echoCmd "... OK"
                :
            else
                verificationPassed="false"
            fi
        
        else
            $echoCmd "NOT found" 1>&2
            verificationPassed="false"
        fi

    done

    if [ "$verificationPassed" = "true" ]
    then
        $echoCmd
        $echoCmd "MD5 verification PASSED"
    else
        $echoCmd
        # TODO: wrapper for this that knows the name of the project specific script so that we can report what the user should run.
        failVerification "MD5 verification FAILED. You must run your generate images script and then commit the generated images under $uiResourcesDir."
    fi
	
}

function failVerification {
    local message="$1"
    $echoCmd "$message" 1>& 2
    exit 1
}

# ==============================================================================
# Processing

while getopts ":r:i:m:p:ohd" option 
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
verifyImageMd5Sums

# ==============================================================================
