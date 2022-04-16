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
                        withAWS(roleAccount:'992247318733', role:'arn:aws:iam::992247318733:role/demo-admin-role',credentials:'aws-user-jenkins') {
                            bat """terraform init  """
                        }

                    }
                }
            }
            stage("Plan"){
                steps{
                    script{
                        withAWS(roleAccount:'992247318733', role:'arn:aws:iam::992247318733:role/demo-admin-role',credentials:'aws-user-jenkins') {
                            bat """terraform plan -var ec2_instance_type="${env.INSTANCE_TYPE}" """
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
