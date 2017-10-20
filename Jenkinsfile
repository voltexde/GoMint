pipeline {
  agent none
  stages {
    stage('Depends') {
      agent {
        docker {
          image 'maven:3'
          args '-v /var/lib/jenkins/.m2:/root/.m2'
        }
        
      }
      steps {
        sh 'apt-get install -y openjfx'
        sh 'mvn -B clean install'
      }
    }
  }
}