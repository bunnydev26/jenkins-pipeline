def call(gitUsername, repositoryName) {

    stage('checkout') {
        node('master') {
            // Clear all the files and directory for the clean build
            sh "git clone --single-branch -b ${repositoryName}  https://github.com/${gitUsername}/${repositoryName}.git"
        }
    }
}
