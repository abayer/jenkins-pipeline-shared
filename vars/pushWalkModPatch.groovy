#!/usr/bin/env groovy

/**
 * Commits and pushes a WalkMod patch
 */
def call(String branch = 'master') {


    sh 'git commit -a --amend -m "Fixing style violations"'
    sh "git pull origin $branch"
    sh "git push origin HEAD:$branch"


}