package com.fprieto.hms.wearable.presentation.ui.di

import androidx.fragment.app.Fragment
import com.fprieto.hms.wearable.di.ActivityScope
import androidx.fragment.app.Fragment
import com.fprieto.hms.wearable.di.ActivityScope
import com.fprieto.hms.wearable.presentation.ui.*
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
internal abstract class WearEngineMainActivityModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindMainActivity(): WearEngineActivity

    @Binds
    @IntoMap
    @FragmentKey(DashboardFragment::class)
    abstract fun dashboardFragment(dashboardFragment: DashboardFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(MessagingFragment::class)
    abstract fun messagingFragment(messagingFragment: MessagingFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(PlayerFragment::class)
    abstract fun playerFragment(playerFragment: PlayerFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(LoginFragment::class)
    abstract fun loginFragment(loginFragment: LoginFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(LibraryListFragment::class)
    abstract fun libraryListFragment(libraryListFragment: LibraryListFragment): Fragment

    @Binds
    @IntoMap
    @FragmentKey(ItemListFragment::class)
    abstract fun itemListFragment(itemListFragment: ItemListFragment): Fragment
}
