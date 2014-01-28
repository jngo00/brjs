package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AliasModelTest extends SpecTest {
	private App app;
	private AppConf appConf;
	private Aspect aspect;
	private AliasesFile aspectAliasesFile;
	private Bladeset bladeset;
	private AliasDefinitionsFile bladesetAliasDefinitionsFile;
	private Blade blade;
	private AliasDefinitionsFile bladeAliasDefinitionsFile;
	
	@Before
	public void initTestObjects() throws Exception {
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			appConf = app.appConf();
			aspect = app.aspect("default");
			aspectAliasesFile = aspect.aliasesFile();
			bladeset = app.bladeset("bs");
			bladesetAliasDefinitionsFile = bladeset.assetLocation("src").aliasDefinitionsFile();
			blade = bladeset.blade("b1");
			bladeAliasDefinitionsFile = blade.assetLocation("src").aliasDefinitionsFile();
	}
	
	@Test
	public void aliasesAreRetrievableViaTheModel() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1");
	}
	
	@Test
	public void nonExistentAliasesAreNotRetrievable() throws Exception {
		when(aspect).retrievesAlias("no-such-alias");
		then(exceptions).verifyException(UnresolvableAliasException.class, "no-such-alias");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesetsMustBeNamespaced() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("the-alias", "appns.Class1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs");
	}
	
	@Test
	public void aliasDefinitionsDefinedWithinBladesMustBeNamespaced() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("the-alias", "appns.Class1");
		when(aspect).retrievesAlias("the-alias");
		then(exceptions).verifyException(NamespaceException.class, "the-alias", "appns.bs.b1");
	}
	
	@Test
	public void aliasDefinitionsCanBeOverriddenWithinTheAliasesFile() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void aliasDefinitionsCantBeOverriddenWithinTheBladeset() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladesetAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void theNonScenarioAliasIsUsedByDefault() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class1");
	}
	
	@Test
	public void settingTheScenarioChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenTheScenarioIsSet() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class3");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class3");
	}
	
	@Test
	public void theNonGroupAliasIsUsedByDefault() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class1");
	}
	
	@Test
	public void settingAGroupChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("g2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void aliasesCanStillBeOverriddenWhenAGroupIsSet() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(aspectAliasesFile).usesGroups("g1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void groupAliasesCanOverrideNonGroupAliases() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2", "appns.Class3")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "appns.bs.b1.the-alias", "appns.Class3")
			.and(aspectAliasesFile).usesGroups("g1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void usingGroupsCanLeadToAmbiguityEvenWhenASingleGroupIsUsed() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladesetAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("g1");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", aspectAliasesFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void settingMultipleGroupsChangesTheAliasesThatAreUsed() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.alias1", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "appns.bs.b1.alias2", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2");
		then(aspect).hasAlias("appns.bs.b1.alias1", "appns.Class1");
		then(aspect).hasAlias("appns.bs.b1.alias2", "appns.Class2");
	}
	
	@Test
	public void usingMultipleGroupsCanLeadToAmbiguity() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClasses("appns.Class1", "appns.Class2")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g2", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("g1", "g2");
		when(aspect).retrievesAlias("appns.bs.b1.the-alias");
		then(exceptions).verifyException(AmbiguousAliasException.class, "appns.bs.b1.the-alias", bladeAliasDefinitionsFile.getUnderlyingFile().getPath());
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAliasesFile() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1")
			.and(aspectAliasesFile).hasAlias("appns.bs.b1.the-alias", "appns.Class2");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2", "appns.Interface1");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInTheScenario() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1")
			.and(bladeAliasDefinitionsFile).hasScenarioAlias("s1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesScenario("s1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2", "appns.Interface1");
	}
	
	@Test
	public void theInterfaceIsMaintainedWhenAnAliasIsOverriddenInAGroup() throws Exception {
		given(appConf).hasNamespace("appns")
			.and(aspect).hasClass("appns.Class1")
			.and(bladeAliasDefinitionsFile).hasAlias("appns.bs.b1.the-alias", "appns.Class1", "appns.Interface1")
			.and(bladeAliasDefinitionsFile).hasGroupAlias("g1", "appns.bs.b1.the-alias", "appns.Class2")
			.and(aspectAliasesFile).usesGroups("g1");
		then(aspect).hasAlias("appns.bs.b1.the-alias", "appns.Class2", "appns.Interface1");
	}
}