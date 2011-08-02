#!/bin/bash
#
# Content Rendering Framework script containing common code for generating scaled images.
#
# CRF Version: @CRFVERSION@
#
# $Revision$
#
# Note that this script was developed to run on multiple platforms, including Windows. As such, you will notice a 
# common idiom of '|tr -d "\r"' is used to strip out carriage return characters that Windows is so fond of.

#set -x

## ==============================================================================
## Define commands using variables. Allows us to easily replace these all with "echo"
## during development/debugging.

echoCmd="echo"
catCmd="cat"
findCmd="find"
exprCmd="expr"
identifyCmd="identify"
convertCmd="convert" 
opensslCmd="openssl dgst -md5 -hex"
diffCmd="diff"

# ==============================================================================
# Functions.

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

function computeMd5 {
    local sourceFile="$1"
    $opensslCmd $sourceFile |cut -f 2 -d" " |tr -d "\r"
}

function findAllSourceImagesToScale {
    # Find all properties files first. Only an image that has a matching properties files in any group are
    # candidates for scaling.
    local propertyFiles=`$findCmd $uiResourcesDir -name "*.properties"`
    if [ "$debug" -eq 0 ]
    then
        $echoCmd >> "$debugFile"
        $echoCmd "All property files found: $propertyFiles" >> "$debugFile"
    fi

    findSourceImagesForPropertyFiles "$propertyFiles"
}

function findSourceImagesToScaleFromBasePath {
    local basePath="$1"

    # Find properties files matching the passed in basePath first. Only an image that has a matching properties files in any group are
    # candidates for scaling.
    local propertyFiles=`$findCmd $uiResourcesDir -regex ".*${basePath}.properties$"`
    if [ "$debug" -eq 0 ]
    then
        $echoCmd >> "$debugFile"
        $echoCmd "Property files found with basePath of ${basePath}: $propertyFiles" >> "$debugFile"
    fi

    if [ -z "$propertyFiles" ]
    then
        $echoCmd 1>&2
        $echoCmd "ERROR: No ${basePath}.properties files found in $uiResourcesDir. Please create these." 1>&2
        $echoCmd 1>&2
        exit 1
    fi

    findSourceImagesForPropertyFiles "$propertyFiles"
}

function findSourceImagesForPropertyFiles {
    local propertyFiles="$1"
    local imagesResourcesDir="$uiResourcesDir/images"
    local generatedImageDirRegex=".*[/\\\\]w[0-9]+[/\\\\]h[0-9]+[/\\\\].*"
    local dotNullImageNamePattern="*.null"
    local propertyFileNamePattern="*.properties"
    local md5FileNamePattern="*.md5"

    local i=1;
    for propFile in $propertyFiles
    do
        local groupRelativePropertyFile=${propFile#$imagesResourcesDir/*/}
        if [ $i -eq 1 ] 
        then
            echo $groupRelativePropertyFile > $$.tmp.propFiles
        else 
            echo $groupRelativePropertyFile >> $$.tmp.propFiles
        fi
        local i=$((i + 1))
    done

    # Now find all images that correspond to the properties files found.
    local findOptions="$imagesResourcesDir -type f -a '!' -path '*CVS*' -a '!' -regex \"$generatedImageDirRegex\" \
            -a '!' -name \"$dotNullImageNamePattern\" -a '!' -name \"$propertyFileNamePattern\" -a '!' -name \"$md5FileNamePattern\" -a \\("
    local i=1;
    for propFile in `sort $$.tmp.propFiles |uniq`
    do
        if [ "$debug" -eq 0 ]
        then
            $echoCmd >> "$debugFile"
            $echoCmd "propertyFile: $propFile" >> "$debugFile"
        fi
        local propertyFileBasename=`basename $propFile |tr -d "\r"`
        local imageFileStem=${propertyFileBasename%.properties}
        if [ $i -eq 1 ] 
        then
            local findOptions="${findOptions} -name \"${imageFileStem}.*\" "
        else
            local findOptions="${findOptions} -o -name \"${imageFileStem}.*\" "
        fi
        local i=$((i + 1))
    done
    local findOptions="${findOptions} \\)"

    eval $findCmd $findOptions
}

# ==============================================================================
