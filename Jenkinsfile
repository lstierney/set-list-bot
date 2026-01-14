pipeline {
    agent any

    triggers {
        pollSCM('* * * * *')
    }

    options {
        skipDefaultCheckout()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout from Github') {
            steps {
                git(
                    branch: 'main',
                    credentialsId: 'jenkins',
                    url: 'git@github.com:lstierney/set-list-bot.git'
                )
            }
        }

        stage('Build with Maven') {
            steps {
                script {
                    withMaven(maven: 'M3') {
                        sh 'mvn clean package'
                    }
                }
            }
        }
    }
}
