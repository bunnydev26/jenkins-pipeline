def call(gitUsername, repositoryName) {

    stage('checkout') {
        node('master') {
            // Clear all the files and directory for the clean build
            // yes test
            sh "git clone --single-branch -b master https://github.com/${gitUsername}/${repositoryName}.git"
        }
    }
}
