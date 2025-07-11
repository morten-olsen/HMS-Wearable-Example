package com.fprieto.hms.wearable.presentation.vm.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fprieto.hms.wearable.android.InjectingViewModelFactory
import com.fprieto.hms.wearable.presentation.vm.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBindingModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: InjectingViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun playerViewModel(viewModel: PlayerViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun dashboardViewModel(viewModel: DashboardViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessagingViewModel::class)
    abstract fun messagingViewModel(viewModel: MessagingViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun loginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LibraryListViewModel::class)
    abstract fun libraryListViewModel(viewModel: LibraryListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemListViewModel::class)
    abstract fun itemListViewModel(viewModel: ItemListViewModel): ViewModel
}
