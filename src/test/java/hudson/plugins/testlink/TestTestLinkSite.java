package hudson.plugins.testlink;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import hudson.plugins.testlink.result.TestCaseWrapper;

import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Attachment;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.ReportTCResultResponse;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class TestTestLinkSite {

	@InjectMocks
	private TestLinkSite testLinkSite;
	
	@Mock
	private TestLinkAPI api;
	
	@Mock
	private TestProject testProject;
	
	@Mock
	private TestPlan testPlan;
	
	@Mock
	private Build build;
	
	@Mock
	private Attachment attachment;
	
	@Mock
	private TestCaseWrapper testCaseWrapper;
	
	@Mock
	private ReportTCResultResponse reportTCResultResponse;
	
	@Test
	public void testUploadAttachment() {
		when(attachment.getTitle()).thenReturn("title");
		when(attachment.getDescription()).thenReturn("description");
		when(attachment.getFileName()).thenReturn("fileName");
		when(attachment.getFileType()).thenReturn("fileType");
		when(attachment.getContent()).thenReturn("content");
		
		testLinkSite.uploadAttachment(123, attachment);
		
		verify(api).uploadExecutionAttachment(123, "title", "description", "fileName", "fileType", "content");
	}
	
	@Test
	public void testUpdateTestCaseWithPassedStatus() {
		ExecutionStatus status = ExecutionStatus.PASSED;

		prepareUpdateTestCaseMocks(status);
		
		int executionId = testLinkSite.updateTestCase(testCaseWrapper);
		
		assertThat(executionId, is(321));

		verify(api)
				.reportTCResult(3, 4, 2, status, 1,
						"build-name", "notes", null, null, null, "platform",
						new HashMap<String, String>(), null);
		Report report = testLinkSite.getReport();
		assertThat(report.getPassed(), is(1));
		assertThat(report.getFailed(), is(0));
		assertThat(report.getBlocked(), is(0));
	}

	@Test
	public void testUpdateTestCaseWithFailedStatus() {
		ExecutionStatus status = ExecutionStatus.FAILED;

		prepareUpdateTestCaseMocks(status);
		
		int executionId = testLinkSite.updateTestCase(testCaseWrapper);
		
		assertThat(executionId, is(321));

		verify(api)
				.reportTCResult(3, 4, 2, status, 1,
						"build-name", "notes", null, null, null, "platform",
						new HashMap<String, String>(), null);
		Report report = testLinkSite.getReport();
		assertThat(report.getPassed(), is(0));
		assertThat(report.getFailed(), is(1));
		assertThat(report.getBlocked(), is(0));
	}
	
	@Test
	public void testUpdateTestCaseWithBlockedStatus() {
		ExecutionStatus status = ExecutionStatus.BLOCKED;

		prepareUpdateTestCaseMocks(status);
		
		int executionId = testLinkSite.updateTestCase(testCaseWrapper);
		
		assertThat(executionId, is(321));

		verify(api)
				.reportTCResult(3, 4, 2, status, 1,
						"build-name", "notes", null, null, null, "platform",
						new HashMap<String, String>(), null);
		Report report = testLinkSite.getReport();
		assertThat(report.getPassed(), is(0));
		assertThat(report.getFailed(), is(0));
		assertThat(report.getBlocked(), is(1));
	}
	
	@Test
	public void testUpdateTestCaseWithNotRunStatus() {
		when(testCaseWrapper.getExecutionStatus()).thenReturn(ExecutionStatus.NOT_RUN);
		
		int executionId = testLinkSite.updateTestCase(testCaseWrapper);
		
		assertThat(executionId, is(0));

		verifyZeroInteractions(api);
		
		Report report = testLinkSite.getReport();
		assertThat(report.getPassed(), is(0));
		assertThat(report.getFailed(), is(0));
		assertThat(report.getBlocked(), is(0));
	}
	
	@Test
	public void testUpdateTestCaseWithNullStatus() {
		when(testCaseWrapper.getExecutionStatus()).thenReturn(ExecutionStatus.NOT_RUN);
		
		int executionId = testLinkSite.updateTestCase(testCaseWrapper);
		
		assertThat(executionId, is(0));
		
		verifyZeroInteractions(api);
		
		Report report = testLinkSite.getReport();
		assertThat(report.getPassed(), is(0));
		assertThat(report.getFailed(), is(0));
		assertThat(report.getBlocked(), is(0));
	}
	
	@Test
	public void getAutomatedTestCasesShouldReturnNoTestCasesWhenStatusesDoNotMatch() {
		TestCase testCase1 = mock(TestCase.class);
		TestCase testCase2 = mock(TestCase.class);
		TestCase[] returnedTestCases = {testCase1, testCase2};
		
		when(api.getTestCasesForTestPlan(anyInt(), anyListOf(Integer.class),
				anyInt(), anyListOf(Integer.class), anyString(), anyBoolean(),
				anyListOf(Integer.class), anyListOf(String.class).toArray(new String[0]),
				any(ExecutionType.class), anyBoolean(),
				any(TestCaseDetails.class))).thenReturn(returnedTestCases);
		
		TestCase[] testCases = testLinkSite.getAutomatedTestCases(null);
		
		assertThat(testCases.length, is(2));
 	}

	@Test
	public void getAutomatedTestCasesShouldReturnTestCaseWhenStatusMatches() {
		TestCase testCase1 = mock(TestCase.class);
		when(testCase1.getExecutionStatus()).thenReturn(ExecutionStatus.FAILED);
		TestCase testCase2 = mock(TestCase.class);
		when(testCase2.getExecutionStatus()).thenReturn(ExecutionStatus.BLOCKED);
		TestCase[] returnedTestCases = {testCase1, testCase2};
		
		when(api.getTestCasesForTestPlan(anyInt(), anyListOf(Integer.class),
                anyInt(), anyListOf(Integer.class), anyString(), anyBoolean(),
                anyListOf(Integer.class), anyListOf(String.class).toArray(new String[0]),
                any(ExecutionType.class), anyBoolean(),
                any(TestCaseDetails.class))).thenReturn(returnedTestCases);
		
		TestCase[] testCases = testLinkSite.getAutomatedTestCases(null);
		
		assertThat(testCases.length, is(2));
		assertThat(testCases[0], is(sameInstance(testCase1)));
 	}

	private void prepareUpdateTestCaseMocks(ExecutionStatus status) {
		when(build.getId()).thenReturn(1);
		when(build.getName()).thenReturn("build-name");
		when(testPlan.getId()).thenReturn(2);
		when(testCaseWrapper.getId()).thenReturn(3);
		when(testCaseWrapper.getInternalId()).thenReturn(4);
		when(testCaseWrapper.getExecutionStatus()).thenReturn(status);
		when(testCaseWrapper.getNotes()).thenReturn("notes");
		when(testCaseWrapper.getPlatform()).thenReturn("platform");
		
		when(
				api.reportTCResult(anyInt(), anyInt(), anyInt(),
						eq(status), anyInt(), anyString(),
						anyString(), anyBoolean(), anyString(), anyInt(),
						anyString(), anyMapOf(String.class, String.class),
						anyBoolean())).thenReturn(reportTCResultResponse);
		when(reportTCResultResponse.getExecutionId()).thenReturn(321);
	}
	

}
