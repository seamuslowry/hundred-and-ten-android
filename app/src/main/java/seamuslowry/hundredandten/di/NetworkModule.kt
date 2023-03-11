package seamuslowry.hundredandten.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import seamuslowry.hundredandten.BuildConfig
import seamuslowry.hundredandten.data.AuthRepository
import seamuslowry.hundredandten.sources.NetworkUserSource
import seamuslowry.hundredandten.sources.UserSource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideUserApi(
        auth: AuthRepository,
    ): UserSource = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
        .client(
            OkHttpClient.Builder()
                .addInterceptor {
                    val builder = it.request().newBuilder()
                    runBlocking {
                        auth.authToken.first().let {
                            builder.header(
                                "X-ZUMO-AUTH",
                                runBlocking { auth.authToken.first() ?: "" },
                            )
                        }
                    }
                    it.proceed(builder.build())
                }
                .build(),
        )
        .baseUrl(BuildConfig.GAME_API_URL)
        .build()
        .create(NetworkUserSource::class.java)
}
