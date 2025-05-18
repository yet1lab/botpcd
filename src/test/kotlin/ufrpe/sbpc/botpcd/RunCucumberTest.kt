package ufrpe.sbpc.botpcd

@Suite
@IncludeEngines("cucumber")
@SelectPackages("ufrpe.sbpc.botpcd")
@RunWith(Cucumber::class)
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ufrpe.sbpc.botpcd")
class RunCucumberTest {
}