package com.caplin.cutlass.request;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.conf.AppConf;
import com.google.common.base.Joiner;

public class LocaleHelperTest
{
	private HttpServletRequest request;
	private Vector<Locale> preferedLocales;
	private AppConf appConf;
	
	@Before
	public void setup()
	{
		request = mock(HttpServletRequest.class);
		preferedLocales = new Vector<Locale>();
		appConf = new AppConf("appx","");
	}
	
	@Test
	public void getLanguageFromLocale()
	{
		String language = LocaleHelper.getLanguageFromLocale("en_GB");
		assertEquals("en", language);
	}
	
	@Test
	public void browserGetsTheDefaultLocaleIfNoneOfTheirPreferencesAreAvailable() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("de_DE", "de");
		whenRequestIsMade();
		thenLocaleProvidedIs("en");
	}
	
	@Test
	public void browserAlsoGetsTheDefaultLocaleIfThisMatchesTheirPreference() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("en_GB", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en");
	}
	
	@Test
	public void browserGetsNonDefaultLocaleIfThisMatchesTheirPreference() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("es_ES", "es");
		whenRequestIsMade();
		thenLocaleProvidedIs("es");
	}
	
	@Test
	public void browserGetsCountrySpecificLocale() throws Exception
	{
		givenServerSupportsLocales("en_GB", "en");
			andBrowserPrefersLocales("en_GB", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void browserGetsCountrySpecificLocaleEvenWhenThatAppearsSecondInTheListOfSupportedLocales() throws Exception
	{
		givenServerSupportsLocales("en", "en_GB");
			andBrowserPrefersLocales("en_GB", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en_GB");
	}
	
	@Test
	public void browserGetsGeneralLocaleIfTheirLanguageSpecificVariationMayNotBeAvailable() throws Exception
	{
		givenServerSupportsLocales("en", "en_GB");
			andBrowserPrefersLocales("en_US", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("en");
	}
	
	@Test
	public void browserGetsFirstLanguageInTheirListEvenIfTheySupportTheDefault() throws Exception
	{
		givenServerSupportsLocales("en", "es");
			andBrowserPrefersLocales("es_ES", "es", "en");
		whenRequestIsMade();
		thenLocaleProvidedIs("es");
	}
	
	@Test
	public void localeSpecifiedInCookieOverridesBrowserLocales() throws Exception
	{
		givenServerSupportsLocales("en", "de", "es");
			andBrowserPrefersLocales("en_GB", "en");
			andBrowserHasCookies(
				new Cookie("some-cookie", "a-value"),
				new Cookie("another-cookie", "a-value"),
				new Cookie("CAPLIN.LOCALE", "de"));
		whenRequestIsMade();
		thenLocaleProvidedIs("de");
	}
	
	@Test
	public void browserFallsBackToNormalSelectionBehaviourIfTHeCookieSpecifiesAnUnsupportedLocale() throws Exception
	{
		givenServerSupportsLocales("en", "de", "es");
			andBrowserPrefersLocales("es_ES", "es");
			andBrowserHasCookies(
				new Cookie("CAPLIN.LOCALE", "zh_CN"));
		whenRequestIsMade();
		thenLocaleProvidedIs("es");
	}
	
	private void givenServerSupportsLocales(String... locales)
	{
		appConf.locales = Joiner.on(",").join(locales);
	}
	
	private void andBrowserPrefersLocales(String... locales)
	{
		for(String locale : locales) {
			String[] localeSplit = locale.split("_");
			
			if (localeSplit.length == 1)
			{
				preferedLocales.add(new Locale(localeSplit[0]));
			}
			else
			{
				preferedLocales.add(new Locale(localeSplit[0],localeSplit[1]));
			}
		}
	}
	
	private void andBrowserHasCookies(Cookie... cookies) {
		when(request.getCookies()).thenReturn( cookies );
	}
	
	private void whenRequestIsMade() {
		when(request.getLocales()).thenReturn( preferedLocales.elements() );
	}
	
	private void thenLocaleProvidedIs(String expectedLocale) throws Exception {
		assertEquals(expectedLocale, LocaleHelper.getLocaleFromRequest(appConf, request));
	}
}
