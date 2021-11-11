package com.rfid.ui.project

import android.content.Intent
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.itemSelections
import com.rfid.R
import com.rfid.data.local.DatabaseClient
import com.rfid.data.local.user.UserDataSourceImpl
import com.rfid.data.remote.RetrofitClient
import com.rfid.data.remote.login.LoginDataSourceImpl
import com.rfid.data.remote.project.Manufacture
import com.rfid.data.remote.project.Project
import com.rfid.data.remote.project.ProjectDataSourceImpl
import com.rfid.data.repository.project.ProjectRepositoryImpl
import com.rfid.databinding.ActivitySelectProjectBinding
import com.rfid.ui.RFIDControlActivity
import com.rfid.ui.base.BaseActivityK
import com.rfid.ui.login.LoginActivity
import com.rfid.util.Constants
import com.rfid.util.SharedPreferencesPackage
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit

class ProjectActivity :
    BaseActivityK<ActivitySelectProjectBinding>(R.layout.activity_select_project) {

    private val viewModel: ProjectViewModel by lazy {
        ProjectViewModel(
            repository = ProjectRepositoryImpl(
                userDataSource = UserDataSourceImpl(
                    userDao = DatabaseClient.userDao()
                ),
                projectDataSource = ProjectDataSourceImpl(
                    projectApi = RetrofitClient.projectAPI
                ),
                loginDataSource = LoginDataSourceImpl(
                    loginApi = RetrofitClient.loginAPI
                )
            )
        )
    }

    private var projectList: MutableList<Project> = mutableListOf()
    private lateinit var projectAdapter: ArrayAdapter<Project>
    private var manufactureList: MutableList<Manufacture> = mutableListOf()
    private lateinit var manufactureAdapter: ArrayAdapter<Manufacture>

    private lateinit var mProject: Project
    private lateinit var mManufacture: Manufacture

    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mProject = SharedPreferencesPackage.getProject()
        mManufacture = SharedPreferencesPackage.getManufacture()

        applyViewModel(mProject, mManufacture)
        applyBinding()

        val projectWatcher = binding.spProject.itemSelections().map { it }
        val manufactureWatcher = binding.spManufacture.itemSelections().map { it }
        compositeDisposable
            .add(
                Observable
                    .combineLatest(
                        projectWatcher,
                        manufactureWatcher,
                        BiFunction { idResult: Int, pwResult: Int ->
                            return@BiFunction idResult == 0 || pwResult == 0
                        }
                    )
                    .subscribe({ blank ->
                        binding.btSelectFinish.isEnabled = !blank
                    }, { it.printStackTrace() })
            )
    }

    private fun applyViewModel(mProject: Project, mManufacture: Manufacture) {
        viewModel.apply {
            isLoading.observe(this@ProjectActivity, { isLoading ->
                if (isLoading) {
                    showLoading(true, binding.pbLoading)
                } else {
                    showLoading(false, binding.pbLoading)
                }
            })
            projects.observe(this@ProjectActivity, {
                projectList = it.data
                projectList.add(0, Project("0", "프로젝트를 선택해주세요."))
                val pos = it.data.indexOf(mProject)
                projectAdapter =
                    ArrayAdapter(
                        this@ProjectActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        projectList
                    )
                binding.spProject.adapter = projectAdapter
                binding.spProject.setSelection(pos)
            })
            manufactures.observe(this@ProjectActivity, {
                manufactureList = it.data
                manufactureList.add(0, Manufacture("0", "제조사를 선택해주세요."))
                val pos = it.data.indexOf(mManufacture)
                manufactureAdapter =
                    ArrayAdapter(
                        this@ProjectActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        manufactureList
                    )
                binding.spManufacture.adapter = manufactureAdapter
                binding.spManufacture.setSelection(pos)
            })
        }
    }

    private fun applyBinding() {
        compositeDisposable.add(
            binding.btSelectFinish
                .clicks()
                .throttleFirst(Constants.THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe({
                    val projectObject = binding.spProject.selectedItem as Project
                    val manufactureObject = binding.spManufacture.selectedItem as Manufacture
                    SharedPreferencesPackage.setProject(projectObject.id, projectObject.name)
                    SharedPreferencesPackage.setManufacture(
                        manufactureObject.id,
                        manufactureObject.name
                    )
                    val intent = Intent()
                    intent.setClass(applicationContext, RFIDControlActivity::class.java)
                    startActivityForResult(intent, REQUEST_START_MAIN)
                }, { it.printStackTrace() })
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_START_MAIN) {
            if (resultCode == RESULT_OK) {
                // 로그아웃
                val intent = Intent()
                intent.setClass(applicationContext, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // onBackPressed
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            openDialog("로그아웃 하시겠습니까?")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        openDialog("로그아웃 하시겠습니까?")
    }

    companion object {
        private const val TAG = "SelectProjectActivity"
        private const val REQUEST_START_MAIN = 0x101
    }
}