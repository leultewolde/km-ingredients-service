@Library('jenkins-shared-library') _

buildAndDeployApp([
    imageName: 'ivtheforth/km-ingredients-service',
    namespace: 'hidmo',
    deployment: 'km-ingredients-service',
    container: 'km-ingredients-service',
    newRelicAppId: '490970453',
    serviceUrl: 'https://api-hidmo.leultewolde.com/kitchen/actuator/health/liveness',
])