import example.com.pkg.Compile

def call(body){
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Compile compile = new Compile(this)

    pipeline{
        agent any

        stages{
            stage("Display"){
                steps{
                    script{
                        echo "Hello World"
                        compile.run()
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