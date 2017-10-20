pipeline {
    agent {
        docker {
            image 'maven:3'
            args '-v /var/lib/jenkins/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
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
