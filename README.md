| Plugin Information                                                                            |
|-----------------------------------------------------------------------------------------------|
| View TestLink [on the plugin site](https://plugins.jenkins.io/testlink) for more information. |

The current version of this plugin may not be safe to use. Please review
the following warnings before use:

-   [Credentials stored in plain
    text](https://jenkins.io/security/advisory/2019-08-07/#SECURITY-1428)

Older versions of this plugin may not be safe to use. Please review the
following warnings before using an older version:

-   [Stored cross-site scripting
    vulnerability](https://jenkins.io/security/advisory/2018-02-26/#SECURITY-731)

**This plugin is up for adoption.** Want to help improve this plugin?
[Click here to learn
more](https://wiki.jenkins.io/display/JENKINS/Adopt+a+Plugin "Adopt a Plugin")!

This plug-in integrates Jenkins and [TestLink](http://testlink.org/) and
generates reports on automated test execution. With this plug-in you can
manage your tests in TestLink, schedule and control in Jenkins, and
execute using your favorite test execution tool (TestPartner, Selenium,
TestNG, Perl modules, PHPUnit, among others).  
This plug-in is not part of TestLink project, issues regarding Jenkins
and TestLink integration must be reported in [Jenkins
JIRA](http://issues.jenkins-ci.org/). It has been reported by users that
it is also possible to use ReportNG, as this tool has an option to
output TestNG XML too.

When filing an issue, remember to include as much data as possible, but
avoid including sensitive data, as attachments can take a while to be
deleted from JIRA, as the process is manual and quite troublesome
![(smile)](https://wiki.jenkins.io/s/en_GB/8100/5084f018d64a97dc638ca9a178856f851ea353ff/_/images/icons/emoticons/smile.svg)

## Jenkins TestLink Plug-in Tutorial

Check out this tutorial with step-by-step instructions on how to
integrate Jenkins and TestLink using TestLink Plug-in.

-   [Jenkins TestLink Plug-in Tutorial
    (HTML)](http://tupilabs.com/books/jenkins-testlink-plugin-tutorial/en/index.html)
-   [Jenkins TestLink Plug-in Tutorial
    (PDF)](http://tupilabs.com/books/jenkins-testlink-plugin-tutorial/en/book.pdf)
-   [Jenkins TestLink Plug-in Tutorial
    (HTML)](http://tupilabs.com/books/jenkins-testlink-plugin-tutorial/de-de/index.html)
    (German)
-   [Jenkins TestLink Plug-in Tutorial
    (HTML)](http://tupilabs.com/books/jenkins-testlink-plugin-tutorial/fr/index.html)
    (French)
-   [Jenkins TestLink Plug-in Tutorial
    (HTML)](http://tupilabs.com/books/jenkins-testlink-plugin-tutorial/es/index.html)
    (Spanish)
-   [Jenkins TestLink Plug-in Tutorial
    (HTML)](http://tupilabs.com/books/jenkins-testlink-plugin-tutorial/pt-br/index.html)
    (Brazilian Portuguese)

#### [Who is using Jenkins TestLink Plug-in](https://wiki.jenkins.io/display/JENKINS/Who+is+using+TestLink+Plug-in)

[Let us know if you are using this
plug-in.](https://wiki.jenkins.io/display/JENKINS/Who+is+using+TestLink+Plug-in)

## Description

The TestLink plug-in integrates **Jenkins** with
[TestLink](http://testlink.org/). TestLink plug-in uses
[testlink-java-api](https://github.com/kinow/testlink-java-api "testlink-java-api")
to access TestLink **XML-RPC** API. With the information that you
provide in the build step configuration the plug-in **retrieves
automated tests from TestLink**. With the plug-in, you are able to
execute **build steps** that call testing tools. It reads **TestNG**,
**JUnit** and **TAP** test report **formats**, used to update TestLink
test cases' executions.

![](https://wiki.jenkins.io/download/attachments/46335320/1.png?version=2&modificationDate=1318036291000&api=v2)

The job configuration changed! Now there are three sections, what helps
you to configure your job, and add extra build steps, result seeking
strategies and general information about TestLink.

TestLink configuration section

![](https://wiki.jenkins.io/download/attachments/46335320/001.PNG?version=2&modificationDate=1331609348000&api=v2)

Test Execution section

![](https://wiki.jenkins.io/download/attachments/46335320/002.PNG?version=2&modificationDate=1331609353000&api=v2)

And Result Seeking Strategy section

![](https://wiki.jenkins.io/download/attachments/46335320/003.PNG?version=2&modificationDate=1331609355000&api=v2)

You can use environment variables in Test project name, Test plan name
or Build name. For instance, build-project-$BUILD\_NUMBER would be
replaced by build-project- and the number of the Build executed in
Jenkins.

![](https://wiki.jenkins.io/download/attachments/46335320/3.png?version=1&modificationDate=1318035980000&api=v2)

![](https://wiki.jenkins.io/download/attachments/46335320/4.png?version=1&modificationDate=1318035980000&api=v2)

![](https://wiki.jenkins.io/download/attachments/46335320/5.png?version=1&modificationDate=1318035979000&api=v2)

#### How does the plug-in execute my automated tests?

TestLink Plug-in retrieves automated test cases data from TestLink. This
data is then set as environment variables. This way your build steps can
make use of this information to execute your automated tests.

For instance, you could run ant passing a Test Class as parameter, more
or less something like this, /opt/java/apache-ant-1.8.0/bin/ant
-DtestClass=$TESTLINK\_TESTCASE\_TEST\_CLASS. In this case, Test Case is
the name of a custom field in TestLink.

List of environment variables available during TestLink Plug-in
execution of single build steps
([JENKINS-17622](https://issues.jenkins-ci.org/browse/JENKINS-17622)):

-   TESTLINK\_BUILD\_NAME
-   TESTLINK\_TESTPLAN\_NAME
-   TESTLINK\_TESTPROJECT\_NAME
-   TESTLINK\_TESTCASE\_TOTAL

List of environment variables available during TestLink Plug-in
execution of iterative build steps:

-   TESTLINK\_TESTCASE\_ID
-   TESTLINK\_TESTCASE\_NAME
-   TESTLINK\_TESTCASE\_TESTPROJECTID
-   TESTLINK\_TESTCASE\_AUTHOR
-   TESTLINK\_TESTCASE\_SUMMARY
-   TESTLINK\_BUILD\_NAME
-   TESTLINK\_TESTCASE\_TESTSUITEID
-   TESTLINK\_TESTPLAN\_NAME
-   TESTLINK\_TESTPROJECT\_NAME
-   TESTLINK\_TESTCASE\_$CUSTOM\_FIELD\_NAME

For each Custom Field found, TestLink Plug-in defines an environment
variable for with its name and value, as shown above
(%CUSTOM\_FIELD\_NAME%). If the custom field name contains spaces the
plug-in will convert them into \_'s. i.e., Test Class can be accessed as
%TESTLINK\_TESTCASE\_TEST\_CLASS% in Windows or
$TESTLINK\_TESTCASE\_TEST\_CLASS in \*nix-like systems.

For each test case, the plug-in executes one call for each custom field.
In other words, if you have 1000 tests in a test plan, the plug-in will
execute a lot of calls to TestLink, downgrading the execution of your
job.

#### How does the plug-in know if a test passed or failed?

You can choose among different **result seeking strategies** in your job
configuration. The plug-in supports three **result formats**, *TestNG*,
*JUnit* and *TAP*. 

#### TestNG result seeking strategies

**TestNG class name**: The plug-in matches the TestNG class name (e.g.:
org.tap4j.TestParser) and the key custom field value.

**TestNG method name**: The plug-in matches the TestNG method name
(e.g.: org.tap4j.TestParser\#testTokenizer) and the key custom field
value.

**TestNG suite name**: The plug-in matches the TestNG suite name (e.g.:
functional tests) and the key custom field value.

#### JUnit result seeking strategies

**JUnit case class name**: The plug-in matches the JUnit case class name
(e.g.: org.tap4j.TestParser) and the key custom field value.

**JUnit case name**: The plug-in matches the JUnit case class name
(e.g.: testTokenizer) and the key custom field value.

**JUnit method name**: The plug-in matches the JUnit case class name
(e.g.: org.tap4j.TestParser\#testTokenizer) and the key custom field
value.

**JUnit suite name**: The plug-in matches the JUnit suite name
(e.g.:functional tests) and the key custom field value.

#### TAP result seeking strategies

**TAP file name**: The plug-in matches the TAP file name (e.g.:
testFtpProtocol.tap) and the key custom field value. 

**TAP file name multiple test points:** Adds one test execution in
TestLink for each TAP Test Result with its execution status

## Configuration

1.  Download and install the latest version of TestLink
    (<http://testlink.org/>).
2.  Create automated tests in TestLink with Custom Field for automation.
3.  Install the TestLink plug-in from the Jenkins Plugin Manager
4.  Define a TestLink configuration from the Configure System page.
5.  Add a Invoke TestLink build step in the job you want execute
    automated tests.
6.  Configure the required properties.

## Languages Supported

1.  English (American)
2.  Portuguese (Brazil)
3.  Spanish (Thanks to César Fernandes de Almeida)
4.  French (Thanks to Floréal Toumikian, Olivier Renault, Latifa
    Elkarama)

Want to see this plug-in in your language? Send us an e-mail and we will
get in touch with the text that needs to be translated.

## Resources

1.  [Enabling SSL for Jenkins TestLink plugin in
    Tomcat](http://yasassriratnayake.blogspot.co.nz/2016/04/enabling-ssl-for-jenkins-testlink.html)
2.  [Google Test + TAP Listener + Jenkins TestLink
    Plug-in](http://www.kinoshita.eti.br/2012/10/11/jenkins-testlink-and-gtest-in-5-minutes-or-so/)
3.  [Article](http://www.kinoshita.eti.br/wp-content/uploads/2010/12/testingexperience12_12_10_Kinoshita_Santos.pdf)
    published in [Testing Experience
    magazine](http://www.testingexperience.com/) issue number 12 (Open
    Source Tools) written by [Bruno P.
    Kinoshita](http://www.kinoshita.eti.br/) and Anderson dos
    Santos. 2010.
4.  [Slides](http://www.scribd.com/doc/43729582/Automatizando-Testes-Com-Hudson-e-TestLink)
    used in the lighting talk presented at [Encontro
    Ágil](http://www.encontroagil.com.br/) 2010 in
    [IME-USP](http://www.ime.usp.br/) (Portuguese Only). 2010.
5.  [Article](http://www.automatedtestinginstitute.com/home/ASTMagazine/2011/AutomatedSoftwareTestingMagazine_March2011.pdf)
    published in [Automated Software Testing
    Magazine](http://www.automatedtestinginstitute.com/), volume 3,
    issue 1. March, 2011.
6.  [Slides](http://www.belgiumtestingdays.com/archive/bruno_de_paula_kinoshita_how_to_automate_tests_using_testlink_and_hudson.pdf)
    used in the presentation done in [Belgium Testing
    Days](http://www.belgiumtestingdays.com/) 2011.
7.  [Lighting talk](http://www.vimeo.com/16924211) for [Encontro
    Ágil](http://www.encontroagil.com.br/) 2010, at
    [IME-USP](http://www.ime.usp.br/).
8.  More articles and tips on [Bruno P. Kinoshita's
    website](http://www.kinoshita.eti.br/).
9.  Sponsor company (until May 2011): [Sysmap
    Solutions](http://www.sysmap.com.br/) - Brazil.
10. Sponsor company: [TupiLabs](http://www.tupilabs.com/) - Brazil
11. Presentation at [STPCon](http://www.stpcon.com/) Spring March 2012
    in New Orleans - USA.
12. A [collection of
    links](http://forza.cocolog-nifty.com/blog/2012/10/jenkins-testlin.html)
    about jenkins-testlink plugin
    by [http://forza.cocolog-nifty.com](http://forza.cocolog-nifty.com/) (Japanese/日本語)

## Sponsors

[![](https://wiki.jenkins.io/download/attachments/46335320/logo1.png?version=1&modificationDate=1342969072000&api=v2)](http://www.tupilabs.com/)

For commercial support, please get contact us
via [@tupilabs](https://twitter.com/tupilabs)

## Release Notes

### Release 4.0 (????-??-??)

1.  [JENKINS-64023](https://issues.jenkins-ci.org/browse/JENKINS-64023) - org.apache.commons.jelly.JellyTagException: jar:file:/var/lib/jenkins/plugins/testlink/WEB-INF/lib/testlink.jar!/hudson/plugins/testlink/TestLinkResult/index.jelly:6:57: No page found 'sidepanel.jelly' for class hudson.plugins.testlink.TestLinkResult
2.  Added Credentials Plugin, and Plain Credentials Plugin as dependency, storing devKey in a string credential now, instead of using plain text

### Release 3.16 (2019-02-07)

1.  [JENKINS-48488](https://issues.jenkins-ci.org/browse/JENKINS-48488) -
    Request to make Testcase version available in Jenkins Build
    environment

### Release 3.15 (2018-12-29)

1.  Updated testlink-java-api to 1.9.17-0
2.  Updated Jenkins parent in pom.xml
3.  Updated dependencies (lang, io, codec)
4.  Set project Java to 8
5.  Tested with TestLink 1.9.17 (basic workflow, with a TAP file name
    strategy)

### Release 3.14 (2018-03-28)

1.  [JENKINS-50445](https://issues.jenkins-ci.org/browse/JENKINS-50445) -
    Add tap4j model objects to the whitelist for serialization to make
    TAP reporting compatible with Jenkins 2.102+
2.  [JENKINS-49302](https://issues.jenkins-ci.org/browse/JENKINS-49302) -
    Fix escaping of summary reports (regression in 3.13)
3.  [PR \#31](https://github.com/jenkinsci/testlink-plugin/pull/31) -
    Fix issues in French localization

### Release 3.13 (2017--)

1.  [Pull request \#26: make the testcase external ID visible as an
    environment variable in a Jenkins
    shell](https://github.com/jenkinsci/testlink-plugin/pull/26) thanks
    @johnwalker247!
2.  [Pull request \#22: Updateparser to version 0.5 with code
    fixes](https://github.com/jenkinsci/testlink-plugin/pull/22) thanks
    @yasassri
3.  [Fix security
    issue](https://jenkins.io/security/advisory/2018-02-26/)

### Release 3.12 (2016-04-17)

1.  Upgraded
    [tap4j](https://wiki.jenkins.io/display/JENKINS/TestLink+Plugin#)([http://tap4j.org](http://tap4j.org/))
2.  Upgraded
    [testlink-java-api](https://wiki.jenkins.io/display/JENKINS/TestLink+Plugin#)(<https://github.com/kinow/testlink-java-api>)
3.  [Pull request \#20: Plan and build custom fields
    support](https://github.com/jenkinsci/testlink-plugin/pull/20)
    thanks @maiksaray!

### Release 3.11 (2015-11-14)

1.  [TAP test plans support "n..m" with
    n\>1](https://github.com/jenkinsci/testlink-plugin/pull/19)
2.  [Added build number and error message in notes of JUnit Test
    Result](https://github.com/jenkinsci/testlink-plugin/pull/18)
3.  [Update
    TestLinkSite.java](https://github.com/jenkinsci/testlink-plugin/pull/17)

### Release 3.10

1.  <https://issues.jenkins-ci.org/browse/JENKINS-20599>

### Release 3.9

1.  <https://issues.jenkins-ci.org/browse/JENKINS-20587>
2.  <https://issues.jenkins-ci.org/browse/JENKINS-20589>

### Release 3.8

1.  [JENKINS-20014: Jenkins Testlink plugin reports "Found 0 automated
    test cases in
    TestLink."](https://issues.jenkins-ci.org/browse/JENKINS-20014)
2.  Reverted [JENKINS-17567: Allow the plug-in to filter test cases by
    last execution
    status](https://issues.jenkins-ci.org/browse/JENKINS-17567) due to a
    backward incompatibility in TestLink 1.9.8 XML-RPC API
3.  Updated [tap4j](http://tap4j.org/) to 4.0.4

### Release 3.7

1.  Merged pull
    request <https://github.com/jenkinsci/testlink-plugin/pull/8> that
    adds platforms to the job config

### Release 3.6

1.  [JENKINS-17801: TAP-attachments produce file not found
    error](https://issues.jenkins-ci.org/browse/JENKINS-17801)
2.  [JENKINS-15790: Name of Test Case is
    null](https://issues.jenkins-ci.org/browse/JENKINS-15790)
3.  [JENKINS-19209: Testlink Plugin Not Run Test should mark build as
    failed](https://issues.jenkins-ci.org/browse/JENKINS-19209)
4.  [JENKINS-19390: Testlink Plugin did not manage to get 2nd execution
    status](https://issues.jenkins-ci.org/browse/JENKINS-19390)
5.  Updated testlink-java-api to 1.9.7-0

### Release 3.5

1.  [JENKINS-17622: Cannot access TestLink environment variables within
    Jenkins build](https://issues.jenkins-ci.org/browse/JENKINS-17622)
2.  [JENKINS-16640: Mark in Jenkins build with "NOT RUN" Test Cases and
    display in the UI (with colours,
    etc)](https://issues.jenkins-ci.org/browse/JENKINS-16640)

### Release 3.4

1.  [JENKINS-17567: Allow the plug-in to filter test cases by last
    execution
    status](https://issues.jenkins-ci.org/browse/JENKINS-17567)
2.  [JENKINS-13821: When test uses DataProvider it mark in TestLink only
    by last result](https://issues.jenkins-ci.org/browse/JENKINS-13821)
3.  [JENKINS-17642: Trouble finding test results using TAP result
    seeking
    strategy](https://issues.jenkins-ci.org/browse/JENKINS-17642)
4.  Quick profiling with Yourkit. Nothing worth of refactoring was
    found. Thanks to Yourkit for providing an Open Source license to us.

### Release 3.3

1.  **[Jenkins-17442: When connectin to TestLink find error
    ClassCastException](https://issues.jenkins-ci.org/browse/JENKINS-17442) (due
    to a bug in TL XML-RPC API, the plug-in wasn't working with TestLink
    1.9.6)**
2.  [Jenkins-16118: Testlink Plug-In: get Custom field information for
    Testplans and
    Testprojects](https://issues.jenkins-ci.org/browse/JENKINS-16118)
3.  [Jenkins-17023: Jenkins Testlink plugin Found 0 test result is show
    while test is
    runned](https://issues.jenkins-ci.org/browse/JENKINS-17023)
4.  [Jenkins-15588: TestLink is not getting
    Updated](https://issues.jenkins-ci.org/browse/JENKINS-15588)
5.  [Jenkins-17147: The configuration for testng method name and data
    provider is not
    saved](https://issues.jenkins-ci.org/browse/JENKINS-17147)

### Release 3.2

1.  **Updated Jenkins version to 1.466**
2.  The plug-in now should work correctly with other plug-ins that
    require a BuildStepDescriptor, as Conditional Build Step
3.  [JENKINS-15486: Documentation is needed for Project GTest Sample
    Tap&Testlink for C++
    testing](https://issues.jenkins-ci.org/browse/JENKINS-15486)
4.  [JENKINS-15343: Unable to use Conditional BuildStep Plugin with
    Testlink Plugin](https://issues.jenkins-ci.org/browse/JENKINS-15343)

### Release 3.1.5

[JENKINS-10904](https://issues.jenkins-ci.org/browse/JENKINS-10904) -
Include test step information as env vars. We had to update
testlink-java-api, as the bug was in there. However, the TestLink data
stored in builds will be lost. So if you need any of the data, back it
up before updating the plug-in. 

### Release 3.1.2

Added test summary. This way the user can see more details in TestLink
reports.

### Release 3.1.1

Fixing bugs in JUnit and TestNG strategies

### Release 3.1

1.  **Created Result Seeking Strategy extension point**
2.  Fixed all blocker issues
3.  Added more result seeking strategies (TestNG method name, for
    example)
4.  Added new contributors to the project
5.  **Code reviewed and refactored several parts of the code, it's way
    cleaner now**
6.  **Added configuration to make optional attachments upload**

### Release 3.0.2

Minor improvements for issues found during update of Jenkins TestLink
Plug-in Tutorial

### Release 3.0.1

[JENKINS-11264](https://issues.jenkins-ci.org/browse/JENKINS-11264) -
Test execution notes being added twice and incorrectly formatted

### Release 3.0

1.  [JENKINS-10623](https://issues.jenkins-ci.org/browse/JENKINS-10623) -
    Organize and update French, Spanish and Brazilian Portuguese
    translation and documentation
2.  [JENKINS-9054](https://issues.jenkins-ci.org/browse/JENKINS-9054)
    **- Add support to platforms in TestLink plug-in**
3.  [JENKINS-10809](https://issues.jenkins-ci.org/browse/JENKINS-10809) -
    Add a way to call other build steps in test execution for the
    plug-in (Yay for DRY!)
4.  [JENKINS-10849](https://issues.jenkins-ci.org/browse/JENKINS-10849) -
    OutOfMemoryError using TestLink plugin (Thanks to YourKit!)

### Release 2.5

1.  JENKINS-9811 Add a POST \<Single test command\>-field, to execute a
    process after the plug-in iterates the retrieved automated test
    cases
2.  JENKINS-9672 Test link custom field parser split the String value by
    semicolon or comma.
3.  JENKINS-9993 Add root element for JUnit test results

### Release 2.2.2

1.  FIXED-9444 - Add environment variables to single test command
    (actually, I used some code from Jenkins core to execute both
    commands now :-)

### Release 2.2.1

1.  French translation
2.  Small issue with non-existent option Debug in Job configuration
3.  FIXED-9229 - JUnit wrong status
4.  Portuguese i18n messages typo

### Release 2.2

1.  JUnit BUG fix
2.  Enhancement of the Build logs

### Release 2.1

1.  **Migration from Hudson to Jenkins**
2.  Test transaction property.
3.  i18n (version 1.0 supports only English).
4.  Add Javascript validation functions to each field in the global and
    config pages.
5.  Add single test command feature (it will enable running test suites)
6.  Fixed Java class headers
7.  JFreechart graphics betterment
8.  Fixed BUG JENKINS-8636 TestLink Plugin FATAL: Error creating test
    project
9.  Fixed BUG JENKINS-8531 Unexprssive error message

### Release 2.0.1

1.  Fixed BUG 8292 (java.lang.NullPointerException at
    hudson.plugins.testlink.updater.TestLinkTestStatusUpdater.updateTestCases(TestLinkTestStatusUpdater.java:55)

### Release 2.0

1.  **Settings automated tests properties as environment variables.**
2.  **Switch from dbfacade-testlink-java-api to testlink-java-api**
    **<https://github.com/kinow/testlink-java-api>.**
3.  **JUnit parser.**
4.  **TestNG parser.**
5.  **TAP parser using tap4j**
    **<http://tupilabs.com/tap4j/>.**

### Release 1.1

1.  Execute Test Suites.
2.  Let the user define the custom fields name.
3.  Implement feature that lets user to use the latest revision from a
    SVN repository as Build name.

### Release 1.0

1.  First version of the plug-in (the development was guided based on
    the source code of the following plug-ins:
    [CCM](http://wiki.jenkins-ci.org/display/HUDSON/CCM+Plugin "CCM") ,
    [Sonar](http://wiki.jenkins-ci.org/display/HUDSON/Sonar+Plugin "Sonar")
    (how to ref maven installations)).

## Roadmap

1.  Keep compatibility with latest versions of Jenkins and TestLink.
2.  Add test case steps
    ([JENKINS-10904](https://issues.jenkins-ci.org/browse/JENKINS-10904))

## Open source licenses donated for this project

[![](https://wiki.jenkins.io/download/attachments/46335320/stan4j-88x31-o1.png?version=1&modificationDate=1301522276000&api=v2)](http://stan4j.com/)

YourKit is kindly supporting open source projects with its full-featured
Java Profiler.  
YourKit, LLC is the creator of innovative and intelligent tools for
profiling  
Java and .NET applications. Take a look at YourKit's leading software
products:  
[YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp)
and  
[YourKit .NET Profiler](http://www.yourkit.com/java/profiler/index.jsp).
