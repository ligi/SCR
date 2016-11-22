
node {
 def flavorCombination='Prod'

 stage 'checkout'
  checkout scm

 stage 'lint'
  try {
   sh "./gradlew lint${flavorCombination}Release"
  } catch(err) {
   currentBuild.result = FAILURE
  } finally {
   androidLint canComputeNew: false, defaultEncoding: '', healthy: '', pattern: '', unHealthy: ''
  }
  
 stage 'assemble'
  sh "./gradlew assemble${flavorCombination}Release"
  archive 'android/build/outputs/apk/*'
  archive 'android/build/outputs/mapping/*/release/mapping.txt'
     
}