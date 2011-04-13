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

inputTestData="test/scripts/image-generation/test-data/uiresources-input"
baseExpectedOutputData="test/scripts/image-generation/test-data/expected-output"
baseWorkDir="build/work/image-generation-tests"
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

    copyInputTestDataToWorkDir "$workDir"
}

function copyInputTestDataToWorkDir {
    local workDir="$1"

    $echoCmd "Copying $inputTestData/images to $workDir."
    $mkdirCmd -p "$workDir"
    $cpCmd -r "$inputTestData/images" "$workDir"
}

function invokeGimagesForAllImages {
    local workDir="$1"

    $echoCmd "Running invokeGimagesForAllImages against work directory $workDir."
    $gimagesScript -r "$workDir" -i50 -m5
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
    (cd "$workDir/images" ; $findCmd . -type f -a '!' -path '*CVS*' -exec "ls" "-l" "{}" ";"| tr -s " "|cut -f 9 -d " "|sort) > "$actualResourcePathsFile"
    (cd "$expectedOutputDir/images" ; $findCmd . -type f -a '!' -path '*CVS*' -exec "ls" "-l" "{}" ";"| tr -s " "|cut -f 9 -d " "|sort) > "$expectedResourcePathsFile"

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
    local workDir="$1"
    local expectedOutputDir="$2"

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
    local workDir="$1"
    local expectedOutputDir="$2"

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

    setup "$baseWorkDir/testGenerateAllImages"
    invokeGimagesForAllImages "$baseWorkDir/testGenerateAllImages"
    assertExpectedOutput "$baseExpectedOutputData/generateAllImages" "$baseWorkDir/testGenerateAllImages" 

    $echoCmd "testGenerateAllImages end"
}

function testGenerateImagesFromBasepath {
    $echoCmd "testGenerateImagesFromBasepath start"

    setup "$baseWorkDir/testGenerateImagesFromBasepath"
    invokeGimagesForBasepath "$baseWorkDir/testGenerateImagesFromBasepath" "common/search"
    assertExpectedOutput "$baseExpectedOutputData/generateImagesFromBasepath" "$baseWorkDir/testGenerateImagesFromBasepath" 

    $echoCmd "testGenerateImagesFromBasepath end"
}


# ==============================================================================
# Run the tests.

testGenerateAllImages
testGenerateImagesFromBasepath
