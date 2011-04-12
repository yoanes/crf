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
workDir="build/work/image-generation-tests"
testFailedFile="$workDir/testFailed.txt"
gimagesScript="src/scripts/image-generation/gimages.sh"

# ==============================================================================
# Signal traps.

#function removeTempFilesIfNecessary {
#    if [ ! -e "$testFailedFile" ]
#    then
#        $echoCmd -e "\n\nTest cleanup ...removing temporary files ... " && $rmCmd -rf $$.tmp*
#    else
#        $echoCmd -e "TEST FAILED...not removing temporary files created by the test run ... "
#    fi
#}
#
#trap "removeTempFilesIfNecessary" EXIT

# ==============================================================================
# Functions.

function setup {
    copyInputTestDataToTempDir
}

function copyInputTestDataToTempDir {
#    $echoCmd "Making temporary directory $workDir."
#    $mkdirCmd "$workDir"

    $echoCmd "Copying $inputTestData to $workDir."
    $cpCmd -r "$inputTestData" "$workDir"
}

function invokeGimages {
    $echoCmd "Running test against work directory $workDir."
    $gimagesScript -r "$workDir/uiresources-input" -i50 -m5
}

function assertExpectedOutput {
    assertExpectedFilePaths
    assertIdentifiedImages
    assertMd5CheksumFiles
    assertLastRunPropertiesFile

    $echoCmd
    $echoCmd "TEST PASSED"
}

function assertExpectedFilePaths {
    $echoCmd
    $echoCmd "Comparing expected file paths in $expectedOutputData to actual file paths in $workDir"

    local actualResourcePathsFile="$workDir/actualResources.txt"
    local expectedResourcePathsFile="$workDir/expectedResources.txt"

    (cd "$workDir/uiresources-input" ; $findCmd "images" -type f -a '!' -path '*CVS*' -exec "ls" "-l" "{}" ";"| tr -s " "|cut -f 9 -d " "|sort) > "$actualResourcePathsFile"
    (cd $expectedOutputData ; $findCmd "images" -type f -a '!' -path '*CVS*' -exec "ls" "-l" "{}" ";"| tr -s " "|cut -f 9 -d " "|sort) > "$expectedResourcePathsFile"

    if $diffCmd "$expectedResourcePathsFile" "$actualResourcePathsFile"
    then
        # Do nothing
        :
    else
        $echoCmd
        failTestCase "TEST FAILED. See diff output above. Test output stored in $workDir."
    fi
}

function assertIdentifiedImages {
    ###########################################
    # NOTE: we only do a light comparison of images using the ImageMagick identify command. This is because
    # images generated with the exact same ImageMagick convert command are not necessarily binary equal. Not
    # sure exactly why but I think ImageMagick attaches extra properties to the image like the creation date.
    # So this test isn't particularly good but it's better than nothing.
    ###########################################

    $echoCmd
    $echoCmd "Comparing expected images in $expectedOutputData to actual images in $workDir using basic 'identify' command."

    local identifiedActualResourcesFile="$workDir/identifiedActualResources.txt"
    local identifiedExpectedResourcesFile="$workDir/identifiedExpectedResources.txt"

    cat /dev/null > "$identifiedActualResourcesFile"
    for actualResource in `findAllImagesRelativeToDir $workDir/uiresources-input`
    do
        (cd $workDir/uiresources-input ; $identifyCmd $actualResource |cut -f 1,2,3,4,5,6,7 -d" ") >> "$identifiedActualResourcesFile"
    done

    cat /dev/null > "$identifiedExpectedResourcesFile"
    for expectedResource in `findAllImagesRelativeToDir $expectedOutputData`
    do
        (cd $expectedOutputData ; $identifyCmd $expectedResource |cut -f 1,2,3,4,5,6,7 -d" ") >> "$identifiedExpectedResourcesFile"
    done

    if $diffCmd "$identifiedExpectedResourcesFile" "$identifiedActualResourcesFile"
    then
        # Do nothing
        :
    else
        $echoCmd
        failTestCase "TEST FAILED. See diff output above. Test output stored in $workDir."
    fi
}

function assertMd5CheksumFiles {
    $echoCmd
    $echoCmd "Comparing expected MD5 checksum files in $expectedOutputData to actual MD5 checksum files in $workDir."

    local testFailed=1
    for actualMd5File in `( cd "$workDir/uiresources-input" ; $findCmd . -name "*.md5" )`
    do
        if $diffCmd "$expectedOutputData/$actualMd5File" "$workDir/uiresources-input/$actualMd5File"
        then
            # Do nothing
            :
        else
            local testFailed=0
        fi
    done

    if [ "$testFailed" -eq 0 ]
    then
        $echoCmd 
        failTestCase "TEST FAILED. See diff output above. Test output stored in $workDir."
    fi
}

function assertLastRunPropertiesFile {
    local expectedLastRunPropertiesFile="$expectedOutputData/images/gimages-last-run.properties"
    local actualLastRunPropertiesFile="$workDir/uiresources-input/images/gimages-last-run.properties"

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
