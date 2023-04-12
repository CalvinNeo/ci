// REF: https://<your-jenkins-server>/plugin/job-dsl/api-viewer/index.html
// For trunk and latest release branches.
pipelineJob('pingcap_enterprise/tidb-enterprise-manager/pr-verify') {
    logRotator {
        daysToKeep(30)
    }
    parameters {
        // Ref: https://docs.prow.k8s.io/docs/jobs/#job-environment-variables
        stringParam("BUILD_ID")
        stringParam("PROW_JOB_ID")
        stringParam("JOB_SPEC")
    }
    properties {
        giteeConnection {
            giteeConnection('https://gitee.com/pingcap_enterprise/tidb-enterprise-manager')
        }
    }    
    triggers {
        gitee {
            triggerOnPush(false)
            triggerOnCommitComment(false)

            // pull requests
            noteRegex('^/test\s+pr-verify$')
            buildInstructionFilterType(CI_SKIP)
            skipWorkInProgressPullRequest(true)
            triggerOnOpenPullRequest(true)
            // 0: None, 1: source branch updated, 2: target branch updated, 3: both source and target branch updated.
            triggerOnUpdatePullRequest(true) 
            cancelIncompleteBuildOnSamePullRequest(true)
        }        
    }
    definition {
        cpsScm {
            lightweight(true)
            scriptPath("gitee/pipelines/pingcap_enterprise/tidb-enterprise-manager/pr-verify.groovy")
            scm {
                git{
                    remote { url('https://github.com/PingCAP-QE/ci.git') }
                    branch('main')
                    extensions {
                        cloneOptions {
                            depth(1)
                            shallow(true)
                            timeout(5)
                        } 
                    }
                }
            }
        }
    }
}