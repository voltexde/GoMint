pipeline {
    agent {
        docker {
            image 'maven:3'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Depends') {
            steps {
                sh 'apt-get update'
                sh 'apt-get install -y openjfx'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean install'
            }
        }
    }
}