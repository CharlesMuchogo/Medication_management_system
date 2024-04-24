package com.example.medapp.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medapp.data.AppDatabase
import com.example.medapp.domain.models.Patient
import com.example.medapp.utils.ResultStatus
import com.example.medapp.utils.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientsViewModel @Inject constructor(private val database: AppDatabase): ViewModel() {

    val patientsState = MutableStateFlow(
        Results<List<Patient>>(
            data = null,
            message = null,
            status = ResultStatus.INITIAL
        )
    )

    init {
        getPatients()
    }

    private fun getPatients() {
        viewModelScope.launch {
            patientsState.value = Results.loading()
            database.patientRecordDao().getAllProducts().catch {
                patientsState.value = Results.error(it.message.toString())
            }.collect{
                patientsState.value = Results.success(it)
            }
        }
    }

    fun addPatient(patient: Patient){
        viewModelScope.launch {
            database.patientRecordDao().insertPatient(patient)
        }
    }
    fun deletePatient(patient: Patient){
        viewModelScope.launch {
            database.patientRecordDao().deletePatient(patient)
        }
    }
}