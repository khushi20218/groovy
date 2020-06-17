job("groovyjob1"){
	description("Job to pull the developer code from github")
	scm {
		github('khushi20218/devopsrepo.git','master')
	}
	triggers {
        githubPush()
    }

	steps{
	shell('sudo cp * /task3')
	}
}


job("groovyjob2")

	{
	label('kubectl')

	triggers {
        upstream('groovyjob1', 'SUCCESS')
}
steps{
	shell('''cd /root/.kube
python3 a.py
count=$(kubectl get deployment | grep website | wc -l)
if [ $count -eq 1 ]
then
echo "deployment already running !"
else
kubectl create -f deployment.yaml
kubectl create -f service.yaml
fi''')
	}
}




job("groovyjob3")
	{
	label('kubectl')

	triggers {
        upstream('groovyjob2', 'SUCCESS')
}
steps{
	shell('''if kubectl get deployments website
then
echo "pod running "
curl 192.168.99.104:32000
else 
exit 1
fi''')
	}

publishers {
        extendedEmail {
            recipientList('khushithareja17@gmail.com')
            defaultSubject('Unstable build')
            defaultContent('The build is unstable')
            contentType('text/html')
            triggers {
                failure {
		    attachBuildLog(true)
                    subject('Unstable build')
                    content('The build is unstable')
                    sendTo {
                        developers()
                    }
                }
            }
        }
    }
}




buildPipelineView('groovyyyyyyyyyyyy pipeline') {
    title('task6groovy')
    displayedBuilds(5)
    selectedJob('groovyjob1')
    showPipelineParameters(true)
    refreshFrequency(3)
}

