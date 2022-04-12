import example.com.pkg.Compile

def call(body){
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    pipeline{
        agent any

        stages{
            stage("Checkout"){
                steps{
                    script{
                        //git (branch: 'main', credentialsId: 'github-http-creds', url: 'https://github.com/nguyenstrai/learn_terraform.git')
                        sh "git clone git@github.com:nguyenstrai/learn_terraform.git"
                        env.WORKSPACE = "${env.WORKSPACE}/learn_terraform"
                        echo(env.WORKSPACE)
                    }
                }
            }
            stage("Setup AWS workspace"){
                steps{
                    script{
                        def commandOuput = sh (command: "aws assume-role --role-arn 'arn:aws:iam::432276108419:role/demo-admin-role' --session-name 'jenkins' ", returnStdOut: true)
                        def json = readJSON (text: commandOuput)
                        def accessKeyId = json.Credentials.AccessKeyId
                        def sessionToken = json.Credentials.SessionToken
                        def secretKey = json.Credentials.SecretAccessKey

                        env.AWS_ACCESS_KEY_ID = accessKeyId
                        env.AWS_SECRET_ACCESS_KEY = secretKey
                        env.AWS_SESSION_TOKEN = sessionToken
                    }
                }
            }
            stage("Init"){
                steps{
                    script{
                        sh "terraform init"
                    }
                }
            }
            stage("Plan"){
                steps{
                    script{
                        sh "terraform plan"
                    }
                }
            }
            stage("Apply"){
                steps{
                    script{

                        when {
                            expression {
                                input message: "Do you approve the plan?"
                                return true
                            }
                        }

                       echo ("apply coming soon")
                    }
                }
            }
        }

        post{
            always{
                script{
                    echo "post step"
                }
            }
        }
    }
}