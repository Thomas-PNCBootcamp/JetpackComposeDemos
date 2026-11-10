pipeline{
    agent any

    tools{
        jdk 'jdk-25'
    }

    options{
        timestamps()
        timeout(time:45, unit: 'MINUTES')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
    }

    environment{
        GRADLE = 'gradlew.bat --console=plain --stacktrace'
    }

    stages{
//        stage('hello'){
//            steps{
//                echo "Building ${env.JOB_NAME} #${env.BUILD_Number} on ${env.NODE_NAME}"
//                bat 'java -version'
//                bat 'git log -1 --oneline'
//            }
//        }
        stage('Check tools'){
            steps{
                bat 'java -version'
                bat 'echo ANDROID_HOME=%ANDROID_HOME%'
                script{
                    if (!env.ANDROID_HOME){
                        error 'ANDROID_HOME is not set'
                    }
                }
                bat "${GRADLE} --version"

            }
        }

        stage('Build debug APK'){
            steps{
                bat "${GRADLE} assembleDebug"
            }
            post{
                success{
                    archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', fingerprint: true
                }
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