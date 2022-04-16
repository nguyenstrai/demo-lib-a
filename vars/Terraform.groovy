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
                        git (branch: 'main', credentialsId: 'github-creds', url: 'git@github.com:nguyenstrai/learn_terraform.git')
                    }
                }
            }
            stage("Init"){
                steps{
                    script{
                        dir("${env.WORKSPACE}/envs/develop"){
                            withAWS(roleAccount:'992247318733', role:'arn:aws:iam::992247318733:role/demo-admin-role',credentials:'aws-user-jenkins') {
                            if (isUnix()){
                                sh """terraform init  """
                            }
                            else{
                                bat """terraform init  """
                           }
                          }
                        }
                    }
                }
            }
            stage("Plan"){
                steps{
                    script{
                        dir("${env.WORKSPACE}/envs/develop"){
                            withAWS(roleAccount:'992247318733', role:'arn:aws:iam::992247318733:role/demo-admin-role',credentials:'aws-user-jenkins') {
                            if (isUnix()){
                                sh """terraform plan -var ec2_instance_type="${env.INSTANCE_TYPE}"  -var ec2_tag="${env.EC2_TAG}" -var key_name="${env.KEY_NAME}" -var environment="${env.ENVIRONMENT}" """
                            }
                            else{
                                bat """terraform plan -var ec2_instance_type="${env.INSTANCE_TYPE}"  -var ec2_tag="${env.EC2_TAG}" -var key_name="${env.KEY_NAME}" -var environment="${env.ENVIRONMENT}" """
                            }                            
                           }
                        }
                    }
                }
            }
            stage("Apply"){
                when {
                    expression {
                        input message: "Do you approve the plan?"
                        return true
                    }
                }

                steps{
                    script{
                        dir("${env.WORKSPACE}/envs/develop"){
                            withAWS(roleAccount:'992247318733', role:'arn:aws:iam::992247318733:role/demo-admin-role',credentials:'aws-user-jenkins') {
                            if (isUnix()){
                                sh """terraform apply -var ec2_instance_type="${env.INSTANCE_TYPE}"  -var ec2_tag="${env.EC2_TAG}" -var key_name="${env.KEY_NAME}" -var environment="${env.ENVIRONMENT}" """
                            }
                            else{
                                bat """terraform apply -var ec2_instance_type="${env.INSTANCE_TYPE}"  -var ec2_tag="${env.EC2_TAG}" -var key_name="${env.KEY_NAME}" -var environment="${env.ENVIRONMENT}" """
                            }                            
                           }
                        }
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
