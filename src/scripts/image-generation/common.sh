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
rmCmd="rm"
rmDirCmd="rmdir"
teeCmd="tee"
wcCmd="wc"
cutCmd="cut"
trCmd="tr"
grepCmd="grep"
mkdirCmd="mkdir"

# ==============================================================================
# Define default vars.

# Remember that 1 in shell is false. So debugging is disabled by default.
debug=1
debugFile=$$.tmp.debug
defaultUiResourcesDir="src/web/uiresources"
uiResourcesDir="$defaultUiResourcesDir"

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

function initDerivedVariables {
    lastRunPropertiesFile="$uiResourcesDir/images/gimages-last-run.properties"
}

function computeMd5 {
    local sourceFile="$1"
    $opensslCmd $sourceFile |cut -f 2 -d" " |tr -d "\r"
}

function findAllSourceImagesToScale {
    local imagesResourcesDir="$uiResourcesDir/images"
    
    if [ ! -e "$imagesResourcesDir" ]
    then
        $mkdirCmd -p "$imagesResourcesDir"
    fi

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
    # Use [0-9][0-9]* instead of [0-9]+ in the following pattern since the latter doesn't seem 
    # to work correctly under Mac OS X 10.x.
    local generatedImageDirRegex=".*[/\\\\]w[0-9][0-9]*[/\\\\]h[0-9][0-9]*[/\\\\].*"
    local dotNullImageNamePattern="*.null"
    local propertyFileNamePattern="*.properties"
    local md5FileNamePattern="*.md5"

    if [ -n "$propertyFiles" ]
    then
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
    else 
        echo ""
    fi
}

function clobberGeneratedImagesAndDirs {
    local forceClobber="$1"

    local generatedImageDirRegex=".*[/\\\\]w[0-9]+"
    local generatedImageFileRegex="${generatedImageDirRegex}[/\\\\]h[0-9]+[/\\\\].*"
    local imagesResourcesDir="$uiResourcesDir/images"

    clobberingPerformed=1

    $echoCmd "Images to be clobbered: "
    $findCmd $imagesResourcesDir -regex $generatedImageFileRegex -o -name "*.md5"|$teeCmd $$.tmp.foundImages
    local numFoundImages=`$wcCmd -l $$.tmp.foundImages`
    local numFoundImages=`$echoCmd $numFoundImages|$cutCmd -f 1 -d" "|$trCmd -d "\r"`
    if [ "$numFoundImages" -gt 0 ]
    then
        $echoCmd ""
        if [ ! "$forceClobber" ]
        then
            read -p "Confirm delete? [y/n]" confirmation
        fi
        if [ "$forceClobber" -o "$confirmation" = "y" -o "$confirmation" = "Y" ]
        then
            $echoCmd ""
            $echoCmd "Clobbering images ..."
            $rmCmd `$catCmd $$.tmp.foundImages|$trCmd -d " "`
            $echoCmd "Done"
            clobberingPerformed=0
        else
            $echoCmd ""
            $echoCmd "Aborting clobber at user request."
        fi
    else 
        $echoCmd "none"
    fi

    $echoCmd ""
    $echoCmd "Dirs to be clobbered: "
    $findCmd $imagesResourcesDir -regex "$generatedImageDirRegex" |$teeCmd $$.tmp.foundImageDirs
    $echoCmd ""
    if [ ! "$forceClobber" ]
    then
        read -p "Confirm delete? [y/n]" confirmation
    fi
    if [ "$forceClobber" -o "$confirmation" = "y" -o "$confirmation" = "Y" ]
    then
        $echoCmd ""
        $echoCmd "Clobbering dirs ..."
        $rmCmd -rf `$catCmd $$.tmp.foundImageDirs|$trCmd -d " "`
        $echoCmd "Done"
        clobberingPerformed=0
    else
        $echoCmd ""
        $echoCmd "Aborting clobber at user request."
    fi

    if [ -e "$lastRunPropertiesFile" ]
    then
        $echoCmd ""
        $echoCmd "Clobbering $lastRunPropertiesFile ..."
        $rmCmd "$lastRunPropertiesFile"
        $echoCmd "Done"
    fi
}

# ==============================================================================
