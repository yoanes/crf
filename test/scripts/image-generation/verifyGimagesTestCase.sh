#!/bin/bash
#
# Test case for verify-gimages.sh
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

inputTestData="test/scripts/image-generation/test-data/input"
baseExpectedOutputData="test/scripts/image-generation/test-data/expected-output"
baseWorkDir="build/work/image-generation-tests/verifyGimagesTestCase"
testFailedFile="$baseWorkDir/testFailed.txt"
verifyGimagesScript="src/scripts/image-generation/verify-gimages.sh"

# ==============================================================================
# Functions.

function setup {
    local workDir="$1"

    $echoCmd "Making directory $workDir."
    $mkdirCmd -p "$workDir"
}

function doTest {
    local inputDir="$1"
    local workDir="$2"
    local expectedExitStatus="$3"

    $echoCmd "Running invokeVerifyGimages against input directory $inputDir."
    $verifyGimagesScript -r "$inputDir" > "$workDir/test.log" 2>&1

    if [ $? -ne "$expectedExitStatus" ]
    then
        failTestCase "TEST FAILED. $verifyGimagesScript did not return a status code of $expectedExitStatus. See output in $workDir/test.log"
    fi
}

function failTestCase {
    local message="$1"
    $echoCmd "$message" |tee "$testFailedFile"
    exit 1
}

function assertExpectedOutput {
    local expectedOutputDir="$1"
    local workDir="$2"

    assertExpectedFilePaths "$expectedOutputDir" "$workDir" 
    assertIdentifiedImages "$expectedOutputDir" "$workDir" 
    assertMd5ChecksumFiles "$expectedOutputDir" "$workDir" 
    assertLastRunPropertiesFile "$expectedOutputDir" "$workDir" 

    $echoCmd
    $echoCmd "TEST PASSED"
}

# ==============================================================================
# Define the tests.

function testVerifyImagesMissingMd5File {
    $echoCmd
    $echoCmd "testVerifyImagesMissingMd5File start"

    setup "$baseWorkDir/testVerifyImagesMissingMd5File"
    doTest "$inputTestData/verifyImagesMissingMd5File" "$baseWorkDir/testVerifyImagesMissingMd5File" "1"

    $echoCmd "testVerifyImagesMissingMd5File PASSED"
}

function testVerifyImagesIncorrectMd5Sum {
    $echoCmd
    $echoCmd "testVerifyImagesIncorrectMd5Sum start"

    setup "$baseWorkDir/testVerifyImagesIncorrectMd5Sum"
    doTest "$inputTestData/verifyImagesIncorrectMd5Sum" "$baseWorkDir/testVerifyImagesIncorrectMd5Sum" "1"

    $echoCmd "testVerifyImagesIncorrectMd5Sum PASSED"
}

function testVerifyImagesMissingLastRunProperties {
    $echoCmd
    $echoCmd "testVerifyImagesMissingLastRunProperties start"

    setup "$baseWorkDir/testVerifyImagesMissingLastRunProperties"
    doTest "$inputTestData/verifyImagesMissingLastRunProperties" "$baseWorkDir/testVerifyImagesMissingLastRunProperties" "1"

    $echoCmd "testVerifyImagesMissingLastRunProperties PASSED"
}

function testVerifyImagesSuccessful {
    $echoCmd
    $echoCmd "testVerifyImagesSuccessful start"

    setup "$baseWorkDir/testVerifyImagesSuccessful"
    doTest "$baseExpectedOutputData/generateAllImages" "$baseWorkDir/testVerifyImagesSuccessful" "0"

    $echoCmd "testVerifyImagesSuccessful PASSED"
}

# ==============================================================================
# Run the tests.

testVerifyImagesMissingMd5File
testVerifyImagesIncorrectMd5Sum
testVerifyImagesMissingLastRunProperties
testVerifyImagesSuccessful
