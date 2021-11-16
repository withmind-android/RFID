package com.rfid.ui.login;

import android.content.Intent
import android.util.Log
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import com.rfid.R
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.repository.login.LoginRepositoryImpl
import com.rfid.databinding.ActivityLoginBinding
import com.rfid.ui.base.BaseActivityK
import com.rfid.ui.project.ProjectActivity
import com.rfid.util.Constants
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit

class LoginActivity : BaseActivityK<ActivityLoginBinding>(R.layout.activity_login) {
    private val viewModel: LoginViewModel by lazy {
        LoginViewModel(
            repository = LoginRepositoryImpl(
                userDataSource = UserDataSourceImpl(
                    userDao = DatabaseClient.userDao()
                ),
                loginDataSource = LoginDataSourceImpl(
                    loginApi = RetrofitClient.loginAPI
                )
            )
        )
    }

    override fun init() {
        viewModel.apply {
            val isLogin = intent.getBooleanExtra(Constants.LOGOUT, false)

            userInfo.observe(this@LoginActivity, { userEntity ->
                binding.etLoginId.setText(userEntity.userId)
                binding.etLoginPw.setText(userEntity.userPw)
                binding.cbLoginAuto.isChecked = userEntity.isAuto

                if (userEntity.isAuto && !isLogin) {
                    startLogin(userEntity.userId, userEntity.userPw, userEntity.isAuto)
                } else {
                    logout()
                }
            })
            isLoading.observe(this@LoginActivity, { isLoading ->
                if (isLoading) {
                    showLoading(true, binding.pbLoading)
                } else {
                    showLoading(false, binding.pbLoading)
                }
            })
            loginState.observe(this@LoginActivity, {
                Log.e(TAG, "loginState login = $it")
                if (it == true) {
                    SharedPreferencesPackage.setAutoLogin(
                        binding.etLoginId.text.toString(),
                        binding.etLoginPw.text.toString(),
                        binding.cbLoginAuto.isChecked
                    )

                    val intent = Intent()
                    intent.setClass(applicationContext, ProjectActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("아이디와 패스워드를 확인해주세요.")
                }
            })
        }

        compositeDisposable.add(
            Observable.combineLatest(
                binding.etLoginId
                    .textChanges(),
                binding.etLoginPw
                    .textChanges(),
                BiFunction { id: CharSequence, password: CharSequence -> return@BiFunction id.isNotEmpty() && password.isNotEmpty() }
            )
                .subscribe({
                    binding.btLogin.isEnabled = it
                }, { it.printStackTrace() })
        )

        compositeDisposable.add(
            binding.btLogin
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    viewModel.startLogin(
                        binding.etLoginId.text.toString(),
                        binding.etLoginPw.text.toString(),
                        binding.cbLoginAuto.isChecked
                    )
                }, { it.printStackTrace() })
        )
    }


    companion object {
        private const val TAG = "LoginActivity"
    }
}