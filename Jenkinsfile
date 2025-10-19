// Jenkinsfile (Syntax Corrected)

pipeline {
    agent {
        kubernetes {
            label 'jenkins-agent-dind'
            defaultContainer 'jnlp'
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: jnlp
    image: jenkins/inbound-agent:latest-jdk17
    command:
    - cat
    tty: true
  - name: dind
    image: docker:20.10.7-dind
    securityContext:
      privileged: true
    volumeMounts:
    - name: dind-storage
      mountPath: /var/lib/docker
  volumes:
  - name: dind-storage
    emptyDir: {}
'''
        }
    }

    environment {
        // Pastikan DOCKER_USERNAME adalah username Docker Hub Anda.
        DOCKER_USERNAME         = "airlangga22"

        // --- Konfigurasi Lainnya ---
        K8S_CONFIG_REPO_URL     = "https://github.com/airlangga2403/my-k8s-configs.git"
        K8S_REPO_CREDENTIALS_ID = "github-token" // ID untuk kredensial GitHub (Username with password)
        GIT_USER_NAME           = "Jenkins CI"
        GIT_USER_EMAIL          = "jenkins-ci@your-domain.com"

        // --- Variabel Otomatis (Sudah Benar) ---
        APP_NAME                = "kai-test-kafka"
        DOCKER_IMAGE_NAME       = "${DOCKER_USERNAME}/${APP_NAME}"
    }

    stages {
        stage('Checkout Kode Aplikasi') {
            steps {
                checkout scm
            }
        }

        stage('Build & Push Docker Image') {
            when { expression { return fileExists('Dockerfile') } }
            steps {
                script {
                    def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                    env.IMAGE_TAG = commitHash

                    container('dind') {
                        withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USER')]) {
                            sh "docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD}"
                            sh "export DOCKER_BUILDKIT=1 && docker build -t ${DOCKER_IMAGE_NAME}:${IMAGE_TAG} ."
                            sh "docker push ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}"
                        }
                    }
                }
            }
        }


        stage('Update Deployment di Git (GitOps)') {
            when { expression { return fileExists('Dockerfile') } }
            steps {
                // ==================== PERBAIKAN DI SINI ====================
                // Seluruh logika di dalam tahap ini dibungkus dengan blok 'script'.
                script {
                    dir('k8s-configs-checkout') {
                        // Clone repositori konfigurasi
                        git credentialsId: K8S_REPO_CREDENTIALS_ID, url: K8S_CONFIG_REPO_URL, branch: 'main'

                        sh "git config --global user.email '${GIT_USER_EMAIL}'"
                        sh "git config --global user.name '${GIT_USER_NAME}'"

                        // Ganti tag image di file deployment
                        sh "sed -i 's|image: .*|image: ${DOCKER_IMAGE_NAME}:${IMAGE_TAG}|' ${APP_NAME}/deployment.yaml"

                        sh "git add ${APP_NAME}/deployment.yaml"
                        sh 'git diff-index --quiet HEAD || git commit -m "Update image untuk ${APP_NAME} ke tag ${IMAGE_TAG} [ci skip]"'

                        // Suntikkan kredensial GitHub untuk melakukan push
                        withCredentials([usernamePassword(credentialsId: K8S_REPO_CREDENTIALS_ID, usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_TOKEN')]) {
                            // Buat URL remote yang sudah terotentikasi
                            def repoPath = K8S_CONFIG_REPO_URL.split('//')[1]
                            def remoteUrlWithAuth = "https://${GIT_USERNAME}:${GIT_TOKEN}@${repoPath}"

                            // Lakukan push menggunakan URL yang sudah terotentikasi
                            sh "git push ${remoteUrlWithAuth} main"
                        }
                    }
                    echo "Manifestasi telah di-push ke Git. Argo CD akan menangani sisanya."
                }
                // ==========================================================
            }
        }
    }
}
