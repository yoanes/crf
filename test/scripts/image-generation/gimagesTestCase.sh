#!/bin/bash
#
# Test case for gimages.sh
#
# $Revision$
#
# Note that this script was developed to run on multiple platforms, including Windows. As such, you will notice a 
# common idiom of '|tr -d "\r"' is used to strip out carriage return characters that Windows is so fond of.

#set -x

# ==============================================================================
# Define commands using variables. Allows us to easily prepend these all with "echo"
# during development/debugging.

echoCmd="echo"
cpCmd="cp"
mkdirCmd="mkdir"
diffCmd="diff"
findCmd="find"
teeCmd="tee"
rmCmd="rm"
identifyCmd="identify"

# ==============================================================================
# Define default vars.
debug=1

expectedOutputData="test/scripts/image-generation/test-data/expected-output"
inputTestData="test/scripts/image-generation/test-data/uiresources-input"
tempDir="$$.tmp.work"
testFailedFile="$tempDir/testFailed.txt"
gimagesScript="src/scripts/image-generation/gimages.sh"

# ==============================================================================
# Signal traps.

function removeTempFilesIfNecessary {
    if [ ! -e "$testFailedFile" ]
    then
        $echoCmd -e "\n\nTest cleanup ...removing temporary files ... " && $rmCmd -rf $$.tmp*
    else
        $echoCmd -e "TEST FAILED...not removing temporary files created by the test run ... "
    fi
}

trap "removeTempFilesIfNecessary" EXIT

# ==============================================================================
# Functions.

function setup {
    copyInputTestDataToTempDir
}

function copyInputTestDataToTempDir {
    $echoCmd "Making temporary directory $tempDir."
    $mkdirCmd "$tempDir"

    $echoCmd "Copying $inputTestData to $tempDir."
    $cpCmd -r "$inputTestData" "$tempDir"
}

function invokeGimages {
    $echoCmd "Running test against temporary directory $tempDir."
    $gimagesScript -r "$tempDir/uiresources-input" -i50 -m5
}

function assertExpectedOutput {
    assertExpectedFilePaths
    assertIdentifiedImages
    assertLastRunPropertiesFile

    $echoCmd
    $echoCmd "TEST PASSED"
}

function assertExpectedFilePaths {
    $echoCmd
    $echoCmd "Comparing expected file paths in $expectedOutputData to actual file paths in $tempDir"

    (cd "$tempDir/uiresources-input" ; $findCmd "images" -type f -exec "ls" "-l" "{}" ";"| tr -s " "|cut -f 9 -d " "|sort) > $$.tmp.actualResources
    (cd $expectedOutputData ; $findCmd "images" -type f -exec "ls" "-l" "{}" ";"| tr -s " "|cut -f 9 -d " "|sort) > $$.tmp.expectedResources

    if $diffCmd $$.tmp.expectedResources $$.tmp.actualResources
    then
        # Do nothing
        :
    else
        $echoCmd
        failTestCase "TEST FAILED. See diff output above. Test output stored in $tempDir."
    fi
}

function assertIdentifiedImages {
    $echoCmd
    $echoCmd "Comparing expected images in $expectedOutputData to actual images in $tempDir using basic 'identify' command."

    cat /dev/null > $$.tmp.identifiedActualResources
    for actualResource in `findAllImagesRelativeToDir $tempDir/uiresources-input`
    do
        (cd $tempDir/uiresources-input ; $identifyCmd $actualResource |cut -f 1,2,3,4,5,6,7 -d" ") >> $$.tmp.identifiedActualResources
    done

    cat /dev/null > $$.tmp.identifiedExpectedResources
    for expectedResource in `findAllImagesRelativeToDir $expectedOutputData`
    do
        (cd $expectedOutputData ; $identifyCmd $expectedResource |cut -f 1,2,3,4,5,6,7 -d" ") >> $$.tmp.identifiedExpectedResources
    done

    if $diffCmd $$.tmp.identifiedExpectedResources $$.tmp.identifiedActualResources
    then
        # Do nothing
        :
    else
        $echoCmd
        failTestCase "TEST FAILED. See diff output above. Test output stored in $tempDir."
    fi
}

function assertLastRunPropertiesFile {
    local expectedLastRunPropertiesFile="$expectedOutputData/images/gimages-last-run.properties"
    local actualLastRunPropertiesFile="$tempDir/uiresources-input/images/gimages-last-run.properties"

    $echoCmd "Comparing expected $expectedLastRunPropertiesFile to $actualLastRunPropertiesFile."

    if [ ! -e "$actualLastRunPropertiesFile" ]
    then
        $echoCmd
        failTestCase "TEST FAILED. '$actualLastRunPropertiesFile' not found." 
    fi

    # We have to ignore the date and uiResourcesDir fields because they have transient values. Not the most
    # rigid test but will do for now. 
    if $diffCmd --ignore-matching-lines="^date.*" --ignore-matching-lines="^uiResourcesDir.*" "$expectedLastRunPropertiesFile" "$actualLastRunPropertiesFile"
    then
        # Do nothing.
        :
    else 
        $echoCmd
        failTestCase "TEST FAILED. '$expectedLastRunPropertiesFile' is different to '$actualLastRunPropertiesFile' (ignoring the date and uiResourcesDir fields)."
    fi
}

function failTestCase {
    local message="$1"
    $echoCmd "$message" |tee "$testFailedFile"
    exit 1
}

function findAllImagesRelativeToDir {
    local imagesResourcesDir="$1"

    local generatedImageDirRegex=".*w[0-9]+[/\\\\]h[0-9]+[/\\\\].*"
    local dotNullImageNamePattern="*.null"
    local propertyFileNamePattern="*.properties"
    local md5FileNamePattern="*.md5"
    (cd $imagesResourcesDir; $findCmd . -type f -a '!' -path '*CVS*' -a '!' -regex "$generatedImageDirRegex" \
        -a '!' -name "$dotNullImageNamePattern" -a '!' -name "$propertyFileNamePattern" -a '!' -name "$md5FileNamePattern")
}

# ==============================================================================
# Run the test

setup
invokeGimages
assertExpectedOutput
