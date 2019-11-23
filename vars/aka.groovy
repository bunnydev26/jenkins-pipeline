def call(gitUsername, repositoryName, dockerUsername) {

    stage('checkout') {
        node('master') {
            // Clear all the files and directory for the clean build
            sh "rm -rf *";
            // Cloning the repostiroy which stores the data to build the docker file and the kube deployment files
            sh "git clone --single-branch -b master https://github.com/${gitUsername}/${repositoryName}.git";
            // Pulling the changes to the renderer repository.
            sh "cd ~/kube_renderer && git pull";
            
            // Populating the image version.
            imageVersion = date.format("yyddMMHHmm");
        }
    }
    stage('Build and Test') {
        node('master') {
            // Building the docker image.
            dockerImageName = "${dockerUsername}/${repositoryName}:${imageVersion}";
            sh "echo 'docker build -t ${dockerImageName} .'";
            
            // Runing the test cases on the docker image if it is applicable.
            sh "echo 'Running test cases'"
            
            // Pushing the docker image.
            sh "echo 'docker push ${dockerImageName}'"
            
        }
    }
}
