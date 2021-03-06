#!/usr/bin/env groovy

/**
 *  Fixes the source code according the patch generated by WalkMod. By default,
 *  the script pushes the changes it if tests pass.
 */
def call(body) {
    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def validatePatch = config.validatePatch?:false
    def reportDir = config.reportDir?:'target'
    def branch = config.branch?:'master'
    def mvnHomeDir = config.mvnHomeDir
    def alwaysFailOnPatch = config.alwaysFailOnPatch?:false


    echo "Checking if there are WalkMod changes to apply"
    if (hasWalkModPatch(mvnHomeDir)){

        echo "Generating WalkMod report"
        generateWalkModReport reportDir

        if(alwaysFailOnPatch){

            currentBuild.result = 'FAILURE'
            error("Build failed by the lack of consistent coding style")

        }else {

            if (validatePatch) {
                input "Does the patch look ok?"
            }

            echo "Applying the generated patch by WalkMod"
            applyWalkModPatch()

            if (mvnHomeDir != null) {
                echo "Running tests to see if patches work"
                sh "${mvnHomeDir}/bin/mvn -DskipWalkmod test"
            }

            echo "Pushing WalkMod changes"
            pushWalkModPatch branch
        }

    }else{
        echo "Coding Style Status: SUCCESS"
    }


}
