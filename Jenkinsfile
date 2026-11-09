pipeline{
    agent any

    tools{
        jdk 'jdk-25'
    }

    options{
        timestamps()
        timeout(time:45, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5')
    }

    environment{
        GRADLE = 'gradlew.bat --console=plain --stacktrace'
    }

    stages{
        stage('hello'){
            steps{
                echo "Building ${env.JOB_NAME} #${env.BUILD_Number} on ${env.NODE_NAME}"
                bat 'java -version'
                bat 'git log -1 --oneline'
            }
        }
    }


    post{
        always{
            echo "finished with result: ${currentBuild.currentResult}"
        }
        failure{
            echo 'build failed: open first read stage and read log from the bottom'
        }
    }




}