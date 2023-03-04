package seamuslowry.hundredandten.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import seamuslowry.hundredandten.sources.NetworkUserSource
import seamuslowry.hundredandten.sources.UserSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideUserApi(): UserSource = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        // TODO see if this can be configured for local / staging / prod
        .baseUrl("https://hundredandten-staging.azurewebsites.net")
        .build()
        .create(NetworkUserSource::class.java)
}
