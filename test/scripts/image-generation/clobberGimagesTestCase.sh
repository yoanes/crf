#!/bin/bash
#
# Test case for clobber-gimages.sh
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
findCmd="find"
teeCmd="tee"
rmCmd="rm"
wcCmd="wc"

# ==============================================================================
# Define default vars.
debug=1

existingRunInputData="test/scripts/image-generation/test-data/input/gimagesExistingRun"
baseWorkDir="build/work/image-generation-tests/gimagesTestCase"
testFailedFile="$baseWorkDir/testFailed.txt"
clobberGimagesScript="src/scripts/image-generation/clobber-gimages.sh"

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
    local workDir="$1"
    local testDataToCopyDir="$2"

    copyInputTestDataToWorkDir "$workDir" "$testDataToCopyDir"
}

function copyInputTestDataToWorkDir {
    local workDir="$1"
    local testDataToCopyDir="$2"

    $echoCmd "Copying $testDataToCopyDir/images to $workDir."
    $mkdirCmd -p "$workDir"
    $cpCmd -r "$testDataToCopyDir/images" "$workDir"
}

function invokeForcedClobber {
    local workDir="$1"

    $echoCmd "Running invokeForcedClobber  against work directory $workDir."
    $clobberGimagesScript -r "$workDir" -f
}

function failTestCase {
    local message="$1"
    $echoCmd "$message" |tee "$testFailedFile"
    exit 1
}

function countGeneratedFiles {
    local imagesResourcesDir="$1"

    local generatedImageDirRegex=".*w[0-9]+[/\\\\]h[0-9]+[/\\\\].*"
    local md5FileNamePattern="*.md5"
    
    $findCmd "$imagesResourcesDir" -regex "$generatedImageDirRegex" -o -name "$md5FileNamePattern" | $wcCmd -l
}

function assertNumGeneratedFilesBeforeClobber {
    local imagesResourcesDir="$1"
    
    local numGeneratedFiles=`countGeneratedFiles $imagesResourcesDir`
    if [ "$numGeneratedFiles" -eq "0" ]
    then
        $echoCmd ""
        failTestCase "TEST FAILED. '$imagesResourcesDir' does not contain generated files. Your test data is broken" 
    fi
}

function assertNumGeneratedFilesAfterClobber {
    local imagesResourcesDir="$1"
    
    local numGeneratedFiles=`countGeneratedFiles $imagesResourcesDir`
    if [ "$numGeneratedFiles" -ne "0" ]
    then
        $echoCmd ""
        failTestCase "TEST FAILED. '$imagesResourcesDir' should contain no generated files after clobber."
    fi
}



# ==============================================================================
# Define the tests.

function testClobberImages {
    $echoCmd "testClobberImages start"

    setup "$baseWorkDir/testClobberImages" "$existingRunInputData"

    assertNumGeneratedFilesBeforeClobber "$baseWorkDir/testClobberImages"
    invokeForcedClobber "$baseWorkDir/testClobberImages"
    assertNumGeneratedFilesAfterClobber "$baseWorkDir/testClobberImages"

    $echoCmd "testClobberImages PASSED"
}




# ==============================================================================
# Run the tests.

testClobberImages
