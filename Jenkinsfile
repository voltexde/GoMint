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
    stage('Store') {
      steps {
        archiveArtifacts 'gomint-server/target/GoMint.jar'
      }
    }
  }
  post {
    success {
      slackSend color: 'good', message: 'New build has been built: #${currentBuild.number} - ${currentBuild.absoluteUrl}'
    }

    failure {
      slackSend color: 'danger', message: 'Failed build: #${currentBuild.number} - ${currentBuild.absoluteUrl}'
    }
  }
}
