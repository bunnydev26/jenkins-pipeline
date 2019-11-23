def call(gitUsername, repositoryName, dockerUsername) {

    stage('checkout') {
        node('master') {
            // Clear all the files and directory for the clean build
            sh "rm -rf *";
            // Cloning the repostiroy which stores the data to build the docker file and the kube deployment files
            sh "git clone --single-branch -b master https://github.com/${gitUsername}/${repositoryName}.git";
            // Pulling the changes to the renderer repository.
            sh "cd ~/kube_renderer && git pull";
            
            // Setting up the minor build version
            Date date = new Date()
            // Populating the image version.
            imageVersion = date.format("yyddMMHHmm");
            // Docker image version
            dockerImageName = "${dockerUsername}/${repositoryName}:${imageVersion}";
        }
    }
    
    stage('Build and Test') {
        node('master') {
            // Building the docker image.
            sh "docker build -t ${dockerImageName} .";
            
            // Runing the test cases on the docker image if it is applicable.
            sh "echo 'Running test cases'";
            
            // Pushing the docker image.
            sh "docker push ${dockerImageName}";
            
        }
    }
    
    stage('Deploy on Staging') {
        node('master') {
            sh "echo 'Deploy on staging'";
            sh "python3 ~/kube_renderer/kube_renderer.py --instance_status staging --docker_image_version ${imageVersion} --base_dir ./${repositoryName} | kubectl apply -f -"
        }
    }
    
    stage('Confirm') {
        def userInput = input(
            id: 'userInput', message: 'This is PRODUCTION!', parameters: [
            [$class: 'BooleanParameterDefinition', defaultValue: false, description: '', name: 'Please confirm you sure to proceed']
        ])

        if(!userInput) {
            error "Build was canceled";
        }
    }
    
    stage('Deploy on Production') {
        node('master') {            
            sh "echo 'Deploy on Production'";
            sh "python3 ~/kube_renderer/kube_renderer.py --instance_status production --docker_image_version ${imageVersion} --base_dir ./${repositoryName} | kubectl apply -f -"
        }
    }
}
