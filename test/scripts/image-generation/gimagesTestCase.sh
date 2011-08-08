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
grepCmd="grep"

# ==============================================================================
# Define default vars.
debug=1

inputTestData="test/scripts/image-generation/test-data/input/gimages"
existingRunInputData="test/scripts/image-generation/test-data/input/gimagesExistingRun"
baseExpectedOutputData="test/scripts/image-generation/test-data/expected-output"
baseWorkDir="build/work/image-generation-tests/gimagesTestCase"
testFailedFile="$baseWorkDir/testFailed.txt"
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

function invokeGimagesForAllImages {
    local workDir="$1"

    invokeGimagesForAllImagesWithPixelsIncrement "$workDir" 50
}

function invokeGimagesForAllImagesWithPixelsIncrement {
    local workDir="$1"
    local pixelsIncrement="$2"

    $echoCmd "Running invokeGimagesForAllImages against work directory $workDir using pixelsIncrement of $pixelsIncrement."
    $gimagesScript -r "$workDir" "-i$pixelsIncrement" -m5
}


function invokeGimagesForBasepath {
    local workDir="$1"
    local basePath="$2"

    $echoCmd "Running invokeGimagesForBasepath against work directory $workDir and using base image path of $basePath."
    $gimagesScript -r "$workDir" -i50 -m5 -p "$basePath"
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

function assertExpectedFilePaths {
    local expectedOutputDir="$1"
    local workDir="$2"

    $echoCmd
    $echoCmd "Comparing expected file paths in $expectedOutputDir to actual file paths in $workDir"

    local actualResourcePathsFile="$workDir/actualResources.txt"
    local expectedResourcePathsFile="$workDir/expectedResources.txt"

    # Find all workDir/expectedOutputDir relative paths so that we can compare them.
    (cd "$workDir/images" ; $findCmd . -type f -a '!' -path '*CVS*' -exec "ls" "-1" "{}" ";"|sort) > "$actualResourcePathsFile"
    (cd "$expectedOutputDir/images" ; $findCmd . -type f -a '!' -path '*CVS*' -exec "ls" "-1" "{}" ";"|sort) > "$expectedResourcePathsFile"

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
    local expectedOutputDir="$1"
    local workDir="$2"

    ###########################################
    # NOTE: we only do a light comparison of images using the ImageMagick identify command. This is because
    # images generated with the exact same ImageMagick convert command are not necessarily binary equal. Not
    # sure exactly why but I think ImageMagick attaches extra properties to the image like the creation date.
    # So this test isn't particularly good but it's better than nothing.
    ###########################################

    $echoCmd
    $echoCmd "Comparing expected images in $expectedOutputDir to actual images in $workDir using basic 'identify' command."

    local identifiedActualResourcesFile="$workDir/identifiedActualResources.txt"
    local identifiedExpectedResourcesFile="$workDir/identifiedExpectedResources.txt"

    # Apply identify command to every actual resource, strip out the fields we're interested in and capture the result.
    cat /dev/null > "$identifiedActualResourcesFile"
    for actualResource in `findAllImagesRelativeToDir "$workDir/images"`
    do
        (cd "$workDir/images" ; $identifyCmd $actualResource |cut -f 1,2,3,4,5,6 -d" ") >> "$identifiedActualResourcesFile"
    done

    # Apply identify command to every expected resource, strip out the fields we're interested in and capture the result.
    cat /dev/null > "$identifiedExpectedResourcesFile"
    for expectedResource in `findAllImagesRelativeToDir "$expectedOutputDir/images"`
    do
        (cd "$expectedOutputDir/images" ; $identifyCmd $expectedResource |cut -f 1,2,3,4,5,6 -d" ") >> "$identifiedExpectedResourcesFile"
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

function assertMd5ChecksumFiles {
    local expectedOutputDir="$1"
    local workDir="$2"

    $echoCmd
    $echoCmd "Comparing expected MD5 checksum files in $expectedOutputDir to actual MD5 checksum files in $workDir."

    local testFailed=1
    for actualMd5File in `( cd "$workDir" ; $findCmd . -name "*.md5" )`
    do
        if $diffCmd "$expectedOutputDir/$actualMd5File" "$workDir/$actualMd5File"
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
    local expectedOutputDir="$1"
    local workDir="$2"

    local expectedLastRunPropertiesFile="$expectedOutputDir/images/gimages-last-run.properties"
    local actualLastRunPropertiesFile="$workDir/images/gimages-last-run.properties"

    $echoCmd
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
# Define the tests.

function testGenerateAllImages {
    $echoCmd "testGenerateAllImages start"

    setup "$baseWorkDir/testGenerateAllImages" "$inputTestData"
    invokeGimagesForAllImages "$baseWorkDir/testGenerateAllImages"
    assertExpectedOutput "$baseExpectedOutputData/generateAllImages" "$baseWorkDir/testGenerateAllImages" 

    $echoCmd "testGenerateAllImages PASSED"
}

function testGenerateImagesFromBasepath {
    $echoCmd "testGenerateImagesFromBasepath start"

    setup "$baseWorkDir/testGenerateImagesFromBasepath" "$inputTestData"
    invokeGimagesForBasepath "$baseWorkDir/testGenerateImagesFromBasepath" "common/search"
    assertExpectedOutput "$baseExpectedOutputData/generateImagesFromBasepath" "$baseWorkDir/testGenerateImagesFromBasepath" 

    $echoCmd "testGenerateImagesFromBasepath PASSED"
}

function testGenerateAllImagesWhenImageScalingSkipped {
    $echoCmd "testGenerateAllImagesWhenImageScalingSkipped start"

    setup "$baseWorkDir/testGenerateAllImagesWhenImageScalingSkipped" "$existingRunInputData"
    
    # Create a dummy file to keep track of the current time, then sleep for a couple of seconds
    # to protect against small time differences.
    local timeControlFile="$baseWorkDir/testGenerateAllImagesWhenImageScalingSkipped/timeControlFile"
    $echoCmd "" > "$timeControlFile"
    sleep 2

    local logFile="$baseWorkDir/testGenerateAllImagesWhenImageScalingSkipped/testGenerateAllImagesWhenImageScalingSkipped.log"
    invokeGimagesForAllImages "$baseWorkDir/testGenerateAllImagesWhenImageScalingSkipped" |$teeCmd "$logFile"
    
    # Use the timeControlFile created above to detect if any new images were generated.
    local newlyGeneratedFiles=`$findCmd "$baseWorkDir/testGenerateAllImagesWhenImageScalingSkipped/images" -type f -newer "$timeControlFile" ! -name "gimages-last-run.properties"`
    if [ -n "$newlyGeneratedFiles" ]
    then
        failTestCase "TEST FAILED. Images generated when they shouldn't have been. Test output stored in $baseWorkDir/testGenerateAllImagesWhenImageScalingSkipped."
    fi

    assertLogContains "$logFile" "Skipping scaling of build/work/image-generation-tests/gimagesTestCase/testGenerateAllImagesWhenImageScalingSkipped/images/default/yellow-pages.png because it is consistent with the last run."
    assertLogContains "$logFile" "Skipping scaling of build/work/image-generation-tests/gimagesTestCase/testGenerateAllImagesWhenImageScalingSkipped/images/default/search.png because it is consistent with the last run."
    assertLogContains "$logFile" "Skipping scaling of build/work/image-generation-tests/gimagesTestCase/testGenerateAllImagesWhenImageScalingSkipped/images/S/selenium/common/search.gif because it is consistent with the last run."

    $echoCmd "testGenerateAllImagesWhenImageScalingSkipped PASSED"
}

function testGenerateAllImagesWhenChangedVariablesDetected {
    $echoCmd "testGenerateAllImagesWhenChangedVariablesDetected start"

    setup "$baseWorkDir/testGenerateAllImagesWhenChangedVariablesDetected" "$existingRunInputData"
    
    # Create a dummy file to keep track of the current time, then sleep for a couple of seconds
    # to protect against small time differences.
    local timeControlFile="$baseWorkDir/testGenerateAllImagesWhenChangedVariablesDetected/timeControlFile"
    $echoCmd "" > "$timeControlFile"
    sleep 2

    local logFile="$baseWorkDir/testGenerateAllImagesWhenChangedVariablesDetected/testGenerateAllImagesWhenChangedVariablesDetected.log"
    invokeGimagesForAllImagesWithPixelsIncrement "$baseWorkDir/testGenerateAllImagesWhenChangedVariablesDetected" 100 |$teeCmd "$logFile"
    
    # Use the timeControlFile created above to detect if any new images were generated.
    local newlyGeneratedFiles=`$findCmd "$baseWorkDir/testGenerateAllImagesWhenChangedVariablesDetected/images" -type f -newer "$timeControlFile" ! -name "gimages-last-run.properties"`
    if [ -n "$newlyGeneratedFiles" ]
    then
        failTestCase "TEST FAILED. Images generated when they shouldn't have been. Test output stored in $baseWorkDir/testGenerateAllImagesWhenChangedVariablesDetected."
    fi

    assertLogContains "$logFile" "Variables have changed since the last run."
    assertLogContains "$logFile" "Will delete all generated images to account for the new pixelsIncrement and minimumPixels values above ..."
    assertLogContains "$logFile" "Aborting at user request."

    $echoCmd "testGenerateAllImagesWhenChangedVariablesDetected PASSED"
}

function assertLogContains {
    local logFile="$1"
    local searchString="$2"

    if $grepCmd "$searchString" "$logFile" 2>&1 > /dev/null
    then
        :
    else
        failTestCase "TEST FAILED. Output does not contain: '$searchString'"
    fi
}


# ==============================================================================
# Run the tests.

testGenerateAllImages
testGenerateImagesFromBasepath
testGenerateAllImagesWhenImageScalingSkipped
testGenerateAllImagesWhenChangedVariablesDetected
