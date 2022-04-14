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
                        withAWS(roleAccount:'432276108419', role:'arn:aws:iam::432276108419:role/demo-admin-role') {
                            bat """terraform init  """
                        }

                    }
                }
            }
            stage("Plan"){
                steps{
                    script{
                        withAWS(roleAccount:'432276108419', role:'arn:aws:iam::432276108419:role/demo-admin-role') {
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