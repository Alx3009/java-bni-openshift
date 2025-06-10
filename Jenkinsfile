pipeline{
    agent any

    environment{
        PROJECT_NAME = "alexsta30-dev"
        BUILD_NAME = "java-bni-openshift-git"
    }
    stages{
        stage('Trigger Build in Openshift'){
            steps{
                sh "oc start-build ${BUILD_NAME} --from-dir=. --follow -n ${PROJECT_NAME}"
            }
        }
        stage('Deploy to Openshift'){
            steps{
                sh "oc rollout restart deployment/${BUILD_NAME} --from -dir=. --follow -n ${PROJECT_NAME}"
            }
        }
    }

    post{
        success{
            echo 'Build & deploy berhasil via Openshift BuildConfig.'
        }
        failure{
            echo 'Gagal menjalankan pipeline.'
        }
    }
}