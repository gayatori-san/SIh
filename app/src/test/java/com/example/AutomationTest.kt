package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.TestingState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class AutomationTest {

    @Test
    fun testPassingInputsProducePass() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MetroCertViewModel(app)
        vm.autoPopulateDemoSetup()
        
        val report = vm.activeReports.value[vm.currentIndex.value]
        val maxCap = report.maxCapacity
        val e = report.e
        val minCap = report.minCapacity

        val t1Loads = listOf(minCap, maxCap * 0.25, maxCap * 0.5, maxCap * 0.75, maxCap)
        val t3Load = maxCap / 3
        val t4BaseLoad = minCap
        val t4ExtraLoad = 1.4 * e
        val t5Load = maxCap * 0.8
        val t6Load = maxCap * 0.5
        val t7Load = maxCap * 0.5
        val t8Load = maxCap
        val t9Loads = listOf(minCap, maxCap * 0.5, maxCap * 0.9)
        val t10Load = maxCap * 0.5
        val t11Load = maxCap * 0.5
        val t13Load = maxCap * 0.5

        val state = TestingState()
        state.autoFillPassingData(
            t1Loads, t3Load, t4BaseLoad, t4ExtraLoad, e, t5Load, t6Load, t7Load, t8Load,
            t9Loads, t10Load, t11Load, t13Load, maxCap
        )

        val inputs = state.toTestInputs(
            t1Loads, t3Load, t4BaseLoad, t4ExtraLoad, t5Load, t6Load, t7Load, t8Load,
            t9Loads, t10Load, t11Load, t13Load, maxCap * 0.5
        )

        vm.processTests(inputs)
        val processedReport = vm.activeReports.value[vm.currentIndex.value]
        assertEquals("Pass", processedReport.status)
        assertTrue(processedReport.weighingResults.all { it.isPass })
        assertTrue(processedReport.eccentricityResult?.isPass == true)
        assertTrue(processedReport.repeatabilityResult?.isPass == true)
    }

    @Test
    fun testFailingInputsProduceFail() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MetroCertViewModel(app)
        vm.autoPopulateDemoSetup()
        
        val report = vm.activeReports.value[vm.currentIndex.value]
        val maxCap = report.maxCapacity
        val e = report.e
        val minCap = report.minCapacity

        val t1Loads = listOf(minCap, maxCap * 0.25, maxCap * 0.5, maxCap * 0.75, maxCap)
        val t3Load = maxCap / 3
        val t4BaseLoad = minCap
        val t4ExtraLoad = 1.4 * e
        val t5Load = maxCap * 0.8
        val t6Load = maxCap * 0.5
        val t7Load = maxCap * 0.5
        val t8Load = maxCap
        val t9Loads = listOf(minCap, maxCap * 0.5, maxCap * 0.9)
        val t10Load = maxCap * 0.5
        val t11Load = maxCap * 0.5
        val t13Load = maxCap * 0.5

        val state = TestingState()
        state.autoFillFailingData(
            t1Loads, t3Load, t4BaseLoad, t4ExtraLoad, e, t5Load, t6Load, t7Load, t8Load,
            t9Loads, t10Load, t11Load, t13Load, maxCap
        )

        val inputs = state.toTestInputs(
            t1Loads, t3Load, t4BaseLoad, t4ExtraLoad, t5Load, t6Load, t7Load, t8Load,
            t9Loads, t10Load, t11Load, t13Load, maxCap * 0.5
        )

        vm.processTests(inputs)
        val processedReport = vm.activeReports.value[vm.currentIndex.value]
        assertEquals("Fail", processedReport.status)
    }

    @Test
    fun testDemoAutoFillCalculatesComplianceDeterministically() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = MetroCertViewModel(app)
        vm.autoPopulateDemoSetup()
        vm.autoPopulateDemoTests()
        
        val report = vm.activeReports.value[vm.currentIndex.value]
        val maxCap = report.maxCapacity
        val e = report.e
        val minCap = report.minCapacity

        val t1Loads = listOf(minCap, maxCap * 0.25, maxCap * 0.5, maxCap * 0.75, maxCap)
        val t3Load = maxCap / 3
        val t4BaseLoad = minCap
        val t4ExtraLoad = 1.4 * e
        val t5Load = maxCap * 0.8
        val t6Load = maxCap * 0.5
        val t7Load = maxCap * 0.5
        val t8Load = maxCap
        val t9Loads = listOf(minCap, maxCap * 0.5, maxCap * 0.9)
        val t10Load = maxCap * 0.5
        val t11Load = maxCap * 0.5
        val t13Load = maxCap * 0.5

        val state = vm.getTestingState(vm.currentIndex.value)
        val inputs = state.toTestInputs(
            t1Loads, t3Load, t4BaseLoad, t4ExtraLoad, t5Load, t6Load, t7Load, t8Load,
            t9Loads, t10Load, t11Load, t13Load, maxCap * 0.5
        )

        vm.processTests(inputs)
        val processedReport = vm.activeReports.value[vm.currentIndex.value]
        // Must be calculated via the engine
        assertEquals("Pass", processedReport.status)
        assertTrue(processedReport.weighingResults.all { it.isPass })
    }
}
