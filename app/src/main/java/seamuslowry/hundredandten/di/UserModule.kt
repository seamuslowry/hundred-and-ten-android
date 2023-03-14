package seamuslowry.hundredandten.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import seamuslowry.hundredandten.data.NetworkUserRepository
import seamuslowry.hundredandten.data.UserRepository

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserModule {
    @Binds
    abstract fun bindUserRepo(impl: NetworkUserRepository): UserRepository
}
