def call(application_name, module_name) {

    stage('checkout') {
        node('master') {
            // Clear all the files and directory for the clean build
            sh "echo Hello world";
        }
    }
}
