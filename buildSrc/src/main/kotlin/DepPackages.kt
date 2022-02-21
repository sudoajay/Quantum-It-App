import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.implementBasicAndroid(){
//    android  kotlin core
    add("implementation", Dependencies.Androidx.CoreKtx)
//    android  Appcompat
    add("implementation", Dependencies.Androidx.AppCompat)
//    android  Material
    add("implementation", Dependencies.Androidx.Material)

    add("implementation", Dependencies.Androidx.ConstraintLayout)

    add("implementation", Dependencies.Androidx.swipeRefreshLayout)

}

fun DependencyHandler.implementDependencyInjection(){
    add("implementation", Dependencies.DependencyInjection.daggerHilt)
    add("kapt", Dependencies.DependencyInjection.daggerHiltCompiler)
    add("implementation", Dependencies.DependencyInjection.androidHiltViewModel)
    add("kapt", Dependencies.DependencyInjection.androidHiltCompiler)
}

fun DependencyHandler.implementAndroidX(){

    add("implementation", Dependencies.Lifecycle.viewModelKtx)
    add("implementation", Dependencies.Lifecycle.lifecycleRuntime)
    add("implementation", Dependencies.Lifecycle.lifecycleLiveData)
    add("implementation",Dependencies.Coroutine.coroutineCore)
    add("implementation",Dependencies.Coroutine.coroutineAndroid)
    add("implementation", Dependencies.Androidx.ActivityKtx)
    add("implementation", Dependencies.Androidx.navigationFragment)
    add("implementation", Dependencies.Androidx.navigationUi)


}

fun DependencyHandler.implementFirebaseGoogle(){
    add("implementation" , Dependencies.FireBase.playServiceAuth)
//    add("implementation" , Dependencies.FireBase.FirebaseCommonktx)


    add("implementation" ,platform(Dependencies.FireBase.fireBaseBom))
    add("implementation" ,Dependencies.FireBase.firBaseAnalytics)
    add("implementation" ,Dependencies.FireBase.firBaseAuth)

    add("implementation" ,Dependencies.FireBase.facebookLogin)

//    add("implementation" ,Dependencies.FireBase.firBaseDatabase)
//    add("implementation" ,Dependencies.FireBase.firBaseStorage)
//
//    add("implementation" ,Dependencies.FireBase.firebaseUiAuth)
//    add("implementation" ,Dependencies.FireBase.firebaseUiDatabase)

}

fun DependencyHandler.implementDataBase(){
    add("implementation", Dependencies.Storage.paging)
//    add("implementation", Dependencies.Storage.protoDataStore)
//    add("implementation", Dependencies.Storage.protobuf)

}

fun DependencyHandler.implementNetwork(){
    add("implementation", Dependencies.Network.retrofit)
    add("implementation", Dependencies.Network.retrofitConverterGson)
    add("implementation", Dependencies.Network.googleGson)
    add("implementation", Dependencies.Network.okhttp)
    add("implementation", Dependencies.Network.loggingInterceptor)
    add("implementation", Dependencies.Network.glide)
    add("kapt", Dependencies.Network.glideCompiler)

}

fun DependencyHandler.implementExternalLibrary(){
    add("implementation",Dependencies.ExternalLibrary.shreyasPatilMaterialDialog)
    add("implementation",Dependencies.ExternalLibrary.airbnbLottie)
}
fun DependencyHandler.implementTest() {
    add("testImplementation", Dependencies.Test.junit)
}

fun DependencyHandler.implementAndroidTest() {
    add("implementation",Dependencies.AndroidTest.testCore)
    add("androidTestImplementation", Dependencies.AndroidTest.espresso)
    add("androidTestImplementation", Dependencies.AndroidTest.extJunitKtx)

}




