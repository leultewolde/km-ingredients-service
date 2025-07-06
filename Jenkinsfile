@Library('jenkins-shared-library') _

buildAndDeployApp([
    imageName: 'ivtheforth/km-ingredients-service',
    namespace: 'hidmo',
    deployment: 'km-ingredients-service',
    container: 'km-ingredients-service',
    newRelicAppId: '490970453',
    serviceUrl: 'https://api-hidmo.leultewolde.com/kitchen/stored-items/actuator/health/liveness',
    vaultCred: 'vault-credentials',
    projectType: 'gradle',
    buildScript: './gradlew clean build',
    testScript: './gradlew test',
    sonarScript: './gradlew sonar',
    skipSonar: false
])